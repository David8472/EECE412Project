import encryption.RSA_encrypt;
import vpn.PrimeGenerator;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
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
    public static int dhSecret;

    private BigInteger dhPow;
    public static BigInteger dhMod;
	public static Key publicKey;
	private Key privateKey;

	public static Key clientPublicKey;

	public SocketServer(int port) {
		this.port = port;
		try {
            PrimeGenerator pg = new PrimeGenerator();
            pg.generatePrimes();


            this.dhPow = BigInteger.valueOf(pg.getPrimeOne());
            this.dhMod = BigInteger.valueOf(pg.getPrimeTwo());

			KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
			KeyPair kp = kg.generateKeyPair();

			this.publicKey = kp.getPublic();
			GUI.nextStep("server", "=Generating server public key");
			GUI.displayServerText("Server Public Key:"
					+ this.publicKey.toString());
			GUI.nextStep("", "");

			this.privateKey = kp.getPrivate();
			GUI.nextStep("server", "=Generating server private key");
			GUI.displayServerText("Server Private Key: "
					+ this.privateKey.toString());
			GUI.nextStep("", "");


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

				GUI.displayServerText("=Waiting for clients...");
				Socket client = null;

				try {
					while (true) {
						Socket clientSocket = serverSocket.accept();
						GUI.getProgressBar().setIndeterminate(false);
						GUI.getProgressBar().setValue(100);
						GUI.displayServerText("=Connection established");
						Toolkit.getDefaultToolkit().beep();
						handler = new SocketClientHandler(clientSocket,
								privateKey);

						sendPublicKey();
						handler.readPublicKey();
						handler.readResponse();
						clientProcessingPool.submit(handler);
						GUI.displayServerText("=The following client has connected:"
								+ clientSocket.getInetAddress()
										.getCanonicalHostName());

					}
				} catch (Exception e) {
					GUI.displayServerText("*Unable to process client request. Socket closed*");
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
			GUI.nextStep("server", "=Encrypt server message");
			String encryptedMsg = RSA_encrypt.encrypt(message, clientPublicKey);
			GUI.displayServerText("Encrypted message: " + encryptedMsg);
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
	}

    public void sendAuthData() {
        while (handler == null) {
//            System.out.println("Waiting for handler...");
        }

        if (dhSecret > 0)  {
            BigInteger B = RSA_encrypt.getDHNumToSend(dhSecret, dhPow, dhMod);
            System.out.println("B " + B);
            handler.sendAuthData(dhPow, dhMod, B);
        } else {

        }
    }

    public void setSecret(int secret) {
        this.dhSecret = secret;
    }
}