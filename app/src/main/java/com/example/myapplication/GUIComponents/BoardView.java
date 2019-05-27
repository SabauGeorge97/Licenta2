package com.example.myapplication.GUIComponents;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


public class BoardView extends AppCompatImageView {

    ImageData imageData;

    public BoardView(Context context) {
        super(context);
        init();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        imageData = new ImageData();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(w > 0 && h > 0){
            imageData.setSize(w,h);
        }
    }

    public ImageData getImageData(){return imageData;}

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        imageData.draw(canvas);
    }
}
