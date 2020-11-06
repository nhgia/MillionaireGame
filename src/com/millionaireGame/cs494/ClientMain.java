package com.millionaireGame.cs494;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class ClientMain implements Runnable {
    private final JFrame frame = new JFrame("Millionaire - Client | Connecting...");
    private JPanel panelMain;
    private JButton buttonSend;
    private JButton buttonDisconnect;
    private JTextArea textArea;
    private JTextField tf;

    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 8989;
    private volatile PrintWriter out;
    private Scanner in;
    private Thread thread;

    public int clientId;

    public ClientMain() {
        textArea.setText("");
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
        thread = new Thread(this, "Trying");
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionTappedButton();
            }
        });
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
                new ClientMain().start();
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
            Socket socket;
            socket = new Socket(SERVER_IP, PORT);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            display("Client connected");
            while (true) {
                String message = in.nextLine();
                didReceiveMessageFromServer(message);
            }
        } catch (Exception e) {
            display(e.getMessage());
            e.printStackTrace(System.err);
        }
        finally {
            in.close();
            out.close();
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

    public void didReceiveMessageFromServer(String s) {
        // Guard non-null value
        if (s == null) return;

        String message = s;
        ActionType type = ActionType.valueOf(message.substring(0,4));
        message = message.substring(5);
        switch (type) {
            case MESG:
                display(type.toString() + ": " + message);
                break;
            case CLID:
                clientId = Integer.parseInt(message);
                frame.setTitle("Millionaire - Client | ID: " + clientId);
                display("Your ID is " + clientId);
                break;
            case DISS:
                break;
            case CONN:
                break;
        }
    }

    public void actionTappedButton() {
        String s = tf.getText();
        if (out != null) {
            out.println("Client " + clientId + " said: " + s);
        }
        display(s);
        tf.setText("");
    }
}
