import java.io.*;
import java.net.Socket;

public class SocketClientHandler implements Runnable {

	private Socket client;

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
				client.getInputStream()));
        while ((userInput = stdIn.readLine()) != null) {
            if (!userInput.equals("") && userInput != null) {
                GUI.displayServerText(userInput);
                System.out.println(userInput);

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        client.getOutputStream()));
                writer.write("server says: " + userInput + "\n");
                writer.flush();
            }
        }
	}

}
