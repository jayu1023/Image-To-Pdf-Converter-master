package com.daily.image.pdf.imagetopdfconvert.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityLoginscreenBinding;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityMainBinding;

public class loginscreen extends AppCompatActivity {
ActivityLoginscreenBinding loginBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(loginscreen.this, R.layout.activity_loginscreen);
    }
}