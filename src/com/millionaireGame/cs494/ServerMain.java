package com.millionaireGame.cs494;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ServerMain implements Runnable {
    private final JFrame frame = new JFrame("Millionaire - Server");
    private JPanel panelMain;
    private JButton buttonSend;
    private JButton buttonDisconnect;
    private JTextArea textArea;
    private JTextField tf;

    File font_file = new File("fonts/BebasNeue-Regular.ttf");
    Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);

    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 8989;
    private Thread thread;

    private ExecutorService pool = Executors.newFixedThreadPool(4);
    private ArrayList<ClientController> clients = new ArrayList<>();

    public static int numberOfClients = 0;

    public ServerMain() throws IOException, FontFormatException {
        textArea.setText("");
        textArea.setFont(font.deriveFont(24f));
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 450));
        frame.setLocation(200, 200);
        frame.pack();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        display(SERVER_IP + " on port " + PORT);
        thread = new Thread(this, "Awaiting");
        buttonSend.setFont(font.deriveFont(24f));
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionTappedButton();
            }
        });
        buttonDisconnect.setFont(font.deriveFont(24f));
        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
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
        frame.setVisible(true);
        thread.start();
    }

    @Override
    public void run() {

        try {
            ServerSocket ss = new ServerSocket(PORT);
            while (true) {
                Socket socket = ss.accept();
                numberOfClients++;
                display("Client ID " + numberOfClients + " - Connected");
                ClientController clientThread = new ClientController(socket, this);
                clients.add(clientThread);
                pool.execute(clientThread);

                clientThread.actionSendToClient(String.valueOf(numberOfClients), ActionType.CLID);
            }
        } catch (Exception e) {
            display(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

     void display(final String s) {
        EventQueue.invokeLater(new Runnable() {
            //@Override
            public void run() {
                textArea.append(s + "\n");
            }
        });
    }

    public void actionTappedButton() {
        String s = tf.getText();
        for (ClientController client : clients) {
            client.actionSendToClient(s, ActionType.MESG);
        }
        display(s);
        tf.setText("");
    }

    public void actionDisconnectClient(int clientId, Exception e) {
        display("Id " + clientId + ": " + e.getMessage() + "\nClient ID " + clientId +" - " +
                "Disconnected" + "!");
        e.printStackTrace(System.err);
    }
}
