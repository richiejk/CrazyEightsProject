package views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.agpfd.crazyeights.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import models.Card;

/**
 * Created by RjK on 7/12/13.
 */
public class GameView extends View {
    Context myContext;
    private List<Card> deck = new ArrayList<Card>();
    private int scaledCardW;
    private int scaledCardH;
    private int screenW;
    private int screenH;
    private List<Card> myHand = new ArrayList<Card>();
    private List<Card> oppHand = new ArrayList<Card>();
    private List<Card> discardPile = new ArrayList<Card>();
    private float scale;
    private Paint whitePaint;
    private int oppScore;
    private int myScore;
    private Bitmap cardBack;

    public GameView(Context context) {
        super(context);
        myContext=context;
        scale = myContext.getResources().getDisplayMetrics().density;
        whitePaint=new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setAntiAlias(true);
        whitePaint.setStyle(Paint.Style.STROKE);
        whitePaint.setTextAlign(Paint.Align.LEFT);
        whitePaint.setTextSize(scale*15);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       // canvas.drawBitmap(deck.get(0).getBitmap(), 0, 0, null);
        canvas.drawText("Computer's Score: "+oppScore,0,whitePaint.getTextSize()+10,whitePaint);
        canvas.drawText("My score: "+myScore,0,screenH-whitePaint.getTextSize()-10,whitePaint);
        canvas.drawBitmap(cardBack,
                (screenW/2)-cardBack.getWidth()-10,
                (screenH/2)-(cardBack.getHeight()/2), null);
        for(int i=0;i<7;i++){
            canvas.drawBitmap(myHand.get(i).getBitmap(),(i*myHand.get(i).getBitmap().getWidth())+10,(screenH-whitePaint.getTextSize()-10-whitePaint.getTextSize())-myHand.get(i).getBitmap().getHeight(),null);
            canvas.drawBitmap(cardBack,(i*oppHand.get(i).getBitmap().getWidth()/2),whitePaint.getTextSize()+10+whitePaint.getTextSize()+10,null);

        }

        if (!discardPile.isEmpty()) {
            canvas.drawBitmap(discardPile.get(0).getBitmap(),
                    (screenW/2)+10,
                    (screenH/2)-(cardBack.getHeight()/2),
                    null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.screenH=h;
        this.screenW=w;
        initCards();
        dealCards();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();
        int X = (int)event.getX();
        int Y = (int)event.getY();
        switch (eventaction ) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    private void initCards(){
        Bitmap tempBitmap2 =
                BitmapFactory.decodeResource
                        (myContext.getResources(),
                                R.drawable.card_back);
        scaledCardW = (int) (screenW/8);
        scaledCardH = (int) (scaledCardW*1.28);
        cardBack = Bitmap.createScaledBitmap
                (tempBitmap2, scaledCardW,
                        scaledCardH,false);

        for(int i=0;i<4;i++){
            for(int j=102;j<115;j++){
                int tempId=j+(i*100);
                Card tempCard=new Card(tempId);
                int resourceId=getResources().getIdentifier("card"+tempId,"drawable",myContext.getPackageName());
                Bitmap tempBitmap= BitmapFactory.decodeResource(getResources(),resourceId);
                scaledCardW = (int) (screenW/8);
                scaledCardH = (int) (scaledCardW*1.28);
                Bitmap scaledBitmap=Bitmap.createScaledBitmap(tempBitmap,scaledCardW,scaledCardH,false);
                tempCard.setBitmap(scaledBitmap);
                deck.add(tempCard);

            }
        }
    }

    private void dealCards(){
        Collections.shuffle(deck,new Random());
        for(int i=0;i<7;i++){
            drawCard(myHand);
            drawCard(oppHand);
        }
    }

    private void drawCard(List<Card> handToDraw){
        handToDraw.add(0,deck.get(0));
        deck.remove(0);
        if(deck.isEmpty()){
            for(int i=discardPile.size()-1;i>0;i--){
                deck.add(discardPile.get(i));
                discardPile.remove(i);
                Collections.shuffle(deck,new Random());
            }
        }
    }




}
