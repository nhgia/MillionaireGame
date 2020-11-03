package com.millionaireGame.cs494;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerMain implements Runnable {
    JFrame frame;
    private JPanel panelMain;
    private JButton buttonSend;
    private JLabel myLabel;
    private JButton buttonDisconnect;

    InetAddress hostAddress;
    Selector selector;
    ServerSocketChannel serverSocketChannel;

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
        try {
            connectSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectSocket() throws Exception {
        hostAddress = InetAddress.getByName("localhost");
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false); // Non-blocking
        serverSocketChannel.bind(new InetSocketAddress(hostAddress, 8989));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        SelectionKey key;
        while (true) {
            if (selector.select() <= 0) continue;
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("Connection Accepted: " + socketChannel.getLocalAddress() + "\n");
                }
                if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer bb = ByteBuffer.allocate(1024);
                    socketChannel.read(bb);
                    String result = new String(bb.array()).trim();
                    System.out.println("Message received: " + result + " Message length= " + result.length());
                    if (result.length() <= 0) {
                        socketChannel.close();
                        System.out.println("Connection closed...");
                        System.out.println("Server will keep running. " + "Try running another client to " + "re-establish connection");
                    }
                }
            }
        }
    }
}
