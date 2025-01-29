package org.algorithm.bee_hive;

import org.algorithm.components.HexaNode;

import java.util.Random;
import java.util.Stack;

public class BeeHive {
     private final int nbRow;
    private final  int nbColumn;
 private HexaNode[][] hive;
 HexaNode start;
 HexaNode end;
Random random;
public BeeHive(int nbRow,int nbColumn){
    this.nbRow=nbRow;
    this.nbColumn=nbColumn;
    this.hive=new HexaNode[nbRow][nbColumn];
    this.random=new Random();
    this.start=new HexaNode(0,0);
    this.end=new HexaNode(nbRow-1,nbColumn-1);
    for (int i = 0; i < nbRow; i++) {
        for (int j = 0; j < nbColumn; j++) {
            hive[i][j]=new HexaNode(i,j);
            hive[i][j].setValue((char)(random.nextInt(26)+'A'));

        }

    }
}

    public HexaNode[][] getHive() {
        return hive;
    }

    void generateHive(){

}


}
