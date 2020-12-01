package com.millionaireGame.cs494;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

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
    private JLabel labelAnnounce = new JLabel("...", SwingConstants.CENTER);
    private JScrollPane scrollPane;
    private JLabel labelTime = new JLabel("--", SwingConstants.CENTER);
    private JProgressBar progressBar;
    private JButton buttonSkip = new JButton("Skip this question");
    private static boolean isSkipped = false;

    InputStream brFont = getClass().getResourceAsStream("fonts/BebasNeue-Regular.ttf");
    Font font;

    public MessageToSend actionSendMessage;
    public boolean isNotChooseAnswer = false;

    public ClientGUI(MessageToSend closure) throws IOException, FontFormatException {
        font = Font.createFont(Font.TRUETYPE_FONT, brFont);
        this.actionSendMessage = closure;
        setupView();
    }

    private void setupView() throws IOException {
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        BufferedImage myImage = ImageIO.read(getClass().getResourceAsStream("resource/background_setup.png"));
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
        BufferedImage myImage = ImageIO.read(getClass().getResourceAsStream("resource/background_play.png"));
        textArea.setOpaque(false);
        textArea.setFont(font.deriveFont(24f));
        textArea.setForeground(Color.white);
        textArea.setText("");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setVisible(true);
        panelPlay = new ImagePanel(myImage);
        panelPlay.setLayout(new GridLayout(2,1,0,0));
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        panelQuestionAnswers = new JPanel(new GridBagLayout());
        panelQuestionAnswers.setOpaque(false);
        panelQuestion = new ImagePanel(ImageIO.read(getClass().getResourceAsStream("resource/background_question.png")));
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
        panelAnsA = new ImagePanel(ImageIO.read(getClass().getResourceAsStream("resource/background_answer.png")));
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
        panelAnsB = new ImagePanel(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_right.png")));
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
        panelAnsC = new ImagePanel(ImageIO.read(getClass().getResourceAsStream("resource/background_answer.png")));
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
        panelAnsD = new ImagePanel(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_right.png")));
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
        panelPlay.add(panelInformation);
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
        labelAnnounce.setText("Waiting for host...");
        panelInformation.setOpaque(false);
        panelInformation.add(labelAnnounce, BorderLayout.PAGE_END);

        scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setOpaque(false);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.setBorder(new EmptyBorder(10,10,10,10));
        scrollPane.setPreferredSize(new Dimension(300,120));
        labelTime.setFont(font.deriveFont(110f));
        labelTime.setForeground(Color.white);
        labelTime.setBorder(new EmptyBorder(10,60,10,10));
        labelTime.setPreferredSize(new Dimension(240, labelTime.getHeight()));
        panelInformation.add(labelTime, BorderLayout.LINE_START);
        final JPanel panelSkip = new JPanel(new BorderLayout());
        buttonSkip.setFont(font.deriveFont(32f));
        panelSkip.setOpaque(false);
        panelSkip.add(buttonSkip, BorderLayout.PAGE_END);
//        panelSkip.setPreferredSize(new Dimension(120, panelSkip.getHeight()));
        panelSkip.add(scrollPane, BorderLayout.CENTER);
        panelInformation.add(panelSkip, BorderLayout.LINE_END);
        progressBar = new JProgressBar(0, 20);
        UIManager.put("ProgressBar.foreground", Color.WHITE);
        labelAnsA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isNotChooseAnswer) {
                    isNotChooseAnswer = false;
                    try {
                        panelAnsA.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_chosen.png")));
                        actionSendMessage.mess(ActionType.CLAN, "A");
                        buttonSkip.setEnabled(false);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        labelAnsB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isNotChooseAnswer) {
                    isNotChooseAnswer = false;
                    try {
                        panelAnsB.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_right_chosen.png")));
                        actionSendMessage.mess(ActionType.CLAN, "B");
                        buttonSkip.setEnabled(false);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        labelAnsC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isNotChooseAnswer) {
                    isNotChooseAnswer = false;
                    try {
                        panelAnsC.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_chosen.png")));
                        actionSendMessage.mess(ActionType.CLAN, "C");
                        buttonSkip.setEnabled(false);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        labelAnsD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isNotChooseAnswer) {
                    isNotChooseAnswer = false;
                    try {
                        panelAnsD.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_right_chosen.png")));
                        actionSendMessage.mess(ActionType.CLAN, "D");
                        buttonSkip.setEnabled(false);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        buttonSkip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSendMessage.mess(ActionType.SKIP, "");
                labelAnnounce.setText("You have skipped this question");
                buttonSkip.setEnabled(false);
                isSkipped = true;
            }
        });
    }

    private void resetAnswerChoose() throws IOException {
        panelAnsA.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer.png")));
        panelAnsB.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_right.png")));
        panelAnsC.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer.png")));
        panelAnsD.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_right.png")));
        labelTime.setText("--");
    }

    private void correctAnswer(String trueAns) throws IOException {
        panelInformation.remove(progressBar);
        panelInformation.add(labelAnnounce, BorderLayout.PAGE_END);
        buttonSkip.setEnabled(false);
        buttonSkip.setVisible(false);
        switch (trueAns) {
            case "A":
                panelAnsA.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_correct.png")));
                break;
            case "B":
                panelAnsB.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_right_correct.png")));
                break;
            case "C":
                panelAnsC.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_correct.png")));
                break;
            case "D":
                panelAnsD.setImage(ImageIO.read(getClass().getResourceAsStream("resource/background_answer_right_correct.png")));
                break;
        }
    }

    @Override
    public void run() {
        frame.setVisible(true);
    }

    public void display(final ActionType type, final String s) {
        if (type == ActionType.ERRO) {
            tf.setText(s);
            tf.setForeground(Color.red);
            tf.setBorder(BorderFactory.createLineBorder(Color.red, 6));
            tf.setEditable(false);
            buttonName.setEnabled(false);
            textArea.append("Error: " + s + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
        else if (type == ActionType.MESG) {
            textArea.append(s + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
        else if (type == ActionType.STGM) {
            textArea.setText("Activities:\n" + "Game has started.\n");
            labelAnnounce.setForeground(Color.white);
            labelAnnounce.setText("Waiting for host...");
            cardLayout.next(cards);
            buttonSkip.setEnabled(false);
            buttonSkip.setVisible(false);
            isSkipped = false;
        }
        else if (type == ActionType.QUES) {
            try {
                resetAnswerChoose();
            } catch (IOException e) {
                e.printStackTrace();
            }
            labelQuestion.setText(s);
        }
        else if (type == ActionType.ANSA) {
            labelAnsA.setText(s);
        }
        else if (type == ActionType.ANSB) {
            labelAnsB.setText(s);
        }
        else if (type == ActionType.ANSC) {
            labelAnsC.setText(s);
        }
        else if (type == ActionType.ANSD) {
            labelAnsD.setText(s);
        }
        else if (type == ActionType.ALAN) {
            isNotChooseAnswer = true;
            panelInformation.remove(labelAnnounce);
            panelInformation.add(progressBar, BorderLayout.PAGE_END);
            progressBar.setMinimum(0);
            progressBar.setMaximum(20);
            progressBar.setValue(20);
            textArea.append(s + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
            if (!isSkipped) {
                buttonSkip.setEnabled(true);
                buttonSkip.setVisible(true);
            }
            else {
                buttonSkip.setEnabled(false);
                buttonSkip.setVisible(false);
            }
        }
        else if (type == ActionType.TANS) {
            try {
                correctAnswer(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (type == ActionType.CORR) {
            labelAnnounce.setText(s);
            textArea.append(s + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
        else if (type == ActionType.FINI || type == ActionType.LOST) {
            labelAnnounce.setText(s);
            labelAnnounce.setForeground(type == ActionType.FINI ? Color.GREEN : Color.red);
            textArea.append(s + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
        else if (type == ActionType.TIME) {
            labelTime.setText(s);
//            labelAnnounce.setText("You have " + s + " second" + (Integer.parseInt(s) > 1 ? "s" : "") + " left!");
            progressBar.setValue(Integer.parseInt(s));
        }
        else if (type == ActionType.TIOU) {
            isNotChooseAnswer = false;
            labelTime.setText("--");
            labelAnnounce.setText("");
            panelInformation.remove(progressBar);
            panelInformation.add(labelAnnounce, BorderLayout.PAGE_END);
            textArea.append(s + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
            buttonSkip.setEnabled(false);
            buttonSkip.setVisible(false);
        }
        else if (type == ActionType.BACK) {
            cardLayout.previous(cards);
        }
    }

    public void actionTappedButton() {
        String s = tf.getText();
        if (s.equals("")) {
            JOptionPane.showMessageDialog(frame, "Name must not be empty!","Client - Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        else {
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
            gbc.insets = new Insets(24, 0, 0, 0);
            panelMain.add(buttonDisconnect, gbc);
            panelMain.revalidate();
            panelMain.repaint();
        }
    }
}
