package encryption;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;


public class RSA_encrypt {

	public static void main(String[] args)  throws NoSuchAlgorithmException{

		KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
		kg.initialize(1024);
		KeyPair keys = kg.generateKeyPair();
		Key public_key = keys.getPublic();
		Key private_key = keys.getPrivate();
	
		byte[] ciphertext = encrypt("asdfdsgfsasdfsfaf", public_key);
		System.out.println("cipher is " + ciphertext);
		String plaintext = decrypt(ciphertext, private_key);
		System.out.println("plaintext was " + plaintext);
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
	

	  public static String decrypt(byte[] ciphertext, Key private_key){
		  
		  byte[] plaintext = null;
		  
		  try{
			  Cipher c = Cipher.getInstance("RSA");
			  c.init(Cipher.DECRYPT_MODE, private_key);
			  plaintext = c.doFinal(ciphertext);
		  }catch(Exception e){
		  
		  
		  }
		  
		return new String(plaintext); 
	  }
	  
	
}
