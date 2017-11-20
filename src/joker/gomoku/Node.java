package joker.gomoku;

import java.io.Serializable;

public class Node implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private int x;
    private int y;
    private int color;

    public Node(int x,int y,int color){
        this.x = x;
        this.y = y;
        this.color = color;
    }
    public Node(){

    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }
    public String toString(){
        return this.x+","+this.y;
    }

}
