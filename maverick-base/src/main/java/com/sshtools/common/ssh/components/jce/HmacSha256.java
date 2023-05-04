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
package com.sshtools.common.ssh.components.jce;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.sshtools.common.ssh.SecurityLevel;
import com.sshtools.common.ssh.SshException;
import com.sshtools.common.ssh.components.SshHmacFactory;


/**
 * SHA-1 message authentication implementation.
 * @author Lee David Painter
 *
 */
public class HmacSha256 extends AbstractHmac {
	
	public static class HmacSha256Factory implements SshHmacFactory<HmacSha256> {
		@Override
		public HmacSha256 create() throws NoSuchAlgorithmException, IOException {
			return new HmacSha256();
		}

		@Override
		public String[] getKeys() {
			return new String[] { "hmac-sha2-256" };
		}
	}

	public HmacSha256() {
		super(JCEAlgorithms.JCE_HMACSHA256, 32, SecurityLevel.STRONG, 2000);
	}
	
	protected HmacSha256(int size) {
		super(JCEAlgorithms.JCE_HMACSHA256, 32, size, SecurityLevel.STRONG, 2000);
	}
	
	public String getAlgorithm() {
		return "hmac-sha2-256";
	}
	

	public void init(byte[] keydata) throws SshException {
        try {
            mac = JCEProvider.getProviderForAlgorithm(jceAlgorithm)==null ? Mac.getInstance(jceAlgorithm) : Mac.getInstance(jceAlgorithm, JCEProvider.getProviderForAlgorithm(jceAlgorithm));

            // Create a key of 16 bytes
            byte[] key = new byte[System.getProperty("miscomputes.ssh2.hmac.keys", "false").equalsIgnoreCase("true") ? 16 : 32];
            System.arraycopy(keydata, 0, key, 0, key.length);

            SecretKeySpec keyspec = new SecretKeySpec(key, jceAlgorithm);
            mac.init(keyspec);
        } catch (Throwable t) {
            throw new SshException(t);
        }
	}


}
