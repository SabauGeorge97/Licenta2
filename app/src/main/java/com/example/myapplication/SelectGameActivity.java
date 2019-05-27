package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;



public class SelectGameActivity  extends AppCompatActivity{
    int oldSelectedPlayer1;
    int oldSelectedPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);

        oldSelectedPlayer1 = R.id.human1;
        oldSelectedPlayer2 = R.id.human2;

        //'Default' avem setati 'human1' si 'human2'
        //Ne vom ocupa de focus atunci cand trebuie sa scriem numele

        ((EditText)findViewById(R.id.player1)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    hideKeyboard(v);
            }
        });

        ((EditText)findViewById(R.id.player2)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    hideKeyboard(v);
            }
        });
    }

    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    //atunci cand schimbam 'tipul': Computer sau Human

    public void player1TypeChanged (View v){
        int checked = ((RadioButton) v).getId();
        if(checked == oldSelectedPlayer1)
            return;

        //daca primul plaayer este setat ca si 'Computer'
        if(checked == R.id.computer1){
            ((EditText) findViewById(R.id.player1)).setText("Comp");//setam numele 'Computer' pe <<Comp>>
            ((EditText) findViewById(R.id.player1)).setEnabled(false);//primul jucator este setat ca si 'Computer',deci nu mai poate fi setat ca player
            ((RadioButton) findViewById(R.id.computer2)).setClickable(false);//nu mai putem seta cel de-al 2lea player sa fie tot computer
        } else{
            ((EditText) findViewById(R.id.player1)).setText("");//introducem numele
            ((EditText) findViewById(R.id.player1)).setEnabled(true);//primul jucator este setat ca player propriu-zis
            ((RadioButton) findViewById(R.id.computer2)).setClickable(true);//putem seta cel de-al 2lea player sa fie 'computer'
        }
        oldSelectedPlayer1 = checked;
    }

    public void player2TypeChanged (View v){
        int checked = ((RadioButton) v).getId();
        if(checked == oldSelectedPlayer2)
            return;

        //daca primul plaayer este setat ca si 'Computer'
        if(checked == R.id.computer2){
            ((EditText) findViewById(R.id.player2)).setText("Comp");//setam numele 'Computer' pe <<Comp>>
            ((EditText) findViewById(R.id.player2)).setEnabled(false);//primul jucator este setat ca si 'Computer',deci nu mai poate fi setat ca player
            ((RadioButton) findViewById(R.id.computer1)).setClickable(false);//nu mai putem seta cel de-al 2lea player sa fie tot computer
        } else{
            ((EditText) findViewById(R.id.player2)).setText("");//introducem numele
            ((EditText) findViewById(R.id.player2)).setEnabled(true);//primul jucator este setat ca player propriu-zis
            ((RadioButton) findViewById(R.id.computer2)).setClickable(true);//putem seta cel de-al 2lea player sa fie 'computer'
        }
        oldSelectedPlayer2 = checked;
    }

    public void startGame(View view){
        int compNum = -1;//nu e niciunul selectat

        //numele pentru 'Player1'
        String player1 = ((EditText) findViewById(R.id.player1)).getText().toString();
        //numele pentru 'Player2'
        String player2 = ((EditText) findViewById(R.id.player2)).getText().toString();

        //daca numele playerilor sunt la fel,atunci apare eroare!
        if(player1.equals(player2)){
            ((TextView) findViewById(R.id.error)).setText("Players names must be DIFFERENT!");
            return;
        }

        if(player1 == null || player1.isEmpty() || player2 == null || player2.isEmpty())
            ((TextView) findViewById(R.id.error)).setText("Players names are REQUIRED!");
        else {
            if(oldSelectedPlayer1 == R.id.computer2)
                compNum = 1;
            else if (oldSelectedPlayer1 == R.id.computer1)
                compNum = 0;

            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("COMP_NUM",compNum);
            intent.putExtra("PLAYER1",player1);
            intent.putExtra("PLAYER2",player2);
            intent.putExtra("savedGame",getIntent().getBooleanExtra("savedGame",false));
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        finish();
    }

}
