package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TwoPlayersStatisticActivity extends AppCompatActivity {
    String player1;
    String player2;
    // StatisticController controller;

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

        // controller = new StatisticController(this);
        // Cursor cursor = controller.selectTwoPlayerStatistic(player1, player2);

        // int[] to = {R.id.player1_item, R.id.player2_item, R.id.gametime};
        // SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from, to, 0);

        //ListView lv = ((ListView) findViewById(R.id.list));

        // adapter.setViewBinder(new CustomViewBinder());
        // lv.setAdapter(adapter);

        setSummary();
    }

    private void setSummary() {
        // long count1 = controller.getNumberOfWins(player1, player2, player1);
        // long count2 = controller.getNumberOfWins(player1, player2, player2);

        // ((TextView)findViewById(R.id.player1_all)).setText(player1 + ": "+count1);
        // ((TextView)findViewById(R.id.player2_all)).setText(count2 + ": "+player2);
    }

    public void deleteData(View v){
        // controller.deleteData(player1, player2);
        ListView lv = ((ListView) findViewById(R.id.list));
        ((SimpleCursorAdapter)lv.getAdapter()).changeCursor(null);

        finish();
    }
}