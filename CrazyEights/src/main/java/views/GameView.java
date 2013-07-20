package views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agpfd.crazyeights.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import models.Card;
import models.ComputerPlayer;

/**
 * Created by RjK on 7/12/13.
 */
public class GameView extends View implements GestureDetector.OnGestureListener {
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
    private Bitmap backArrow;
    private boolean myTurn;
    private int movingCardIdx = -1;
    private int movingX;
    private int movingY;
    private int validRank = 8;
    private int validSuit = 0;
    private Bitmap nextCardButton;
    private ComputerPlayer computerPlayer = new ComputerPlayer();
    private int scoreThisHand = 0;
    private ProgressDialog pg;
    private HashMap<Integer,Bitmap> bmHash;
    private static int INIT_CARDS_DONE=0;
    private static int INIT_BMPS=0;
    private static int INIT_BMPS_HASH=0;
    private static int READY_FOR_TOUCH=0;
    Handler mainHandler;
    Runnable myRunnable;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mainHandler.post(myRunnable);

    }

    public GameView(Context context) {
        super(context);
        myContext=context;
        scale = myContext.getResources().getDisplayMetrics().density;
        whitePaint=new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setAntiAlias(true);
        whitePaint.setStyle(Paint.Style.STROKE);
        whitePaint.setTextAlign(Paint.Align.LEFT);
        whitePaint.setTextSize(scale*13);
        INIT_CARDS_DONE=0;
        INIT_BMPS=0;
        mainHandler = new Handler(myContext.getMainLooper());
        READY_FOR_TOUCH=0;
        myRunnable = new Runnable() {
            @Override
            public void run() {
                pg=new ProgressDialog(myContext);
                pg.setMessage("Loading game...");
                pg.setCanceledOnTouchOutside(false);
                pg.show();
            }
        }; // This is your code
    }

    private void updateScores() {
        for (int i = 0; i < myHand.size(); i++) {
            oppScore += myHand.get(i).getScoreValue();
            scoreThisHand += myHand.get(i).getScoreValue();
        }
        for (int i = 0; i < oppHand.size(); i++) {
            myScore += oppHand.get(i).getScoreValue();
            scoreThisHand += oppHand.get(i).getScoreValue();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if(INIT_BMPS==1){

       // canvas.drawBitmap(deck.get(0).getBitmap(), 0, 0, null);
        canvas.drawText("Computer's Score: "+oppScore,10,whitePaint.getTextSize()+10,whitePaint);
        canvas.drawText("My score: "+myScore,10,screenH-whitePaint.getTextSize(),whitePaint);
        canvas.drawBitmap(cardBack,
                (screenW/2)-cardBack.getWidth()-10,
                (screenH/2)-(cardBack.getHeight()/2), null);

        for(int i=0;i<oppHand.size();i++){

            canvas.drawBitmap(cardBack,(i*oppHand.get(i).getBitmap().getWidth()/2)+10,whitePaint.getTextSize()+10+whitePaint.getTextSize(),null);
        }

        if (!discardPile.isEmpty()) {
            canvas.drawBitmap(discardPile.get(0).getBitmap(),
                    (screenW/2)+10,
                    (screenH/2)-(cardBack.getHeight()/2),
                    null);
        }

        if (myHand.size() > 9)
        {
            canvas.drawBitmap(nextCardButton,(int)(screenW-(scaledCardW/2))+10
                    ,
                    screenH-scaledCardH-whitePaint.getTextSize()+10,
                    null);

            canvas.drawBitmap(backArrow,5,
                    screenH-scaledCardH-whitePaint.getTextSize()+10,
                    null);
        }

        for(int i=0;i<myHand.size();i++){
            if(movingCardIdx==i){
                canvas.drawBitmap(myHand.get(i).getBitmap(),movingX,movingY,null);
            }else{
                if (i <9) {
                canvas.drawBitmap(myHand.get(i).getBitmap(),(i*myHand.get(i).getBitmap().getWidth()/2)+15+backArrow.getWidth(),(screenH-whitePaint.getTextSize()-10-whitePaint.getTextSize())-myHand.get(i).getBitmap().getHeight(),null);
                }
            }

        }

        if(pg.isShowing()){
            pg.dismiss();
        }
        READY_FOR_TOUCH=1;
        invalidate();
        }else{
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            invalidate();
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.screenH=h;
        this.screenW=w;

        Thread th=new Thread(new Runnable(){
            public void run(){

                initBitmaps();
                setBitmaps();
            }
        });
        setBackgroundResource(R.drawable.bgg);
        initCards();
        dealCards();
        drawCard(discardPile);
        th.start();
        mDetector = new GestureDetectorCompat(myContext,this);
        validRank=discardPile.get(0).getRank();
        validSuit=discardPile.get(0).getSuit();
        myTurn=new Random().nextBoolean();
        if (!myTurn) {
            makeComputerPlay();
        }
    }

    //(i*myHand.get(i).getBitmap().getWidth()/2)+10



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation

        int eventaction = event.getAction();
        int X = (int)event.getX();
        int Y = (int)event.getY();
        switch (eventaction ) {
            case MotionEvent.ACTION_DOWN:
                if(READY_FOR_TOUCH==1&&myTurn==true){
                    int lessThan;

                    if (myHand.size() > 9 &&
                            X >(screenW-(scaledCardW/2))+10 &&
                            Y > screenH-scaledCardH-whitePaint.getTextSize()-5 &&
                            Y <(screenH-whitePaint.getTextSize()-10-whitePaint.getTextSize())) {
                        Collections.rotate(myHand, 1);
                    }else if(myHand.size() > 9 &&
                            X <(0*scaledCardW/2)+20+backArrow.getWidth() &&
                            Y > screenH-scaledCardH-whitePaint.getTextSize()-5 &&
                            Y <(screenH-whitePaint.getTextSize()-10-whitePaint.getTextSize())) {
                        Collections.rotate(myHand, -1);
                    }

                    for(int i=0;i<myHand.size();i++){
                        lessThan=(i+1)*scaledCardW/2+15+backArrow.getWidth();

                        if(myHand.size()<9){
                            lessThan=(i+1)*scaledCardW+10;
                        }else if(i==8){
                            lessThan=(screenW-(scaledCardW/2))+10;
                        }


                        if(i<9&&(X>(i*scaledCardW/2+15)+backArrow.getWidth())&&(X<lessThan)&&(Y>(screenH-whitePaint.getTextSize()-10-whitePaint.getTextSize())-myHand.get(i).getBitmap().getHeight())&&(Y<screenH-whitePaint.getTextSize()-10-whitePaint.getTextSize())){
                            movingCardIdx=i;
                            movingX = X-(int)(25*scale);
                            movingY = Y-(int)(30*scale);
                        }

                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                movingX = X-(int)(25*scale);
                movingY = Y-(int)(30*scale);
                break;
            case MotionEvent.ACTION_UP:
                if(movingCardIdx>-1&&(X>(screenW/2)+10)&&(X<(screenW/2)+10+scaledCardW)&&(Y>(screenH/2)-(cardBack.getHeight()/2))&&(Y<(screenH/2)-(cardBack.getHeight()/2)+scaledCardH)
                &&(myHand.get(movingCardIdx).getRank() == 8 ||
                        myHand.get(movingCardIdx).getRank() == validRank ||
                        myHand.get(movingCardIdx).getSuit() == validSuit)){
                    validRank = myHand.get(movingCardIdx).getRank();
                    validSuit = myHand.get(movingCardIdx).getSuit();
                    discardPile.add(0, myHand.get(movingCardIdx));
                    myHand.remove(movingCardIdx);
                    if (myHand.isEmpty()) {
                        endHand();
                    }else{
                        if(validRank==8){
                            showChooseSuitDialog();
                        }else{
                            myTurn = false;
                            makeComputerPlay();
                        }
                    }
            }else if(movingCardIdx==-1&&myTurn==true&&
                        X>(screenW/2)-cardBack.getWidth()-10&&
                        X<(screenW/2)&&
                        Y>(screenH/2)-(cardBack.getHeight()/2)&&
                        Y<(screenH/2)-(cardBack.getHeight()/2)+scaledCardH
                        ){
                    if (checkForValidDraw()) {
                        drawCard(myHand);
                    } else {
                      drawCard(myHand); //only for testing arrow
                     //   Toast.makeText(myContext, "You cannot pick a card from the deck when you have at least 1 valid play remaining", Toast.LENGTH_SHORT).show();
                    }
                }


                movingCardIdx=-1;
                break;
        }
        invalidate();
        return true;
    }

    private void initBitmaps(){
        Bitmap tempBitmap2 =
                BitmapFactory.decodeResource
                        (myContext.getResources(),
                                R.drawable.card_back);
        nextCardButton =BitmapFactory.decodeResource(myContext.getResources(),R.drawable.arrow_next);
        backArrow=BitmapFactory.decodeResource(myContext.getResources(),R.drawable.arrow_prev);
        scaledCardW = (int) (screenW/6);
        scaledCardH = (int) (scaledCardW*1.4);
        cardBack = Bitmap.createScaledBitmap
                (tempBitmap2, scaledCardW,
                        scaledCardH,false);

        bmHash=new HashMap<Integer, Bitmap>();
        for(int i=0;i<4;i++){
            for(int j=102;j<115;j++){
                int tempId=j+(i*100);
                int resourceId=getResources().getIdentifier("card"+tempId,"drawable",myContext.getPackageName());
                Bitmap tempBitmap= BitmapFactory.decodeResource(getResources(),resourceId);
                Bitmap scaledBitmap=Bitmap.createScaledBitmap(tempBitmap,scaledCardW,scaledCardH,false);
                bmHash.put(tempId,scaledBitmap);
            }

        }

    }

    private void setBitmaps(){
        if(INIT_CARDS_DONE==1){
            for(Card temp:myHand){
                temp.setBitmap(bmHash.get(temp.getId()));
            }
            for(Card temp:oppHand){
                temp.setBitmap(bmHash.get(temp.getId()));
            }
            for(Card temp:deck){
                temp.setBitmap(bmHash.get(temp.getId()));
            }
            for(Card temp:discardPile){
                temp.setBitmap(bmHash.get(temp.getId()));
            }

        INIT_BMPS=1;

        }else{
            try {
                Thread.sleep(500);
                setBitmaps();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void initCards(){


        for(int i=0;i<4;i++){
            for(int j=102;j<115;j++){
                int tempId=j+(i*100);
                Card tempCard=new Card(tempId);
                //int resourceId=getResources().getIdentifier("card"+tempId,"drawable",myContext.getPackageName());
               //Bitmap tempBitmap= BitmapFactory.decodeResource(getResources(),resourceId);
                //scaledCardW = (int) (screenW/8);
               // scaledCardH = (int) (scaledCardW*1.28);
              //  Bitmap scaledBitmap=Bitmap.createScaledBitmap(tempBitmap,scaledCardW,scaledCardH,false);
                //tempCard.setBitmap(scaledBitmap);
                deck.add(tempCard);

            }
        }
        INIT_CARDS_DONE=1;
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

    private void showChooseSuitDialog() {
        final Dialog chooseSuitDialog =
        new Dialog(myContext);
        chooseSuitDialog.requestWindowFeature
                (Window.FEATURE_NO_TITLE);
        chooseSuitDialog.setContentView
                (R.layout.choose_suit_dialog);
        final Spinner suitSpinner = (Spinner)
        chooseSuitDialog.findViewById(R.id.suitSpinner);
        ArrayAdapter<CharSequence> adapter =
        ArrayAdapter.createFromResource(
                myContext, R.array.suits,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        suitSpinner.setAdapter(adapter);
        Button okButton =
        (Button) chooseSuitDialog.findViewById
                (R.id.okButton);
        okButton.setOnClickListener
                (new View.OnClickListener(){
                    public void onClick(View view){
                        validSuit = (suitSpinner.
                                getSelectedItemPosition()+1)*100;
                        String suitText = "";
                        if (validSuit == 100) {
                            suitText = "Diamonds";
                        } else if (validSuit == 200) {
                            suitText = "Clubs";
                        } else if (validSuit == 300) {
                            suitText = "Hearts";
                        } else if (validSuit == 400) {
                            suitText = "Spades";
                        }
                        chooseSuitDialog.dismiss();
                        Toast.makeText(myContext, 
                                "You chose " + suitText,
                                Toast.LENGTH_SHORT).show();
                        myTurn = false;
                        makeComputerPlay();
                    }
                });
        chooseSuitDialog.show();
    }

    private boolean checkForValidDraw() {
        boolean canDraw = true;
        for (int i = 0; i < myHand.size(); i++) {
            int tempId = myHand.get(i).getId();
            int tempRank = myHand.get(i).getRank();
            int tempSuit = myHand.get(i).getSuit();
            if (validSuit == tempSuit || validRank == tempRank
                    ||
                    tempId == 108 || tempId == 208 ||
                    tempId == 308 || tempId == 408) {
                canDraw = false;
            }
        }
        return canDraw;
    }

    private void makeComputerPlay(){
        int tempPlay=0;
        while(tempPlay==0){
            tempPlay=computerPlayer.makePlay(oppHand,validSuit,validRank);
            if(tempPlay==0){
                drawCard(oppHand);
            }
        }if(tempPlay==108||tempPlay==208||tempPlay==308||tempPlay==408){
            validRank=8;
            String suitText="";
            validSuit=computerPlayer.chooseSuit(myHand);
            if(validSuit==100){
                suitText="Diamonds";
            }else if(validSuit==200){
                suitText="Clubs";
            }else if(validSuit==300){
                suitText="Hearts";
            }else if(validSuit==400){
                suitText="Spades";
            }
            Toast.makeText(myContext,suitText,Toast.LENGTH_SHORT).show();
        }else{
            validSuit=Math.round((tempPlay/100) * 100);
            validRank=tempPlay-validSuit;
        }
        for (int i = 0; i < oppHand.size(); i++) {
            Card tempCard = oppHand.get(i);
            if (tempPlay == tempCard.getId()) {
                discardPile.add(0, oppHand.get(i));
                oppHand.remove(i);
            }
        }
        if(oppHand.isEmpty()){
            endHand();
        }
        myTurn = true;
    }

    private void endHand() {
        final Dialog endHandDialog = new Dialog(myContext);
        endHandDialog.requestWindowFeature
                (Window.FEATURE_NO_TITLE);
        endHandDialog.setContentView(R.layout.end_hand_dialog);
        updateScores();
        TextView endHandText = (TextView)
        endHandDialog.findViewById(R.id.endHandText);
        if (myHand.isEmpty()) {
            if (myScore >= 300) { 
                endHandText.setText("You reached " + myScore +
                " points. You won! Would you like to play again?");
            }else{
            endHandText.setText("You've won " +
                    scoreThisHand + " points!");
            }
        } else if (oppHand.isEmpty()) {
            if (oppScore >= 300) {
                endHandText.setText("The computer has reached " + oppScore +
                        " points. You lost! Would you like to play again?");
            }else{
            endHandText.setText("The computer won "
                    + scoreThisHand + " points.");
            }
        }
        Button nextHandButton = (Button)
        endHandDialog.findViewById(R.id.nextHandButton);
        if (oppScore >= 300 || myScore >= 300) {
            nextHandButton.setText("New Game");
        }
        nextHandButton.setOnClickListener
                (new View.OnClickListener(){
                    public void onClick(View view){
                        if (oppScore >= 300 || myScore >= 300) {
                            myScore = 0;
                            oppScore = 0;
                        }
                        initNewHand();
                        endHandDialog.dismiss();
                    }
                });
        endHandDialog.show();
    }

    private void initNewHand(){
        scoreThisHand=0;
        if(myHand.isEmpty()){
            myTurn=true;
        }else{
            myTurn=false;
        }
        deck.addAll(discardPile);
        deck.addAll(oppHand);
        deck.addAll(myHand);
        discardPile.clear();
        myHand.clear();
        oppHand.clear();
        dealCards();
        drawCard(discardPile);
        validRank=discardPile.get(0).getRank();
        validSuit=discardPile.get(0).getSuit();
        if(!myTurn){
            makeComputerPlay();
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        int X1=(int)motionEvent.getX();
        int Y1=(int)motionEvent.getY();

        int X2=(int)motionEvent2.getX();
        int Y2=(int)motionEvent2.getY();

        float diffY = motionEvent2.getY() - motionEvent.getY();
        float diffX = motionEvent2.getX() - motionEvent.getX();

        if(myHand.size()>9&&Y1>((screenH-whitePaint.getTextSize()-10-whitePaint.getTextSize())-scaledCardH)){
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(v) > SWIPE_VELOCITY_THRESHOLD) {
                    int fV=firstDigit(v);
                   if (diffX > 0) {
                      /*  int fV=firstDigit(v);

                        for(int i=0;i<fV;i++){
                            if(fV<0){
                                Collections.rotate(myHand,-1);
                            }else{

                            }
                            invalidate();
                        }

*/

                       if(fV<0){
                           fV=-fV;
                       }
                       for(int i=0;i<fV;i++){
                           Collections.rotate(myHand,1);
                           invalidate();
                       }

                       invalidate();
                    } else {
                        if(fV>0){
                            fV=-fV;
                        }
                       for(int i=0;i<(-fV);i++){
                       Collections.rotate(myHand,fV);
                       invalidate();
                       }
                    }
                }
            }
        }

        return true;
    }

    public static int firstDigit(float n1) {
        int n=(int)n1;
        while (n < -9 || 9 < n) n /= 10;
        return Math.abs(n);
    }

    public static char firstDigit2(float n1) {
        if(n1<0){
            n1=-n1;
        }

        String f=n1+"";
        if(f.length()==3){
            return f.charAt(0);
        }else{
            return '8';
        }

       /* int n=(int)n1;
        while (n < -9 || 9 < n) n /= 10;
        return Math.abs(n);*/
    }

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetectorCompat mDetector;
}
