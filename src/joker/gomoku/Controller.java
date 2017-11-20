package joker.gomoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

import javax.swing.*;

public class Controller {

    int color = Node.BLACK;
    boolean boardLocked = false;
    boolean waiting = true;
    boolean gameStarted = false;
    boolean isPlaying = false;

    Socket gameSocket;
    Socket chatSocket;
    DataInputStream fromServer;
    DataOutputStream toServer;
    InputStreamReader chatIn;
    OutputStreamWriter chatOut;

    JFrame startFrame;
    JFrame gameFrame;

    JPanel background;
    JPanel buttonPanel;
    BoardPanel board;

    JTextArea chatText;
    JTextArea infoText;
    JTextField chatBox;

    Node nodeToSend;

    public static int LOCAL_MODE = 0;
    public static int ONLINE_MODE = 1;
    public static String HOST = "lovehelodie.com";
    public static int GAME_PORT = 55535;
    public static int CHAT_PORT = 55536;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Controller controller = new Controller();
        controller.initStarter();
    }

    void initStarter(){
        startFrame = new JFrame("Choose Mode");
        JPanel background = new JPanel(null);
        background.setBackground(Color.DARK_GRAY);

        JButton startLocalBtn = new JButton("本地对战");
        startLocalBtn.addActionListener(new LocalButtonListener());
        startLocalBtn.setBounds(50, 80, 100, 40);
        startLocalBtn.setBackground(Color.LIGHT_GRAY);
        background.add(startLocalBtn);


        JButton startOnlineBtn = new JButton("联机对战");
        startOnlineBtn.addActionListener(new OnlineButtonListener());
        startOnlineBtn.setBounds(50, 180, 100, 40);
        startOnlineBtn.setBackground(Color.LIGHT_GRAY);
        background.add(startOnlineBtn);

        startFrame.add(background);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setSize(200, 300);
        startFrame.setResizable(false);
        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);
    }

    void initGameFrame(int mode){
        gameFrame = new JFrame("Gomoku");
        background = new JPanel(null);
        background.setBackground(Color.DARK_GRAY);

        board = new BoardPanel();
        board.setBounds(0, 0, 641, 641);

        buttonPanel = new JPanel(null);
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.setBounds(641, 0, 150, 641);

        background.add(board);
        background.add(buttonPanel);

        if(mode == LOCAL_MODE){
            JButton reverseButton = new JButton("悔棋");
            reverseButton.addActionListener(new ReverseButtonListener());
            reverseButton.setBounds(25, 30, 100, 40);
            reverseButton.setBackground(Color.LIGHT_GRAY);

            JButton replayButton = new JButton("重开一局");
            replayButton.addActionListener(new ReplayButtonListener());
            replayButton.setBounds(25, 100, 100, 40);
            replayButton.setBackground(Color.LIGHT_GRAY);

            JButton returnButton = new JButton("返回");
            returnButton.addActionListener(new ReturnButtonListener());
            returnButton.setBounds(25, 170, 100, 40);
            returnButton.setBackground(Color.LIGHT_GRAY);

            JButton exitButton = new JButton("退出");
            exitButton.addActionListener(new ExitButtonListener());
            exitButton.setBounds(25, 240, 100, 40);
            exitButton.setBackground(Color.LIGHT_GRAY);

            buttonPanel.add(reverseButton);
            buttonPanel.add(replayButton);
            buttonPanel.add(returnButton);
            buttonPanel.add(exitButton);
        }else{
            chatText = new JTextArea(2, 20);
            chatText.setLineWrap(true);
            chatText.setEditable(false);
            chatText.setBackground(Color.DARK_GRAY);
//			chatText.setForeground(new Color(255,192,203));
            chatText.setForeground(Color.WHITE);
            chatText.setFont(new Font("Default", Font.PLAIN, 17));

            JScrollPane scroll = new JScrollPane(chatText);
            scroll.setBackground(Color.DARK_GRAY);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setBounds(0, 641, 791, 120);

            infoText = new JTextArea(10,10);
            infoText.setLineWrap(true);
            infoText.setEditable(false);
            infoText.setBackground(Color.DARK_GRAY);
            infoText.setForeground(Color.WHITE);
            infoText.setFont(new Font("Default", Font.PLAIN, 12));

            JScrollPane scroll2 = new JScrollPane(infoText);
            scroll2.setBackground(Color.DARK_GRAY);
            scroll2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            scroll2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroll2.setBounds(0, 341, 150, 300);

            chatBox = new JTextField();
            chatBox.setFont(new Font("Default", Font.PLAIN, 15));
            chatBox.setBounds(0, 761, 691, 25);
            chatBox.setFocusable(true);
            chatBox.setEditable(false);

            background.add(scroll);
            buttonPanel.add(scroll2);
            background.add(chatBox);

            JButton sendBtn = new JButton("发送");
            sendBtn.addActionListener(new SendButtonListener());
            sendBtn.setBounds(691, 761, 100, 25);
            sendBtn.setBackground(Color.LIGHT_GRAY);
            background.add(sendBtn);

            JButton returnButton = new JButton("返回");
            returnButton.addActionListener(new ReturnButtonListenerOnline());
            returnButton.setBounds(25, 30, 100, 40);
            returnButton.setBackground(Color.LIGHT_GRAY);

            JButton replayButton = new JButton("重开一局");
            replayButton.addActionListener(new ReplayButtonListenerOnline());
            replayButton.setBounds(25, 100, 100, 40);
            replayButton.setBackground(Color.LIGHT_GRAY);

            JButton exitButton = new JButton("退出");
            exitButton.addActionListener(new ExitButtonListenerOnline());
            exitButton.setBounds(25, 170, 100, 40);
            exitButton.setBackground(Color.LIGHT_GRAY);

            buttonPanel.add(returnButton);
            buttonPanel.add(replayButton);
            buttonPanel.add(exitButton);
        }

        gameFrame.add(background);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if(mode == LOCAL_MODE){
            gameFrame.setSize(791, 641);
        }else{
            gameFrame.setSize(791, 786);
        }
        gameFrame.setUndecorated(true);
        gameFrame.setResizable(false);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }

    void playLocal(){

        initGameFrame(LOCAL_MODE);
        boardLocked = false;

        board.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
                int x = e.getX()%40>20?(e.getX()/40+2):(e.getX()/40+1);
                int y = e.getY()%40>20?(e.getY()/40+2):(e.getY()/40+1);
                Node node = new Node(x,y,color);
                if(x!=1 && x!=17 && y!=1 && y!=17 && !NodeList.isExist(node) && !boardLocked){
                    NodeList.addNode(node);
                    board.repaint();
                    String winner = Check.check(NodeList.getList());
                    if(winner!=null){
                        JOptionPane.showMessageDialog(null, winner+" win!");
                        boardLocked = true;
                        return;
                    }
                    resetColor();
                }
            }
        });
    }

    void playOnline(){
        boardLocked = true;
        gameStarted = false;
        isPlaying = true;
        initGameFrame(ONLINE_MODE);

        try {
            //connet to server
            infoText.append("正在连接到服务器\n");
            gameSocket = new Socket(HOST, GAME_PORT);
            fromServer = new DataInputStream(gameSocket.getInputStream());
            toServer = new DataOutputStream(gameSocket.getOutputStream());

            chatSocket = new Socket(HOST, CHAT_PORT);
            chatIn = new InputStreamReader(new DataInputStream(chatSocket.getInputStream()), "UTF-8");
            chatOut = new OutputStreamWriter(new DataOutputStream(chatSocket.getOutputStream()), "UTF-8");

            Thread game = new Thread(new OnlineHandler());
            Thread chat = new Thread(new ChatHandler());
            game.start();
            chat.start();
        } catch (IOException e) {
            infoText.append("服务器未开启\n");
            isPlaying = false;
        }

        board.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int x = e.getX()%40>20?(e.getX()/40+2):(e.getX()/40+1);
                int y = e.getY()%40>20?(e.getY()/40+2):(e.getY()/40+1);
                Node node = new Node(x,y,color);
                if(x!=1 && x!=17 && y!=1 && y!=17 && !NodeList.isExist(node) && !boardLocked){
                    boardLocked = true;
                    NodeList.addNode(node);
                    board.repaint();
                    nodeToSend = node;
                    waiting = false;
                }
            }
        });

        chatBox.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(!chatBox.getText().equals("")){
                        String tmp = mkMsg(chatBox.getText());
                        chatText.append(tmp+"\n");
                        chatBox.setText(null);
                        sendMsg(tmp);
                        chatText.selectAll();
                    }
                }
            }
        });
    }



    void resetColor(){
        color = color==Node.BLACK?Node.WHITE:Node.BLACK;
    }

    class OnlineHandler implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                color = fromServer.readInt();

                if(color == Node.BLACK){
                    infoText.append("你先手\n");
                    infoText.append("等待玩家2加入\n");
                    //read the start signal
                    fromServer.readInt();
                    infoText.append("玩家2已加入，游戏开始\n");
                    //It's my turn
                    boardLocked = false;
                    gameStarted = true;
                }
                else if(color == Node.WHITE){
                    infoText.append("你后手\n");
                    infoText.append("游戏开始\n");
                    gameStarted = true;
                }
                chatBox.setEditable(true);



                while(true){
                    if(color == Node.BLACK){
                        waitForPlayerAction();
                        sendMove();
                        String winner = Check.check(NodeList.getList());
                        if(winner!=null){
                            if(winner.equals("black")){
                                JOptionPane.showMessageDialog(null, "你赢了！");
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "你输了！");
                            }
                            boardLocked = true;
                            isPlaying = false;
                            break;
                        }
                        Node node = receiveMove();
                        if(node != null){
                            NodeList.addNode(node);
                            board.repaint();
                        }else{
                            boardLocked = true;
                            break;
                        }
                    }
                    else if(color == Node.WHITE){
                        Node node = receiveMove();
                        if(node != null){
                            NodeList.addNode(node);
                            board.repaint();
                        }else{
                            boardLocked = true;
                            break;
                        }
                        String winner = Check.check(NodeList.getList());
                        if(winner!=null){
                            if(winner.equals("white")){
                                JOptionPane.showMessageDialog(null, "你赢了！");
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "你输了！");
                            }
                            isPlaying = false;
                            boardLocked = true;
                            break;
                        }
                        waitForPlayerAction();
                        sendMove();
                    }
                }
            } catch (Exception e) {
                infoText.append("你的对手离开了游戏\n");
                boardLocked = true;
                isPlaying = false;
            } finally{
                try {
                    fromServer.close();
                    toServer.close();
                    gameSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlaying = false;
                waiting = true;
            }
        }

    }

    class ChatHandler implements Runnable{

        @Override
        public void run() {
            try {
                while(true){
                    char[] tmp = new char[1000];
                    chatIn.read(tmp);
                    String msg = new String(trunkMsg(tmp));
                    if(!((int)tmp[0] == 0)){
                        chatText.append(msg+"\n");
                        chatText.selectAll();
                    }else{
                        chatText.append("[!!]你的对手已离开游戏\n");
                        chatBox.setEditable(false);
                        chatIn.close();
                        chatOut.close();
                        chatSocket.close();

                        if(isPlaying){
                            waiting = false;
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                try {
                    chatSocket.close();
                } catch (IOException e1) {

                }
            }
        }

    }

    public void waitForPlayerAction() throws InterruptedException {
        while(waiting){
            Thread.sleep(100);
        }
        waiting = true;
    }

    public void sendMove() throws IOException{
        toServer.writeInt(nodeToSend.getX());
        toServer.writeInt(nodeToSend.getY());
        toServer.flush();
    }

    public Node receiveMove() throws IOException{
        Node point = null;
        int x = fromServer.readInt();
        if(x == -1){
            infoText.append("你的对手离开了游戏\n");
        }else{
            int y = fromServer.readInt();
            point = new Node();
            point.setX(x);
            point.setY(y);
            point.setColor(color==Node.BLACK?Node.WHITE:Node.BLACK);
            boardLocked = false;
        }
        return point;
    }

    @SuppressWarnings({ "resource", "unused" })
    public void disconnect(){
        try {
            if(gameStarted){
                //sender
                toServer.writeInt(-1);
                toServer.flush();
                fromServer.close();
                toServer.close();
                gameSocket.close();
                chatSocket.close();
            }else{
                Socket socket = new Socket(HOST, GAME_PORT);
                Socket socket2 = new Socket(HOST, CHAT_PORT);
                new DataInputStream(socket.getInputStream()).readInt();
            }
        } catch (IOException e1) {

        }
    }

    public void sendMsg(String msg){
        try {
            chatOut.write(msg);
            chatOut.flush();
        } catch (IOException e) {
            chatText.append("[!!]你的对手已离开游戏\n");
            try {
                chatSocket.close();
                chatBox.setEditable(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    public String mkMsg(String msg){
        Date date = new Date();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(date.getHours());
        sb.append(":");
        sb.append(date.getMinutes());
        sb.append("] ");
        sb.append(msg);
        return sb.toString();
    }

    public String trunkMsg(char[] msg){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<msg.length; i++){
            if(msg[i] == 0){
                break;
            }else{
                sb.append(msg[i]);
            }
        }
        return sb.toString();
    }

    class LocalButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            startFrame.setVisible(false);
            playLocal();
        }

    }

    class OnlineButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            startFrame.setVisible(false);
            playOnline();
        }

    }

    class ReverseButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if(!boardLocked){
                NodeList.removeLast();
                gameFrame.repaint();
                resetColor();
            }
        }

    }

    class ReplayButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            NodeList.purge();
            color = Node.BLACK;
            boardLocked = false;
            gameFrame.repaint();
        }

    }

    class ReturnButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            NodeList.purge();
            color = Node.BLACK;
            boardLocked = false;
            gameFrame.setVisible(false);
            startFrame.setVisible(true);
        }
    }

    class SendButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if(!chatBox.getText().equals("")){
                String tmp = mkMsg(chatBox.getText());
                chatText.append(tmp+"\n");
                chatBox.setText(null);
                sendMsg(tmp);
                chatText.selectAll();
            }
        }

    }

    class ReturnButtonListenerOnline implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            disconnect();
            NodeList.purge();
            color = Node.BLACK;
            boardLocked = false;
            gameFrame.setVisible(false);
            startFrame.setVisible(true);
        }

    }

    class ReplayButtonListenerOnline implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if(!isPlaying){
                try {
                    chatSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                NodeList.purge();
                gameFrame.setEnabled(false);
                gameFrame.setVisible(false);
                playOnline();
            }
        }

    }

    class ExitButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            System.exit(0);
        }
    }

    class ExitButtonListenerOnline implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            disconnect();
            System.exit(0);
        }

    }
}
