package com.millionaireGame.cs494;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.text.ParseException;
import java.util.*;

public class ClientMain implements Runnable {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 8989;
    private volatile PrintWriter out;
    private Scanner in;
    private Thread thread;

    public int clientId;

    public ClientGUI frontend;
    private Thread frontendThread;
    public MessageToSend actionSendMessage;

    public ClientMain() throws IOException, FontFormatException, ParseException {
//        display(SERVER_IP + " on port " + PORT);
        thread = new Thread(this, "Trying");
        actionSendMessage = this::actionSendMessageToServer;
        frontend = new ClientGUI(actionSendMessage);
        frontendThread = new Thread(frontend);
    }

    public static void main(String[] args) throws IOException, FontFormatException, ParseException {
        new ClientMain().start();
    }

    public void start() {
        thread.start();
        frontendThread.start();
    }

    @Override
    public void run() {
        try {
            Socket socket;
            socket = new Socket(SERVER_IP, PORT);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            frontend.display(ActionType.CONN,"Client connected");
            while (true) {
                String message = in.nextLine();
                didReceiveMessageFromServer(message);
            }
        } catch (Exception e) {
            frontend.display(ActionType.ERRO, e.getMessage());
            e.printStackTrace(System.err);
        }
        finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }

    public void didReceiveMessageFromServer(String s) {
        // Guard non-null value
        if (s == null) return;

        String message = s;
        ActionType type = ActionType.valueOf(message.substring(0,4));
        message = message.substring(5);
        switch (type) {
            case MESG:
                frontend.display(type, message);
                break;
            case CLID:
                clientId = Integer.parseInt(message);
                frontend.frame.setTitle("Millionaire - Client | ID: " + clientId);
                frontend.display(type, "Your ID is " + clientId);
                break;
            case DISS:
                break;
            case CONN:
                break;
        }
    }

    public void actionSendMessageToServer(ActionType type, String s) {
        if (out != null) {
            out.println(type.toString() + " " + s);
        }
    }
}
