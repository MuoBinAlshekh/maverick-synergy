/**
 * (c) 2002-2019 JADAPTIVE Limited. All Rights Reserved.
 *
 * This file is part of the Maverick Synergy Java SSH API.
 *
 * Maverick Synergy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Maverick Synergy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Maverick Synergy.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.sshtools.vsession.commands.ssh;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.sshtools.client.ClientAuthenticator;
import com.sshtools.client.PasswordAuthenticator;
import com.sshtools.client.PublicKeyAuthenticator;
import com.sshtools.client.SessionChannelNG;
import com.sshtools.client.SshClient;
import com.sshtools.client.SshClientContext;
import com.sshtools.client.shell.ShellTimeoutException;
import com.sshtools.client.tasks.AbstractCommandTask;
import com.sshtools.client.tasks.ShellTask;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.logger.Log;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.publickey.SshPrivateKeyFile;
import com.sshtools.common.publickey.SshPrivateKeyFileFactory;
import com.sshtools.common.ssh.Connection;
import com.sshtools.common.ssh.ConnectionAwareTask;
import com.sshtools.common.ssh.SecurityLevel;
import com.sshtools.common.ssh.SshContext;
import com.sshtools.common.ssh.SshException;
import com.sshtools.common.ssh.components.SshKeyPair;
import com.sshtools.common.util.IOUtils;
import com.sshtools.server.vsession.CommandArgumentsParser;
import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.VirtualConsole;
import com.sshtools.server.vsession.commands.sftp.SftpClientOptions;

public class SshClientCommand extends ShellCommand {
	
	private String[] originalArguments = null;
	private Options options = new Options();

	public SshClientCommand() {
		super("ssh", SUBSYSTEM_SHELL, "", "Returns the ssh client shell");
		for (Option option : SftpClientOptions.getOptions()) {
			this.options.addOption(option);
		}
	}
	
	public Options getOptions() {
		return options;
	}

	@Override
	public String getUsage() {
		StringWriter out = new StringWriter();

		PrintWriter pw = new PrintWriter(out);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(pw, formatter.getWidth(), 
				"ssh",
				"", 
				getOptions(), 
				formatter.getLeftPadding(),
				formatter.getDescPadding(), "");
		pw.flush();

		String result = out.toString();

		return result;
	}
	
	@Override
	public void run(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException {

		String[] filteredArgs = filterArgs(args);
		CommandLine cli = CommandArgumentsParser.parse(getOptions(), filteredArgs, getUsage());
		
		SshClientArguments arguments = SshClientOptionsEvaluator.evaluate(cli, this.originalArguments);
		
		if (Log.isDebugEnabled()) {
			Log.debug(String.format("The arguments parsed are %s", arguments));
		}
		
		SshClient sshClient = null;
		try {
			
			SshClientContext ctx = getSshContext(arguments);
			
			setUpCipherSpecs(arguments, ctx);
			setUpMacSpecs(arguments, ctx);
			setUpCompression(arguments, ctx);
			
			sshClient = new SshClient(arguments.getDestination(), arguments.getPort(), arguments.getLoginName(), ctx);
			ClientAuthenticator auth;

			if (CommandUtil.isNotEmpty(arguments.getIdentityFile())) {
				
				String identityFile = arguments.getIdentityFile();
				AbstractFile identityFileTarget = console.getCurrentDirectory().resolveFile(identityFile);
				SshPrivateKeyFile pkf = SshPrivateKeyFileFactory.parse(identityFileTarget.getInputStream());
				
				String passphrase = null;
				if (pkf.isPassphraseProtected()) {
					do {
						passphrase = console.getLineReader().readLine("Passphrase :", '\0');
						SshKeyPair pair = pkf.toKeyPair(passphrase);

						auth = new PublicKeyAuthenticator(pair);

						if (sshClient.authenticate(auth, 30000)) {
							break;
						}
					} while (sshClient.isConnected());
				}

			} 

			if(!sshClient.isAuthenticated()) {
				do {
					auth = new PasswordAuthenticator(console.getLineReader().readLine("Password :", '*'));
					if (sshClient.authenticate(auth, 30000)) {
						break;
					}
				} while (sshClient.isConnected());
			}
			
			if (CommandUtil.isNotEmpty(arguments.getCommand())) {
				String command = arguments.getCommand();
				Connection<SshClientContext> connection = sshClient.getConnection();
				AbstractCommandTask task = new AbstractCommandTask(connection, command) {
					
					@Override
					protected void beforeExecuteCommand(SessionChannelNG session) {
						session.allocatePseudoTerminal(console.getTerminal().getType(), console.getTerminal().getWidth(),
								console.getTerminal().getHeight());
					}
					
					@Override
					protected void onOpenSession(SessionChannelNG session) throws IOException {
						
						console.getSessionChannel().enableRawMode();

						con.addTask(new ConnectionAwareTask(con) {
							@Override
							protected void doTask() throws Throwable {
								IOUtils.copy(console.getSessionChannel().getInputStream(), session.getOutputStream());
							}
						});
						IOUtils.copy(session.getInputStream(), console.getSessionChannel().getOutputStream());
					}
				};
				
				connection.addTask(task);
				task.waitForever();
				console.getSessionChannel().disableRawMode();

				return;
			}

			Connection<SshClientContext> connection = sshClient.getConnection();

			console.println("Starting new shell.");
			console.println();

			ShellTask shell = new ShellTask(connection) {

				protected void beforeStartShell(SessionChannelNG session) {

					session.allocatePseudoTerminal(console.getTerminal().getType(), console.getTerminal().getWidth(),
							console.getTerminal().getHeight());
				}

				@Override
				protected void onOpenSession(final SessionChannelNG session)
						throws IOException, SshException, ShellTimeoutException {

					console.getSessionChannel().enableRawMode();

					con.addTask(new ConnectionAwareTask(con) {
						@Override
						protected void doTask() throws Throwable {
							IOUtils.copy(console.getSessionChannel().getInputStream(), session.getOutputStream());
						}
					});
					IOUtils.copy(session.getInputStream(), console.getSessionChannel().getOutputStream());
				}

			};

			connection.addTask(shell);
			shell.waitForever();

			console.getSessionChannel().disableRawMode();
			console.println();
			console.println("Shell closed.");

		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			if (sshClient != null) {
				sshClient.close();
			}
		}
	}

	protected String[] filterArgs(String[] args) {
		this.originalArguments = args;
		int indexTillSshClientCommandFound = SshClientOptionsExtractor.extractSshCommandLineFromExecuteCommand(args);
		return Arrays.copyOfRange(args, 0, indexTillSshClientCommandFound + 1);
	}
	
	private SshClientContext getSshContext(SshClientArguments arguments) throws IOException, SshException {
		if(CommandUtil.isNotEmpty(arguments.getSecurityLevel()) && 
				(CommandUtil.isNotEmpty(arguments.getCiphers()) || CommandUtil.isNotEmpty(arguments.getHmacs()))) {
			throw new IllegalArgumentException("Security level cannot be specified together with cipher or hmac spec.");
		}
		
		if (CommandUtil.isNotEmpty(arguments.getSecurityLevel())) {
			SecurityLevel securityLevel = SecurityLevel.valueOf(arguments.getSecurityLevel());
			return new SshClientContext(securityLevel);
		}
		
		if (CommandUtil.isNotEmpty(arguments.getCiphers()) || CommandUtil.isNotEmpty(arguments.getHmacs())) {
			return new SshClientContext(SecurityLevel.NONE);
		}
				
		return new SshClientContext();
	}
	
	private void setUpCipherSpecs(SshClientArguments arguments, SshClientContext ctx)
			throws IOException, SshException {
		if (CommandUtil.isNotEmpty(arguments.getCiphers())) {
			String[] cipherSpecs = arguments.getCiphers();
			
			for (int i = cipherSpecs.length - 1; i >= 0; --i) {
				ctx.setPreferredCipherCS(cipherSpecs[i]); 
				ctx.setPreferredCipherSC(cipherSpecs[i]);
			}
			
		}
	}
	
	
	private void setUpMacSpecs(SshClientArguments arguments, SshClientContext ctx)
			throws IOException, SshException {
		if (CommandUtil.isNotEmpty(arguments.getHmacs())) {
			String[] macSpecs = arguments.getHmacs();
			
			for (int i = macSpecs.length - 1; i >= 0; --i) {
				ctx.setPreferredMacCS(macSpecs[i]); 
				ctx.setPreferredMacSC(macSpecs[i]);
			}
			
		}
	}
	
	private void setUpCompression(SshClientArguments arguments, SshClientContext ctx) 
			throws IOException, SshException {
		if (arguments.isCompression()) {
			ctx.setPreferredCompressionCS(SshContext.COMPRESSION_ZLIB);
 			ctx.setPreferredCompressionSC(SshContext.COMPRESSION_ZLIB);
		}
	}

}
