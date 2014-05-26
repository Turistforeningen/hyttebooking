package utilities;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * http://www.bouncycastle.org/wiki/display/JA1/Frequently+Asked+Questions
 * YOU MUST INSTALL Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
 */
public class AESBouncyCastle {

	public final static int IV_BLOCK_SIZE = 16;
	private static final String HMAC_SHA512_ALGORITHM = "HmacSHA512";

	private byte[] keyBytes;
	private SecretKeySpec key;
	private Cipher cipher;
	private byte[] prevPText;
	private byte[] prevIv;

	public AESBouncyCastle(byte[] keyBytes) throws Exception  {
		if(keyBytes.length != 32) 
			throw new Exception("Incorrect key size : "+keyBytes.length);

		Security.addProvider(new BouncyCastleProvider());

		this.keyBytes = keyBytes;
		this.cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
		this.key = new SecretKeySpec(keyBytes, "AES");
		new IvParameterSpec(new byte[IV_BLOCK_SIZE]);
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
		byte[] iv = new byte[IV_BLOCK_SIZE];
		new Random().nextBytes(iv); //generates random iv

		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
		byte[] cipherText = cipher.doFinal(input);
		this.prevIv = iv;
		byte[] ivAndCipherText = ArrayUtils.addAll(iv, cipherText);


		return ivAndCipherText;
		/*
		System.out.println();
		System.out.println("######## encryption start ########");
		System.out.println("IV IS: "+DatatypeConverter.printBase64Binary(iv));
		System.out.println("plainText size:"+input.length+" : "+new String(input, "UTF-8"));
		System.out.println("cipherText size:"+cipherText.length+" : "+DatatypeConverter.printBase64Binary(cipherText));
		System.out.println("ivAndCipherText:"+ivAndCipherText.length+" : "+DatatypeConverter.printBase64Binary(ivAndCipherText));
		System.out.println("######## encryption end   ########");
		System.out.println();
		 */
	}

	/**
	 * @return the decrypt as byte array
	 */
	public byte[] decrypt(byte[] ivAndCipherText, byte[] hmacActual) throws Exception {
		if(ivAndCipherText == null || ivAndCipherText.length < 1) {
			System.err.println("Error: Data sent to be decrypted is null");
			return null;
		}

		byte[] iv = Arrays.copyOf(ivAndCipherText, IV_BLOCK_SIZE); //take first IV_BLOCK_SIZE bytes from ivAndCipherText
		byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, IV_BLOCK_SIZE, ivAndCipherText.length); //bytes from IV_BLOCK_SIZE+1 and onwards (i.e. everything after prepended iv)
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv)); 
		byte[] plainText = cipher.doFinal(cipherText);
		byte[] hmacOfData = DatatypeConverter.parseBase64Binary(sha512AndBase64(ArrayUtils.addAll(iv, plainText)));
		

		//security checks TODO timestamp check
		JsonNode login;
		if(Arrays.equals(hmacOfData, hmacActual)) { //our hash same as their hash
			login = Json.parse(new String(plainText, "UTF-8"));
			long timestamp = login.get("timestamp").asLong();
			if(utilities.DateHelper.isValidTimeStamp(timestamp))
				return plainText;
			System.err.println("Timestamp not valid: "+timestamp);
			return null;
		}
		else {
			System.out.println("Generated HMAC of decrypted data and actual HMAC not equal!");
			return null;
		}
		/*
		System.out.println();
		System.out.println("######## decryption start ########");
		System.out.println("Iv size: "+iv.length+" : "+DatatypeConverter.printBase64Binary(iv));
		System.out.println("ivAndCipherText size: "+ivAndCipherText.length+" : "+DatatypeConverter.printBase64Binary(ivAndCipherText));
		System.out.println("cipherText size: "+cipherText.length+" : "+DatatypeConverter.printBase64Binary(cipherText));
		System.out.println("plainText: "+new String(plainText, "UTF-8"));
		System.out.println("hmacActual: "+DatatypeConverter.printBase64Binary(hmacActual));
		System.out.println("hmacOfData: "+DatatypeConverter.printBase64Binary(hmacOfData));		
		System.out.println("######## encryption end   ########");
		System.out.println();
		*/
	}	

	/**
	 * Hashes (HMAC_SHA512) data and then base64 encodes it
	 */
	public String sha512AndBase64(byte[] data) {
		String result = null;
		try {
			SecretKeySpec secret_key = new SecretKeySpec(keyBytes, HMAC_SHA512_ALGORITHM);

			Mac mac = Mac.getInstance(HMAC_SHA512_ALGORITHM);
			mac.init(secret_key); 

			byte[] rawHmac = mac.doFinal(data);

			result = DatatypeConverter.printBase64Binary(rawHmac);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return result;
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