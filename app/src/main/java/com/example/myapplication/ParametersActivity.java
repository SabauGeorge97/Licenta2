package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import static com.example.myapplication.UtilParameter.DEFAULT_DETECT_SHAKE;
import static com.example.myapplication.UtilParameter.DEFAULT_MIN_SHAKE;
import static com.example.myapplication.UtilParameter.DETECT_SHAKE;
import static com.example.myapplication.UtilParameter.MIN_SHAKE;
import static com.example.myapplication.UtilParameter.MIN_SHAKE_THRESHOLD;
import static com.example.myapplication.UtilParameter.PREF_FILE_NAME;
import static com.example.myapplication.UtilParameter.SHAKE_THRESHOLD;

public class ParametersActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        ((EditText)findViewById(R.id.detectShake)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    hideKeyboard(v);
            }
        });

        ((EditText)findViewById(R.id.minShake)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    hideKeyboard(v);
            }
        });
    }

    private void hideKeyboard(View v){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
    }

    public void save (View view){
        String text = ((EditText)findViewById(R.id.detectShake)).getText().toString();
        SharedPreferences settings = getSharedPreferences(PREF_FILE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        boolean change = false;

        if(!text.isEmpty()){
            change = true;
            editor.putFloat(DETECT_SHAKE,Float.parseFloat(text));
            ((EditText)findViewById(R.id.detectShake)).setText("");
        }
        text = ((EditText)findViewById(R.id.minShake)).getText().toString();
        if(!text.isEmpty()){
            change = true;
            editor.putFloat(MIN_SHAKE,Float.parseFloat(text));
            ((EditText)findViewById(R.id.minShake)).setText("");
        }
        editor.apply();
        if(change)
            Toast.makeText(this,"Changes saved",Toast.LENGTH_SHORT).show();
    }

    public void reset (View view){
        SharedPreferences settings = getSharedPreferences(PREF_FILE_NAME,MODE_PRIVATE);
        float defaultShake = settings.getFloat(DEFAULT_DETECT_SHAKE,SHAKE_THRESHOLD);
        float minShake = settings.getFloat(DEFAULT_MIN_SHAKE,MIN_SHAKE_THRESHOLD);

        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(DETECT_SHAKE,defaultShake);
        editor.putFloat(MIN_SHAKE,minShake);
        editor.apply();

        Toast.makeText(this,"Parameters restored to default values",Toast.LENGTH_SHORT).show();
    }
}
