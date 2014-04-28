package utilities;
import javax.crypto.Cipher;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

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
	
	SecretKeySpec key;
	Cipher cipher;
	
	/**
	 * TODO take care of exception
	 */
	public AESBouncyCastle(byte[] keyBytes) throws Exception  {
		Security.addProvider(new BouncyCastleProvider());

		this.key = new SecretKeySpec(keyBytes, "AES");
		this.cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
	}
	
	/**
	 * @return the length of the plaintext and 
	 * Note, does not encode to base64, just encrypts
	 */
	public Payload encrypt(byte[] input) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] iv = cipher.getIV();
		byte[] cipherText = new byte[cipher.getOutputSize(input.length)+iv.length];
		System.arraycopy(iv, 0, cipherText, 0, iv.length);
		int ctLength 	= cipher.update(input, 0, input.length, cipherText, 0);
		ctLength += cipher.doFinal(cipherText, ctLength);
		
		//System.out.println("cipher: " + DatatypeConverter.printBase64Binary(cipherText)
			//	+ " bytes: " + ctLength);
		
		return new Payload(ctLength, cipherText);
	}
	
	/**
	 * @return the decrypted bytes
	 * Note, does not decode base64, just decrypts
	 */
	public byte[] decrypt(int ctLength, byte[] ivAndCipherText) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key);
		cipher.init(Cipher.DECRYPT_MODE, key, ); //get IV ?TODO
		byte[] iv = new byte[cipher.getIV().length];
		byte[] cipherText = new byte[ivAndCipherText.length-cipher.getIV().length];
		byte[] plainText = new byte[cipherText.length];
		System.arraycopy(cipherText, cipher.getIV().length, plainText, 0, plainText.length);
		int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
		ptLength += cipher.doFinal(plainText, ptLength);
		
		//System.out.println("plain : " + new String(plainText, "UTF-8")
			//	+ " bytes: " + ptLength);
		
		return plainText;
	}
}