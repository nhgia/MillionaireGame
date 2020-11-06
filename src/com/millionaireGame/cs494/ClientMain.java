package com.millionaireGame.cs494;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class ClientMain implements Runnable {
    private final JFrame frame = new JFrame("Millionaire - Client");
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
            display("Connected");
            while (true) {
                display(in.nextLine());
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

    private void display(final String s) {
        EventQueue.invokeLater(new Runnable() {
            //@Override
            public void run() {
                textArea.append(s + "\n");
            }
        });
    }

    public void actionTappedButton() {
        String s = tf.getText();
        if (out != null) {
            out.println(s);
        }
        display(s);
        tf.setText("");
    }
}
