import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import encryption.RSA_encrypt;

public class SocketClient {

	private Socket socketClient;

	private String hostname;
	private int port;

	private Key publicKey;
	private Key privateKey;

	private Key serverPublicKey;

	public SocketClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;

		try {
			KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
			KeyPair kp = kg.generateKeyPair();
			this.publicKey = kp.getPublic();
			this.privateKey = kp.getPrivate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connects to a server with specified host name and port
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect() throws UnknownHostException, IOException {
		GUI.displayClientText("Attempting to connect to " + hostname + ":"
				+ port);
		socketClient = new Socket(hostname, port);
		GUI.displayClientText("Connection Established");

		Runnable clientTask = new Runnable() {
			@Override
			public void run() {
				try {
					readPublicKey();
					readResponse();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Thread serverThread = new Thread(clientTask);
		serverThread.start();
	}

	/**
	 * Sends a message to server through a buffer
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		System.out.println("client is sending");
		if (serverPublicKey != null) {
			try {
				String encryptedText = RSA_encrypt.encrypt(message,
						serverPublicKey);
				GUI.nextStep(encryptedText, "Encrypted Text");
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(socketClient.getOutputStream()));
				writer.write(encryptedText);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("Server public key unknown.");
		}
	}

	/**
	 * Read received messages from server
	 * 
	 * @throws IOException
	 */
	public void readResponse() throws IOException {
		String userInput;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				socketClient.getInputStream()));

		while ((userInput = stdIn.readLine()) != null) {
			System.out.println(userInput);
			String decryptedText = RSA_encrypt.decrypt(userInput, privateKey);
			GUI.displayClientText(decryptedText);
		}
	}

	/**
	 * Reads the public key
	 */
	public void readPublicKey() throws IOException {
		try {
			ObjectInputStream inFromServer = new ObjectInputStream(
					socketClient.getInputStream());
			serverPublicKey = (Key) inFromServer.readObject();

			System.out.println("Client got " + serverPublicKey.toString());
			GUI.nextStep(serverPublicKey.toString(), "Server public key");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the public key
	 */
	public void sendPublicKey() {
		try {
			ObjectOutputStream outToServer = new ObjectOutputStream(
					socketClient.getOutputStream());
			outToServer.writeObject(publicKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}