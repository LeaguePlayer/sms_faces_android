package com.amobile.mems.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.amobile.mems.R;
import com.amobile.mems.helpers.PinchImageView;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 12.05.13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class scale extends SherlockActivity {
    TextView scaleGesture;
    ImageView myImageView;
    float curScale = 1F;
    Bitmap bitmap;
    int bmpWidth, bmpHeight;

    ScaleGestureDetector scaleGestureDetector;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scale);
//        myImageView = (ImageView)findViewById(R.id.imageview);
        //PinchImageView image = (PinchImageView)findViewById(R.id.imageview);
        PinchImageView image = new PinchImageView(scale.this, getResources().getDrawable(R.drawable.mem1));
        ((RelativeLayout)findViewById(R.id.ll1)).addView(image);

        PinchImageView image2 = new PinchImageView(scale.this, getResources().getDrawable(R.drawable.mem2));
        ((RelativeLayout)findViewById(R.id.ll1)).addView(image2);

        PinchImageView image3 = new PinchImageView(scale.this, getResources().getDrawable(R.drawable.mem3));
        ((RelativeLayout)findViewById(R.id.ll1)).addView(image3);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new ViewGroup.MarginLayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        lp.setMargins(50, 50 , 0, 0);
        image.setLayoutParams(lp);

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(new ViewGroup.MarginLayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        lp2.setMargins(200, 100 , 0, 0);
        image2.setLayoutParams(lp2);

        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(new ViewGroup.MarginLayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        lp3.setMargins(450, 150 , 0, 0);
        image3.setLayoutParams(lp3);
//        scaleGesture = (TextView)findViewById(R.id.ScaleGesture);
//        myImageView = (ImageView)findViewById(R.id.imageview);
//
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//        bmpWidth = bitmap.getWidth();
//        bmpHeight = bitmap.getHeight();
//        drawMatrix();
//
//        scaleGestureDetector = new ScaleGestureDetector(this, new simpleOnScaleGestureListener());
    }
//
//    private void drawMatrix(){
//
//        curScale = ((curScale - 1) * 10) + 1;
//        if (curScale < 0.1){
//            curScale = 0.1f;
//        }
//
//        Bitmap resizedBitmap;
//        int newHeight = (int) (bmpHeight * curScale);
//        int newWidth = (int) (bmpWidth * curScale);
//        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
//        myImageView.setImageBitmap(resizedBitmap);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // TODO Auto-generated method stub
//        scaleGestureDetector.onTouchEvent(event);
//        return true;
//    }
//
//    public class simpleOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            // TODO Auto-generated method stub
//            curScale = detector.getScaleFactor();
//            scaleGesture.setText(String.valueOf(curScale));
//            drawMatrix();
//            return true;
//        }
//
//        @Override
//        public boolean onScaleBegin(ScaleGestureDetector detector) {
//            // TODO Auto-generated method stub
//            return true;
//        }
//
//        @Override
//        public void onScaleEnd(ScaleGestureDetector detector) {
//            // TODO Auto-generated method stub
//            super.onScaleEnd(detector);
//        }
//
//    }
}
