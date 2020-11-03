package com.millionaireGame.cs494;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    ServerMain server;
    ClientMain client;

    public Main() {
        JFrame sFrame = new JFrame("Millionaire - Server");
        server = new ServerMain(sFrame);
        Thread sThread = new Thread(server);
        sThread.start();

        JFrame cFrame = new JFrame("Millionaire - Client");
        client = new ClientMain(cFrame);
        Thread cThread = new Thread(client);
        cThread.start();
    }

    public static void main(String[] args) {
        Main myMain = new Main();
    }
}
