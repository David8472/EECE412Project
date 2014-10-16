import encryption.RSA_encrypt;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.Key;

public class SocketClientHandler implements Runnable {

    private Socket client;
    private Key privateKey;
    private BigInteger A;
    public boolean authenticated;

	public SocketClientHandler(Socket client, Key privateKey) {
		this.client = client;
		this.privateKey = privateKey;
        this.authenticated = false;
	}

	@Override
	public void run() {
		try {
            verifyAuth();

			readPublicKey();
			readResponse();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

    public void verifyAuth() throws IOException {
        try {
            ObjectInputStream inFromServer = new ObjectInputStream(client.getInputStream());
            A = (BigInteger) inFromServer.readObject();
            BigInteger result =  (BigInteger)inFromServer.readObject();


            BigInteger myresult = RSA_encrypt.getDHNumToSend(SocketServer.dhSecret, A, SocketServer.dhMod);

            System.out.println("A from client:" + A);
            System.out.println("Result from client:" + result + " compare " + myresult);

            if (result.equals(myresult)) {
                System.out.println("Authenticated");
                GUI.displayClientText("You have been authenticated by the server");
                GUI.displayServerText("This client has been authenticated");

                this.authenticated = true;
            }

            else {
                GUI.displayClientText("Client unauthorized");
                GUI.displayServerText("Your are not authenticated");
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAuthData(BigInteger p, BigInteger g, BigInteger B) {
        try {


            System.out.println("p:" +p + ", g:" + g);
            ObjectOutputStream outToServer = new ObjectOutputStream(client.getOutputStream());

            outToServer.writeObject(p);
            outToServer.reset();

            outToServer.writeObject(g);
            outToServer.reset();

            outToServer.writeObject(B);
            outToServer.reset();
        } catch (IOException e) {
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
}
