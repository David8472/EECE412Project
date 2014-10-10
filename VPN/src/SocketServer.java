import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
		System.out.println("Starting the socket server at port:" + port);
		serverSocket = new ServerSocket(port);

		final ExecutorService clientProcessingPool = Executors
				.newFixedThreadPool(10);

		Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				System.out.println("Waiting for clients...");
				try {
					while (true) {
						Socket clientSocket = serverSocket.accept();
						clientProcessingPool
								.submit(new ClientTask(clientSocket));
						System.out
								.println("The following client has connected:"
										+ clientSocket.getInetAddress()
												.getCanonicalHostName());
					}
				} catch (IOException e) {
					System.err
							.println("Unable to process client request. Socket closed");
					// e.printStackTrace();
				}
			}
		};
		// A client has connected to this server. Send welcome message
		Thread serverThread = new Thread(serverTask);
		serverThread.start();
	}

	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Port has not been opened");
		}
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
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendWelcomeMessage(Socket client) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				client.getOutputStream()));
		writer.write("Hello. You are connected to a Simple Socket Server. What is your name?");
		writer.flush();
		writer.close();
	}

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