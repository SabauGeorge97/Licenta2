package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

//pentru crearea de fisiere
import java.io.File;

import static com.example.myapplication.UtilParameter.*;

public class  MainActivity extends AppCompatActivity {

    //"onCreate"
    //este apelat atunci cand activitatea este creata prima data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        ((ImageView) findViewById(R.id.logo)).setImageBitmap(img);

        //accesam si modificam 'setarile'
        SharedPreferences settings = getSharedPreferences(PREF_FILE_NAME,MODE_PRIVATE);

        //setam 'shake-ul'
        float detectShake = settings.getFloat(DEFAULT_DETECT_SHAKE,-100);
        if(detectShake == -100)
        {
            SharedPreferences.Editor editor = settings.edit();
         editor.putFloat(DEFAULT_DETECT_SHAKE,7f);
         editor.putFloat(DEFAULT_DETECT_SHAKE,0.8f);
         editor.apply();
        }
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

    public void openParameters(View view){
        Intent intent = new Intent(this,ParametersActivity.class);
        startActivity(intent);
    }
}
