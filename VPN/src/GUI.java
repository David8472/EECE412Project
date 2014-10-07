import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.TextArea;
import javax.swing.JProgressBar;

public class GUI {

	private JFrame frame;

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

	final static String CLIENTPANEL = "Client";
	final static String SERVERPANEL = "Server";
	final static int extraWindowWidth = 100;
	private JTextField hostnameField;
	private JTextField hostMessageField;

	public void addClientServerPane(Container pane) {
		JTabbedPane tabbedPane = new JTabbedPane();

		// Create the "cards".
		JPanel clientPanel = new JPanel();

		hostnameField = new JTextField();
		hostnameField.setText("Please enter host address.");
		clientPanel.add(hostnameField);
		hostnameField.setColumns(10);

		clientPanel.add(new JButton("Connect to host"));

		JPanel serverPanel = new JPanel();

		tabbedPane.addTab(CLIENTPANEL, clientPanel);
		tabbedPane.addTab(SERVERPANEL, serverPanel);
		
		JProgressBar progressBar = new JProgressBar();
		serverPanel.add(progressBar);

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
		
		pane.add(displayPane, BorderLayout.SOUTH);
		
		TextArea textArea = new TextArea();
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
}
