package vpn;
import java.math.BigInteger;
import java.util.Random;

public class PrimeGenerator {
	private int primeOne;
	private int primeTwo;
	Random random = new Random();
	boolean done = false;
	void generatePrimes(){
		
		while(!done){
			primeOne = random.nextInt(200)+100;
			if(primeCheck(primeOne)){
				done = true;
				break;
			}
		}
		done = false;
		
		while(!done){
			primeTwo = random.nextInt(200)+100;
			if(primeCheck(primeTwo)&&(primeTwo!=primeOne)){
				done = true;
				break;
			}
		}
	}
	
	int getPrimeOne(){
		return primeOne;
	}	
	
	int getPrimeTwo(){
		return primeTwo;
	}
	
	private boolean primeCheck(int prime){
		boolean check=false;
		
		if(prime%2 == 0){
			check = false;
		}
		else{
			for(int i = 3; i < Math.sqrt(prime); i+=2){
				if(prime%i == 0){
					check = false;
					break;
				}
				else{
					check = true;
				}
			}
		}
		return check;
	}
}