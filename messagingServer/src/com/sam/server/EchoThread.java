package com.sam.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class EchoThread extends Thread {
    protected Socket socket;

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        ObjectOutputStream out = null;

        try {
            // we need an output stream to echo back what they said
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            inp = socket.getInputStream();
            // create a buffered reader for its utility methods and wrap it arounf our input stream
            brinp = new BufferedReader(new InputStreamReader(inp));
        } catch (IOException e) {
            return;
        }

        String line;

        while (socket.isConnected()) {
            try {
                line = brinp.readLine();
                if (line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                    // write out what they sent to the connection
                    out.writeBytes(line + "\n\r");
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}