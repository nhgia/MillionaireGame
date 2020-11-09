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
                serverMain.didReceiveMessage(in.nextLine());
            }
        } catch (Exception e) {
            serverMain.actionDisconnectClient(clientId, e);
        }
        finally {
            in.close();
            out.close();
        }
    }

    public void actionSendToClient(String str, ActionType action) {
        if (out != null) {
            getId(str, action);
            out.println(action.toString() + " " + str);
        }
    }

    private void getId(String str, ActionType type) {
        if (type == ActionType.CLID) {
            clientId = Integer.parseInt(str);
        }
    }
}
