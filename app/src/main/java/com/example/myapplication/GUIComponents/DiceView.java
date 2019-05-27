package com.example.myapplication.GUIComponents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.ArrayList;


public class DiceView extends AppCompatImageView {
    float radius;
    Paint paint = new Paint();
    PointF[] dots = new PointF[9];
    ArrayList<Integer> drawingPositions = new ArrayList<>();

    public DiceView(Context context) { super(context); }

    public DiceView (Context context,AttributeSet attrs) { super(context, attrs); }

    public DiceView(Context context,AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }

    public void setNumber(ArrayList<Integer> draw) { drawingPositions = draw;}

    @Override

    protected void onSizeChanged(int w,int h,int oldw,int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        float space = w/11f;
        radius = space;

        float x = space + radius;
        float y = space + radius;

        for(int i = 0; i < 9; i++){
            y = space + radius + (3*space) * (i/3);
            dots[i] = new PointF(x,y);
            x = (x+3*space)%(9*space);
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        for(Integer i: drawingPositions)
            canvas.drawCircle(dots[i].x,dots[i].y,radius,paint);
    }
}
