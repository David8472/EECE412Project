import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class GUI {

	private JFrame frame;

	final static String CLIENTPANEL = "Client";
	final static String SERVERPANEL = "Server";
	final static int extraWindowWidth = 100;
	private static JTextField hostnameField;
	private static JTextField clientMessageField;
	private static TextArea clientText;

	private static JTextField serverMessageField;
	private static TextArea serverText;

	private static JProgressBar progressBar;

	private static SocketClient sc;
	private static SocketServer ss;
	private static int portNumber = 9990;


	private JTextField clientPort;

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
		hostnameField.setText("localhost:9990");
		hostnameField.setColumns(10);

		// port field
		clientPort = new JTextField();
		clientPort.setText("Port number.");
		clientConnectionPanel.add(clientPort);
		clientPort.setColumns(10);

		// Client connect
		JButton clientConnect = new JButton("Connect to host");
		clientConnectionPanel.add(clientConnect);
		clientConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					ArrayList<String> strings = GUI.getHostParams();
					sc = new SocketClient(strings.get(0), Integer
							.valueOf(strings.get(1)));
					sc.connect();
				} catch (UnknownHostException e) {
					SocketClient
							.displayText("Host unknown. Cannot establish connection");
				} catch (IOException e) {
					SocketClient
							.displayText("Cannot establish connection. Server may not be up.");
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

		progressBar = new JProgressBar();
		serverConnectionPanel.add(progressBar);
		
		JButton hostServer = new JButton("Host Server");
		hostServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					// initializing the Socket Server
					ss = new SocketServer(portNumber);
					ss.start();
					progressBar.setIndeterminate(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		serverConnectionPanel.add(hostServer);

		JButton endServer = new JButton("End Server");
		endServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ss.close();
			}
		});
		serverConnectionPanel.add(endServer);

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

	public static JTextField getHostnameField() {
		return hostnameField;
	}

	public static TextArea getTextArea() {
		return clientText;
	}

	public static JProgressBar getProgressBar() {
		return progressBar;
	}
	
	/**
	 * Gets hosts name and port
	 */
	public static ArrayList<String> getHostParams() {
		String s = getHostnameField().getText();
		String[] strings = s.split(":");
		return new ArrayList<>(Arrays.asList(strings[0], strings[1]));
	}

}
