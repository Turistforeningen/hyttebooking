package utilities;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import java.security.InvalidKeyException;
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

	private byte[] keyBytes;
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

		this.keyBytes = keyBytes;
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
		byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, IV_BLOCK_SIZE, ivAndCipherText.length); //bytes from IV_BLOCK_SIZE+1 and onwards (i.e. everything after prepended iv)
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

	/**
	 * Hashes data and then base64 encodes it
	 */
	public String sha512AndBase64(byte[] data) {
		try {
			/*
			md = MessageDigest.getInstance("SHA-512");
			//md.update(keyBytes); maybe should be salted with key?
			byte[] hash = md.digest(data);
			Mac mac = Mac.getInstance("HmacSHA512");
			mac.init(key);
			hmacData = mac.doFinal(data);
			//System.out.println("hash: "+DatatypeConverter.printBase64Binary(hash));
			return DatatypeConverter.printBase64Binary(hmacData);
			 */

			Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
			SecretKeySpec secret_key = new SecretKeySpec(keyBytes, "HmacSHA512");
			sha512_HMAC.init(secret_key);

			String hash = DatatypeConverter.printBase64Binary((sha512_HMAC.doFinal(data)));
			return hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the iv and plaintext used for last encryption
	 */
	public byte[] getIvAndPlainText() {
		if(prevIv == null || prevPText == null)
			return null;
		return ArrayUtils.addAll(prevIv, prevPText);
	}
}