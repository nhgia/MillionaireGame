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
import java.util.*;
import java.util.List;
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
    private JSONArray questionSet;
    private int currentQuestionIndex = -1;
    private int numberOfConnectedClient = 0;
    private int currentClientIndex = 0;
    private int playersRemaining = 0;

    public ServerMain() throws IOException, FontFormatException {
        actionSendMessage = this::actionSendMessageToClients;
        frontend = new ServerGUI(actionSendMessage);
        frontendThread = new Thread(frontend);
        thread = new Thread(this, "Awaiting");
        questions = new JSONArray(dataJSON);
        frontend.display(ActionType.DBCN, "Loaded");
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
                clientThread.setConnectStatus(true);
                clientThread.actionSendToClient(ActionType.CLID, String.valueOf(numberOfClients));
            }
        } catch (Exception e) {
            frontend.display(ActionType.ERRO, e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void actionDisconnectClient(int clientId, String name, Exception e) {
        clients.get(clientId - 1).setConnectStatus(false);
        frontend.playersName.removeElement("#" + clientId + ": " + name);
        e.printStackTrace(System.err);

    }

    void actionSendMessageToClients(ActionType type, String s) {
        switch (type) {
            case STGM:
                numberOfConnectedClient = 0;
                for (ClientController client: clients) {
                    if (client.getConnectStatus()) {
                        numberOfConnectedClient++;
                    }
                }
                playersRemaining = numberOfConnectedClient;
                questionSet = getRandomQuestionSet(questions, 5 * numberOfConnectedClient);
                for (ClientController client : clients) {
                    client.actionSendToClient(type, s);
                };
                break;
            case NXQT:
                currentQuestionIndex++;
                boolean validate = false;
                currentClientIndex++;
                for (int i = currentClientIndex; i < clients.size(); i++) {
                    if (!clients.get(i).getLostStatus() && clients.get(i).getConnectStatus()) {
                        currentClientIndex = i;
                        validate = true;
                        break;
                    }
                }
                if (!validate) {
                    currentClientIndex = 0;
                    for (int i = currentClientIndex; i < clients.size(); i++) {
                        if (!clients.get(i).getLostStatus() && clients.get(i).getConnectStatus()) {
                            currentClientIndex = i;
                            break;
                        }
                    }
                };
                if (currentQuestionIndex >= 5 * numberOfConnectedClient || playersRemaining < 2) {
                    frontend.display(ActionType.FINI, "We have winner(s).");
                    frontend.display(ActionType.QUES, "");
                    frontend.display(ActionType.ANSA, "");
                    frontend.display(ActionType.ANSB, "");
                    frontend.display(ActionType.ANSC, "");
                    frontend.display(ActionType.ANSD, "");
                    for (ClientController client: clients) {
                        if (!client.getLostStatus() && client.getConnectStatus()) {
                            client.actionSendToClient(ActionType.FINI, "You are the winner!");
                        }
                        else {
                            client.actionSendToClient(ActionType.FINI, "You have lost. Try again!");
                        }
                        client.actionSendToClient(ActionType.QUES, "");
                        client.actionSendToClient(ActionType.ANSA, "");
                        client.actionSendToClient(ActionType.ANSB, "");
                        client.actionSendToClient(ActionType.ANSC, "");
                        client.actionSendToClient(ActionType.ANSD, "");
                    };
                    break;
                }
                JSONObject question = (JSONObject) questionSet.get(currentQuestionIndex);
                frontend.display(ActionType.QUES, (String) question.get("question"));
                frontend.display(ActionType.ANSA, (String) question.get("A"));
                frontend.display(ActionType.ANSB, (String) question.get("B"));
                frontend.display(ActionType.ANSC, (String) question.get("C"));
                frontend.display(ActionType.ANSD, (String) question.get("D"));
                frontend.display(ActionType.TANS, (String) question.get("answer"));
                frontend.display(ActionType.ALAN, String.valueOf(clients.get(currentClientIndex).getId()));
                for (ClientController client : clients) {
                    if (client.getId() == currentClientIndex + 1) {
                        client.actionSendToClient(ActionType.ALAN, "It's your turn to answer");
                    }
                    else {
                        client.actionSendToClient(ActionType.CORR,
                                "Player " + clients.get(currentClientIndex).getName() + "'s turn. Please wait.");
                    }
                    client.actionSendToClient(ActionType.QUES, (String) question.get("question"));
                    client.actionSendToClient(ActionType.ANSA, (String) question.get("A"));
                    client.actionSendToClient(ActionType.ANSB, (String) question.get("B"));
                    client.actionSendToClient(ActionType.ANSC, (String) question.get("C"));
                    client.actionSendToClient(ActionType.ANSD, (String) question.get("D"));
                };
                break;
            case LOST:
            case TANS:
                frontend.display(type, s);
                for (ClientController client : clients) {
                    client.actionSendToClient(type, s);
                    if (client.getId() == currentClientIndex + 1) {
                        if (client.getLostStatus()) {
                            client.actionSendToClient(ActionType.CORR, "Wrong answer! Better luck " +
                                    "next time.");
                            frontend.playersPlayingModel.removeElement("#" + client.getId() + ": " + client.getName());
                        }
                        else {
                            client.actionSendToClient(ActionType.CORR, "Correct answer.");
                        }
                    }
                    else {
                        if (clients.get(currentClientIndex).getLostStatus()) {
                            client.actionSendToClient(ActionType.CORR, clients.get(currentClientIndex).getName() + " " +
                                    "got wrong answer.");
                        }
                        else {
                            client.actionSendToClient(ActionType.CORR, "");
                        }
                    }
                    client.actionSendToClient(ActionType.MESG, playersRemaining + " player(s) remaining.");
                };
                break;
            default:
                frontend.display(type, s);
                for (ClientController client : clients) {
                    client.actionSendToClient(type, s);
                };
                break;
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
                frontend.playersPlayingModel.addElement("#" + clientID + ": " + message);
                break;
            case CLAN:
                JSONObject question = (JSONObject) questionSet.get(currentQuestionIndex);
                String ans = (String) question.get("answer");
                if (!message.equals(ans)) {
                    clients.get(currentClientIndex).setLost(true);
                    playersRemaining--;
                }
                frontend.display(type, message);
                break;
            default:
                frontend.display(type, message);
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
