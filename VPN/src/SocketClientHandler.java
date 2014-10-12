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
            System.out
                    .println("CLIENT REQUESTED RESPONSE");

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    client.getOutputStream()));
            writer.write(userInput);
            writer.flush();
            writer.close();

            break;
		}
	}

}
