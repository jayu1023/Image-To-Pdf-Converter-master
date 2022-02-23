package com.daily.image.pdf.imagetopdfconvert.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.adapter.PageAdapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;


public class loginscreen extends AppCompatActivity {

    TabLayout tabLayout;
//    TabItem tabItem1,tabItem2;
    ViewPager viewPager;
//    PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);

        tabLayout = findViewById(R.id.tablayout1);
//        tabItem1 = findViewById(R.id.tab1);
//        tabItem2 = findViewById(R.id.tab2);
        viewPager = findViewById(R.id.vpager);

        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager);

    }
}