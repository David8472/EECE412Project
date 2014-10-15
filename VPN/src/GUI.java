import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import vpn.VPN;

public class GUI {

	private static GUI applicationGUI;
	private static JFrame frame;

	private static JPanel ipPane;
	private static JPanel confPane;

	final static String CLIENTPANEL = "Client";
	final static String SERVERPANEL = "Server";
	final static int extraWindowWidth = 100;

	private static JTextField hostnameField;
	private JTextField clientPort;
	private static JTextField clientMessageField;
	private static TextArea clientText;

	private static JTextField serverPort;
	private static JTextField serverMessageField;
	private static TextArea serverText;

	private static JProgressBar progressBar;

	private static SocketClient sc;
	private static SocketServer ss;

	private static VPN vpn;

	private JLabel clientSecretVal = new JLabel("Shared Secret Value: ");
	private JLabel serverSecretVal = new JLabel("Shared Secret Value: ");

	public static String[] options = { "Next Step" };

	/*
	 * Launch the application.
	 */
	public static void main(String[] args) {
		vpn.generateKeys();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("VPN");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		applicationGUI = new GUI();
		applicationGUI.addClientServerPane(frame.getContentPane());

		// Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
	}

	/**
	 * Add client, server and ip paone to main panel
	 * 
	 * @param pane
	 */
	public void addClientServerPane(Container pane) {
		JTabbedPane tabbedPane = new JTabbedPane();
		addServerPane(tabbedPane);
		addClientPane(tabbedPane);
		pane.add(tabbedPane, BorderLayout.CENTER);
		addIpPane(pane);
	}

	/**
	 * Creates all the components used on the client side and uses parent as the
	 * component it resides in
	 * 
	 * @param parent
	 */
	private void addClientPane(JTabbedPane parent) {
		// Create base panel
		JPanel clientPanel = new JPanel();
		clientPanel.setLayout(new BorderLayout(0, 0));

		// Client base panel
		JPanel clientConnectionPanel = new JPanel();
		clientPanel.add(clientConnectionPanel, BorderLayout.NORTH);

		// Host field
		hostnameField = new JTextField();
		clientConnectionPanel.add(hostnameField);
		hostnameField.setText("localhost");
		hostnameField.setColumns(10);

		// client port field
		clientPort = new JTextField();
		clientPort.setText("9990");
		clientConnectionPanel.add(clientPort);
		clientPort.setColumns(10);

		// Client connect
		JButton clientConnect = new JButton("Connect to host");
		clientConnectionPanel.add(clientConnect);
		clientConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					String hostName = hostnameField.getText();
					int hostPort = Integer.valueOf(clientPort.getText());
					sc = new SocketClient(hostName, hostPort);
					sc.connect();
					sc.sendPublicKey();
					ss.sendPublicKey();
				} catch (UnknownHostException e) {
					displayClientText("*Host unknown. Cannot establish connection*");
				} catch (IOException e) {
					displayClientText("*Cannot establish connection. Server may not be up*");
				}
			}
		});
		// shared secret value field
		final JTextField secretValueField = new JTextField();
		secretValueField.setColumns(20);

		clientConnectionPanel.add(clientSecretVal);
		clientConnectionPanel.add(secretValueField);

		JButton secretValBtn = new JButton("Set secret value");
		secretValBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String key_val = secretValueField.getText();
				int key = Integer.parseInt(key_val);
				vpn.setPrivateKey(key);
			}
		});
		clientConnectionPanel.add(secretValBtn);

		// Client message panel
		JPanel clientMsgPanel = new JPanel();
		clientPanel.add(clientMsgPanel, BorderLayout.CENTER);

		// Client message text field
		clientMessageField = new JTextField();
		clientMsgPanel.add(clientMessageField);
		clientMessageField.setColumns(50);

		// Client button to send message
		JButton clientSendMessage = new JButton("Send message");
		clientSendMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				final String message = clientMessageField.getText();

				if (message == null || message.equals("")) {
					throw new NullPointerException(
							"Error: please specify a message");
				}

				// //encrypting the message
				// // vpn.setMessage(message);
				// // vpn.setEncryptedMessage(vpn.encrypt(vpn.getMessage()));
				// // message = new String(vpn.getEncryptedMessage());
				// char[] encrypted_msg =vpn.encrypt(message);
				// message = new String(encrypted_msg);
				// char[] signed_msg = vpn.sign_signature(message,
				// encrypted_msg);
				// message = new String(signed_msg);

				try {
					sc.sendMessage(message);
				} catch (IOException e) {
					GUI.displayClientText("Sorry, we were unable to send that message!");
					e.printStackTrace();
				}

			}
		});

		clientMsgPanel.add(clientSendMessage);
		JPanel clientDisplay = new JPanel();
		clientPanel.add(clientDisplay, BorderLayout.SOUTH);
		clientDisplay.setLayout(new BorderLayout(0, 0));

		// Client display
		clientText = new TextArea("Hello World.");
		clientDisplay.add(clientText);

		parent.addTab(CLIENTPANEL, clientPanel);
	}

	/**
	 * Creates all the components used on the server side and uses parent as the
	 * component it resides in
	 * 
	 * @param parent
	 */
	private void addServerPane(JTabbedPane parent) {
		JPanel serverPanel = new JPanel();
		parent.addTab(SERVERPANEL, serverPanel);
		serverPanel.setLayout(new BorderLayout(0, 0));

		JPanel serverConnectionPanel = new JPanel();
		serverPanel.add(serverConnectionPanel, BorderLayout.NORTH);

		// Button to host server connection
		JButton hostServer = new JButton("Host Server");
		hostServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					int portNumber = Integer.valueOf(GUI.getServerPort()
							.getText());
					// initializing the Socket Server
					ss = new SocketServer(portNumber);
					ss.start();
					displayServerText("Starting the socket server at port:"
							+ portNumber);
					progressBar.setIndeterminate(true);
				} catch (IOException e) {
					displayServerText("Unable to read.");
					e.printStackTrace();
				} catch (NumberFormatException e) {
					displayServerText("*Invalid port number*");
				} catch (IllegalArgumentException e) {
					displayServerText("*Port number out of range*");
				}
			}
		});

		// Button to end server connection
		JButton endServer = new JButton("End Server");
		endServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ss.close();
				} catch (IOException e1) {
					displayServerText("*Port has not been opened*");
				} catch (NullPointerException e2) {
					e2.printStackTrace();
				}
			}
		});
		serverConnectionPanel.setLayout(new BorderLayout(0, 0));

		// buttonPane to hold server buttons
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));
		buttonPane.add(hostServer);
		buttonPane.add(endServer);

		// server port field
		serverPort = new JTextField();
		serverPort.setText("9990");
		serverPort.setColumns(10);

		progressBar = new JProgressBar();

		// SidePane to show textfield and progress
		JPanel serverSidePane = new JPanel();
		serverSidePane
				.setLayout(new BoxLayout(serverSidePane, BoxLayout.Y_AXIS));
		serverSidePane.add(serverPort);
		serverSidePane.add(progressBar);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(buttonPane);
		splitPane.setRightComponent(serverSidePane);
		serverConnectionPanel.add(splitPane);

		// Server message panel
		JPanel serverMsgPanel = new JPanel();
		serverPanel.add(serverMsgPanel, BorderLayout.CENTER);

		// Server message text field
		serverMessageField = new JTextField();
		serverMsgPanel.add(serverMessageField);
		serverMessageField.setColumns(10);

		// Server button to send message
		JButton serverSendMessage = new JButton("Send message");
		serverSendMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				ss.sendMessageToHandler(serverMessageField.getText());
			}
		});

		serverMsgPanel.add(serverSendMessage);

		// shared secret value field
		JTextField secretValueField = new JTextField();
		secretValueField.setColumns(20);

		serverMsgPanel.add(serverSecretVal);
		serverMsgPanel.add(secretValueField);

		JButton secretValBtn = new JButton("Set secret value");
		serverMsgPanel.add(secretValBtn);

		JPanel serverDisplay = new JPanel();
		serverPanel.add(serverDisplay, BorderLayout.SOUTH);
		serverDisplay.setLayout(new BorderLayout(0, 0));

		// Server display
		serverText = new TextArea("Hello Server.");
		serverDisplay.add(serverText);
	}

	private void addSecretValueField(JPanel panel) {
		JTextField sharedValue = new JTextField();
		panel.add(sharedValue);
	}

	/**
	 * Adds the ip pane to gui
	 * 
	 * @param pane
	 */
	private void addIpPane(Container pane) {
		// Get ip button
		JButton getIpButton = new JButton("Get IP");
		getIpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getIP();
			}
		});

		ipPane = new JPanel();
		ipPane.setLayout(new BoxLayout(ipPane, BoxLayout.Y_AXIS));
		ipPane.add(getIpButton);
		pane.add(ipPane, BorderLayout.NORTH);

		confPane = new JPanel();
		pane.add(confPane, BorderLayout.EAST);
	}

	/**
	 * Retrieves list of ips and displays to text area of both client and server
	 */
	private void getIP() {
		try {
			Enumeration<NetworkInterface> e = NetworkInterface
					.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration<InetAddress> ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					String s = i.getHostAddress();
					if (!s.contains("fe")) {
						displayClientText(i.getHostAddress());
						displayServerText(i.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			displayClientText("*Socket error*");
			displayServerText("*Socket error*");
		}
	}

	/**
	 * Displays string s with the given window title
	 * 
	 * @param s
	 * @param title
	 */
	public static void nextStep(String s, String title) {
		JOptionPane.showOptionDialog(confPane, "", "", JOptionPane.NO_OPTION,
				JOptionPane.NO_OPTION, null, options, options[0]);
	}

	/**
	 * Display string s to client text area
	 * 
	 * @param s
	 */
	public static void displayClientText(String s) {
		clientText.append("\n" + s);
	}

	/**
	 * Display string s to server text area
	 * 
	 * @param s
	 */
	public static void displayServerText(String s) {
		serverText.append("\n" + s);
	}

	/**
	 * Returns server port
	 * 
	 * @return
	 */
	public static JTextField getServerPort() {
		return serverPort;
	}

	/**
	 * Returns progress bar
	 * 
	 * @return
	 */
	public static JProgressBar getProgressBar() {
		return progressBar;
	}
}
