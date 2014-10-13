package vpn;
import java.math.BigInteger;

public class VPN {

	public static void main(String[] args){
		PrimeGenerator primes = new PrimeGenerator();
		primes.generatePrimes();
		int primeOne = primes.getPrimeOne();
		int primeTwo = primes.getPrimeTwo();
		System.out.println("Prime One is "+primeOne);
		System.out.println("Prime Two is "+primeTwo);
		//int primeOne = 31;
		//int primeTwo = 37;
		int modulus = primeOne*primeTwo;
		System.out.println("modulus is "+modulus);
		int temp = (primeOne-1) * (primeTwo-1);
		int encryptionExponent = 1;
		int privateKey=1;
		
	
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
		
		String message = "This is a test";
		
		
		char messageChar;
		char cipherChar;
		char decryptedChar;
		for(int i=0; i<message.length(); i++){
			messageChar = message.charAt(i);
			BigInteger encrypt = new BigInteger("0");
			encrypt = BigInteger.valueOf(messageChar);
			encrypt = encrypt.pow(encryptionExponent);
			encrypt = encrypt.mod(BigInteger.valueOf(modulus));
			cipherChar = (char)encrypt.longValueExact();
			//System.out.print(cipherChar);
			
			BigInteger decrypt = new BigInteger("0");
			decrypt = BigInteger.valueOf(cipherChar);
			decrypt = decrypt.pow(privateKey);
			decrypt = decrypt.mod(BigInteger.valueOf(modulus));
			decryptedChar = (char)decrypt.longValueExact();
			System.out.print(decryptedChar);
		}
	}
	

}
