import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI {

	private JFrame frame;

	final static String CLIENTPANEL = "Client";
	final static String SERVERPANEL = "Server";
	final static int extraWindowWidth = 100;
	private static JTextField hostnameField;
	private static JTextField hostMessageField;

	private static SocketClient sc;
	private static SocketServer ss;
	private static int portNumber = 9990;

	private static TextArea textArea;

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

		// Create the "cards".
		JPanel clientPanel = new JPanel();

		hostnameField = new JTextField();
		// hostnameField.setText("Please enter host address.");
		hostnameField.setText("localhost:9990");
		hostnameField.setColumns(10);
		clientPanel.add(hostnameField);

		JButton button = new JButton("Connect to host");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					ArrayList<String> strings = GUI.getHostParams();
					sc = new SocketClient(strings.get(0), Integer
							.valueOf(strings.get(1)));
					sc.connect();
                    sc.readResponse();
				} catch (UnknownHostException e) {
					System.err
							.println("Host unknown. Cannot establish connection");
				} catch (IOException e) {
					System.err
							.println("Cannot establish connection. Server may not be up."
									+ e.getMessage());
				}
			}
		});
		clientPanel.add(button);

		JPanel serverPanel = new JPanel();

		tabbedPane.addTab(CLIENTPANEL, clientPanel);
		tabbedPane.addTab(SERVERPANEL, serverPanel);

		JButton btnHostServer = new JButton("Host Server");
		btnHostServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					// initializing the Socket Server
					ss = new SocketServer(portNumber);
					ss.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		serverPanel.add(btnHostServer);

		JProgressBar progressBar = new JProgressBar();
		serverPanel.add(progressBar);

		JButton btnEndServer = new JButton("End Server");
		btnEndServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ss.close();
			}
		});
		serverPanel.add(btnEndServer);

		pane.add(tabbedPane, BorderLayout.CENTER);

	}

	public void addDisplayPane(Container pane) {
		JPanel displayPane = new JPanel();
		displayPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		displayPane.add(panel, BorderLayout.NORTH);

		hostMessageField = new JTextField();
		panel.add(hostMessageField);
		hostMessageField.setColumns(10);

		JButton btnSendMessage = new JButton("Send message");
		panel.add(btnSendMessage);

        btnSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {

                } catch (Exception e) {

                }
            }
        });

		pane.add(displayPane, BorderLayout.SOUTH);

		textArea = new TextArea();
		displayPane.add(textArea);
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("VPN");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		GUI applicationGUI = new GUI();
		applicationGUI.addClientServerPane(frame.getContentPane());
		applicationGUI.addDisplayPane(frame.getContentPane());

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
		return textArea;
	}

	/**
	 * Gets hosts name and port
	 */
	public static ArrayList<String> getHostParams() {
		String s = getHostnameField().getText();
		String[] strings = s.split(":");
		return new ArrayList<String>(Arrays.asList(strings[0], strings[1]));
	}

}
