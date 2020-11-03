package com.millionaireGame.cs494;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain implements Runnable {
    JFrame frame;
    private JPanel panelMain;
    private JButton buttonSend;
    private JLabel myLabel;
    private JButton buttonDisconnect;

    public ServerMain(JFrame f) {
        myLabel.setText("");
        this.frame = f;
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        JFrame sFrame = new JFrame("Millionaire - Server");
        Thread sThread = new Thread(new ServerMain(sFrame));
        sThread.start();
    }

    @Override
    public void run() {
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 450));
        frame.setLocation(200, 200);
        frame.pack();
        frame.setVisible(true);
    }


}
