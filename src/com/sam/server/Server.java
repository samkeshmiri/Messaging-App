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
			public void actionPerformed(ActionEvent event) { // when user types in and hits enter, this is performed
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300, 150);
		setVisible(true);
	}

	public void startRunning() {
		try {
			server = new ServerSocket(6789, 100);
			while (true) {
				try {
					// wait for someone to connect
					// connect and talk
					waitForConnection();
					setupStreams();
					whileChatting();
				} catch (EOFException eofException) {
					showMessage("\n Server ended the connection");
				} finally {
					closeEverything();
				}
			}
		} catch (IOException ioException) {

		}
	}

	/*
	 * wait for connection then display connection information
	 * only creates a socket/connection once it is connected
	 */
	private void waitForConnection() throws IOException {
		showMessage("waiting for connection... \n");
		connection = server.accept(); // blocks connection until it is made
		showMessage("Now connected to " + connection.getInetAddress().getHostName());
	}

	private void setupStreams() {
		// TODO Auto-generated method stub

	}

	private void whileChatting() {
		// TODO Auto-generated method stub

	}

}
