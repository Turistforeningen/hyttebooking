package utilities;

import javax.crypto.Cipher;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESBouncyCastle {
	
	/*
	public final static int IV_BLOCK_SIZE = 16;
	byte[] iv = new byte[IV_BLOCK_SIZE];
	*/
	SecretKeySpec key;
	Cipher cipher;
	
	public AESBouncyCastle(byte[] keyBytes) throws Exception  {
		if(keyBytes.length != 32) 
			throw new Exception("Incorrect key size : "+keyBytes.length);
		
		Security.addProvider(new BouncyCastleProvider());

		this.cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
		this.key = new SecretKeySpec(keyBytes, "AES");
	}
	
	/**
	 * Gets iv, encrypts input array and appends the iv to the encrypted array
	 */
	public byte[] encrypt(byte[] input) throws Exception {
		if(input == null) {
			System.err.println("Error: Data sent to be encrypted is null");
			return null;
		}
		
		cipher.init(Cipher.ENCRYPT_MODE, key);
		//byte[] iv = cipher.getIV();
		byte[] cipherText = cipher.doFinal(input);
		
		/*
		if(iv == null){
			System.err.println("Error: Iv is null after cipher.getIV()");
			return null;
		}
		*/
		//byte[] cipherText = new byte[iv.length+data.length];
		
		//System.arraycopy(iv, 0, cipherText, 0, iv.length);
		//System.arraycopy(data, 0, cipherText, iv.length, data.length);
		
		//System.out.println("cipher: " + DatatypeConverter.printBase64Binary(cipherText)
			//	+ " bytes: " + ctLength);
		
		return cipherText;
	}
	
	/**
	 * @return the decrypted bytes
	 * Note, does not decode base64, just decrypts
	 */
	public byte[] decrypt(byte[] cipherText) throws Exception {
		//byte[] iv = new byte[IV_BLOCK_SIZE]; 
		//byte[] cipherText = new byte[ivAndCipherText.length-iv.length];
		//System.arraycopy(ivAndCipherText, 0, iv, 0, IV_BLOCK_SIZE); //iv always the starting 16 blocks
		//System.arraycopy(ivAndCipherText, 16, cipherText, 0, ivAndCipherText.length-iv.length);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] plainText = cipher.doFinal(cipherText);
		
		//System.out.println("plain : " + new String(plainText, "UTF-8")
			//	+ " bytes: " + ptLength);
		
		return plainText;
	}
}