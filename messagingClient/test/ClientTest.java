package com.client;

import org.junit.Test;
import org.junit.Assert;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class ClientTest {
	
	@Test
	public void testConnection() {
		Client client = new Client("127.0.0.1");
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextArea chatWindow = client.getChatWindow();
		Assert.assertNotNull(chatWindow);
	}
	
}
