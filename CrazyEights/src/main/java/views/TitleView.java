package views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.agpfd.crazyeights.R;
import com.agpfd.crazyeights.activities.GameActivity;


/**
 * Created by RjK on 7/12/13.
 */
public class TitleView  extends View {

    private int screenW;
    private int screenH;
    private Bitmap startButton;
    private Bitmap helpButton;
    private Bitmap exitButton;
    private int X;
    private int Y;
    private boolean isPressed=false;
    Context myContext;
    private int startButtonY;
    private int helpButtonY;
    private int scaledButtonW;
    private int scaledButtonH;

    public TitleView(Context context) {
        super(context);
        this.myContext=context;
        this.startButton=BitmapFactory.decodeResource(getResources(),R.drawable.start_button);

        this.exitButton=BitmapFactory.decodeResource(getResources(),R.drawable.exit_button);

        this.helpButton=BitmapFactory.decodeResource(getResources(),R.drawable.help_button);

        setBackgroundResource(R.drawable.bg);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.screenH=h;
        this.screenW=w;
        scaledButtonW =(int) (screenW/2.1);
        scaledButtonH = (int)(screenW*0.12);
        this.startButtonY=(int)(screenH*0.6);

        this.startButton=Bitmap.createScaledBitmap(startButton,scaledButtonW,scaledButtonH,false);
        this.exitButton=Bitmap.createScaledBitmap(exitButton,scaledButtonW,scaledButtonH,false);
        this.helpButton=Bitmap.createScaledBitmap(helpButton,scaledButtonW,scaledButtonH,false);
        this.helpButtonY=this.startButtonY+startButton.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isPressed){

        }
        else{
                canvas.drawBitmap(startButton,(screenW-startButton.getWidth())/2,startButtonY, null);
                canvas.drawBitmap(helpButton,(screenW-startButton.getWidth())/2,helpButtonY,null);
                canvas.drawBitmap(exitButton,(screenW-startButton.getWidth())/2,helpButtonY+helpButton.getHeight(),null);
        }


    }

    public boolean onTouchEvent(MotionEvent event){
        int eventAction=event.getAction();
        X=(int)event.getX();
        Y=(int)event.getY();
        switch (eventAction){
            case MotionEvent.ACTION_DOWN:
                if((X>((screenW-startButton.getWidth())/2))&&X<((screenW-startButton.getWidth())/2)+startButton.getWidth()){
                    if((Y>(startButtonY))&&(Y < helpButtonY)){
                        isPressed=true;
                        Toast.makeText(myContext,"Loading Game. Please wait",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(myContext, GameActivity.class);
                        myContext.startActivity(intent);
                    }
                    else if((Y>helpButtonY)&&(Y<helpButtonY+helpButton.getHeight())){
                        showInstructions();
                    }else if((Y>helpButtonY+helpButton.getHeight())&&(Y<helpButtonY+helpButton.getHeight()+exitButton.getHeight())){
                        System.exit(0);
                    }
                }break;
            case MotionEvent.ACTION_UP:
                isPressed=false;break;
            default: isPressed=false;break;
        }
        invalidate();
        return true;

    }

    private void showInstructions() {
        final Dialog showHelp =
                new Dialog(myContext);
        showHelp.requestWindowFeature
                (Window.FEATURE_NO_TITLE);
        showHelp.setContentView
                (R.layout.how_to_play);
        Button okButton =
                (Button) showHelp.findViewById
                        (R.id.ok_Button);
        okButton.setOnClickListener
                (new View.OnClickListener(){
                    public void onClick(View view){
                     showHelp.dismiss();
                    }
                });
        showHelp.show();
    }
}
