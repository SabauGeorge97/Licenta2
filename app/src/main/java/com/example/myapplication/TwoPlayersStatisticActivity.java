package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TwoPlayersStatisticActivity extends AppCompatActivity {
    String player1;
    String player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_players_statistic);

        Intent intent = getIntent();
        player1 = intent.getStringExtra("Player1");
        player2 = intent.getStringExtra("Player2");
        String winner = intent.getStringExtra("Winner");

        TextView gameInfo = ((TextView) findViewById(R.id.gameinfo));
        if (winner == null) {
            gameInfo.setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.removedata)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.removedata)).setEnabled(true);
        } else
            gameInfo.setText("GAME OVER.\nThe winner is " + winner);

    }
}