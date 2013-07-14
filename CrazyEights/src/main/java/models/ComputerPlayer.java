package models;

import java.util.List;

/**
 * Created by RjK on 7/13/13.
 */
public class ComputerPlayer {

    public int makePlay(List<Card> hand,int suit,int rank){
        int play=0;
        for (int i = 0; i < hand.size(); i++) {
            int tempId = hand.get(i).getId();
            int tempRank = hand.get(i).getRank();
            int tempSuit = hand.get(i).getSuit();
            if (tempRank != 8) {
                if (rank == 8) {
                    if (suit == tempSuit) {
                        play = tempId;
                    }
                } else if (suit == tempSuit || rank == tempRank)
                {
                    play = tempId;
                }
            }
        }
        if (play == 0) {
            for (int i = 0; i < hand.size(); i++) {
                int tempId = hand.get(i).getId();
                if (tempId == 108 || tempId == 208 || tempId == 308 || tempId == 408) {
                    play = tempId;
                }
            }
        }
        return play;
    }

    public int chooseSuit(List<Card> myHand){
        int numDiamonds=0;
        int numClubs=0;
        int numHearts=0;
        int numSpades=0;
        for(Card temp:myHand){
            if(temp.getSuit()==100){
                numDiamonds++;
            }
            if(temp.getSuit()==200){
                numClubs++;
            }
            if(temp.getSuit()==300){
                numHearts++;
            }
            if(temp.getSuit()==400){
                numSpades++;
            }
        }
        if(numDiamonds>numClubs&&numDiamonds>numSpades&&numDiamonds>numHearts){
            return 100;
        }else if(numClubs>numDiamonds&&numClubs>numSpades&&numClubs>numHearts){
            return 200;
        }else if(numSpades>numDiamonds&&numClubs<numSpades&&numSpades>numHearts){
            return 400;
        }else{
            return 300;
        }

    }
}
