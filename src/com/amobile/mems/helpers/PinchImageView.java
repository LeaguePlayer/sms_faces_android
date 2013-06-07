package com.amobile.mems.helpers;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PinchImageView extends View {

    private static final int INVALID_POINTER_ID = -1;

    private Drawable mImage;
    int width, height, maxWidth = 200, maxHeight = 200;

    // private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float mRotationDegrees = 0.f;


    public PinchImageView(Context context, Drawable drawable) {
        this(context, null, 0);
        mImage = drawable;
        width = 200;
        height = 200;
        mImage.setBounds(0, 0, width, height);
    }

    public void setSize(int width, int height){
        this.width = width;
        this.height = height;
        maxWidth = width;
        maxHeight = height;
        mImage.setBounds(0,0,width,height);
        invalidate();
    }

    public PinchImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public Drawable getDrawable(){
        return mImage;
    }

    public void setDrawable(Drawable d) {
        Rect r = this.mImage.getBounds();
        this.mImage = d;
        mImage.setBounds(r);
        invalidate();
    }

    public void setRotationDegrees(float mRotationDegrees){
        this.mRotationDegrees = mRotationDegrees;
    }

    public float getRotationDegrees(){
        return mRotationDegrees;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        mImage.draw(canvas);

        canvas.restore();

        width = (int)(maxWidth * mScaleFactor);
        height = (int)(maxHeight * mScaleFactor);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.getLayoutParams();
        lp.width = width;
        lp.height = height;
        this.setLayoutParams(lp);
    }

    public void refresh(float mScaleFactor) {
        this.mScaleFactor = mScaleFactor;
        invalidate();
    }

}