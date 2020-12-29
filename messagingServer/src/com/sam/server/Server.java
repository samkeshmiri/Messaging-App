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
}
