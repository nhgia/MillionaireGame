package com.millionaireGame.cs494;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ClientGUI implements Runnable{
    public final JFrame frame = new JFrame("Millionaire - Client | Connecting...");
    private ImagePanel panelMain;
    private JButton buttonSend = new JButton("Send");
    private JButton buttonDisconnect = new JButton("Disconnect");
    private JTextArea textArea = new JTextArea();
    private JTextField tf = new JTextField();
    private JLabel labelEnterName = new JLabel("Please enter your name");
    private JLabel labelName = new JLabel("");

    File font_file = new File("fonts/BebasNeue-Regular.ttf");
    Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);

    public MessageToSend actionSendMessage;

    public ClientGUI(MessageToSend closure) throws IOException, FontFormatException{
        this.actionSendMessage = closure;
        setupView();
    }

    private void setupView() throws IOException {
        BufferedImage myImage = ImageIO.read(new File("resource/background_setup.png"));
        panelMain = new ImagePanel(myImage);
        panelMain.setLayout(new GridLayout(2,1));
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Dimension dimen = new Dimension(960, 540);
        frame.setLocation(300, 300);
        frame.setPreferredSize(dimen);
        frame.setMinimumSize(dimen);
        frame.setResizable(false);
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                tf.requestFocus();
            }
        });
        labelEnterName.setFont(font.deriveFont(36f));
        labelEnterName.setForeground(Color.white);
        tf.setOpaque(false);
        tf.setBackground(new Color(255, 255, 255, 0));
        tf.setForeground(Color.white);
        tf.setFont(font.deriveFont(48f));
        tf.setHorizontalAlignment(JTextField.CENTER);
        panelMain.add(labelEnterName);
        panelMain.add(tf);

        textArea.setText("");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
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

    @Override
    public void run() {
        frame.setVisible(true);
    }

    public void display(final String s) {
        textArea.append(s + "\n");
    }

    public void actionTappedButton() {
        String s = tf.getText();
        actionSendMessage.mess(s);
        display(s);
        tf.setText("");
    }
}
