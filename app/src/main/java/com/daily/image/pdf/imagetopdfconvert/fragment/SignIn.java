package com.daily.image.pdf.imagetopdfconvert.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.image.pdf.imagetopdfconvert.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;

import es.dmoral.toasty.Toasty;

public class SignIn extends Fragment {

    FirebaseAuth fauth;
    Context c1;
    TextInputLayout emailnputlayout,passwordlayout;
    TextInputEditText emailtxtfield,passwordtxtfield;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ftab2, container, false);
    }

    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        c1=context;
        fauth=FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailtxtfield=view.findViewById(R.id.emailtextfield);
        view.findViewById(R.id.btnsignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailtxtfield.getText().toString().isEmpty()||passwordtxtfield.getText().toString().isEmpty()){
                    Toasty.error(c1,"textfield is empty").show();
                }else {

                }
            }
        });
    }
}