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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class SignIn extends Fragment {

    FirebaseAuth fauth;
    Context c1;
    FirebaseUser currentuser;
    TextInputLayout emailnputlayout, passwordlayout;
    TextInputEditText emailtxtfield, passwordtxtfield;

    public SignIn(Context c2) {
        this.c1 = c2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ftab2, container, false);
    }

    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        c1 = context;
        fauth = FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailtxtfield = view.findViewById(R.id.emailtextfield);
        passwordtxtfield = view.findViewById(R.id.passwordtextfield);
        view.findViewById(R.id.btnsignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailtxtfield.getText().toString().isEmpty() || passwordtxtfield.getText().toString().isEmpty()) {
                    Toasty.error(c1, "textfield is empty").show();
                } else {

                    ///
                    ///create user with email and password
                    ///
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        if(fauth.getCurrentUser().isEmailVerified())
                        {
                            Toasty.success(c1,"succes you have login");
                        }else
                        {
                            fauth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                    }else{
                                        Toasty.error(c1,task.getException().toString()).show();
                                    }
                                }
                            });
                        }


                    } else {
                        fauth.createUserWithEmailAndPassword(emailtxtfield.getText().toString(), passwordtxtfield.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                                Toasty.success(c1, "Please verify your email address!!").show();
                                            } else {
                                                Toasty.error(c1, task.getException().toString()).show();
                                            }
                                        }
                                    });

                                } else {
                                    Toasty.error(c1, task.getException().toString());
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(c1, e.getMessage().toString()).show();
                            }
                        });
                    }

                }
            }
        });
    }
}