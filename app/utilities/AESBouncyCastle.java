package utilities;

import javax.crypto.Cipher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * http://www.bouncycastle.org/wiki/display/JA1/Frequently+Asked+Questions
 * YOU MUST INSTALL Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
 */
public class AESBouncyCastle {
	
	public final static int IV_BLOCK_SIZE = 16;

	private SecretKeySpec key;
	private Cipher cipher;
	private IvParameterSpec iv;
	private byte[] prevPText;
	private byte[] prevIv;
	//TODO remove
	private byte[] testIv = DatatypeConverter.parseBase64Binary("EdZ8Ivcfug+V3lsCdB2oVw==");
	
	public AESBouncyCastle(byte[] keyBytes) throws Exception  {
		if(keyBytes.length != 32) 
			throw new Exception("Incorrect key size : "+keyBytes.length);
		
		Security.addProvider(new BouncyCastleProvider());
		
		
		this.cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		this.key = new SecretKeySpec(keyBytes, "AES");
		this.iv = new IvParameterSpec(new byte[IV_BLOCK_SIZE]);
	}
	
	/**
	 * Encrypts input and prepends iv
	 * @return iv + cipherText byte array
	 */
	public byte[] encrypt(byte[] input) throws Exception {
		if(input == null || input.length < 1) {
			System.err.println("Error: Data sent to be encrypted is null");
			return null;
		}
		this.prevPText = input;
		
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(testIv));
		byte[] cipherText = cipher.doFinal(input);
		byte[] iv = cipher.getIV();
		this.prevIv = iv;
		byte[] ivAndCipherText = ArrayUtils.addAll(iv, cipherText);
		
		System.out.println();
		System.out.println("######## encryption start ########");
		System.out.println("IV IS: "+DatatypeConverter.printBase64Binary(iv));
		System.out.println("plainText size:"+input.length+" : "+new String(input, "UTF-8"));
		System.out.println("cipherText size:"+cipherText.length+" : "+DatatypeConverter.printBase64Binary(cipherText));
		System.out.println("ivAndCipherText:"+ivAndCipherText.length+" : "+DatatypeConverter.printBase64Binary(ivAndCipherText));
		System.out.println("######## encryption end   ########");
		System.out.println();
		
		return ivAndCipherText;
	}
	
	/**
	 * @return the decrypt as byte array
	 */
	public byte[] decrypt(byte[] ivAndCipherText) throws Exception {
		if(ivAndCipherText == null || ivAndCipherText.length < 1) {
			System.err.println("Error: Data sent to be decrypted is null");
			return null;
		}
	
		byte[] iv = Arrays.copyOf(ivAndCipherText, IV_BLOCK_SIZE); //take first IV_BLOCK_SIZE bytes from ivAndCipherText
		byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, IV_BLOCK_SIZE, ivAndCipherText.length); //bytes fsrom IV_BLOCK_SIZE+1 and onwards (i.e. everything after prepended iv)
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv)); 
		byte[] plainText = cipher.doFinal(cipherText); 
		
		System.out.println();
		System.out.println("######## decryption start ########");
		System.out.println("Iv size: "+iv.length);
		System.out.println("cipherText size: "+cipherText.length);
		System.out.println("ivAndCipherText size: "+ivAndCipherText.length);
		System.out.println("ivAndCipherText: "+DatatypeConverter.printBase64Binary(ivAndCipherText));
		System.out.println("cipherText: "+DatatypeConverter.printBase64Binary(cipherText));
		System.out.println("plainText: "+new String(plainText, "UTF-8"));
		System.out.println("######## encryption end   ########");
		System.out.println();
		
		return plainText;
	}	
	
	public String sha512AndBase64(byte[] data) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
			byte[] hash = md.digest(data);
			System.out.println("hash: "+DatatypeConverter.printBase64Binary(hash));
			return DatatypeConverter.printBase64Binary(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] getIvAndPlainText() {
		if(prevIv == null || prevPText == null)
			return null;
		return ArrayUtils.addAll(prevIv, prevPText);
	}
}