package models;

import android.graphics.Bitmap;

/**
 * Created by RjK on 7/12/13.
 */
public class Card {
    private int id;
    private int suit;
    private int rank;
    private Bitmap cardImage;
    private int scoreValue = 0;


    public Card(int newId){
        this.id=newId;
        this.suit=Math.round((id/100) * 100);
        this.rank=id-suit;
        if (rank == 8){
        scoreValue = 50;
        } else if (rank == 14) {
            scoreValue = 1;
        } else if (rank > 9 && rank < 14) {
            scoreValue = 10;
        } else {
            scoreValue = rank;
        }
    }



    public int getScoreValue() {
        return scoreValue;
    }

    public void setBitmap(Bitmap newBitmap) {
        cardImage = newBitmap;
    }
    public Bitmap getBitmap() {
        return cardImage;
    }
    public int getId() {
        return id;
    }

    public int getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }
}
