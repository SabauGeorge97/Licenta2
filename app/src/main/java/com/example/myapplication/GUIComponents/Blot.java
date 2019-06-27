package com.example.myapplication.GUIComponents;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import java.util.ArrayList;

//deseneaza toate piesele pe mijlocul tablei
//blot = bara din mijloc
public class Blot extends Border {
    ArrayList<Checker> white = new ArrayList<>();
    ArrayList<Checker> black = new ArrayList<>();
    float space;

    public Blot(float l, float r, float t, float b) {
        super(l, r, t, b);
        space = (position.top - position.bottom) / 20;
    }

    public void addChecker(int color) {
        if (color == Color.WHITE) {
            if (white.isEmpty())
                white.add(new Checker(color, new PointF((position.left + position.right) / 2, position.bottom - Checker.getSize())));
            else {
                PointF newPos = new PointF((position.left + position.right) / 2, white.get(white.size() - 1).getPosition().y + space);
                white.add(new Checker(color, newPos));
            }
        } else if (black.isEmpty())
            black.add(new Checker(color, new PointF((position.left + position.right) / 2, position.top + Checker.getSize())));
        else {
            PointF newPos = new PointF((position.left + position.right) / 2, black.get(black.size() - 1).getPosition().y - space);
            black.add(new Checker(color, newPos));
        }
    }


    public void removeChecker(int color){
        if(color == Color.WHITE)
            white.remove(white.size()-1);
        else
            black.remove(black.size()-1);
    }

    public Checker getTopBlack(){
        return black.get(black.size()-1);
    }
    public Checker getTopWhite(){
        return white.get(white.size()-1);
    }

    public void drawElements(Canvas canvas){
        for (Checker c : white)
            c.draw(canvas);

        for (Checker c : black)
            c.draw(canvas);
    }

    public int blackCount(){
        return black.size();
    }
    public int whiteCount(){
        return white.size();
    }
}

