/**
 * (c) 2002-2021 JADAPTIVE Limited. All Rights Reserved.
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


public class Ssh2EcdsaSha2Nist521PublicKey extends Ssh2EcdsaSha2NistPublicKey {

	public Ssh2EcdsaSha2Nist521PublicKey() {
		super("ecdsa-sha2-nistp521", JCEAlgorithms.JCE_SHA512WithECDSA, "secp521r1", "nistp521");
	}
	
	public byte[] getOid() {
		return new byte[] { 0x2B, (byte) 0x81, 0x04, 0x00, 0x23};
	}
}
