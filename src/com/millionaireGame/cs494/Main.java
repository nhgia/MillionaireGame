package com.millionaireGame.cs494;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private JPanel panelMain;
    private JButton myButton;
    private JLabel myLabel;

    public Main() {
        myButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLabel.setText("Tapped");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new Main().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800,450));
        frame.setLocation(200,200);
        frame.pack();
        frame.setVisible(true);
    }
}
