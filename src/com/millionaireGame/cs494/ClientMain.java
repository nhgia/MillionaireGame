package com.millionaireGame.cs494;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ClientMain implements Runnable {
    JFrame frame;
    private JPanel panelMain;
    private JLabel labelMaster;
    private JButton buttonSend;
    private JButton buttonDisconnect;

    public ClientMain(JFrame f) {
        labelMaster.setText("");
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
        JFrame cFrame = new JFrame("Millionaire - Client");
        Thread cThread = new Thread(new ClientMain(cFrame));
        cThread.start();
    }

    @Override
    public void run() {
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800,450));
        frame.setLocation(300,300);
        frame.pack();
        frame.setVisible(true);
    }
}
