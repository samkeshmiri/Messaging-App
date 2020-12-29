package com.sam.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server; // listens for incoming requests
	private ArrayList<Socket> connectionPool; // connection between computers

	public Server() {
		connectionPool = new ArrayList<Socket>();
	}

	public void run() {
		try {
			server = new ServerSocket(6789, 100);

			while (true) {
				try {
					socket = serverSocket.accept();

					// keep track of our connections in the pool
					connectionPool.add(socket);

					// prune our connection pool after each connection
					pruneConnectionPool();
				} catch (IOException e) {
					System.out.println("I/O error: " + e);
				}

				// delegate the new thread to the client
				(new EchoThread(socket)).start();
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// dispose of any connections that are disconnected
	// to free up memory
	public void pruneConnectionPool() {
		for (int i = 0; i < connectionPool.size(); i++) {
			if (!connectionPool.get(i).isConnected()) {
				connectionPool.remove(i);
			}
		}
	}
}
