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
        System.out.println("Got a client !");

        try {
            readPublicKey();
            readResponse();
            // client.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void readResponse() throws IOException, InterruptedException {
        try {
            String userInput;
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(client.getInputStream()));

            while ((userInput = stdIn.readLine()) != null) {
                System.out.println(userInput);
                String decrypted = RSA_encrypt.decrypt(userInput, privateKey);
                GUI.displayServerText(decrypted);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String message) throws IOException {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),
                    "UTF-8"));
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Key readPublicKey() {
        Key publicKey = null;
        try {
            ObjectInputStream inFromServer = new ObjectInputStream(client.getInputStream());
            publicKey = (Key) inFromServer.readObject();

            System.out.println(publicKey.toString());
            System.out.println("Client's public key is:  " + publicKey);

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
        System.out.println("Handler:" + publicKey);
        try {
            ObjectOutputStream outToServer = new ObjectOutputStream(client.getOutputStream());
            outToServer.writeObject(publicKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
