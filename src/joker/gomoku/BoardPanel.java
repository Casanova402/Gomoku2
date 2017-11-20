package joker.gomoku;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

class BoardPanel extends JPanel{

    private static final long serialVersionUID = 1L;
    public int RADIUS = 15;
    public int BLANK_WIDTH = 40;

    public BoardPanel(){
        this.setBackground(Color.DARK_GRAY);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.CYAN);
        //draw basic board
        for(int i = 0,j = 0;j<17;i+=BLANK_WIDTH,j++){
            g.drawLine(0, i, 640, i);
            g.drawLine(i, 0, i, 640);
        }

        //draw nodes
        ArrayList<Node> nodeList = NodeList.getList();
        for(Node point : nodeList){
            int x = (point.getX()-1)*BLANK_WIDTH;
            int y = (point.getY()-1)*BLANK_WIDTH;

            //draw black
            if(point.getColor() == Node.BLACK){
//				g.setColor(new Color(255,192,203));
                g.setColor(Color.BLACK);
            }
            //draw white
            else if(point.getColor() == Node.WHITE){
                g.setColor(Color.WHITE);
            }
            g.fillOval(x-RADIUS, y-RADIUS, RADIUS*2, RADIUS*2);
            //newly point
            if(point.equals(NodeList.getLastNode())){
                g.setColor(Color.RED);
                g.drawRect(x-RADIUS, y-RADIUS, RADIUS*2, RADIUS*2);
            }
        }
    }
}