/*
 * (c) 2002-2023 JADAPTIVE Limited. All Rights Reserved.
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
package com.sshtools.server.components.jce;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.sshtools.common.ssh.SecurityLevel;
import com.sshtools.server.components.SshKeyExchangeServerFactory;

public class DiffieHellmanEcdhNistp384 extends DiffieHellmanEcdh {

	private static final String KEY_EXCHANGE = "ecdh-sha2-nistp384";

	public static class DiffieHellmanEcdhNistp384Factory implements SshKeyExchangeServerFactory<DiffieHellmanEcdhNistp384> {
		@Override
		public DiffieHellmanEcdhNistp384 create() throws NoSuchAlgorithmException, IOException {
			return new DiffieHellmanEcdhNistp384();
		}

		@Override
		public String[] getKeys() {
			return new String[] { KEY_EXCHANGE };
		}
	}
	
	public DiffieHellmanEcdhNistp384() {
		super(KEY_EXCHANGE, "secp384r1", "SHA-384", SecurityLevel.STRONG,2384);
	}

}
