package com.sahaprojects.drivechat.Animations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.sahaprojects.drivechat.R;

public class FirstTimeSplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Boolean firstTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animated_splash_screen);
        sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);

        firstTime = sharedPreferences.getBoolean("firstTime",true);
        if (firstTime){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    firstTime = false;
                    editor.putBoolean("firstTime",firstTime);
                    editor.apply();

                    Intent i  = new Intent(FirstTimeSplashScreen.this, OpeningSplashScreen.class);
                    startActivity(i);
                    finish();
                }
            }, 3000);
        }
        else {
            Intent i  = new Intent(FirstTimeSplashScreen.this, OpeningSplashScreen.class);
            startActivity(i);
            finish();
        }


    }
}