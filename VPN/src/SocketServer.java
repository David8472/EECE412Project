import java.awt.Toolkit;
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
		serverSocket = new ServerSocket(port);

		final ExecutorService clientProcessingPool = Executors
				.newFixedThreadPool(10);

		Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				GUI.displayServerText("Waiting for clients...");
				try {
					while (true) {
						Socket clientSocket = serverSocket.accept();
						GUI.getProgressBar().setIndeterminate(false);
						GUI.getProgressBar().setValue(100);
						Toolkit.getDefaultToolkit().beep();
						clientProcessingPool
								.submit(new ClientTask(clientSocket));
						// DataInputStream din = new DataInputStream(
						// clientSocket.getInputStream());
						// String message = din.readUTF();
						// System.out.println(message);
						// Thread thread = new Thread(new
						// SocketClientHandler(clientSocket));
						// thread.start();

						// SocketClientHandler handler = new
						// SocketClientHandler(clientSocket);
						// clientProcessingPool.submit(new
						// ClientHandlerTask(handler));
						GUI.displayServerText("The following client has connected:"
								+ clientSocket.getInetAddress()
										.getCanonicalHostName());
					}
				} catch (IOException e) {
					GUI.displayServerText("*Unable to process client request. Socket closed*");
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
				sendWelcomeMessage(clientSocket);
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ClientHandlerTask implements Runnable {
		private final SocketClientHandler handler;

		private ClientHandlerTask(SocketClientHandler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			System.out.println("Handler");
			GUI.displayServerText("Got a client !");

			// Do whatever required to process the client's request

			try {
				handler.readResponse();
			} catch (Exception e) {
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

	// private void readMessage(Socket client) throws IOException {
	// BufferedReader reader = new BufferedReader(new InputStreamReader(
	// client.getInputStream()));
	//
	// String in = reader.readLine();
	// System.out.println("!!!" + in);
	// }

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