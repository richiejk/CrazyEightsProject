package models;

import java.util.List;

/**
 * Created by RjK on 7/13/13.
 */
public class ComputerPlayer {

    public int makePlay(List<Card> myHand,int suit,int rank){
        int play=0;
        for(int i=0;i<myHand.size();i++){
            int tempId=myHand.get(i).getId();
            int tempRank=myHand.get(i).getRank();
            int tempSuit=myHand.get(i).getSuit();
            if(rank==8){
                if(tempSuit==suit){
                    play=tempId;
                }

            }else if(suit==tempSuit||rank==tempRank||tempId==108||tempId==208||tempId==308||tempId==408){
                play=tempId;
            }
        }
        return play;
    }

    public int chooseSuit(List<Card> myHand){
        return myHand.get(0).getSuit();
    }
}
