package com.seclob.mywallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
        ChangeActivity();
            }
        }, 1000);
    }

    public void ChangeActivity()
    {

        if(sharedPreferences.getString("username","").length()>2 && sharedPreferences.getString("password","").length()>2)
        {
            Intent diya = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(diya);
            finish();

        }else
        {
            Intent diya = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(diya);
            finish();

        }
    }
}