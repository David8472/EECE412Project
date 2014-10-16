import encryption.RSA_encrypt;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class SocketClient {

    private String hostname;
    private int port;
    Socket socketClient;
    private Key publicKey;
    private Key privateKey;

    private Key serverPublicKey;
    private int dhSecret = -1;

    private BigInteger dhPow;
    private BigInteger dhMod;
    private BigInteger B;

    private BigInteger result;

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
                    readAuthData();

                    while (dhPow == null || dhMod == null || dhSecret <=0 || B == null) {

                    }

                    BigInteger A = RSA_encrypt.getDHNumToSend(dhSecret, dhPow, dhMod);
                    BigInteger result = RSA_encrypt.getDHNumToSend(dhSecret, B, dhMod);

                    Object[] objs = new Object[] {A, result};
                    sendObject(objs);


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

    public void readAuthData() throws IOException {
        try {
            ObjectInputStream inFromServer = new ObjectInputStream(socketClient.getInputStream());
            dhPow = (BigInteger) inFromServer.readObject();
            dhMod = (BigInteger) inFromServer.readObject();
            B = (BigInteger) inFromServer.readObject();

            System.out.println("Diffie-Hellman power " + dhPow);
            System.out.println("Diffie-Hellman mod " + dhMod);
            System.out.println("B from server: " + B);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setSecret(int secret) {
        this.dhSecret = secret;
    }

    public void sendObject(Object[] objs) {
        try {
            ObjectOutputStream outToServer = new ObjectOutputStream(socketClient.getOutputStream());
            for (Object o : objs) {
                outToServer.writeObject(o);
                outToServer.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
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