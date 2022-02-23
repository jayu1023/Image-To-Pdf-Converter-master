package com.daily.image.pdf.imagetopdfconvert.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.daily.image.pdf.imagetopdfconvert.fragment.SignIn;
import com.daily.image.pdf.imagetopdfconvert.fragment.SignUp;

public class PageAdapter extends FragmentPagerAdapter {

    public PageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        if(position==0)
        {
            fragment =new SignUp();
        }

        if(position==1)
        {
            fragment =new SignIn();
        }

        return fragment;
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String s="";

        if(position==0)
        {
            s="SignUp";
        }
        if(position==1)
        {
            s="SignIn";
        }

        return s;
    }

}
