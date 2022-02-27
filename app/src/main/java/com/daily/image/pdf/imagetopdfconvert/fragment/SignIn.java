package com.daily.image.pdf.imagetopdfconvert.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class SignIn extends Fragment {

    FirebaseAuth fauth;
    Context c1;
    TextView resendbutton,btnforgotpasword,sendEmail,btnSignin;
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
        sendEmail = view.findViewById(R.id.send);
        btnSignin = view.findViewById(R.id.btnsignin);

        fauth = FirebaseAuth.getInstance();

        btnforgotpasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailtxtfield.getText().toString().isEmpty())
                {
                  Toasty.error(c1,"Your email is empty").show();

                }else{
                    fauth.sendPasswordResetEmail(emailtxtfield.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        btnforgotpasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                passwordtxtfield.setVisibility(view.GONE);
                btnSignin.setVisibility(view.GONE);
                btnforgotpasword.setVisibility(view.GONE);

                sendEmail.setVisibility(view.VISIBLE);


            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    fauth.sendPasswordResetEmail(emailtxtfield.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            try {
                                if(task.isSuccessful())
                                {
                                    Toasty.success(c1,"email sent succesfully!!").show();

                                    passwordtxtfield.setVisibility(view.VISIBLE);
                                    btnSignin.setVisibility(view.VISIBLE);
                                    btnforgotpasword.setVisibility(view.VISIBLE);

                                    sendEmail.setVisibility(view.GONE);


                                }else
                                {
                                    Toasty.error(c1,task.getException().getMessage()).show();

                                }
                            }catch (Exception e){
                                Toasty.error(c1,e.getMessage()).show();

                            }


                        }
                    });
                } catch (Exception e) {
                    Toasty.error(c1,e.getMessage()).show();
                }

            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fauth.getCurrentUser().reload();

                if (emailtxtfield.getText().toString().trim().isEmpty() || passwordtxtfield.getText().toString().trim().isEmpty()) {
                    Toasty.error(c1, "textfield is empty").show();
                } else {

                    circularProgressIndicator.setVisibility(View.VISIBLE);

                    try {

                        fauth.signInWithEmailAndPassword(emailtxtfield.getText().toString().trim(),passwordtxtfield.getText().toString().trim()).addOnSuccessListener(
                                new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        if(authResult.getUser()!=null)
                                        {
                                            if(authResult.getUser().isEmailVerified())
                                            {
                                                circularProgressIndicator.setVisibility(View.GONE);
                                                startActivity(new Intent(c1, MainActivity.class));
//                                                c1.getAp
                                                getActivity().finish();
                                                Toasty.success(c1,"succes").show();
                                            }else{
                                                try {
                                                    authResult.getUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {   circularProgressIndicator.setVisibility(View.GONE);
                                                                Toasty.success(c1,"link has benn sent").show();
                                                            }else
                                                            {   circularProgressIndicator.setVisibility(View.GONE);
                                                                Toasty.error(c1,task.getException().getMessage()).show();
                                                            }
                                                        }
                                                    });
                                                }catch (Exception e){
                                                    circularProgressIndicator.setVisibility(View.GONE);
                                                    Toasty.error(c1,e.getMessage().toString()).show();
                                                }
                                            }
                                        }else
                                        {
                                            circularProgressIndicator.setVisibility(View.GONE);
                                            Toasty.error(c1,"error").show();
                                        }
                                    }
                                }
                        );

                    }catch(Exception e){
                        circularProgressIndicator.setVisibility(View.GONE);
                        Toasty.error(c1,e.getMessage()).show();
                    }


                    ///
                    ///create user with email and password
                    ///
//                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//
//
//                                if (fauth.getCurrentUser().isEmailVerified()) {
//                                    Toasty.success(c1, "succes you have login");
//                                    circularProgressIndicator.setVisibility(View.VISIBLE);
//                                } else {
//                                    fauth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                circularProgressIndicator.setVisibility(View.GONE);
//                                                resendbutton.setVisibility(View.VISIBLE);
//                                                Toasty.success(c1, "link sent to your email").show();
//
//                                            } else {
//                                                circularProgressIndicator.setVisibility(View.GONE);
//                                                Toasty.error(c1, task.getException().getMessage().toString()).show();
//                                            }
//                                        }
//                                    });
//                                }
//
//
//
//
//                    } else {
//                        circularProgressIndicator.setVisibility(View.GONE);
//
////                        fauth.signInWithEmailAndPassword(emailtxtfield.getText().toString(),
////                                passwordtxtfield.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
////                            @Override
////                            public void onComplete(@NonNull Task<AuthResult> task) {
////                                if (task.isSuccessful()) {
////
////                                    currentuser = fauth.getCurrentUser();
////
////
////                                    ///
////                                    ///send verification
////
////
////
////
////                                } else {
////                                    circularProgressIndicator.setVisibility(View.GONE);
////                                    Toasty.error(c1, task.getException().getLocalizedMessage().toString()).show();
////                                }
////                            }
////                        });
//                    }

                }
            }
        });
    }
}