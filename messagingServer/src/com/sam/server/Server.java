package com.sam.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	public final int PORT = 6789;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server; // listens for incoming requests
	private ArrayList<EchoThread> connectionPool; // threads containers of connections

	public Server() {
		connectionPool = new ArrayList<EchoThread>();
	}

	public void run() {
		try {
			server = new ServerSocket(PORT, 100);
			System.out.println("Server running on port " + PORT + "...");

			while (true) {
				try {
					Socket socket = server.accept();

					// delegate the new thread to the client
					EchoThread thread = new EchoThread(socket);

					// keep track of our multithreaded connections in the pool
					connectionPool.add(thread);
					thread.start();

					// prune our connection pool after each connection
					pruneConnectionPool();

				} catch (IOException e) {
					System.out.println("I/O error: " + e);
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// dispose of any resources that are disconnected
	// to free up memory
	public void pruneConnectionPool() {
		for (int i = 0; i < connectionPool.size(); i++) {
			Socket socket = connectionPool.get(i).getSocket();
			if (!socket.isConnected()) {
				connectionPool.remove(i);
			}
		}
	}

	private class EchoThread extends Thread {
		protected Socket socket;
		protected String identifier;
	
		private ObjectInputStream inp = null;
		private ObjectOutputStream out = null;
	
		public EchoThread(Socket clientSocket) {
			this.socket = clientSocket;
			inp = null;
			out = null;
	
			try {
				// we need an output stream to echo back what they said
				out = new ObjectOutputStream(socket.getOutputStream());
				out.flush();
	
				inp = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
		public Socket getSocket() {
			return socket;
		}
	
		public void sendMessage(String message) {
			try {
				if (message.equalsIgnoreCase("QUIT")) {
					socket.close();
					return;
				} else {
					out.writeObject(message);
					out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void broadcastMessage(String message) {
			for (EchoThread connection : connectionPool) {
				connection.sendMessage(message);
			}
		}
	
		public String buildMessage(String message) {
			return this.identifier + ": " + message;
		}
	
		public void run() {
			try {
				// first line will be the username in our protocol
				this.identifier = (String) inp.readObject();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
	
			System.out.println(this.identifier + " has connected");
			String line;
	
			while (socket.isConnected()) {
				try {
					line = (String) inp.readObject();
					String message = buildMessage(line);
					broadcastMessage(message);
				} catch (Exception e) { // TODO better handling
					System.out.println(this.identifier + " has disconnected");
					return;
				}
			}
		}
	}
}
