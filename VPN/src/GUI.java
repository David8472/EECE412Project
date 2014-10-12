import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class GUI {

	private JFrame frame;

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

	// private static int portNumber = 9990;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
	}

	public void addClientServerPane(Container pane) {
		JTabbedPane tabbedPane = new JTabbedPane();
		addClientPane(tabbedPane);
		addServerPane(tabbedPane);
		pane.add(tabbedPane, BorderLayout.CENTER);
		addIpPane(pane);
	}

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
		// hostnameField.setText("Please enter host address.");
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

                    sc.askForTime();
                    sc.readResponse();

				} catch (UnknownHostException e) {
					displayClientText("*Host unknown. Cannot establish connection*");
				} catch (IOException e) {
					displayClientText("*Cannot establish connection. Server may not be up*");
				}
			}
		});

		// Client message panel
		JPanel clientMsgPanel = new JPanel();
		clientPanel.add(clientMsgPanel, BorderLayout.CENTER);

		// Client message text field
		clientMessageField = new JTextField();
		clientMsgPanel.add(clientMessageField);
		clientMessageField.setColumns(10);

		// Client button to send message
		JButton clientSendMessage = new JButton("Send message");
		clientMsgPanel.add(clientSendMessage);
		JPanel clientDisplay = new JPanel();
		clientPanel.add(clientDisplay, BorderLayout.SOUTH);
		clientDisplay.setLayout(new BorderLayout(0, 0));

		// Client display
		clientText = new TextArea("Hello World.");
		clientDisplay.add(clientText);

		parent.addTab(CLIENTPANEL, clientPanel);
	}

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
                }
                    catch (NumberFormatException e) {
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

		// SidePane to show txtfield and progress
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
		serverMsgPanel.add(serverSendMessage);

		JPanel serverDisplay = new JPanel();
		serverPanel.add(serverDisplay, BorderLayout.SOUTH);
		serverDisplay.setLayout(new BorderLayout(0, 0));

		// Server display
		serverText = new TextArea("Hello Server.");
		serverDisplay.add(serverText);
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("VPN");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		GUI applicationGUI = new GUI();
		applicationGUI.addClientServerPane(frame.getContentPane());

		// Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);
	}

	private void addIpPane(Container pane) {
		// Get ip button
		JButton getIpButton = new JButton("Get IP");
		getIpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getIP();
			}
		});
		JPanel ipPane = new JPanel();
		ipPane.add(getIpButton);
		pane.add(ipPane, BorderLayout.NORTH);
	}

	// Retrieves list of ips and displays to text
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

	public static void displayClientText(String s) {
		clientText.append("\n" + s);
	}

	public static void displayServerText(String s) {
		serverText.append("\n" + s);
	}

	public static JTextField getServerPort() {
		return serverPort;
	}

	public static JProgressBar getProgressBar() {
		return progressBar;
	}
}
