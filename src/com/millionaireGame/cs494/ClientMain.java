package com.millionaireGame.cs494;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class ClientMain implements Runnable {
    JFrame frame;
    private JPanel panelMain;
    private JButton buttonSend;
    private JButton buttonDisconnect;
    private JTextArea textArea;

    InetSocketAddress addressSocket;
    Selector selector;
    SocketChannel channel;

    String actionStr;

    public ClientMain(JFrame f) {
        textArea.setText("");
        this.frame = f;
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionStr = "HIHIHIHIH";
                sendAndReceiveSocket();
            }
        });
        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (actionStr == "quit") {
                    actionStr = "";
                    try {
                        connectSocket();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                else {
                    actionStr = "quit";
                    sendAndReceiveSocket();
                }
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

        try {
            connectSocket();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void display(final String str) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append(str + "\u23CE\n");
            }
        });
    }

    public void sendAndReceiveSocket() {
        try {
            display(actionStr);
            if (selector.select() > 0) {
                Boolean doneStatus = processWaitSet(selector.selectedKeys());
                if (doneStatus) {
                    disconnectSocket();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void connectSocket() throws Exception {
        addressSocket = new InetSocketAddress(InetAddress.getByName("localhost"), 8989);
        selector = Selector.open();
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(addressSocket);
        channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        sendAndReceiveSocket();
    }

    public void disconnectSocket() throws IOException {
        channel.close();
    }

    public Boolean processWaitSet(Set waitSet) throws Exception {
        SelectionKey key = null;
        Iterator iterator = null;
        iterator = waitSet.iterator();
        while (iterator.hasNext()) {
            key = (SelectionKey) iterator.next();
            iterator.remove();
        }
        if (key.isConnectable()) {
            Boolean connected = processConnect(key);
            if (!connected) {
                return true;
            }
        }
        if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer bBuffer = ByteBuffer.allocate(1024);
            socketChannel.read(bBuffer);
            String result = new String(bBuffer.array()).trim();
            display("Message received from Server: " + result + " Message length= " + result.length());
        }
        if (key.isWritable()) {
            if (actionStr.equalsIgnoreCase("quit")) {
                return true;
            }
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer bBuffer = ByteBuffer.wrap(actionStr.getBytes());
            socketChannel.write(bBuffer);
        }
        return false;
    }
    public static Boolean processConnect(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            while (socketChannel.isConnectionPending()) {
                socketChannel.finishConnect();
            }
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
