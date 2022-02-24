package com.daily.image.pdf.imagetopdfconvert.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.image.pdf.imagetopdfconvert.R;

public class SignUp extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ftab1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}


/*
*
*
*   fauth.createUserWithEmailAndPassword(emailtxtfield.getText().toString(),
                                passwordtxtfield.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    currentuser = fauth.getCurrentUser();


                                    ///
                                    ///send verification
                                    ///


                                    currentuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                circularProgressIndicator.setVisibility(View.VISIBLE);
                                                Toasty.success(c1, "Please verify your email address!!").show();
                                                resendbutton.setVisibility(View.GONE);
                                            } else {

                                                circularProgressIndicator.setVisibility(View.GONE);
                                                Toasty.error(c1, task.getException().toString()).show();
                                            }
                                        }
                                    });

                                } else {
                                    circularProgressIndicator.setVisibility(View.GONE);
                                    Toasty.error(c1, task.getException().toString());
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                circularProgressIndicator.setVisibility(View.GONE);
                                Toasty.error(c1, e.getMessage().toString()).show();
                            }
                        });
*
* */