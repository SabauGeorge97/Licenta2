package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.GUIComponents.BoardView;
import com.example.myapplication.GUIComponents.Checker;
import com.example.myapplication.GUIComponents.DiceView;
import com.example.myapplication.GUIComponents.ImageData;
import com.example.myapplication.gameLogic.GameLogic;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements GameLogic.GameInterface, View.OnTouchListener {

    private GameLogic gameLogic;
    private boolean clickEnabled = false;
    private TextView messageBox;
    private TextView[] players = new TextView[2];
    private int player = 1;
    private BoardView table;
    private boolean oldValClick;
    private boolean paused = false;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        players[0] = ((TextView) findViewById(R.id.player1Name));
        players[1] = ((TextView) findViewById(R.id.player2Name));
        players[1].setTextColor(Color.parseColor("#a18d78"));
        table = ((BoardView) findViewById(R.id.board));
        table.setOnTouchListener(this);
        messageBox = ((TextView) findViewById(R.id.notification));
        mediaPlayer = MediaPlayer.create(this, R.raw.dices);
        mediaPlayer.setLooping(true);

        Intent intent = getIntent();
        boolean continueGame = intent.getBooleanExtra("savedGame", false);
        if (!continueGame) {
            String player1 = intent.getStringExtra("PLAYER1");
            String player2 = intent.getStringExtra("PLAYER2");
            int compNum = intent.getIntExtra("COMP_NUM", -1);

            ImageData imageData = table.getImageData();
            setPlayersData(player1, player2);
            gameLogic = new GameLogic(compNum, player1, player2, this, imageData);
            imageData.setController(gameLogic);
        } else {
            ImageData imageData = table.getImageData();
            gameLogic = new GameLogic(this, imageData);
            imageData.setController(gameLogic);
        }

    }

    // se stabilesc numele jucătorilor
    @Override
    public void setPlayersData(String player1, String player2) {
        ((TextView) findViewById(R.id.player1Name)).setText(player1);
        ((TextView) findViewById(R.id.player2Name)).setText(player2);

        final ImageView image1 = ((ImageView) findViewById(R.id.player1Checker));
        image1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                image1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setImage(image1, Color.WHITE);
            }
        });
        final ImageView image2 = ((ImageView) findViewById(R.id.player2Checker));
        image2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                image2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setImage(image2, Color.DKGRAY);
            }
        });
    }

    // în această funcție vom desene cele 2 checkere
    private void setImage(ImageView image, int color) {
        int w = image.getWidth();
        Bitmap tempBitmap = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);
        Checker tempChecker = new Checker(color, new PointF(w / 2, w / 2));
        tempCanvas.drawCircle(w / 2, w / 2, w / 2 - 3, tempChecker.getColor());
        tempCanvas.drawCircle(w / 2, w / 2, w / 2 - 3, tempChecker.getBorderColor());
        image.setImageBitmap(tempBitmap);
    }


    @Override
    public void setClickEnable(boolean clickEnable) {
        this.clickEnabled = clickEnable;
    }

    @Override
    public void refresh(String message, boolean renderImage) {
        messageBox.setText(message);
        if (renderImage)
            table.invalidate();
    }

    //jucătorul activ la momentul actual
    public void setActivePlayer(int num) {
        player = num;
    }

    // schimbarea jucătorului
    @Override
    public void changeActivePlayer() {
        players[player].setTextColor(Color.WHITE);
        player = (player + 1) % 2; // schimbă jucătorul
        players[player].setTextColor(Color.parseColor("#a18d78"));
    }


    @Override
    public void setDices(ArrayList<Integer> one, ArrayList<Integer> two) {
        if (one != null) {
            ((DiceView) findViewById(R.id.diceOne)).setNumber(one);
            ((DiceView) findViewById(R.id.diceOne)).invalidate();
        }
        if (two != null) {
            ((DiceView) findViewById(R.id.diceTwo)).setNumber(two);
            ((DiceView) findViewById(R.id.diceTwo)).invalidate();
        }
    }

    @Override
    public void finishGame(String player1, String player2, String winner) {
        Intent intent = new Intent(this, TwoPlayersStatisticActivity.class);
        //putExtra
        intent.putExtra("Player1", player1);
        intent.putExtra("Player2", player2);
        intent.putExtra("Winner", winner);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!clickEnabled)
            return false;
        PointF position = new PointF(event.getX(), event.getY());
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                gameLogic.fingerDown(position);
                return true;
            case MotionEvent.ACTION_MOVE:
                gameLogic.fingerMove(position);
                return true;
            case MotionEvent.ACTION_UP:
                gameLogic.fingerUp(position);
                return true;

        }
        return false;
    }

    public void roll(View v) {
        gameLogic.dicesThrown();
    }

    @Override
    public void onBackPressed() {
        gameLogic.saveGame();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        gameLogic.saveGame();
        super.onStop();
    }

    @Override
    public void onResume() {
        if (paused) {
            clickEnabled = oldValClick;
            paused = false;
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        oldValClick = clickEnabled;
        clickEnabled = false;
        paused = true;
        super.onPause();
    }
}

