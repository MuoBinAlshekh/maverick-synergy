
package com.sshtools.common.ssh.components.jce;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.sshtools.common.ssh.SecurityLevel;

public class ArcFour128 extends AbstractJCECipher {
	
	public ArcFour128() throws IOException {
		super("ARCFOUR", "ARCFOUR", 16, "arcfour128", SecurityLevel.WEAK, 0);	
	}

	public void init(int mode, byte[] iv, byte[] keydata) throws IOException {
	      try {

	          cipher = JCEProvider.getProviderForAlgorithm(spec)==null ?
	              	Cipher.getInstance(spec)
	              	: Cipher.getInstance(spec, JCEProvider.getProviderForAlgorithm(spec));

	          if(cipher==null) {
	              throw new IOException("Failed to create cipher engine for "
	                                    + spec);
	          }

	          // Create a byte key
	          byte[] actualKey = new byte[keylength];
	          System.arraycopy(keydata, 0, actualKey, 0, actualKey.length);

	          SecretKeySpec kspec = new SecretKeySpec(actualKey, keyspec);

	          // Create the cipher according to its algorithm
	          cipher.init(((mode == ENCRYPT_MODE) ? Cipher.ENCRYPT_MODE
	                       : Cipher.DECRYPT_MODE),
	                      kspec);
	          
	          byte[] tmp = new byte[1536];
	          cipher.update(tmp);
	          
	      } catch (NoSuchPaddingException nspe) {
	          throw new IOException("Padding type not supported");
	      } catch (NoSuchAlgorithmException nsae) {
	          throw new IOException("Algorithm not supported:"+spec);
	      } catch (InvalidKeyException ike) {
	          throw new IOException("Invalid encryption key");
	      } 
	}

	public int getBlockSize() {
		return 8;
	}
}
