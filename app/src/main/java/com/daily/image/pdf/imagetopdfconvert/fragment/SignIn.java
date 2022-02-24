package com.daily.image.pdf.imagetopdfconvert.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daily.image.pdf.imagetopdfconvert.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
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
    TextView resendbutton,btnforgotpasword;
    CircularProgressIndicator circularProgressIndicator;
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
//        c1 = context;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resendbutton = view.findViewById(R.id.btnresend);
        circularProgressIndicator = view.findViewById(R.id.progress);

        emailtxtfield = view.findViewById(R.id.emailtextfield);
        passwordtxtfield = view.findViewById(R.id.passwordtextfield);
        btnforgotpasword=view.findViewById(R.id.btnforgotpassword);
        fauth = FirebaseAuth.getInstance();
        btnforgotpasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailtxtfield.getText().toString().isEmpty())
                {
                  Toasty.error(c1,"Your email is empty").show();

                }else{
                    fauth.sendPasswordResetEmail(emailtxtfield.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toasty.success(c1,"password reset link has been dispatched!!").show();
                            }else{
                                Toasty.error(c1,task.getException().getLocalizedMessage().toString()).show();
                            }
                        }
                    });
                }
            }
        });
        resendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circularProgressIndicator.setVisibility(View.VISIBLE);
                if(fauth.getCurrentUser().isEmailVerified())
                {
                    Toasty.success(c1,"login Successfully").show();
                    circularProgressIndicator.setVisibility(View.GONE);
                }else
                {
                    fauth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toasty.success(c1,"email sent to your mail").show();
                                circularProgressIndicator.setVisibility(View.GONE);
                            }else{
                                circularProgressIndicator.setVisibility(View.GONE);
                                Toasty.error(c1,task.getException().getMessage().toString()).show();
                            }
                        }
                    });
                }
            }
        });

        view.findViewById(R.id.btnsignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailtxtfield.getText().toString().isEmpty() || passwordtxtfield.getText().toString().isEmpty()) {
                    Toasty.error(c1, "textfield is empty").show();
                } else {

                    circularProgressIndicator.setVisibility(View.VISIBLE);

                    ///
                    ///create user with email and password
                    ///
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (fauth.getCurrentUser().isEmailVerified()) {
                                    Toasty.success(c1, "succes you have login");
                                    circularProgressIndicator.setVisibility(View.VISIBLE);
                                } else {
                                    fauth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                circularProgressIndicator.setVisibility(View.GONE);
                                                resendbutton.setVisibility(View.VISIBLE);
                                                Toasty.success(c1, "link sent to your email").show();

                                            } else {
                                                circularProgressIndicator.setVisibility(View.GONE);
                                                Toasty.error(c1, task.getException().getMessage().toString()).show();
                                            }
                                        }
                                    });
                                }

                            }
                        }, 900);


                    } else {
                        circularProgressIndicator.setVisibility(View.VISIBLE);

                        fauth.signInWithEmailAndPassword(emailtxtfield.getText().toString(),
                                passwordtxtfield.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    currentuser = fauth.getCurrentUser();


                                    ///
                                    ///send verification




                                } else {
                                    circularProgressIndicator.setVisibility(View.GONE);
                                    Toasty.error(c1, task.getException().getLocalizedMessage().toString()).show();
                                }
                            }
                        });
                    }

                }
            }
        });
    }
}