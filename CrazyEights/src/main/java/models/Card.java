package models;

import android.graphics.Bitmap;

/**
 * Created by RjK on 7/12/13.
 */
public class Card {
    private int id;
    private Bitmap cardImage;

    public Card(int newId){
        this.id=newId;
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

}
