package com.millionaireGame.cs494;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ServerMain implements Runnable {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 8989;
    private Thread thread;

    private ServerGUI frontend;
    private Thread frontendThread;
    public MessageToSend actionSendMessage;

    private ExecutorService pool = Executors.newFixedThreadPool(4);
    private ArrayList<ClientController> clients = new ArrayList<>();

    public static int numberOfClients = 0;

    public ServerMain() throws IOException, FontFormatException {
        actionSendMessage = this::actionSendMessageToClients;
        frontend = new ServerGUI(actionSendMessage);
        frontendThread = new Thread(frontend);
        thread = new Thread(this, "Awaiting");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            //@Override
            public void run() {
                try {
                    new ServerMain().start();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FontFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void start() {
        thread.start();
        frontendThread.start();
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            frontend.display(ActionType.CONN,SERVER_IP + ":" + PORT);
            while (true) {
                Socket socket = ss.accept();
                numberOfClients++;
                frontend.display(ActionType.CLCN, "Client ID " + numberOfClients + " - Connected");
                ClientController clientThread = new ClientController(socket, this);
                clients.add(clientThread);
                pool.execute(clientThread);

                clientThread.actionSendToClient(String.valueOf(numberOfClients), ActionType.CLID);
            }
        } catch (Exception e) {
            frontend.display(ActionType.ERRO, e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void actionDisconnectClient(int clientId, Exception e) {
        frontend.display(ActionType.MESG,"Id " + clientId + ": " + e.getMessage() + "\nClient ID " + clientId +" - " +
                "Disconnected" + "!");
        e.printStackTrace(System.err);
    }

    void actionSendMessageToClients(String s) {
        for (ClientController client : clients) {
            client.actionSendToClient(s, ActionType.MESG);
        }
        frontend.display(ActionType.MESG, s);
    }

    void didReceiveMessage(String s) {
        // Guard
        if (s == null) return;
        // Parse message
        String message = s;
        ActionType type = ActionType.valueOf(message.substring(0,4));
        message = message.substring(5);
        frontend.display(type, message);
//        switch (type) {
//            case MESG:
//                frontend.display(type.toString() + ": " + message);
//                break;
//            case CLID:
//                clientId = Integer.parseInt(message);
//                frame.setTitle("Millionaire - Client | ID: " + clientId);
//                display("Your ID is " + clientId);
//                break;
//            case DISS:
//                break;
//            case CONN:
//                break;
//        }

    }
}
