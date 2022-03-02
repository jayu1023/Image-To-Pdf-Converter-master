package com.daily.image.pdf.imagetopdfconvert.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Splash_activity extends AppCompatActivity {

    ActivitySplashBinding splashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        splashBinding = DataBindingUtil.setContentView(Splash_activity.this, R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    new Intent(getApplicationContext(), loginscreen.class));
                }

                finish();

            }
        }, 5 * 1000);
    }
}