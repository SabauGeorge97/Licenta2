package com.example.myapplication.GUIComponents;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.io.Serializable;

public class Checker implements Serializable{
    private Paint color = new Paint();
    private PointF position;
    private static float size;
    private Paint borderColor = new Paint();
    private int type = 0;
    private static final float DELTA = 10;

    public Checker (int color,PointF position){
        init(position);
        this.color.setColor(color);
        type = color == Color.DKGRAY? 1 : -1;
    }

    public Checker (Checker c){
        init(c.getPosition());
        color.setColor(c.getColor().getColor());
        type = c.getType();
    }

    private void init (PointF position){
        this.position = position;
        color.setStyle(Paint.Style.FILL);
        color.setColor(Color.TRANSPARENT);
        borderColor.setStyle(Paint.Style.STROKE);
        borderColor.setColor(Color.BLACK);
        borderColor.setStrokeWidth(2);
        borderColor.setShadowLayer(40,3,3,Color.BLACK);
    }

    public Paint getColor(){
        return color;
    }
    public void setColor(Paint color){
        this.color = color;
    }
    public PointF getPosition(){
        return position;
    }

    public void setPosition(PointF position){
        this.position = position;
    }

    public static float getSize(){
        return size;
    }

    public static void setSize(float size){ Checker.size = size; }

    public void getBorderSize() { return borderColor.getStrokeWidth();}
    public void setBorderSize(float borderSize){ this.borderColor.setStrokeWidth(borderSize);}

    public Paint getBorderColor(){ return borderColor; }
    public void setBorderColor(Paint borderColor){ this.borderColor = borderColor; }

    public int getType(){ return type; }

    public void setType (int type) { this.type = type; }

    public void draw(Canvas canvas){
        canvas.drawCircle(position.x,position.y,size,color);
        canvas.drawCircle(position.x,position.y,size,borderColor);
    }

    public boolean clicked (PointF position){
        if(Math.sqrt(Math.pow(position.x - this.position.x,2) + Math.pow(position.y - this.position.y,2)) <= size+DELTA)
            return true;
        return false;
    }
}
