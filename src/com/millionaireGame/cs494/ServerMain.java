package com.millionaireGame.cs494;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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

    private String dataJSON = new String((Files.readAllBytes(Paths.get("resource/data.json"))));
    private JSONArray questions;

    public ServerMain() throws IOException, FontFormatException {
        actionSendMessage = this::actionSendMessageToClients;
        frontend = new ServerGUI(actionSendMessage);
        frontendThread = new Thread(frontend);
        thread = new Thread(this, "Awaiting");
        questions = new JSONArray(dataJSON);
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

                clientThread.actionSendToClient(ActionType.CLID, String.valueOf(numberOfClients));
            }
        } catch (Exception e) {
            frontend.display(ActionType.ERRO, e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void actionDisconnectClient(int clientId, String name, Exception e) {
        frontend.playersName.removeElement("#" + clientId + ": " + name);
        e.printStackTrace(System.err);

    }

    void actionSendMessageToClients(ActionType type, String s) {
        frontend.display(type, s);
        if (type == ActionType.STGM) {
            //loop for 0 to length JSONArray
            //violate
            if (frontend.playersName.getSize() < 2) {
                frontend.display(ActionType.ERRO, "You need 2 or more connected players to start.");
            }
            else {
                JSONObject question = (JSONObject) getRandomQuestionSet(questions, 10 * numberOfClients).get(0);
                for (ClientController client : clients) {
                    client.actionSendToClient(type, s);
                    client.actionSendToClient(ActionType.QUES, (String) question.get("question"));
                    client.actionSendToClient(ActionType.ANSA, (String) question.get("A"));
                    client.actionSendToClient(ActionType.ANSB, (String) question.get("B"));
                    client.actionSendToClient(ActionType.ANSC, (String) question.get("C"));
                    client.actionSendToClient(ActionType.ANSD, (String) question.get("D"));
                };
            };
        }
        else {
            for (ClientController client : clients) {
                client.actionSendToClient(type, s);
            }
        }
    }

    void didReceiveMessage(int clientID, String s) {
        // Guard
        if (s == null) return;
        // Parse message
        String message = s;
        ActionType type = ActionType.valueOf(message.substring(0,4));
        message = message.substring(5);
        switch (type) {
            case MESG:
                frontend.display(type, message);
                break;
            case CLID:
                break;
            case DISS:
                break;
            case CONN:
                break;
            case NAME:
                clients.get(clientID - 1).setName(message);
                frontend.playersName.addElement("#" + clientID + ": " + message);
                break;
        }

    }
    public JSONArray getRandomQuestionSet(JSONArray originArray, int totalQuestion){
//        ArrayList<JSONObject> a = new ArrayList<>();
        Random rand = new Random();
        List<Object> setQuestion = new ArrayList<>();
//        for(int i = 0; i < totalQuestion; i++){
//            int randomIndex = rand.nextInt((originArray.length()));
//            setQuestion.add((JSONObject) originArray.get(randomIndex));
//            originArray.remove(randomIndex);
//        }
        setQuestion = originArray.toList();
        Collections.shuffle(setQuestion);
        return new JSONArray(setQuestion.subList(0, totalQuestion));
    }
}
