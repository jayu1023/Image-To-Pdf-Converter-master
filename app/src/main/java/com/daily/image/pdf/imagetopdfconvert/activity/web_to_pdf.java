package com.daily.image.pdf.imagetopdfconvert.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityMainBinding;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityWebToPdfBinding;

public class web_to_pdf extends AppCompatActivity {
    private ActivityWebToPdfBinding myBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       myBinding= DataBindingUtil.setContentView(web_to_pdf.this,R.layout.activity_web_to_pdf);
    }
}