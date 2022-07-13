package com.sahaprojects.drivechat.Animations;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.sahaprojects.drivechat.View.Welcome;
import com.sahaprojects.drivechat.R;
import com.sahaprojects.drivechat.databinding.ActivitySplashScreenBinding;

public class OpeningSplashScreen extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        binding.sqloading.setVisibility(LottieAnimationView.VISIBLE);
//        System.out.println("STATUS OSS : " + binding.sqloading);

//        new Handler().postDelayed(() -> startActivity(new Intent(SplashScreen.this,Welcome.class)),500);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(OpeningSplashScreen.this, Welcome.class));
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            finish();

        },1000);
    }
}