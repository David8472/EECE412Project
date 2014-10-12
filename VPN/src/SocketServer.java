import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {

	private ServerSocket serverSocket;

	private int port;

	public SocketServer(int port) {
		this.port = port;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void start() throws IOException {
		serverSocket = new ServerSocket(port);

		final ExecutorService clientProcessingPool = Executors
				.newFixedThreadPool(10);

		Runnable serverTask = new Runnable() {
			@Override
			public void run() {

				System.out.println("Waiting for clients...");
                Socket client = null;

				try {
					while (true) {
						Socket clientSocket = serverSocket.accept();
						GUI.getProgressBar().setIndeterminate(false);
						GUI.getProgressBar().setValue(100);
						Toolkit.getDefaultToolkit().beep();
						clientProcessingPool
								.submit(new ClientTask(clientSocket));
						System.out
								.println("The following client has connected:"
										+ clientSocket.getInetAddress()
												.getCanonicalHostName());

					}
				} catch (Exception e) {
					System.err
							.println("Unable to process client request. Socket closed");
				}
			}
		};

		// A client has connected to this server. Send welcome message
		Thread serverThread = new Thread(serverTask);
		serverThread.start();
	}

	public void close() throws IOException, NullPointerException {
		serverSocket.close();
		GUI.getProgressBar().setIndeterminate(false);
	}

	private class ClientTask implements Runnable {
		private final Socket clientSocket;

		private ClientTask(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			System.out.println("Got a client !");

			// Do whatever required to process the client's request

			try {
                String userInput;
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                while ((userInput = stdIn.readLine()) != null) {
                        System.out
                                .println("CLIENT REQUESTED RESPONSE");

                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                                clientSocket.getOutputStream()));
                        writer.write(userInput);
                        writer.flush();
                        writer.close();

                        break;
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

			// Do whatever required to process the client's request

	/**
	 * Creates a SocketServer object and starts the server.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// Setting a default port number.
		int portNumber = 9990;

		try {
			// initializing the Socket Server
			SocketServer socketServer = new SocketServer(portNumber);
			socketServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}