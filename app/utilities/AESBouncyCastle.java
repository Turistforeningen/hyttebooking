package utilities;
import javax.crypto.Cipher;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * Construct by sending the key as bytes as parameter
 * Note that key is usually encoded as base64, so just decode base64
 * using something javax.xml.bind.DatatypeConverter
 * 
 * Encryption: call encrypt(byte[] input) and it will return both encrypted input and 
 */
public class AESBouncyCastle {
	
	byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public final static int IV_BLOCK_SIZE = 16;
	SecretKeySpec key;
	Cipher cipher;
	
	/**
	 * TODO take care of exception
	 */
	public AESBouncyCastle(byte[] keyBytes) throws Exception  {
		Security.addProvider(new BouncyCastleProvider());

		this.key = new SecretKeySpec(keyBytes, "AES");
		System.out.println("##### KEY SIZE = "+keyBytes.length*8+" bit #####");
		this.cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
		if(iv.length != IV_BLOCK_SIZE)
			System.out.println("######## WARNING! iv AND IV_BLOCK_SIZE DIFFER! #########");
	}
	
	/**
	 * Gets iv, encrypts input array and appends the iv to the encrypted array
	 */
	public byte[] encrypt(byte[] input) throws Exception {
		
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
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
	public byte[] decrypt(int ctLength, byte[] ivAndCipherText) throws Exception {
		byte[] iv = new byte[IV_BLOCK_SIZE]; 
		byte[] cipherText = new byte[ivAndCipherText.length-iv.length];
		System.arraycopy(ivAndCipherText, 0, iv, 0, IV_BLOCK_SIZE); //iv always the starting 16 blocks
		System.arraycopy(ivAndCipherText, 16, cipherText, 0, ivAndCipherText.length-iv.length);
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		byte[] plainText = cipher.doFinal(cipherText);
		
		//System.out.println("plain : " + new String(plainText, "UTF-8")
			//	+ " bytes: " + ptLength);
		
		return plainText;
	}
}