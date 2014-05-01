package utilities;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import javax.xml.bind.DatatypeConverter;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.test.WithApplication;
import sun.misc.BASE64Decoder;
import utilities.AESBouncyCastle;

public class AESBouncyCastleTest extends WithApplication {

	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}

	@Test
	/**
	 * Test that constructor won't accept keys of length != 32 bytes
	 * Test that constructor will accept keys of length 32
	 */
	public void testAESBouncyCastle() {
		boolean eFlag = false;
		try{
			AESBouncyCastle aesWithException = new AESBouncyCastle("0123456789123456789".getBytes());
			aesWithException.encrypt("test".getBytes("UTF-8"));
		} catch (Exception e){
			eFlag = true;
		}
		assertTrue("key is wrong size, but doesn't cause exception. Install unlimited strength policy files", eFlag);

		eFlag = true;
		try {
			AESBouncyCastle aesNoException = new AESBouncyCastle(DatatypeConverter.parseBase64Binary(play.Play.application().configuration().getString("application.secretKey")));
			aesNoException.encrypt("test".getBytes("UTF-8"));
		} catch (Exception e) {
			eFlag = false;
		}
		assertTrue("key is right size but causes exception. Install unlimited strength policy files", eFlag);	
	}

	@Test
	/**
	 * Test that encrypted text is equal to decrypted text
	 * Test that false hmac returns null for decryption
	 */
	public void testEncrypt() {
		byte[] secretKey =DatatypeConverter.parseBase64Binary(play.Play.application().configuration().getString("application.secretKey"));
		try {
			AESBouncyCastle aes = new AESBouncyCastle(secretKey);
			//generate another iv
			AESBouncyCastle aes2 = new AESBouncyCastle(secretKey);
			
			String hello = "Hello world!";
			byte[] msg = hello.getBytes("UTF-8");
			
			byte[] encr = aes.encrypt(msg);
			aes2.encrypt(msg); //just to generate a bad iv, we initiate aes2.getIv() by encrypting something
			byte[] hmac = DatatypeConverter.parseBase64Binary(aes.sha512AndBase64(aes.getIvAndPlainText()));
			byte[] badHmac = DatatypeConverter.parseBase64Binary(aes.sha512AndBase64(aes2.getIvAndPlainText()));
			String decr = new String(aes.decrypt(encr, hmac), "UTF-8");
			
			assertTrue(hello + " doesn't equal "+ decr, hello.equals(decr));
			assertNull(aes.decrypt(encr, badHmac)); 
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("EXCEPTION AT testENCRYPT! "+e, false); //i.e. if exception obviously something failed
		}
	}

	@Test
	/**
	 * Kinda don't need this anymore
	 */
	public void testBase64Differ() {
		String code = play.Play.application().configuration().getString("application.secretKey");
		String string = "{\"timestamp\":1398764642}";

		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] data = string.getBytes("UTF-8");
			AESBouncyCastle aesB64ONE = new AESBouncyCastle(decoder.decodeBuffer(code));
			AESBouncyCastle aesB64TWO = new AESBouncyCastle(DatatypeConverter.parseBase64Binary(code));

			byte[] oneEncr = aesB64ONE.encrypt(data); //one's encryption
			byte[] oneHmac = DatatypeConverter.parseBase64Binary(aesB64ONE.sha512AndBase64(aesB64ONE.getIvAndPlainText()));
			byte[] twoEncr = aesB64TWO.encrypt(data); //two's encryption
			byte[] twoHmac = DatatypeConverter.parseBase64Binary(aesB64TWO.sha512AndBase64(aesB64TWO.getIvAndPlainText()));

			String oneToOneDecr = new String(aesB64ONE.decrypt(oneEncr, oneHmac),"UTF-8"); //one decrypting one's encryption
			String oneToTwoDecr = new String(aesB64ONE.decrypt(twoEncr, twoHmac),"UTF-8"); //one decrypting two's encryption

			String twoToTwoDecr = new String(aesB64TWO.decrypt(twoEncr, twoHmac),"UTF-8"); //two decrypting two's encryption
			String twoToOneDecr = new String(aesB64TWO.decrypt(oneEncr, oneHmac),"UTF-8"); //two decrypting one's encryption

			//following tests don't work because of timestamp check, remove timestamp check before running
			assertEquals("B64ONE couldn't decrypt B64ONE's encryption", string, oneToOneDecr);
			assertEquals("B64ONE couldn't decrypt B64TWO's encryption", string, oneToTwoDecr);
			assertEquals("B64TWO couldn't decrypt B64TWO's encryption", string, twoToTwoDecr);
			assertEquals("B64TWO couldn't decrypt B64ONE's encryption", string, twoToOneDecr);

		} catch (Exception e) {
			assertTrue("Exception happened: "+e, false);
		}
	}

	@Test
	public void sha512AndBase64Test() {
		try {
			String base64Code = play.Play.application().configuration().getString("application.secretKey");
			byte[] code = DatatypeConverter.parseBase64Binary(base64Code);

			String hei = "hei";
			String expectedHiHash = "0rR22Cz0FoBx+qjLiJqHCEy6dVP4sUMfUpXnN+eYOT6Ocam+Wo+To3rSt+HZVisA5fCCzug1X4KNBkrv0wwItQ==";
			//String expectedHiHashWithHex = "ZDI4YWNmZDBiZGNkNjFlMjEzMDk1OTM2NjkzZWU4MmMxMDIwYWY4ODJjODMxZjJmOThlMTIwMDhkMjA5Y2E5MmI2Nzk0YTA4YjVlMjI0NTYxYTA4MTk1ODNhZTBkMWEyZTNkZTA2NzI1MmYzNGZkNmRiY2M2ZDNiODFkNTg2ZWQ=";

			AESBouncyCastle aes = new AESBouncyCastle(code);
			String actualHiHash = aes.sha512AndBase64(hei.getBytes("UTF-8"));
			System.out.println("### expected hi hash: "+expectedHiHash);
			System.out.println("### actual hi hash: "+actualHiHash);
			assertEquals("Hi hash equalsTest", expectedHiHash, actualHiHash);
		} catch (Exception e) {
			assertTrue("Exception happened: "+e, false);
		}
	}
	
	@Test
	/**
	 * Test that example data from DNT is decrypted
	 * Test that JSONifying works as expected
	 */
	public void testDecrypt() {
		String code = play.Play.application().configuration().getString("application.secretKey");
		try {
			String dataB64 = DecodeURL("dDHcmNogkWDGu2Op%2FTAoreSoBYgO7eb6PIDtiX%2FvNsiSjBL4GczBN9dFLCxnYu%2FeQuBHLROV01kLbvk9I6bMSOE59nYuWt%2FOsYYaoKckXhpzIYOA8VSlfYqIlRyaLAEGpLnDaQbf3qQiGi%2BhREyJQzOLy%2BSV5bJCB5Oi3eHorHqO7GA6Pjt%2BSgZ1XDX7cSNVSnY%2BSn58Rnnz2XHCHPnYsEy4NhVXzlmqgAk4WGmNGctmcwVvf7Y%2FeEcCCZAoVIb2Te%2BL4vPYmwjhx1N%2B%2FiKn5bzuPEVZWCvJj76KF41ClnugeSFzDCsjbyU65INwLmvd");
			String hmacB64 = DecodeURL("YWYxMzVjZWI3NWU5NjA4YzIwNTYwY2E3NTE3OWE3OTg2MDAzMmFiZmQ2YTQwOGQ0OTdmZTgwZWE3M2NkYWI2MGNiOGEyOWIwNmJlYjg4N2RmOGUxM2FhOGViNDM0NDIyYjYwMmViNGUxM2NlYmRhYjUzMTk2M2RkNjA3N2IzNWY%3D");
			
			byte[] data = DatatypeConverter.parseBase64Binary(dataB64);
			byte[] hmac = DatatypeConverter.parseBase64Binary(hmacB64);
			
			AESBouncyCastle aes = new AESBouncyCastle(DatatypeConverter.parseBase64Binary(code));
			//test that decrypt works
			String jsonString = new String(aes.decrypt(data, hmac), "UTF-8");
			JsonNode login = Json.parse(jsonString);
			
			assertEquals(login.get("er_autentisert").asBoolean(), true);
			assertTrue(login.get("etternavn").asText().equals("Noor"));
			assertTrue(login.get("sherpa_id").asText().equals("34827"));
			assertTrue(login.get("fornavn").asText().equals("Jamawadi"));
			assertFalse(login.get("epost").asText().equals("lol@haha.no"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String DecodeURL(String url) throws java.io.UnsupportedEncodingException {
		return java.net.URLDecoder.decode(url, "UTF-8");
	}
}
