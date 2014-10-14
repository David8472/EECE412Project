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
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				client.getInputStream(), "UTF-8"));
        while ((userInput = stdIn.readLine()) != null) {
        	char[] toDecrypt = new char[userInput.length()];
            if (!userInput.equals("") && userInput != null) {
            	System.out.println("Sending Message");
            	for(int i = 0; i < userInput.length(); i++){
            		toDecrypt[i] = userInput.charAt(i);
            	}
            	
            	String decrypted_msg = new String(vpn.decrypt(toDecrypt));
            	GUI.displayServerText(decrypted_msg);

                
  /*              BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        client.getOutputStream()));
                writer.write("client says: " + userInput + "\n");
                writer.flush();
     */
            }
         
        }

               
	}

}
