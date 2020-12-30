package com.sam.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import com.sam.server.EchoThread;

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
}
