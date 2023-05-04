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
import com.sshtools.common.ssh.components.DiffieHellmanGroups;
import com.sshtools.common.ssh.components.jce.JCEAlgorithms;
import com.sshtools.server.components.SshKeyExchangeServerFactory;

public class DiffieHellmanGroup14Sha256JCE extends DiffieHellmanGroup {

	public static final String DIFFIE_HELLMAN_GROUP14_SHA256 = "diffie-hellman-group14-sha256";

	public static class DiffieHellmanGroup14Sha256JCEFactory implements SshKeyExchangeServerFactory<DiffieHellmanGroup14Sha256JCE> {
		@Override
		public DiffieHellmanGroup14Sha256JCE create() throws NoSuchAlgorithmException, IOException {
			return new DiffieHellmanGroup14Sha256JCE();
		}

		@Override
		public String[] getKeys() {
			return new String[] { DIFFIE_HELLMAN_GROUP14_SHA256 };
		}
	}
	
	public DiffieHellmanGroup14Sha256JCE() {
		super(DIFFIE_HELLMAN_GROUP14_SHA256, JCEAlgorithms.JCE_SHA256, DiffieHellmanGroups.group14, SecurityLevel.STRONG, 2001);
	}

}
