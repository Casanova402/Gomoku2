package joker.gomoku;

import java.util.ArrayList;


public class Check {
    public static String check(ArrayList<Node> nodeList){
        //check black
        int[][] matrix_black = new int[20][20];
        int[][] matrix_white = new int[20][20];
        for(Node point : nodeList){
            if(point.getColor()==Node.BLACK){
                matrix_black[point.getX()-1][point.getY()-1]=1;
            }
            else{
                matrix_white[point.getX()-1][point.getY()-1]=1;
            }
        }
        if(checkInMatrix(matrix_black)){
            return "black";
        }
        if(checkInMatrix(matrix_white)){
            return "white";
        }
        return null;
    }

    public static boolean checkInMatrix(int[][] matrix){
        for(int i=0;i<20;i++){
            for(int j=0;j<20;j++){
                if(matrix[i][j]==1){
                    //———
                    if(j<=15&&matrix[i][j+1]==1
                            &&matrix[i][j+2]==1
                            &&matrix[i][j+3]==1
                            &&matrix[i][j+4]==1){
                        return true;
                    }
                    // |
                    else if(i<=15&&matrix[i+1][j]==1
                            &&matrix[i+2][j]==1
                            &&matrix[i+3][j]==1
                            &&matrix[i+4][j]==1){
                        return true;
                    }
                    // \
                    else if(i<=15&&j<=15
                            &&matrix[i+1][j+1]==1
                            &&matrix[i+2][j+2]==1
                            &&matrix[i+3][j+3]==1
                            &&matrix[i+4][j+4]==1){
                        return true;
                    }
                    // /
                    else if(i<=15&&j>=4
                            &&matrix[i+1][j-1]==1
                            &&matrix[i+2][j-2]==1
                            &&matrix[i+3][j-3]==1
                            &&matrix[i+4][j-4]==1){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
