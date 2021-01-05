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
package com.sshtools.client.components;

import com.sshtools.common.ssh.SecurityLevel;
import com.sshtools.common.ssh.components.jce.JCEAlgorithms;

/**
 * Implementation of the required SSH Transport Protocol key exchange method
 * "diffie-hellman-group-exchange-sha1".
 */
public class DiffieHellmanGroupExchangeSha256JCE extends DiffieHellmanGroupExchange {


	/**
	 * Constant for the algorithm name "diffie-hellman-exchange-sha256".
	 */
	public static final String DIFFIE_HELLMAN_GROUP_EXCHANGE_SHA256 = "diffie-hellman-group-exchange-sha256";

	/**
	 * Construct an uninitialized instance.
	 */
	public DiffieHellmanGroupExchangeSha256JCE() {
		super(DIFFIE_HELLMAN_GROUP_EXCHANGE_SHA256, JCEAlgorithms.JCE_SHA256, SecurityLevel.STRONG, 2003);
	}

}
