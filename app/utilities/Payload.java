package utilities;
/**
 * Encryption; you probably want to keep track of length
 * So return this object instead of just cipherText you return
 * both cipherText and int ctLength
 * @author Jama
 */
public class Payload {
	private int ctLength;
	private byte[] cipherText;
	
	/**
	 * @param ctLength Length of the cleartext
	 * @param cipherText the byte[] of the cipherText
	 */
	public Payload(int ctLength, byte[] cipherText) {
		this.ctLength = ctLength;
		this.cipherText = cipherText;
	}

	public byte[] getCipherText() {
		return cipherText;
	}
	
	public int getCtLength() {
		return ctLength;
	}
}
