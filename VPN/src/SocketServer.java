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
	private SocketClientHandler handler;

	private int port;

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

	/**
	 * Opens socket and creates new thread to wait for client connection
	 * 
	 * @throws IOException
	 */
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
						GUI.getProgressBar()
								.setString("Connection established");
						Toolkit.getDefaultToolkit().beep();
						handler = new SocketClientHandler(clientSocket,
								privateKey);
						clientProcessingPool.submit(handler);
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

		// Runs server on a seperate thread
		Thread serverThread = new Thread(serverTask);
		serverThread.start();
	}

	/**
	 * Encrypts for client handler
	 * 
	 * @param message
	 */
	public void sendMessageToHandler(String message) {
		try {
			String encryptedMsg = RSA_encrypt.encrypt(message, clientPublicKey);
			GUI.nextStep(encryptedMsg, "Server Encrypted Message");
			handler.sendMessage(encryptedMsg);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the public key
	 */
	public void sendPublicKey() {
		while (handler == null) {
			System.out.print("Waiting for handler..");
		}

		handler.sendPublicKey(publicKey);
	}

	/**
	 * Close an open socket, otherwise exceptions are thrown
	 * 
	 * @throws IOException
	 * @throws NullPointerException
	 */
	public void close() throws IOException, NullPointerException {
		serverSocket.close();
		GUI.getProgressBar().setIndeterminate(false);
		GUI.getProgressBar().setString("No client connected.");
	}
}