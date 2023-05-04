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
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;

import com.sshtools.common.ssh.components.SshDsaPublicKey;
import com.sshtools.common.ssh.components.SshPrivateKey;
import com.sshtools.common.util.SimpleASNReader;
import com.sshtools.common.util.Utils;

public abstract class Ssh2BaseDsaPrivateKey extends Ssh2BaseJCEPrivateKey implements SshPrivateKey {

    
    public Ssh2BaseDsaPrivateKey(PrivateKey prv) {
    	super(prv);
    }
    
    public Ssh2BaseDsaPrivateKey(PrivateKey prv, Provider customProvider) {
    	super(prv, customProvider);
    }

	public String getAlgorithm() {
		return "ssh-dss";
	}

	public byte[] sign(byte[] data) throws IOException {
		return sign(data, getAlgorithm());
	}
	
	
	@Override
	public byte[] sign(byte[] data, String signingAlgorithm) throws IOException {
		try {
			Signature l_sig = getJCESignature(JCEAlgorithms.JCE_SHA1WithDSA);
			l_sig.initSign(prv);
			l_sig.update(data);

            byte[] signature = l_sig.sign();
            
            SimpleASNReader asn = new SimpleASNReader(signature);
            asn.getByte();
            asn.getLength();
            asn.getByte();

            byte[] r = Utils.stripLeadingZeros(asn.getData());
            asn.getByte();

            byte[] s =  Utils.stripLeadingZeros(asn.getData());
            
            int numSize = (getPublicKey().getQ().bitLength() / 4) / 2;

            byte[] decoded = null;
            decoded = new byte[numSize*2];
            
        	if (r.length >= numSize) {
                System.arraycopy(r, r.length - numSize, decoded, 0, numSize);
             } else {
                System.arraycopy(r, 0, decoded, numSize - r.length, r.length);
             }

             if (s.length >= numSize) {
                 System.arraycopy(s, s.length - numSize, decoded, numSize, numSize);
             } else {
                 System.arraycopy(s, 0, decoded, numSize + (numSize - s.length), s.length);
             }

            return decoded;
		} catch (Exception e) {
			throw new IOException("Failed to sign data! " + e.getMessage());
		}

	}

	public abstract SshDsaPublicKey getPublicKey();

	@Override
	public int hashCode() {
		return prv.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		if(obj instanceof Ssh2BaseDsaPrivateKey) {
			Ssh2BaseDsaPrivateKey other = (Ssh2BaseDsaPrivateKey)obj;
			if(other.prv!=null) {
				return other.prv.equals(prv);
			}
		}
		return false;
	}
}
