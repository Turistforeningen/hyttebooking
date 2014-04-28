package utilities;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import javax.xml.bind.DatatypeConverter;

import models.Booking;

import org.junit.Before;
import org.junit.Test;

import play.test.WithApplication;

public class AESBouncyCastleTest extends WithApplication {

	//no need for fakeApplication
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
		} catch (Exception e){
			eFlag = true;
		}
		assertTrue(eFlag);

		eFlag = true;
		try {
			AESBouncyCastle aesNoException = new AESBouncyCastle(DatatypeConverter.parseBase64Binary(play.Play.application().configuration().getString("application.secretKey")));
		} catch (Exception e) {
			System.out.println(e.toString());
			eFlag = false;
		}
		assertTrue(eFlag);	
	}
	
	
	@Test
	/**
	 * 
	 */
	public void testEncrypt() {
		try {
			byte[] keyBytes = DatatypeConverter.parseBase64Binary(play.Play.application().configuration().getString("application.secretKey"));
			assertEquals("keyBytes isn't 32 bytes long", 32, keyBytes.length);
			AESBouncyCastle aes = new AESBouncyCastle(DatatypeConverter.parseBase64Binary(play.Play.application().configuration().getString("application.secretKey")));
			assertNotNull("AES is null after constructor", aes);
			
			String string = "Hello world!";
			byte[] encr = aes.encrypt(string.getBytes("UTF-8"));
			String decr = new String(aes.decrypt(encr), "UTF-8");
			assertTrue(encr + " doesn't equal "+ decr, string.equals(decr));
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("EXCEPTION AT testENCRYPT! "+e, true, false); //i.e. if exception obviously something failed
		}
	}
}
