package com.sam.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class EchoThread extends Thread {
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
                sendMessage(message);
            } catch (Exception e) { // TODO better handling
                System.out.println(this.identifier + " has disconnected");
                return;
            }
        }
    }
}