import encryption.RSA_encrypt;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {

	private ServerSocket serverSocket;

	private int port;
    private SocketClientHandler handler;
    private Key publicKey;
    private Key privateKey;

    public static Key clientPublicKey;

	public SocketServer(int port) {
		this.port = port;
        try {
            KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
            KeyPair kp = kg.generateKeyPair();
            this.publicKey = kp.getPublic();
            this.privateKey = kp.getPrivate();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
                        handler = new SocketClientHandler(clientSocket, privateKey);
						clientProcessingPool
								.submit(handler);
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

    public void sendMessage(String message) {
        try {
            byte[] encryptedMsg = RSA_encrypt.encrypt(message, clientPublicKey);
            handler.sendMessage(encryptedMsg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void sendPublicKey() {
        while (handler == null) {
            System.out.print("Waiting for handler..");
        }

        handler.sendPublicKey(publicKey);
    }

    public void close() throws IOException, NullPointerException {
		serverSocket.close();
		GUI.getProgressBar().setIndeterminate(false);
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