package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import static com.example.myapplication.UtilParameter.*;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements GameLogic.GameInterface, View.OnTouchListener,
        SensorEventListener {

    private GameLogic gameLogic;
    private boolean shakeEnabled = false;
    private boolean clickEnabled = false;

    private TextView messageBox;
    private TextView[] players = new TextView[2];
    private int player = 1;
    private BoardView table;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean oldValShake;
    private boolean oldValClick;
    private boolean paused = false;

    private boolean shaking;
    private float accelLastGravity = 0.0f;
    private float accelCurrentGravity = 0.0f;
    private float accel = 0f;
    private float alpha = 0.8f;
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
        if(!continueGame) {
            String player1 = intent.getStringExtra("PLAYER1");
            String player2 = intent.getStringExtra("PLAYER2");
            int compNum = intent.getIntExtra("COMP_NUM", -1);

            ImageData imageData = table.getImageData();
            setPlayersData(player1, player2);
            gameLogic = new GameLogic(compNum, player1, player2, this, imageData);
            imageData.setController(gameLogic);
        }else{
            ImageData imageData = table.getImageData();
            gameLogic = new GameLogic(this, imageData);
            imageData.setController(gameLogic);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

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
    public void setShakeEnable(boolean shakeEnable) {
        this.shakeEnabled = shakeEnable;
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

    public void setActivePlayer(int num){
        player = num;
    }
    @Override
    public void changeActivePlayer() {
        players[player].setTextColor(Color.WHITE);
        player = (player + 1) % 2;
        players[player].setTextColor(Color.parseColor("#a18d78"));
    }


    @Override
    public void setDices(ArrayList<Integer> one, ArrayList<Integer> two) {
        if(one != null) {
            ((DiceView) findViewById(R.id.diceOne)).setNumber(one);
            ((DiceView) findViewById(R.id.diceOne)).invalidate();
        }
        if(two != null) {
            ((DiceView) findViewById(R.id.diceTwo)).setNumber(two);
            ((DiceView) findViewById(R.id.diceTwo)).invalidate();
        }
    }

    @Override
    public void finishGame(String player1, String player2, String winner) {
        Intent intent = new Intent(this, TwoPlayersStatisticActivity.class);
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
        if(paused) {
            shakeEnabled = oldValShake;
            clickEnabled = oldValClick;
            paused = false;
        }
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        sensorManager.registerListener(this, accelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        sensorManager.unregisterListener(this);
        oldValShake = shakeEnabled;
        shakeEnabled = false;
        oldValClick = clickEnabled;
        clickEnabled = false;
        paused = true;
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (shakeEnabled) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float SHAKE_THRESHOLD = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE).getFloat(DETECT_SHAKE, UtilParameter.SHAKE_THRESHOLD);
            float MIN_SHAKE_THRESHOLD = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE).getFloat(MIN_SHAKE, UtilParameter.MIN_SHAKE_THRESHOLD);
            accelLastGravity = accelCurrentGravity;
            accelCurrentGravity = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = (float)Math.abs(accelCurrentGravity - accelLastGravity);

            accel =  accel * alpha + (1-alpha)*delta;
            if (accel > SHAKE_THRESHOLD && !shaking) {
                shakeStarted();
            }
            else if(accel < 3f && shaking)
                shakeStopped();
        }

    }

    private void shakeStopped() {
        shaking = false;
        gameLogic.dicesThrown();
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }

    private void shakeStarted() {
        shaking = true;
        mediaPlayer = MediaPlayer.create(this, R.raw.dices);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
