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
            frontend.frame.setTitle("Millionaire - Client | " + e.getMessage());
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
                System.exit(0);
                break;
            case CONN:
                break;
            case STGM:
            case QUES:
            case ANSA:
            case ANSB:
            case ANSC:
            case ANSD:
            case TANS:
                frontend.display(type, message);
                break;
            case ERRO:
                frontend.frame.setTitle("Millionaire - Client | " + message);
                break;
        }
    }

    public void actionSendMessageToServer(ActionType type, String s) {
        if (type == ActionType.NAME) {
            frontend.frame.setTitle("Millionaire - Client | Name: " + s + " | ID: " + clientId);
        }
        if (out != null) {
            out.println(type.toString() + " " + s);
        }
    }
}
