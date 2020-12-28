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
/* HEADER */
package com.sshtools.common.publickey;

import java.io.IOException;

import com.sshtools.common.ssh.SshException;
import com.sshtools.common.ssh.components.ComponentFactory;
import com.sshtools.common.ssh.components.ComponentManager;
import com.sshtools.common.ssh.components.SshKeyPair;
import com.sshtools.common.ssh.components.jce.JCEComponentManager;

/**
 * <p>
 * Generate public/private key pairs.
 * </p>
 * <p>
 * To generate a new pair use the following code <blockquote>
 * 
 * <pre>
 * SshKeyPair pair = SshKeyPairGenerator.generateKeyPair(SshKeyPairGenerator.SSH2_RSA, 1024);
 * </pre>
 * 
 * </blockquote> To create formatted key file for the public key use:
 * <blockquote>
 * 
 * <pre>
 * SshPublicKeyFile pubfile = SshPublicKeyFileFactory.create(pair.getPublicKey(), "Some comment",
 * 		SshPublicKeyFileFactory.OPENSSH_FORMAT);
 * FileOutputStream fout = new FileOutputStream("mykey.pub");
 * fout.write(pubfile.getFormattedKey());
 * fout.close();
 * </pre>
 * 
 * <blockquote> To create a formatted, encrypted private key file use:
 * <blockquote>
 * 
 * <pre>
 * SshPrivateKeyFile prvfile = SshPrivateKeyFileFactory.create(pair, "my passphrase", "Some comment",
 * 		SshPrivateKeyFileFactory.OPENSSH_FORMAT);
 * FileOutputStream fout = new FileOutputStream("mykey");
 * fout.write(prvfile.getFormattedKey());
 * fout.close();
 * </pre>
 * 
 * <blockquote>
 * </p>
 * 
 * @author Lee David Painter
 */
public class SshKeyPairGenerator {

	public static final String SSH2_RSA = "ssh-rsa";
	public static final String ECDSA = "ecdsa";
	public static final String ED25519 = "ed25519";

	/**
	 * Generate a new key pair using the default bit size.
	 * 
	 * @param algorithm
	 * @return
	 * @throws IOException
	 * @throws SshException
	 */
	public static SshKeyPair generateKeyPair(String algorithm) throws IOException, SshException {
		
		switch(algorithm) {
		case ECDSA:
			return generateKeyPair(algorithm, 256);
		case ED25519:
			return generateKeyPair(algorithm, 0);
		case SSH2_RSA:
		case "rsa":
		case "RSA":
			return generateKeyPair(algorithm, 2048)	;
		default:
			throw new IOException(String.format("Unexpected key algorithm %s", algorithm));
	}
	}
	/**
	 * Generates a new key pair.
	 * 
	 * @param algorithm
	 * @param bits
	 * @return SshKeyPair
	 * @throws IOException
	 */
	public static SshKeyPair generateKeyPair(String algorithm, int bits) throws IOException, SshException {

		
		switch(algorithm) {
		case ED25519:
		case "ssh-ed25519":
			return ComponentManager.getDefaultInstance().generateEd25519KeyPair();
		case ECDSA:
			return ComponentManager.getDefaultInstance().generateEcdsaKeyPair(bits);
		case SSH2_RSA:
		case "rsa":
		case "RSA":
			return ComponentManager.getDefaultInstance().generateRsaKeyPair(bits, 2);
		default:
			ComponentFactory<KeyGenerator> generators = new ComponentFactory<>(JCEComponentManager.getDefaultInstance());
			JCEComponentManager.getDefaultInstance().loadExternalComponents("generator.properties",generators);
			
			KeyGenerator gen = generators.getInstance(algorithm);
			return gen.generateKey(bits);
		}
	}

}
