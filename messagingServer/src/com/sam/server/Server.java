package com.sam.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server; // listens for incoming requests
	private Socket connection; // connection between computers

	public Server() {
		super("Instant Messenger");
		userText = new JTextField();
		userText.setEditable(false); // set to true once connected
		userText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) { // when user types in and hits enter, this is performed
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(350, 150);
		setVisible(true);
	}

	public void startRunning() {
		try {
			server = new ServerSocket(6789, 100);
			while (true) {
				try {
					// wait for someone to connect
					// connect and talk
					setupConnection();
					setupStreams();
					whileChatting();
				} catch (EOFException eofException) {
					showMessage("\n Server ended the connection");
				} finally {
					closeEverything();
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/*
	 * wait for connection then display connection information only creates a
	 * socket/connection once it is connected
	 */
	private void setupConnection() throws IOException {
		showMessage("waiting for connection... \n");
		connection = server.accept(); // blocks connection until it is made
		showMessage("Now connected to " + connection.getInetAddress().getHostName());
	}

	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream()); // create pathway to connect to another computer
		output.flush(); // bytes get left in buffer, data get left when sending, so this pushes the rest
						// through
		input = new ObjectInputStream(connection.getInputStream()); // create pathway to receive messages
		showMessage("\n Streams are now setup \n");
	}

	private void whileChatting() throws IOException {
		String message = "Connection established";
		sendMessage(message);
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n " + message);
			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\n User did not send a text message");
			}
		} while (!message.equals("CLIENT: QUIT")); // TODO: change this to accept lower case
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

	private void sendMessage(String message) {
		try {
			output.writeObject("SERVER: " + message);
			output.flush(); // not really necessary but good to ensure it's fully sent
			showMessage("\nSERVER: " + message);
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
