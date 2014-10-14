package vpn;
import java.math.BigInteger;

public class VPN {

	public static int encryptionExponent;
	public static int modulus;
	public static int privateKey;
	public static char[] encryptedMsg;
	public static char[] decryptedMsg;
	
	public static void main(String[] args){
		
		
		generateKeys();
		String message = "sup";
		encryptedMsg = encrypt(message);
		System.out.println(new String(encryptedMsg));
		decryptedMsg = decrypt(encryptedMsg);
		System.out.println(new String(decryptedMsg));
		
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
		char[] encryptedMsg = new char[message.length()];
		for(int i=0; i<message.length(); i++){
			messageChar = message.charAt(i);
			BigInteger encrypt = new BigInteger("0");
			encrypt = BigInteger.valueOf(messageChar);
			encrypt = encrypt.pow(encryptionExponent);
			encrypt = encrypt.mod(BigInteger.valueOf(modulus));
			cipherChar = (char)encrypt.longValue();
			encryptedMsg[i] = cipherChar;
		}
		return encryptedMsg;
	}
	
	public static char[] decrypt(char[] encryptedMsg){
		char[] decryptedMsg = new char[encryptedMsg.length];
		char decryptedChar;

		for(int i = 0; i < encryptedMsg.length; i++){	
			BigInteger decrypt = new BigInteger("0");
			decrypt = BigInteger.valueOf(encryptedMsg[i]);
			decrypt = decrypt.pow(privateKey);
			decrypt = decrypt.mod(BigInteger.valueOf(modulus));
			decryptedChar = (char)decrypt.longValue();
			//System.out.print(decryptedChar);
			decryptedMsg[i] = decryptedChar;
		}
		
		
		return decryptedMsg;
	}
	
	
}
