package com.example.myapplication.GUIComponents;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;


import java.util.ArrayList;



public class Triangle {
    Path path;
    Paint paint;
    PointF a, b, c;
    ArrayList<Checker> checkers = new ArrayList<>();
    float height;
    int orientation;

    public Triangle(float x1, float y1, float x2, float y2, float x3, float y3, Paint paint, int o) {
        orientation = o;
        this.paint = paint;
        a = new PointF(x1, y1);
        b = new PointF(x2, y2);
        c = new PointF(x3, y3);
        height = y3;
        path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x1, y1);
        path.close();
    }

    public void draw(Canvas canvas) {

        canvas.drawPath(path, paint);
        for (Checker c : checkers)
            c.draw(canvas);
    }

    public void addChecker(int color) {
        Checker newCheker;
        PointF position;
        if (checkers.isEmpty())
            position = new PointF(c.x, a.y + orientation * Checker.getSize());
        else {
            PointF lastChecker = checkers.get(checkers.size() - 1).getPosition();
            position = new PointF(lastChecker.x, lastChecker.y + orientation * Checker.getSize() * 2);
        }
        newCheker = new Checker(color, position);
        checkers.add(newCheker);
        if (checkers.size() > 5) {
            moveCheckers();
        }
    }

    private void moveCheckers() {
        float space;
        if (checkers.size() < 6) {
            space = orientation * Checker.getSize() * 2;
        } else
            space = (height - a.y) / (checkers.size());
        for (int i = 1; i < checkers.size(); i++) {
            PointF newPos = new PointF(c.x, checkers.get(i - 1).getPosition().y + space);
            checkers.get(i).setPosition(newPos);
        }
    }

    public Checker getTopChecker() {
        return checkers.get(checkers.size() - 1);
    }

    public void setColor(Paint color) {
        this.paint = color;
    }

    public Checker removeChecker() {
        Checker temp = checkers.remove(checkers.size() - 1);
        moveCheckers();
        return temp;
    }

    public boolean inside(PointF position) {
        double A = area(a,b,c);

        double A1 = area(position, b, c);
        double A2 = area(a, position, c);
        double A3 = area(a, b, position);
        double sum = A1 + A2 + A3;
        return (A + Math.pow(Checker.getSize(), 2)*3 >= sum);

        //return (new RectF(a.x < b.x? a.x: b.x, a.y < c.y? a.y: c.y,a.x > b.x? a.x: b.x, a.y > c.y? a.y: c.y).contains(position.x, position.y));
    }

    private static double area(PointF p1, PointF p2, PointF p3) {
        return Math.abs((p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x * (p1.y - p2.y)) / 2.0);
    }


}