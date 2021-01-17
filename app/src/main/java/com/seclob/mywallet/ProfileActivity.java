package com.seclob.mywallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView Auname,Aumobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        Auname = findViewById(R.id.Auname);
        Aumobile = findViewById(R.id.Aumobile);
        Auname.setText(sharedPreferences.getString("shopname","")+" ("+sharedPreferences.getString("name","")+")");
        Aumobile.setText(sharedPreferences.getString("phone",""));
    }

    public void GoBack(View view)
    {
        super.onBackPressed();
    }

    public void Logout(View view)
    {
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putString("shop_id", "");
        editor.putString("login_id", "");
        editor.apply();

        Intent i = new Intent(getApplicationContext(),LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    public void OpenStatement(View view)
    {
        Intent intents = new Intent(ProfileActivity.this, RechargeHostoryActivity.class);
        startActivity(intents);
    }

    public void OpenWallet(View view)
    {
        Intent intents = new Intent(ProfileActivity.this, WalletActivity.class);
        startActivity(intents);
    }

}