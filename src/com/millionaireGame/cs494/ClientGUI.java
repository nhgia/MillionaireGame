package com.millionaireGame.cs494;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ClientGUI implements Runnable{
    public final JFrame frame = new JFrame("Millionaire - Client | Connecting...");
    private JPanel cards;
    private CardLayout cardLayout;
    private ImagePanel panelMain;
    private JButton buttonName = new JButton("Connect");
    private JButton buttonDisconnect = new JButton("Disconnect & close");
    private JTextField tf = new JTextField();
    private JLabel labelEnterName = new JLabel("Please enter your name");

    private ImagePanel panelPlay;
    private JTextArea textArea = new JTextArea();

    File font_file = new File("fonts/BebasNeue-Regular.ttf");
    Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);

    public MessageToSend actionSendMessage;

    public ClientGUI(MessageToSend closure) throws IOException, FontFormatException {
        this.actionSendMessage = closure;
        setupView();
    }

    private void setupView() throws IOException {
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        BufferedImage myImage = ImageIO.read(new File("resource/background_setup.png"));
        panelMain = new ImagePanel(myImage);
        panelMain.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        frame.setContentPane(cards);
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
        tf.setText("");
        tf.setOpaque(false);
        tf.setBackground(new Color(255, 255, 255, 0));
        tf.setForeground(Color.white);
        tf.setFont(font.deriveFont(48f));
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setBorder(BorderFactory.createLineBorder(Color.white, 6));
        tf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (tf.getText().length() >= 10) {
                    e.consume();
                    return;
                };
                char c = e.getKeyChar();
                if (!Character.isLetterOrDigit(c) && c != '_') e.consume();
            }
            @Override
            public void keyPressed(KeyEvent e) {

            }
            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        buttonName.setFont(font.deriveFont(36f));
        buttonDisconnect.setFont(font.deriveFont(36f));
        gbc.gridy = 0;
        panelMain.add(labelEnterName, gbc);
        gbc.gridy = 1;
        gbc.ipady = 20;
        gbc.ipadx = 640;
        panelMain.add(tf, gbc);
        gbc.gridy = 4;
        gbc.ipady = 0;
        gbc.ipadx = 0;
        gbc.insets = new Insets(24,0,0,0);
        panelMain.add(buttonName, gbc);
        setupPanelPlay();

        cards.add(panelMain, "MAIN");
        cards.add(panelPlay, "PLAY");
        buttonName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionTappedButton();
            }
        });
        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void setupPanelPlay() throws IOException {
        BufferedImage myImage = ImageIO.read(new File("resource/background_play.png"));
        textArea.setOpaque(false);
        textArea.setFont(font.deriveFont(36f));
        textArea.setForeground(Color.white);
        textArea.setText("");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        panelPlay = new ImagePanel(myImage);
        panelPlay.setLayout(new BorderLayout());
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        panelPlay.add(textArea, BorderLayout.CENTER);
    }

    @Override
    public void run() {
        frame.setVisible(true);
    }

    public void display(final ActionType type, final String s) {
        textArea.append(type.toString() + " " + s + "\n");
        if (type == ActionType.ERRO) {
            tf.setText(s);
            tf.setForeground(Color.red);
            tf.setBorder(BorderFactory.createLineBorder(Color.red, 6));
            tf.setEditable(false);
            buttonName.setEnabled(false);
        }
        else if (type == ActionType.STGM) {
            textArea.setText(type.toString() + " " + s + "\n");
            cardLayout.next(cards);
        }
    }

    public void actionTappedButton() {
        String s = tf.getText();
        actionSendMessage.mess(ActionType.NAME, s);
        display(ActionType.NAME, s);
        labelEnterName.setText("Welcome, " + s + "!");
        tf.setText("Waiting for host to start the game");
        tf.setForeground(Color.decode("#00FF28"));
        tf.setBorder(BorderFactory.createLineBorder(Color.decode("#00FF28"), 6));
        tf.setEditable(false);
        panelMain.remove(buttonName);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridy = 4;
        gbc.ipady = 0;
        gbc.ipadx = 0;
        gbc.insets = new Insets(24,0,0,0);
        panelMain.add(buttonDisconnect, gbc);
        panelMain.revalidate();
        panelMain.repaint();
    }
}
