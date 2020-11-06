package com.millionaireGame.cs494;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerController implements Runnable {
    private Socket client;
    private Scanner in;
    private PrintWriter out;
    private final ServerMain serverMain;

    public ServerController(Socket clientSocket, ServerMain server) throws IOException {
        this.client = clientSocket;
        this.serverMain = server;
        in = new Scanner(client.getInputStream());
        out = new PrintWriter(client.getOutputStream(), true);

    }

    @Override
    public void run() {
        try {
            while (true) {
                serverMain.display(in.nextLine());
            }
        } catch (Exception e) {
            serverMain.display(e.getMessage());
            e.printStackTrace(System.err);
        }
        finally {
            in.close();
            out.close();
        }
    }

    public void actionSendToClient(String str) {
        if (out != null) {
            out.println(client.getRemoteSocketAddress() + ": " + str);
        }
    }
}