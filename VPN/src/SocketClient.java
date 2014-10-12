import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {

	private String hostname;
	private int port;
	Socket socketClient;

	public SocketClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public void connect() throws UnknownHostException, IOException {
		GUI.displayClientText("Attempting to connect to " + hostname + ":"
				+ port);
		socketClient = new Socket(hostname, port);
		GUI.displayClientText("Connection Established");
	}

	public void readResponse() throws IOException {
		String userInput;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				socketClient.getInputStream()));

		System.out.println("Response from server:");
		while ((userInput = stdIn.readLine()) != null) {
			System.out.println(userInput);
		}
	}

    public void askForTime() throws IOException{
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
        System.out.println("What time is it?");
        writer.write("TIME?");
        writer.newLine();
        writer.flush();
    }

	public static void main(String arg[]) {
		// Creating a SocketClient object
		SocketClient client = new SocketClient("localhost", 9990);
		try {
			// trying to establish connection to the server
			client.connect();
            client.askForTime();

			// if successful, read response from server
			client.readResponse();
		} catch (UnknownHostException e) {
			GUI.displayClientText("Host unknown. Cannot establish connection");
		} catch (IOException e) {
			System.err
					.println("Cannot establish connection. Server may not be up."
							+ e.getMessage());
		}
	}
}