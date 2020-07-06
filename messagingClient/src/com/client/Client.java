package com.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;

	public Client(String host) {
		super("Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(350, 150);
		setVisible(true);
	}


	// connect to server
	public void startRunning() {
		try {
			connectToServer();
			setUpStreams();
			whileChatting();
		} catch (EOFException eoxException) {
			showMessage("\n Client terminated connection");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			closeEverything();
		}
	}

	private void closeEverything() {
		showMessage("\n Closing connection...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void setUpStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream()); // stream for client to server
		output.flush(); // bytes get left in buffer, data get left when sending, so this pushes the rest
						// through
		input = new ObjectInputStream(connection.getInputStream()); // create pathway to receive messages
		showMessage("\n Streams are now setup. \n");
	}
	
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\nCannot send message");
			}
		} while (!message.equals("CLIENT: QUIT")); // TODO: change this to accept lower case
	}

	private void connectToServer() throws IOException {
		showMessage("Attempting connection...");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostAddress());
	}

	protected void sendMessage(String message) {
		try {
			output.writeObject("CLIENT: " + message);
			output.flush(); // not really necessary but good to ensure it's fully sent
			showMessage("\n CLIENT: " + message);
		} catch (IOException ioException) {
			chatWindow.append("\n Unable to send message");
		}
	}
	
	private void ableToType(final boolean ableToType) {
		SwingUtilities.invokeLater( // basically we update the GUI to be able to type in a message 
				new Runnable() { 
					public void run() {
						userText.setEditable(ableToType);
					}
				});
	}

	private void showMessage(final String text) {
		SwingUtilities.invokeLater( // thread that updates the GUI
				new Runnable() { // create thread
					public void run() {
						chatWindow.append(text);
					}
				});
	}
}