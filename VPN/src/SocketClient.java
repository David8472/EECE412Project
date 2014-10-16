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
			GUI.nextStep("client", "=Generating client public key");
			GUI.displayClientText("Client Public Key:"
					+ this.publicKey.toString());
			GUI.nextStep("", "");
			this.privateKey = kp.getPrivate();
			GUI.nextStep("client", "=Generating client private key");
			GUI.displayClientText("Client Private Key:"
					+ this.publicKey.toString());
			GUI.nextStep("", "");

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
		GUI.nextStep("client", "=Attempting to connect to " + hostname + ":"
				+ port);
		socketClient = new Socket(hostname, port);
		GUI.nextStep("client", "=Connection Established");

		Runnable clientTask = new Runnable() {
			@Override
			public void run() {
				try {
					readPublicKey();
					sendPublicKey();
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
		if (serverPublicKey != null) {
			try {
				GUI.nextStep("client", "=Encrypt message");
				String encryptedText = RSA_encrypt.encrypt(message,
						serverPublicKey);
				GUI.displayClientText("Encrypted message: " + encryptedText);
				GUI.nextStep("client", "=Send message to server");
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
			GUI.displayClientText("Server public key unknown.");
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
			GUI.nextStep("client", "=Wait for server message");
			GUI.displayClientText("Encrypted server message " + userInput);

			GUI.nextStep("client", "=Decrypt server message");
			String decryptedText = RSA_encrypt.decrypt(userInput, privateKey);
			GUI.displayClientText("Decrypted server message " + decryptedText);
		}
	}

	/**
	 * Sends the public key
	 */
	public void sendPublicKey() {
		try {
			GUI.nextStep("client", "=Send client public key to server");
			ObjectOutputStream outToServer = new ObjectOutputStream(
					socketClient.getOutputStream());
			outToServer.writeObject(publicKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the public key
	 */
	public void readPublicKey() throws IOException {
		try {
			GUI.nextStep("client", "=Wait for server public key");
			ObjectInputStream inFromServer = new ObjectInputStream(
					socketClient.getInputStream());
			serverPublicKey = (Key) inFromServer.readObject();
			GUI.displayClientText("Server public key: "
					+ serverPublicKey.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}