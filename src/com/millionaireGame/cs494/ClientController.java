package com.millionaireGame.cs494;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientController implements Runnable {
    private Socket client;
    private Scanner in;
    private PrintWriter out;
    private final ServerMain serverMain;

    private int clientId;
    private String clientName;
    private boolean isConnected = false;

    public ClientController(Socket clientSocket, ServerMain server) throws IOException {
        this.client = clientSocket;
        this.serverMain = server;
        in = new Scanner(client.getInputStream());
        out = new PrintWriter(client.getOutputStream(), true);

    }

    @Override
    public void run() {
        try {
            while (true) {
                serverMain.didReceiveMessage(clientId, in.nextLine());
            }
        } catch (Exception e) {
            serverMain.actionDisconnectClient(clientId, clientName, e);
        }
        finally {
            in.close();
            out.close();
        }
    }

    public void actionSendToClient(ActionType action, String str) {
        if (out != null) {
            if (action == ActionType.CLID) {
                setId(str);
            };
            out.println(action.toString() + " " + str);
        }
    }

    public void setId(String str) {
        clientId = Integer.parseInt(str);
    }

    public void setName(String str) {
        clientName = str;
    }

    public void setConnectStatus(boolean value) {
        isConnected = value;
    }

    public boolean getConnectStatus() {
        return this.isConnected;
    }
}
