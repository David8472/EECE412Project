package encryption;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;


public class RSA_encrypt {

	public static void main(String[] args)  throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException{

		MessageDigest md = MessageDigest.getInstance("MD5");
		Signature sig = Signature.getInstance("MD5WithRSA");
		
		KeyPairGenerator kg1 = KeyPairGenerator.getInstance("RSA");
		kg1.initialize(1024);
		KeyPair keys1 = kg1.generateKeyPair();
		Key public_key1 = keys1.getPublic();
		Key private_key1 = keys1.getPrivate();
		String sender_msg = "hello receiver";
		
		KeyPairGenerator kg2 = KeyPairGenerator.getInstance("RSA");
		kg2.initialize(1024);
		KeyPair keys2 = kg2.generateKeyPair();
		Key public_key2 = keys2.getPublic();
		Key private_key2 = keys2.getPrivate();
		
	/*	TEST CODE: tests the encryption and decryption
		byte[] ciphertext = encrypt("asdfdsgfsasdfsfaf", public_key);
		System.out.println("cipher is " + ciphertext);
		String plaintext = decrypt(ciphertext, private_key);
		System.out.println("plaintext was " + plaintext);
	*/

		byte[] signature = sign_signature(sig, sender_msg, (PrivateKey)private_key1);
		
		boolean verified = verify_signature(sig, sender_msg, (PublicKey)public_key1, signature);
		
		
		if(verified){
			System.out.println("Sender verified \n");
		}
		else{
			System.out.println("Sender not verified\n");
		}
	
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
	

	  public static byte[] decrypt(byte[] ciphertext, Key private_key){
		  
		  byte[] plaintext = null;
		  
		  try{
			  Cipher c = Cipher.getInstance("RSA");
			  c.init(Cipher.DECRYPT_MODE, private_key);
			  plaintext = c.doFinal(ciphertext);
		  }catch(BadPaddingException ns){
			  System.out.println("padding exception caught in decrypt");
		  
		  }catch(Exception e){
			  
		  }
		  
		return plaintext; 
	  }
	  
	  
	  public static byte[] sign_signature(Signature signature, String message, PrivateKey key) throws InvalidKeyException, SignatureException{
		  signature.initSign(key);
		  signature.update(message.getBytes());
		  return signature.sign();
		  
	  }
	  
	  public static boolean verify_signature(Signature signature, String message, PublicKey key, byte[] signature_bytes) throws InvalidKeyException, SignatureException{
			signature.initVerify(key);
			signature.update(message.getBytes());
			return signature.verify(signature_bytes);
	  }
	  
	
}
