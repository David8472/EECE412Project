package vpn;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class VPN {

	private static int encryptionExponent;
	private static int modulus;
	private static int privateKey;
	private static String message;
	private static char[] encryptedMessage;
	private static char[] decryptedMessage;
	
	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest md = MessageDigest.getInstance("MD5");
		
		//generate private and public key
		generateKeys();
	
		//Adversary's private key
		//privateKey = 25015;
		
		//Alice sends a message to Bob
		encryptedMessage = encrypt(message);

		//Alice produces a hash value of her message
				
		md.update(message.getBytes("UTF-8"));
		byte[] hashed_bytes = md.digest(message.getBytes());
		char[] hashed_msg = new char[hashed_bytes.length];
		for(int i = 0; i < hashed_bytes.length; i++){
			hashed_msg[i] = (char)(hashed_bytes[i] & 0xff);
		}
		
		//Alice "decrypts" this hashed message using her private key
		char[] signature = decrypt(hashed_msg);
		//Alice attaches it to her message
		char[] msgToSend = new char[encryptedMessage.length + signature.length];
		System.arraycopy(encryptedMessage, 0, msgToSend, 0, encryptedMessage.length);
		System.arraycopy(signature, 0, msgToSend, encryptedMessage.length, signature.length);

		//Bob receives it and "encrypts" the signature using Alice's public key
		char[] rcv_signature = new char[signature.length];
		System.arraycopy(msgToSend, encryptedMessage.length, rcv_signature, 0, signature.length);

		rcv_signature = encrypt(new String(rcv_signature));
		
		byte[] rcv_sig_in_bytes = new byte[rcv_signature.length];
		for(int i = 0; i < rcv_signature.length; i++){
			rcv_sig_in_bytes[i] = (byte)(0xff & rcv_signature[i]);
		}
		
		md.update(rcv_sig_in_bytes);
		byte[] hashToCompare = md.digest(rcv_sig_in_bytes);
		
		char[] rehashed_msg = new char[rcv_sig_in_bytes.length];
		for(int i = 0; i < rcv_sig_in_bytes.length; i++){
			rehashed_msg[i] = (char)(rcv_sig_in_bytes[i] & 0xff);
		}
		
		
		if(Arrays.equals(rcv_sig_in_bytes, hashed_bytes)){
			System.out.println("sender verified");
			decryptedMessage = decrypt(encryptedMessage);
			System.out.println(new String(decryptedMessage));
		}
		else{
			System.out.println("sender not verified");

		}
		
		
	}
	
	
	
	
	public static void generateKeys(){
		PrimeGenerator primes = new PrimeGenerator();
		primes.generatePrimes();
		int primeOne = primes.getPrimeOne();
		int primeTwo = primes.getPrimeTwo();
		System.out.println("Prime One is "+primeOne);
		System.out.println("Prime Two is "+primeTwo);
		modulus = primeOne*primeTwo;
		System.out.println("modulus is "+modulus);
		int temp = (primeOne-1) * (primeTwo-1);
		encryptionExponent = 1;
		privateKey=1;
		
		
		boolean found = false;
		while(!found){
			encryptionExponent++;
			if(BigInteger.valueOf(encryptionExponent).gcd(BigInteger.valueOf(temp)).intValue()==1){
				if(Math.random() < .3){
					found = true;
					System.out.println("Encryption Exponent is "+encryptionExponent);
					break;
				}
			}
			else if(encryptionExponent>Math.sqrt(primeOne) || encryptionExponent>Math.sqrt(primeTwo)){
				encryptionExponent = 1;
			}
		}
		//System.out.println("Encryption Exponent is "+encryptionExponent);
		found = false;
		
		while(!found){
			privateKey++;
			if(((encryptionExponent*privateKey)%temp)==1){
				found = true;
				break;
			}
		}
		System.out.println("Private Key is "+privateKey);
	}

	
	public static char[] encrypt(String message){
		char messageChar;
		char cipherChar;
		char[] encrypted = new char[message.length()];
		for(int i=0; i<message.length(); i++){
			messageChar = message.charAt(i);
			BigInteger encrypt = new BigInteger("0");
			encrypt = BigInteger.valueOf(messageChar);
			encrypt = encrypt.pow(encryptionExponent);
			encrypt = encrypt.mod(BigInteger.valueOf(modulus));
			cipherChar = (char)encrypt.longValue();
			encrypted[i] = cipherChar;
		}
	//	encryptedMessage = encrypted.clone();
		return encrypted;
	}
	
	public static char[] decrypt(char[] encryptedMsg){
		char[] decrypted = new char[encryptedMsg.length];
		char decryptedChar;
		for(int i = 0; i < encryptedMsg.length; i++){	
			BigInteger decrypt = new BigInteger("0");
			decrypt = BigInteger.valueOf(encryptedMsg[i]);
			decrypt = decrypt.pow(privateKey);
			decrypt = decrypt.mod(BigInteger.valueOf(modulus));
			decryptedChar = (char)decrypt.longValue();
//			System.out.print(decryptedChar);
			decrypted[i] = decryptedChar;
		}
		
	//	decryptedMessage = decrypted.clone();
		return decrypted;
	}




	public static String getMessage() {
		return message;
	}




	public static void setMessage(String message) {
		VPN.message = message;
	}




	public static char[] getEncryptedMessage() {
		return encryptedMessage;
	}




	public static void setEncryptedMessage(char[] encryptedMessage) {
		VPN.encryptedMessage = encryptedMessage;
	}




	public static char[] getDecryptedMessage() {
		return decryptedMessage;
	}




	public static void setDecryptedMessage(char[] decryptedMessage) {
		VPN.decryptedMessage = decryptedMessage;
	}
	
	
	
	
}
