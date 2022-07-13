package com.sahaprojects.drivechat.Extras;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.sahaprojects.drivechat.View.Chats;
import com.sahaprojects.drivechat.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView chats = findViewById(R.id.chats);
        chats.setOnClickListener((View v) -> {
            Intent intent1 = new Intent(MainActivity.this, Chats.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.no_anim,R.anim.no_anim);
            finish();
        });

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_anim_searching);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        },1000);
    }


}
