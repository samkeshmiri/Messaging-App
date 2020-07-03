package com.sam.server;

import javax.swing.JFrame;

public class ServerTest {
	public static void main(String[] args) {
		Server server = new Server();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.startRunning();
	}

}
