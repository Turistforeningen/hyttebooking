package utilities;

import javax.crypto.Cipher;

import java.security.Security;
import java.util.Random;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * http://www.bouncycastle.org/wiki/display/JA1/Frequently+Asked+Questions
 * YOU MUST INSTALL Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
 */
public class AESBouncyCastle {
	
	public final static int IV_BLOCK_SIZE = 16;
	byte[] iv = new byte[IV_BLOCK_SIZE]; //default init is zero 
	
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
		new Random().nextBytes(iv); //randomize iv
		
		if(input == null) {
			System.err.println("Error: Data sent to be encrypted is null");
			return null;
		}
		
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] data = cipher.doFinal(input);
		byte[] cipherText = new byte[iv.length+data.length];
		System.arraycopy(iv, 0, cipherText, 0, iv.length);
		System.arraycopy(data, 0, cipherText, iv.length, data.length);
		
		//System.out.println("cipher: " + DatatypeConverter.printBase64Binary(cipherText)
			//	+ " bytes: " + ctLength);
		
		return cipherText;
	}
	
	/**
	 * @return the decrypted bytes
	 * Note, does not decode base64, just decrypts
	 */
	public byte[] decrypt(byte[] ivAndCipherText) throws Exception {
		if(ivAndCipherText == null) {
			System.err.println("Error: Data sent to be decrypted is null");
			return null;
		}
		
		byte[] cipherText = new byte[ivAndCipherText.length-iv.length];
		System.arraycopy(ivAndCipherText, 0, iv, 0, iv.length); 
		System.arraycopy(ivAndCipherText, iv.length, cipherText, 0, cipherText.length);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] plainText = cipher.doFinal(cipherText);
		
		//System.out.println("plain : " + new String(plainText, "UTF-8")
			//	+ " bytes: " + ptLength);
		
		return plainText;
	}
	
	
}