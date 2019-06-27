package com.example.myapplication.GUIComponents;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.example.myapplication.GUIComponents.Blot;
import com.example.myapplication.GUIComponents.Border;
import com.example.myapplication.GUIComponents.Checker;
import com.example.myapplication.GUIComponents.Triangle;
import com.example.myapplication.gameLogic.GameLogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ImageData {
    private int w,h;
    private float wunit,hunit;
    private Border leftBar,rightBar,topBar,bottomBar;
    private Blot blot;
    private Triangle[] board = new Triangle[24];
    private GameLogic controller;
    private HashMap<Integer, Checker> highlightedCheckers;
    Paint triangleColor[];
    private Checker selectedChecker;

    //dimensiunea ecranului
    public void setSize(int w,int h){
        this.w = w;
        this.h = h;

        wunit = w/15f;
        hunit = h/16f;
        createShapes();
        Checker.setSize(hunit/1.6f);
        controller.sizeSet();
    }

    // desenarea tablei
    private void createShapes(){
        topBar = new Border (0 , w, 0 , hunit / 2);
        bottomBar = new Border (0 , w , h - hunit / 2, h);
        blot = new Blot (7*wunit,8*wunit,0 , h);
        leftBar = new Border(0,wunit,0,h);
        rightBar = new Border(w-wunit,w,0,h);

        //triunghirile
        Paint brightColor = createTrianglePaint(Color.parseColor("#E3854E"));
        Paint darkColor = createTrianglePaint(Color.parseColor("#9B4C1E"));
        triangleColor = new Paint[2];
        triangleColor[0] = brightColor;
        triangleColor[1] = darkColor;


        //2%2 = triunghi alb
        for (int i = 0; i < 6; i++)
            board[i] = new Triangle((13 - i) * wunit, h - hunit / 2, (14 - i) * wunit, h - hunit / 2, (13 - i + 0.5f) * wunit, h - 7.5f * hunit, triangleColor[i % 2], -1);

        for (int i = 6; i < 12; i++)
            board[i] = new Triangle((12 - i) * wunit, h - hunit / 2, (13 - i) * wunit, h - hunit / 2, (12 - i + 0.5f) * wunit, h - 7.5f * hunit, triangleColor[i % 2], -1);

        for (int i = 12; i < 18; i++)
            board[i] = new Triangle((i - 11) * wunit, hunit / 2, (i - 10) * wunit, hunit / 2, (i - 11 + 0.5f) * wunit, 7.5f * hunit, triangleColor[i % 2], 1);

        for (int i = 18; i < 24; i++)
            board[i] = new Triangle((i - 10) * wunit, hunit / 2, (i - 9) * wunit, hunit / 2, (i - 10 + 0.5f) * wunit, 7.5f * hunit, triangleColor[i % 2], 1);

    }


    private Paint createTrianglePaint(int color) {
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        return paint;
    }

    // functia desenarea
    public void draw(Canvas canvas) {
        topBar.draw(canvas);
        bottomBar.draw(canvas);
        leftBar.draw(canvas);
        rightBar.draw(canvas);
        blot.draw(canvas);
        Paint lineColor = new Paint();
        lineColor.setStrokeWidth(3);
        canvas.drawLine(w / 2, 0, w / 2, h, lineColor);
        blot.drawElements(canvas);
        for (Triangle t : board)
            t.draw(canvas);
        if (selectedChecker != null)
            selectedChecker.draw(canvas);
    }

    public void setCheckers(int row, int checkerNum, int color) {
        for (int i = 0; i < checkerNum; i++) {
            board[row].addChecker(color);
        }
    }

    public void setController(GameLogic controller) {
        this.controller = controller;
    }

    public void highlightCheckers(Set<Integer> rows) {
        highlightedCheckers = new HashMap<>();
        Paint newColor = new Paint();
        newColor.setColor(Color.GREEN);
        newColor.setStrokeWidth(4);
        for (Integer r : rows) {
            highlightedCheckers.put(r, board[r].getTopChecker());
            highlightedCheckers.get(r).setBorderColor(newColor);
        }

    }

    public void resetColorChecker() {
        Paint newColor = new Paint();
        newColor.setStyle(Paint.Style.STROKE);
        newColor.setColor(Color.BLACK);
        newColor.setStrokeWidth(2);
        for (Checker c : highlightedCheckers.values())
            c.setBorderColor(newColor);
        highlightedCheckers = null;

    }


    public void highlightTriangles(ArrayList<Integer> rows) {
        Paint temp = createTrianglePaint(Color.GREEN);
        for (Integer i : rows) {
            if (i == 24)
                rightBar.setColor(temp);
            else
                board[i].setColor(temp);
        }
    }

    public void resetColorTriangles(ArrayList<Integer> rows) {
        for (Integer i : rows)
            if (i == 24)
                rightBar.setDefaultColor();
            else
                board[i].setColor(triangleColor[i % 2]);
    }

    // dupa ce dai click pe ea , se highlightuieste
    public int highlightedClicked(PointF position) {
        if (highlightedCheckers == null)
            return -2;
        for (Map.Entry<Integer, Checker> c : highlightedCheckers.entrySet())
            if (c.getValue().clicked(position)) {
                selectedChecker = c.getValue();
                if (c.getKey() == -1)
                    blot.removeChecker(selectedChecker.getColor().getColor());
                else
                    board[c.getKey()].removeChecker();
                return c.getKey();
            }
        return -2;
    }

    //ia cercul si-l muta pe o noua pozitie
    public void moveChecker(PointF position) {
        if (selectedChecker != null)
            selectedChecker.setPosition(position);
    }

    public void putSelectedCheckerOnBoard(int r) {
        if (r == -1)
            blot.addChecker(selectedChecker.getColor().getColor());
        else
            board[r].addChecker(selectedChecker.getColor().getColor());
        selectedChecker = null;
    }


    public int dropOnTriangle(PointF position, ArrayList<Integer> rows) {
        for (Integer r : rows) {
            if (r == 24)
                continue;
            if (board[r].inside(position)) {
                return r;
            }
        }
        return -2;
    }

    public void moveToBlot(int r) {
        Checker eaten = board[r].removeChecker();
        blot.addChecker(eaten.getColor().getColor());
    }

    public void highlightFromBlot(int r, int checkers) {
        highlightedCheckers = new HashMap<>();
        Paint newColor = new Paint();
        newColor.setColor(Color.GREEN);
        newColor.setStrokeWidth(4);
        Checker c;
        if (checkers > 0)
            c = blot.getTopBlack();
        else
            c = blot.getTopWhite();
        highlightedCheckers.put(r, c);
        c.setBorderColor(newColor);
    }

    public boolean hasMoreOnBlot(int checkers) {
        if (checkers > 0)
            return blot.blackCount() > 0;
        else
            return blot.whiteCount() > 0;
    }

    //dupa ce am mutat o piesa,sa o scoata din pozitia veche
    public void removeChecker(int oldPosition) {
        board[oldPosition].removeChecker();
    }

    //ca sa dispara cercul de pe mijloc
    public void removeFromBlot(int checkerColor) {
        blot.removeChecker(checkerColor);
    }

    //daca e dat click pe checker, si dupaia dai click pe alt checker,cel selectat primul sa nu mai fie selectat
    public void removeSelectedChecker() {
        selectedChecker = null;
    }


    public boolean droppedOnHomeBar(PointF position) {
        return rightBar.getPosition().contains(position.x, position.y);
    }

    // scoate piesa de pe tabla
    public void addToBlot(int num, int color) {
        for(int i = 0; i < num; i++)
            blot.addChecker(color);
    }
}
