import java.io.*;
import java.net.Socket;
import vpn.VPN;

public class SocketClientHandler implements Runnable {

	private Socket client;
	private static VPN vpn;
	public SocketClientHandler(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
        System.out.println("Got a client !");

		try {
			readResponse();
//            client.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void readResponse() throws IOException, InterruptedException {
		String userInput;
		boolean verified;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				client.getInputStream(), "UTF-8"));
        while ((userInput = stdIn.readLine()) != null) {
        	char[] sentMsg = new char[userInput.length()];
        	char[] encryptedMsg = new char[userInput.length()-16];
            if (!userInput.equals("") && userInput != null) {
            	System.out.println("Sending Message");
            	for(int i = 0; i < userInput.length(); i++){
            		sentMsg[i] = userInput.charAt(i);
            		if(i < userInput.length() - 16){
            			encryptedMsg[i] = userInput.charAt(i);
            		}
            	}
                        	
            	verified = vpn.verify_signature(sentMsg, encryptedMsg, 16);
            	if(verified){
	            	String decrypted_msg = new String(vpn.decrypt(encryptedMsg));
	            	GUI.displayServerText(decrypted_msg);
            	}
            	else{
            		GUI.displayServerText("Sender Not Verified - Ignoring Message");
            	}
            	

                
  /*              BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        client.getOutputStream()));
                writer.write("client says: " + userInput + "\n");
                writer.flush();
     */
            }
         
        }

               
	}

}
