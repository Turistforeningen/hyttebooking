package utilities;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;
import sun.misc.BASE64Decoder;

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
	 */
	public void testEncrypt() {
		try {
			AESBouncyCastle aes = new AESBouncyCastle(DatatypeConverter.parseBase64Binary(play.Play.application().configuration().getString("application.secretKey")));

			String string = "Hello world!";
			byte[] encr = aes.encrypt(string.getBytes("UTF-8"));
			String decr = new String(aes.decrypt(encr), "UTF-8");
			assertTrue(encr + " doesn't equal "+ decr, string.equals(decr));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("EXCEPTION AT testENCRYPT! "+e, false); //i.e. if exception obviously something failed
		}
	}
	
	@Test
	public void testBase64Differ() {
		String code = play.Play.application().configuration().getString("application.secretKey");
		String string = "{\"timestamp\":1398764642}";
		
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] data = string.getBytes("UTF-8");
			AESBouncyCastle aesB64ONE = new AESBouncyCastle(decoder.decodeBuffer(code));
			AESBouncyCastle aesB64TWO = new AESBouncyCastle(DatatypeConverter.parseBase64Binary(code));
			
			byte[] oneEncr = aesB64ONE.encrypt(data); //one's encryption
			byte[] twoEncr = aesB64TWO.encrypt(data); //two's encryption
			
			String oneToOneDecr = new String(aesB64ONE.decrypt(oneEncr),"UTF-8"); //one decrypting one's encryption
			String oneToTwoDecr = new String(aesB64ONE.decrypt(twoEncr),"UTF-8"); //one decrypting two's encryption
			
			String twoToTwoDecr = new String(aesB64TWO.decrypt(twoEncr),"UTF-8"); //two decrypting two's encryption
			String twoToOneDecr = new String(aesB64TWO.decrypt(oneEncr),"UTF-8"); //two decrypting one's encryption
			
			assertEquals("B64ONE couldn't decrypt B64ONE's encryption", string, oneToOneDecr);
			assertEquals("B64ONE couldn't decrypt B64TWO's encryption", string, oneToTwoDecr);
			assertEquals("B64TWO couldn't decrypt B64TWO's encryption", string, twoToTwoDecr);
			assertEquals("B64TWO couldn't decrypt B64ONE's encryption", string, twoToOneDecr);
			
		} catch (Exception e) {
			assertTrue("Exception happened: "+e, false);
		}
	}
	
	@Test
	public void testDNTDiffer() {
		String code = play.Play.application().configuration().getString("application.secretKey");
		String hei = "hei";
		String expectedB64 = "EdZ8Ivcfug+V3lsCdB2oVym8QBofwbOpMYGuU8h7Mos=";
		String expectedHash = "MGViYTM5MzA2YTRkN2Y4YWMwOGJkYzA0Y2M4ZWJjOGQ1ZmU3NTliNmU4OWUyZTU3M2Y0ZTA5YjZmZjE0MWY1ZGJlYWU4NWE4OTgzMjhhYjlkNTY0OGVhYmI3M2FjNDA3NDI1NTljN2RkNzlhMGFjYjVhMmY3OGQ1Yjk5OTY4Njc="; //TODO find what that is
		
		try {
			AESBouncyCastle aes = new AESBouncyCastle(DatatypeConverter.parseBase64Binary(code));
			String encr = DatatypeConverter.printBase64Binary(aes.encrypt(hei.getBytes("UTF-8"))); 
			
			assertEquals(expectedB64, encr);
		} catch (Exception e) {
			assertTrue("Exception happened: "+e, false);
		}
	}
}
