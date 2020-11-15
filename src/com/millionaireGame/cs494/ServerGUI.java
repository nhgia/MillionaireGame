package com.millionaireGame.cs494;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

interface MessageToSend {
    void mess(ActionType type, String s);
}

public class ServerGUI implements Runnable {
    private final JFrame frame = new JFrame("Millionaire - Server");
    private ImagePanel panelMain;
    private JButton buttonSend = new JButton("Send");

    private JButton buttonStart = new JButton("  START GAME  ");
    private JButton buttonConnectDb = new JButton("Connect");
    private JComboBox questionSetsList = new JComboBox();
    private JComboBox modeList = new JComboBox();
    private JComboBox answerTimeList = new JComboBox();
    private JScrollPane scrollPane;
    private JTextArea textArea = new JTextArea();
    private JTextField textField = new JTextField();
    private JLabel labelSocketServer = new JLabel("Not connected", SwingConstants.CENTER);
    private JCheckBox checkBox = new JCheckBox();
    private JButton buttonReset = new JButton("Reset settings");
    private JButton buttonDisconnect = new JButton("Disconnect");

    File font_file = new File("fonts/BebasNeue-Regular.ttf");
    Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);

    public MessageToSend actionSendMessage;

    public String[] qSets = {"Set #100-299", "Set #200-399"};
    public String[] modeSets = {"Battle-royale", "Lightning"};
    public String[] timeSets = {"10 seconds", "20 seconds", "30 seconds"};

    public ServerGUI(MessageToSend closure) throws IOException, FontFormatException {
        this.actionSendMessage = closure;
        setupView();
    }

    private void setupView() throws IOException {

        BufferedImage myImage = ImageIO.read(new File("resource/background_setup.png"));
        panelMain = new ImagePanel(myImage);
        panelMain.setLayout(new BorderLayout());
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dimen = new Dimension(960, 540);
        frame.setPreferredSize(dimen);
        frame.setMinimumSize(dimen);
        frame.setResizable(false);
        frame.setLocation(200, 200);
        frame.pack();

        final JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(10,20,8,20));
        final JLabel titleLabel = new JLabel("Game settings");
        titleLabel.setFont(font.deriveFont(48f));
        titleLabel.setForeground(Color.white);
        navPanel.add(titleLabel, BorderLayout.LINE_START);

        buttonStart.setFont(font.deriveFont(36f));
        buttonStart.setBackground(Color.white);
        navPanel.add(buttonStart,BorderLayout.LINE_END);
        panelMain.add(navPanel, BorderLayout.PAGE_START);

        final JPanel navPanelContent = new JPanel(new GridLayout(1, 2, 32, 0));
        final JPanel navPanelSettings = new JPanel(new GridLayout(7, 2, 22, 10));
        final JPanel navPanelPlayers = new JPanel(new BorderLayout());
        navPanelContent.setOpaque(false);
        navPanelContent.setBorder(new EmptyBorder(0,20,20,20));
        panelMain.add(navPanelContent, BorderLayout.CENTER);
        navPanelSettings.setOpaque(false);
        navPanelPlayers.setOpaque(false);
        navPanelContent.add(navPanelSettings);
        navPanelContent.add(navPanelPlayers);


        //Left: Settings
        final JLabel labelConnectSocket = new JLabel("Connect to socket", SwingConstants.RIGHT);
        labelConnectSocket.setFont(font.deriveFont(30f));
        labelConnectSocket.setForeground(Color.white);
        navPanelSettings.add(labelConnectSocket);
        labelSocketServer.setFont(font.deriveFont(30f));
        labelSocketServer.setForeground(Color.white);
        navPanelSettings.add(labelSocketServer);
        final JLabel labelConnectDb = new JLabel("Connect to database", SwingConstants.RIGHT);
        labelConnectDb.setFont(font.deriveFont(30f));
        labelConnectDb.setForeground(Color.white);
        navPanelSettings.add(labelConnectDb);
        buttonConnectDb.setFont(font.deriveFont(30f));
        buttonConnectDb.setBackground(Color.white);
        navPanelSettings.add(buttonConnectDb);
        final JLabel labelQuestionSet = new JLabel("Question set:", SwingConstants.RIGHT);
        labelQuestionSet.setFont(font.deriveFont(30f));
        labelQuestionSet.setForeground(Color.white);
        navPanelSettings.add(labelQuestionSet);
        for (String qSet : qSets) {
            questionSetsList.addItem(qSet);
        }
        questionSetsList.setFont(font.deriveFont(18f));
        navPanelSettings.add(questionSetsList);
        final JLabel labelShuffle = new JLabel("Shufle:", SwingConstants.RIGHT);
        labelShuffle.setFont(font.deriveFont(30f));
        labelShuffle.setForeground(Color.white);
        navPanelSettings.add(labelShuffle);
        navPanelSettings.add(checkBox);
        final JLabel labelMode = new JLabel("Mode:", SwingConstants.RIGHT);
        labelMode.setFont(font.deriveFont(30f));
        labelMode.setForeground(Color.white);
        navPanelSettings.add(labelMode);
        for (String mSet : modeSets) {
            modeList.addItem(mSet);
        }
        modeList.setFont(font.deriveFont(18f));
        navPanelSettings.add(modeList);
        final JLabel labelTime = new JLabel("Answer time:", SwingConstants.RIGHT);
        labelTime.setFont(font.deriveFont(30f));
        labelTime.setForeground(Color.white);
        navPanelSettings.add(labelTime);
        for (String tSet : timeSets) {
            answerTimeList.addItem(tSet);
        }
        answerTimeList.setFont(font.deriveFont(18f));
        navPanelSettings.add(answerTimeList);
        buttonDisconnect.setFont(font.deriveFont(24f));
        buttonReset.setFont(font.deriveFont(24f));
        navPanelSettings.add(buttonReset);
        navPanelSettings.add(buttonDisconnect);

        //Right: Players
        final JLabel labelPlayersConnected = new JLabel("Players connected:");
        labelPlayersConnected.setFont(font.deriveFont(30f));
        labelPlayersConnected.setForeground(Color.white);
        navPanelPlayers.add(labelPlayersConnected, BorderLayout.PAGE_START);
        textArea.setOpaque(false);
        textArea.setText("");
        textArea.setForeground(Color.black);
        textArea.setFont(font.deriveFont(32f));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setHighlighter(null);
        textArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        navPanelPlayers.add(scrollPane, BorderLayout.CENTER);

        buttonSend.setFont(font.deriveFont(24f));
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionTappedButton();
            }
        });
        buttonDisconnect.setFont(font.deriveFont(24f));
        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        buttonConnectDb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public void actionTappedButton() {
        String myString = textField.getText();
        actionSendMessage.mess(ActionType.MESG, myString);
    }

    public void display(final ActionType type,final String mess) {
        if (type == ActionType.CONN) {
            labelSocketServer.setText(mess);
            labelSocketServer.setForeground(new Color(0, 255, 71));
        }
        textArea.append(mess + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    @Override
    public void run() {
        frame.setVisible(true);
    }
}
