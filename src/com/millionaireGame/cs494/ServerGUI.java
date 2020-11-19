package com.millionaireGame.cs494;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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
    private ImagePanel panelPlay;
    private JPanel cards;
    private CardLayout cardLayout;
    private JButton buttonStart = new JButton("  START GAME  ");
    private JLabel labelDb = new JLabel("Not loaded", SwingConstants.CENTER);
    private JComboBox questionSetsList = new JComboBox();
    private JComboBox modeList = new JComboBox();
    private JComboBox answerTimeList = new JComboBox();
    private JScrollPane scrollPane;
    private JList players;
    private JLabel labelSocketServer = new JLabel("Not connected", SwingConstants.CENTER);
    private JCheckBox checkBox = new JCheckBox();
    private JButton buttonReset = new JButton("Reset settings");
    private JButton buttonDisconnect = new JButton("Disconnect");

    private JPanel panelQuestionAnswers;
    private ImagePanel panelQuestion;
    private ImagePanel panelAnsA;
    private ImagePanel panelAnsB;
    private ImagePanel panelAnsC;
    private ImagePanel panelAnsD;
    private JTextPane labelQuestion = new JTextPane();
    private JButton labelAnsA = new JButton("");
    private JButton labelAnsB = new JButton("");
    private JButton labelAnsC = new JButton("");
    private JButton labelAnsD = new JButton("");
    private JLabel labelA = new JLabel("                       A:", SwingConstants.LEADING);
    private JLabel labelB = new JLabel("       B:", SwingConstants.LEADING);
    private JLabel labelC = new JLabel("                       C:", SwingConstants.LEADING);
    private JLabel labelD = new JLabel("       D:", SwingConstants.LEADING);
    private JPanel panelInformation = new JPanel(new BorderLayout());
    private JPanel panelNext = new JPanel(new FlowLayout(FlowLayout.LEADING, 10,10));
    private JLabel labelAnnounce = new JLabel("...", SwingConstants.CENTER);
    private JButton buttonNext = new JButton("Next >");
    private JButton buttonCheckAnswer = new JButton("Check answer");
    private JScrollPane scrollPanePlayersPlaying;
    private JList playersPlaying;

    File font_file = new File("fonts/BebasNeue-Regular.ttf");
    Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);

    public MessageToSend actionSendMessage;

    public String[] qSets = {"10"};
    public String[] modeSets = {"Normal"};
    public String[] timeSets = {"20 seconds"};
    public DefaultListModel playersName = new DefaultListModel();
    public DefaultListModel playersPlayingModel = new DefaultListModel();
    private String trueAnswer = "";

    public ServerGUI(MessageToSend closure) throws IOException, FontFormatException {
        this.actionSendMessage = closure;
        setupView();
    }

    private void setupView() throws IOException {
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        BufferedImage myImage = ImageIO.read(new File("resource/background_setup.png"));
        panelMain = new ImagePanel(myImage);
        panelMain.setLayout(new BorderLayout());
        frame.setContentPane(cards);
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
        final JLabel labelConnectDb = new JLabel("Database", SwingConstants.RIGHT);
        labelConnectDb.setFont(font.deriveFont(30f));
        labelConnectDb.setForeground(Color.white);
        navPanelSettings.add(labelConnectDb);
        labelDb.setFont(font.deriveFont(30f));
        labelDb.setBackground(Color.white);
        navPanelSettings.add(labelDb);
        final JLabel labelQuestionSet = new JLabel("Questions/Person:", SwingConstants.RIGHT);
        labelQuestionSet.setFont(font.deriveFont(30f));
        labelQuestionSet.setForeground(Color.white);
        navPanelSettings.add(labelQuestionSet);
        for (String qSet : qSets) {
            questionSetsList.addItem(qSet);
        }
        questionSetsList.setFont(font.deriveFont(18f));
        navPanelSettings.add(questionSetsList);
        final JLabel labelShuffle = new JLabel("Random:", SwingConstants.RIGHT);
        labelShuffle.setFont(font.deriveFont(30f));
        labelShuffle.setForeground(Color.white);
        navPanelSettings.add(labelShuffle);
        checkBox.setSelected(true);
        checkBox.setEnabled(false);
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
        labelPlayersConnected.setBorder(new EmptyBorder(10,20,10,20));
        navPanelPlayers.add(labelPlayersConnected, BorderLayout.PAGE_START);
        players = new JList(playersName);
        players.setFont(font.deriveFont(30f));
        players.setOpaque(false);
        players.setDragEnabled(false);
        players.setEnabled(false);
        players.setBorder(new EmptyBorder(0,10,0,10));
        scrollPane = new JScrollPane(players);
        scrollPane.setOpaque(false);
        navPanelPlayers.add(scrollPane, BorderLayout.CENTER);

        setupPanelPlay();
        cards.add(panelMain, "MAIN");
        cards.add(panelPlay, "PLAY");

        buttonDisconnect.setFont(font.deriveFont(24f));
        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(
                        frame,
                        "Do you want to disconnect server?\nThis will close all clients' connection that have " +
                                "connected.",
                        "Warning - Disconnect Server",
                        JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
                if(n == JOptionPane.OK_OPTION)
                {
                    actionSendMessage.mess(ActionType.DISS, "");
                    System.exit(0);
                }
            }
        });
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playersName.getSize() < 2) {
                    JOptionPane.showMessageDialog(frame, "You need 2 or more connected players to start.","Server - Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
                else {
                    actionSendMessage.mess(ActionType.STGM, "");
                    cardLayout.next(cards);
                }
            }
        });
    }

    private void setupPanelPlay() throws IOException {
        BufferedImage myImage = ImageIO.read(new File("resource/background_play.png"));
        panelPlay = new ImagePanel(myImage);
        panelQuestionAnswers = new JPanel(new GridBagLayout());
        panelQuestionAnswers.setOpaque(false);
        panelQuestion = new ImagePanel(ImageIO.read(new File("resource/background_question.png")));
        panelQuestion.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 960;
        gbc.weightx = 960;
        gbc.gridheight = 96;
        gbc.weighty = 96;
        gbc.insets = new Insets(0,0,0,0);
        panelQuestionAnswers.add(panelQuestion, gbc);
        panelAnsA = new ImagePanel(ImageIO.read(new File("resource/background_answer.png")));
        panelAnsA.setOpaque(false);
        gbc.gridy = 96;
        gbc.gridx = 0;
        gbc.gridheight = 57;
        gbc.weighty = 57;
        gbc.gridwidth = 480;
        gbc.weightx = 480;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,0,0,0);
        panelQuestionAnswers.add(panelAnsA, gbc);
        panelAnsB = new ImagePanel(ImageIO.read(new File("resource/background_answer_right.png")));
        panelAnsB.setOpaque(false);
        gbc.gridy = 96;
        gbc.gridx = 480;
        gbc.gridheight = 57;
        gbc.weighty = 57;
        gbc.gridwidth = 480;
        gbc.weightx = 480;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,0,0,0);
        panelQuestionAnswers.add(panelAnsB, gbc);
        panelAnsC = new ImagePanel(ImageIO.read(new File("resource/background_answer.png")));
        panelAnsC.setOpaque(false);
        gbc.gridy = 153;
        gbc.gridx = 0;
        gbc.gridheight = 57;
        gbc.weighty = 57;
        gbc.gridwidth = 480;
        gbc.weightx = 480;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0,0,0,0);
        panelQuestionAnswers.add(panelAnsC, gbc);
        panelAnsD = new ImagePanel(ImageIO.read(new File("resource/background_answer_right.png")));
        panelAnsD.setOpaque(false);
        gbc.gridy = 153;
        gbc.gridx = 480;
        gbc.gridheight = 57;
        gbc.weighty = 57;
        gbc.gridwidth = 480;
        gbc.weightx = 480;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0,0,0,0);
        panelQuestionAnswers.add(panelAnsD, gbc);
        panelPlay.add(panelQuestionAnswers);

        labelQuestion.setForeground(Color.white);
        labelQuestion.setText("");
        labelQuestion.setBorder(new EmptyBorder(8,142,8,142));
        labelQuestion.setOpaque(false);
        labelQuestion.setEditable(false);
        labelQuestion.setHighlighter(null);
        labelQuestion.setPreferredSize(new Dimension(960, 96));
        labelQuestion.setMaximumSize(new Dimension(960, 96));
        labelQuestion.setMinimumSize(new Dimension(960, 96));
        StyledDocument doc = labelQuestion.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(center, 24);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        panelQuestion.add(labelQuestion);

        final Dimension ansDimen = new Dimension(480, 57);
        panelAnsA.setLayout(new BorderLayout());
        panelAnsA.setPreferredSize(ansDimen);
        panelAnsA.setMaximumSize(ansDimen);
        panelAnsA.setMinimumSize(ansDimen);
        labelA.setFont(font.deriveFont(36f));
        labelA.setForeground(Color.decode("#FFE600"));
        labelAnsA.setFont(font.deriveFont(28f));
        labelAnsA.setForeground(Color.white);
        labelAnsA.setOpaque(false);
        labelAnsA.setBorder(null);
        labelAnsA.setBorderPainted(false);
        labelAnsA.setContentAreaFilled(false);
        labelAnsA.setHorizontalAlignment(SwingConstants.LEFT);
        panelAnsA.add(labelA, BorderLayout.LINE_START);
        panelAnsA.add(labelAnsA, BorderLayout.CENTER);

        panelAnsB.setLayout(new BorderLayout());
        panelAnsB.setPreferredSize(ansDimen);
        panelAnsB.setMaximumSize(ansDimen);
        panelAnsB.setMinimumSize(ansDimen);
        labelB.setFont(font.deriveFont(36f));
        labelB.setForeground(Color.decode("#FFE600"));
        labelAnsB.setFont(font.deriveFont(28f));
        labelAnsB.setForeground(Color.white);
        labelAnsB.setOpaque(false);
        labelAnsB.setBorder(null);
        labelAnsB.setBorderPainted(false);
        labelAnsB.setContentAreaFilled(false);
        labelAnsB.setHorizontalAlignment(SwingConstants.LEFT);
        panelAnsB.add(labelB, BorderLayout.LINE_START);
        panelAnsB.add(labelAnsB, BorderLayout.CENTER);

        panelAnsC.setLayout(new BorderLayout());
        panelAnsC.setPreferredSize(ansDimen);
        panelAnsC.setMaximumSize(ansDimen);
        panelAnsC.setMinimumSize(ansDimen);
        labelC.setFont(font.deriveFont(36f));
        labelC.setForeground(Color.decode("#FFE600"));
        labelAnsC.setFont(font.deriveFont(28f));
        labelAnsC.setForeground(Color.white);
        labelAnsC.setOpaque(false);
        labelAnsC.setBorder(null);
        labelAnsC.setBorderPainted(false);
        labelAnsC.setContentAreaFilled(false);
        labelAnsC.setHorizontalAlignment(SwingConstants.LEFT);
        panelAnsC.add(labelC, BorderLayout.LINE_START);
        panelAnsC.add(labelAnsC, BorderLayout.CENTER);

        panelAnsD.setLayout(new BorderLayout());
        panelAnsD.setPreferredSize(ansDimen);
        panelAnsD.setMaximumSize(ansDimen);
        panelAnsD.setMinimumSize(ansDimen);
        labelD.setFont(font.deriveFont(36f));
        labelD.setForeground(Color.decode("#FFE600"));
        labelAnsD.setFont(font.deriveFont(28f));
        labelAnsD.setForeground(Color.white);
        labelAnsD.setOpaque(false);
        labelAnsD.setBorder(null);
        labelAnsD.setBorderPainted(false);
        labelAnsD.setContentAreaFilled(false);
        labelAnsD.setHorizontalAlignment(SwingConstants.LEFT);
        panelAnsD.add(labelD, BorderLayout.LINE_START);
        panelAnsD.add(labelAnsD, BorderLayout.CENTER);

        labelAnnounce.setFont(font.deriveFont(32f));
        labelAnnounce.setForeground(Color.white);
        labelAnnounce.setText("Press next to process to next question!");
        panelInformation.setOpaque(false);
        panelInformation.add(panelNext, BorderLayout.PAGE_START);
        buttonNext.setFont(font.deriveFont(28f));
        buttonCheckAnswer.setFont(font.deriveFont(28f));
        panelNext.setOpaque(false);
        panelNext.add(buttonCheckAnswer);
        panelNext.add(buttonNext);
        panelNext.add(labelAnnounce);

        playersPlaying = new JList(playersPlayingModel);
        playersPlaying.setFont(font.deriveFont(30f));
        playersPlaying.setOpaque(false);
        playersPlaying.setDragEnabled(false);
        playersPlaying.setEnabled(false);
        playersPlaying.setBorder(new EmptyBorder(0,10,0,10));
        scrollPanePlayersPlaying = new JScrollPane(playersPlaying);
        scrollPanePlayersPlaying.setOpaque(false);
        panelInformation.add(scrollPanePlayersPlaying);
        panelPlay.add(panelInformation);

        buttonNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actionSendMessage.mess(ActionType.NXQT, "");
                    buttonNext.setEnabled(false);
                    resetAnswerChoose();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        buttonCheckAnswer.setEnabled(false);
        buttonCheckAnswer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (trueAnswer) {
                    case "A":
                        try {
                            panelAnsA.setImage(ImageIO.read(new File("resource/background_answer_correct.png")));
                            buttonNext.setEnabled(true);
                            buttonCheckAnswer.setEnabled(false);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                    case "B":
                        try {
                            panelAnsB.setImage(ImageIO.read(new File("resource/background_answer_right_correct.png")));
                            buttonNext.setEnabled(true);
                            buttonCheckAnswer.setEnabled(false);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                    case "C":
                        try {
                            panelAnsC.setImage(ImageIO.read(new File("resource/background_answer_correct.png")));
                            buttonNext.setEnabled(true);
                            buttonCheckAnswer.setEnabled(false);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                    case "D":
                        try {
                            panelAnsD.setImage(ImageIO.read(new File("resource/background_answer_right_correct.png")));
                            buttonNext.setEnabled(true);
                            buttonCheckAnswer.setEnabled(false);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                }
                actionSendMessage.mess(ActionType.TANS, trueAnswer);
            }
        });
    }

    private void resetAnswerChoose() throws IOException {
        panelAnsA.setImage(ImageIO.read(new File("resource/background_answer.png")));
        panelAnsB.setImage(ImageIO.read(new File("resource/background_answer_right.png")));
        panelAnsC.setImage(ImageIO.read(new File("resource/background_answer.png")));
        panelAnsD.setImage(ImageIO.read(new File("resource/background_answer_right.png")));
    }

    public void display(final ActionType type,final String mess) {
        switch (type) {
            case CONN:
                labelSocketServer.setText(mess);
                labelSocketServer.setForeground(new Color(0, 255, 71));
                break;
            case NAME:
                break;
            case ERRO:
                JOptionPane.showMessageDialog(frame, "An error occurred.\n" + mess + "\nPlease close and reopen this " +
                                "program.","Server - Warning",
                        JOptionPane.WARNING_MESSAGE);
                buttonStart.setVisible(false);
                break;
            case DBCN:
                labelDb.setText(mess);
                labelDb.setForeground(new Color(0, 255, 71));
                break;
            case QUES:
                labelQuestion.setText(mess);
                break;
            case ANSA:
                labelAnsA.setText(mess);
                break;
            case ANSB:
                labelAnsB.setText(mess);
                break;
            case ANSC:
                labelAnsC.setText(mess);
                break;
            case ANSD:
                labelAnsD.setText(mess);
                break;
            case ALAN:
                labelAnnounce.setText("It's client " + mess + " turn.");
                break;
            case TANS:
                trueAnswer = mess;
                break;
            case CLAN:
                buttonCheckAnswer.setEnabled(true);
                switch (mess) {
                    case "A":
                        try {
                            panelAnsA.setImage(ImageIO.read(new File("resource/background_answer_chosen.png")));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                    case "B":
                        try {
                            panelAnsB.setImage(ImageIO.read(new File("resource/background_answer_right_chosen.png")));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                    case "C":
                        try {
                            panelAnsC.setImage(ImageIO.read(new File("resource/background_answer_chosen.png")));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                    case "D":
                        try {
                            panelAnsD.setImage(ImageIO.read(new File("resource/background_answer_right_chosen.png")));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                }
                break;
            case FINI:
                labelAnnounce.setText(mess);
                break;
        }
    }

    @Override
    public void run() {
        frame.setVisible(true);
    }
}
