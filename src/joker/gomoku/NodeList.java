package joker.gomoku;

import java.util.ArrayList;

public class NodeList {
    private static ArrayList<Node> nodeList = new ArrayList<Node>();

    public static ArrayList<Node> getList(){
        return nodeList;
    }

    public static void addNode(Node node){
        nodeList.add(node);
    }

    public static Node getLastNode(){
        return nodeList.get(nodeList.size()-1);
    }

    public static void purge(){
        nodeList.clear();
    }

    public static int[][] getMatrix(){
        int[][] matrix = new int[20][20];
        for(Node node : nodeList){
            matrix[node.getX()-1][node.getY()-1] = node.getColor();
        }
        return matrix;
    }

    public static boolean isExist(Node node){
        for(Node p : nodeList){
            if(p.getX() == node.getX() && p.getY() == node.getY()){
                return true;
            }
        }
        return false;
    }

    public static void removeLast(){
        nodeList.remove(nodeList.size()-1);
    }

}
