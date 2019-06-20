package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


//pentru crearea de fisiere
import java.io.File;


public class  MainActivity extends AppCompatActivity {

    //"onCreate"
    //este apelat atunci cand activitatea este creata prima data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    //"onStart"
    //apelata atunci cand activitatea devine vizibila utilizatorului
    @Override
    protected void onStart(){
        super.onStart();

        //cautam daca exista vreun joc(fisier) salvat
        String path = getFilesDir().getAbsolutePath() + "/savedGame.txt";
        File file = new File (path);

        if(!file.exists())
            ((Button)findViewById(R.id.continueGame)).setEnabled(false);
        else
            ((Button)findViewById(R.id.continueGame)).setEnabled(true);
    }

    //crearea unui nou joc
    public void newGame(View v){
        Intent intent = new Intent(this, SelectGameActivity.class);
        startActivity(intent);
    }

    //continuarea unui joc salvat
    public void continueGame(View v){
        Intent intent = new Intent(this,GameActivity.class);
        intent.putExtra("savedGame",true);
        startActivity(intent);
    }
}

