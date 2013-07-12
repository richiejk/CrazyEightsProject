package views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.agpfd.crazyeights.R;
import com.agpfd.crazyeights.activities.GameActivity;


/**
 * Created by RjK on 7/12/13.
 */
public class TitleView  extends View {

    private Bitmap titleGraphic;
    private int screenW;
    private int screenH;
    private Bitmap startButton;
    private int X;
    private int Y;
    private boolean isPressed=false;
    Context myContext;

    public TitleView(Context context) {
        super(context);
        this.myContext=context;
        this.titleGraphic= BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        this.startButton=BitmapFactory.decodeResource(getResources(),R.drawable.start_button);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.screenH=h;
        this.screenW=w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isPressed){

        }
        else{
            canvas.drawBitmap(titleGraphic,(screenW-titleGraphic.getWidth())/2,(screenH-titleGraphic.getHeight())/6, null);
            canvas.drawBitmap(startButton,(screenW-startButton.getWidth())/2,(int)(screenH*0.6), null);
        }


    }

    public boolean onTouchEvent(MotionEvent event){
        int eventAction=event.getAction();
        X=(int)event.getX();
        Y=(int)event.getY();
        switch (eventAction){
            case MotionEvent.ACTION_DOWN:
                if((X>((screenW-startButton.getWidth())/2))&&X<((screenW-startButton.getWidth())/2)+startButton.getWidth()){
                    if((Y>(int)(screenH*0.6))&&(Y<(int)(screenH*0.6)+startButton.getHeight())){
                        isPressed=true;
                        Toast.makeText(myContext,"Loading Game. Please wait",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(myContext, GameActivity.class);
                        myContext.startActivity(intent);
                    }
                }break;
            case MotionEvent.ACTION_UP:
                isPressed=false;break;
            default: isPressed=false;break;
        }
        invalidate();
        return true;

    }
}
