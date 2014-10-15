package encryption;
import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;


public class RSA_encrypt {

	public static void main(String[] args)  throws NoSuchAlgorithmException{

		KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
		kg.initialize(1024);
		KeyPair keys = kg.generateKeyPair();
		Key public_key = keys.getPublic();
		Key private_key = keys.getPrivate();
	
		byte[] ciphertext = encrypt("hello and goodbye", public_key);
		System.out.println("cipher is " + ciphertext);
	
	}
	
	  public static byte[] encrypt(String plaintext, Key public_key) throws NoSuchAlgorithmException{
		    byte[] cipherText = null;
		   
		    try{
			    Cipher c = Cipher.getInstance("RSA");
			    c.init(Cipher.ENCRYPT_MODE, public_key);
			    cipherText = c.doFinal(plaintext.getBytes());
		    }catch(Exception e){
		    	
		    }	    
		    return cipherText;
		  }
	

	
}
