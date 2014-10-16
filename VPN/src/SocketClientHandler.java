import encryption.RSA_encrypt;

import java.io.*;
import java.net.Socket;
import java.security.Key;

public class SocketClientHandler implements Runnable {

	private Socket client;
	private Key privateKey;

	public SocketClientHandler(Socket client, Key privateKey) {
		this.client = client;
		this.privateKey = privateKey;
	}

	@Override
	public void run() {
		try {
			readPublicKey();
			readResponse();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends message to client through buffer
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					client.getOutputStream(), "UTF-8"));
			writer.write(message);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Read message from client
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void readResponse() throws IOException, InterruptedException {
		try {
			String userInput;
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(
					client.getInputStream()));

			while ((userInput = stdIn.readLine()) != null) {
				GUI.nextStep("server", "=Wait for client message");
				GUI.displayServerText("Encrypted server message " + userInput);

				GUI.nextStep("server", "=Decrypt server message");
				String decrypted = RSA_encrypt.decrypt(userInput, privateKey);
				GUI.displayServerText(decrypted);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads a public key
	 * 
	 * @return publicKey
	 */
	public Key readPublicKey() {
		GUI.nextStep("server", "=Wait for client public key");
		Key publicKey = null;
		try {
			ObjectInputStream inFromServer = new ObjectInputStream(
					client.getInputStream());
			publicKey = (Key) inFromServer.readObject();

			System.out.println(publicKey.toString());
			System.out.println("Client's public key is:  " + publicKey);

			GUI.displayServerText("Client's public key is " + publicKey);
			SocketServer.clientPublicKey = publicKey;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return publicKey;
	}

	/**
	 * Sends the public key to client
	 * 
	 * @param publicKey
	 */
	public void sendPublicKey(Key publicKey) {
		try {
			GUI.nextStep("server", "=Send server public key");
			ObjectOutputStream outToServer = new ObjectOutputStream(
					client.getOutputStream());
			outToServer.writeObject(publicKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
