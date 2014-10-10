package encryption;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;


public class RSA_encrypt {

	public static void main(String[] args)  throws NoSuchAlgorithmException{

		KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
		kg.initialize(1024);
		KeyPair keys = kg.generateKeyPair();
		Key public_key = keys.getPublic();
		Key private_key = keys.getPrivate();
	
		
	}
	
}
