import encryption.RSA_encrypt;

import java.io.*;
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

	public SocketClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;

        try {
            KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
            KeyPair kp =  kg.generateKeyPair();
            this.publicKey = kp.getPublic();
            this.privateKey = kp.getPrivate();


        } catch (Exception e) {
            e.printStackTrace();
        }
	}

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

	public void readResponse() throws IOException {
		String userInput;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				socketClient.getInputStream()));

		while ((userInput = stdIn.readLine()) != null) {
			System.out.println(userInput);
//            String decryptedText = RSA_encrypt.decrypt(userInput.getBytes(), privateKey).toString();
            GUI.displayClientText(userInput);
		}
	}

    public void readPublicKey() throws IOException {
        try {
            ObjectInputStream inFromServer = new ObjectInputStream(socketClient.getInputStream());
            serverPublicKey = (Key) inFromServer.readObject();

            System.out.println("Client got " +  serverPublicKey.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendPublicKey() {
        try {
            ObjectOutputStream outToServer = new ObjectOutputStream(socketClient.getOutputStream());
            outToServer.writeObject(publicKey);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException{
        System.out.println("client is sending");
        if (serverPublicKey != null) {
            try {
                byte[] encryptedText = RSA_encrypt.encrypt(message, serverPublicKey);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream(), "UTF-8"));
                writer.write(encryptedText.toString());
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

	public static void main(String arg[]) {
		// Creating a SocketClient object
		SocketClient client = new SocketClient("localhost", 9990);
		try {
			// trying to establish connection to the server
			client.connect();

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