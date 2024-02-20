package com.sshtools.common.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.publickey.SshPublicKeyFile;
import com.sshtools.common.ssh.SshConnection;
import com.sshtools.common.ssh.SshException;
import com.sshtools.common.ssh.components.SshPublicKey;

public class UniversalAuthenticationProvider implements PublicKeyAuthenticationProvider {

	String hostname;
	int port;
	
	UniversalAuthenticatorAccountDatabase accountDatabase;
	
	public UniversalAuthenticationProvider(UniversalAuthenticatorAccountDatabase accountDatabase) {
		this(accountDatabase, "gateway.jadaptive.com", 443);
	}
	
	public UniversalAuthenticationProvider(UniversalAuthenticatorAccountDatabase accountDatabase, String hostname) {
		this(accountDatabase, hostname, 443);
	}
	
	public UniversalAuthenticationProvider(UniversalAuthenticatorAccountDatabase accountDatabase, String hostname, int port) {
		this.accountDatabase = accountDatabase;
		this.hostname = hostname;
		this.port = port;
	}
	
	@Override
	public boolean isAuthorizedKey(SshPublicKey key, SshConnection con) throws IOException {
		
		Set<String> gatewayAccounts = accountDatabase.getAccounts(con.getUsername());
		if(gatewayAccounts==null) {
			return false;
		}
		for(String gatewayAccount : gatewayAccounts) {
			for(SshPublicKey gatewayKey : getGatewayKeys(gatewayAccount)) {
				if(gatewayKey.equals(key)) {
					return true;
				}
			}
		}
		return false;
		
	}

	private Collection<SshPublicKey> getGatewayKeys(String username) throws IOException {
		
		StringBuffer buf = new StringBuffer();
		buf.append("https://");
		buf.append(hostname);
		if(port!=443) {
			buf.append(":");
			buf.append(port);
		}
		buf.append("/app/api/agent/authorizedKeys/");
		buf.append(username);
		
		URL url = new URL(buf.toString());
		InputStream in = url.openStream();
		Collection<SshPublicKey> keys = new ArrayList<>();
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("#")) {
					continue;
				}
				keys.add(SshKeyUtils.getPublicKey(line));
			}
		}
		
		return keys;
	}
	
	@Override
	public Iterator<SshPublicKeyFile> getKeys(SshConnection con)
			throws PermissionDeniedException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(SshPublicKey key, SshConnection con)
			throws IOException, PermissionDeniedException, SshException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(SshPublicKey key, String comment, SshConnection con)
			throws IOException, PermissionDeniedException, SshException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean checkKey(SshPublicKey key, SshConnection con) throws IOException {
		return isAuthorizedKey(key, con);
	}
}
