package com.daily.image.pdf.imagetopdfconvert.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.image.pdf.imagetopdfconvert.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class SignUp extends Fragment {

    FirebaseAuth mauth;
    CircularProgressIndicator cp;
    CardView signup;
    TextInputEditText emailtext,passwordtext;
    Context context;



    public SignUp(Context ce){
        this.context=ce;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ftab1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cp=view.findViewById(R.id.progress);
      signup=view.findViewById(R.id.btnsignup);
        mauth=FirebaseAuth.getInstance();
        emailtext=view.findViewById(R.id.emailtxtfld);
        passwordtext=view.findViewById(R.id.passwordtxtfld);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mauth!=null){
                if(emailtext.getText().toString().trim().isEmpty()||passwordtext.getText().toString().trim().isEmpty())
                {
                    Toasty.error(context,"field is empty").show();
                }else{
                    cp.setVisibility(View.VISIBLE);


                    /* Custom setting to change TextView text,Color and Text Size according to your Preference*/



                    try {

                        mauth.createUserWithEmailAndPassword(emailtext.getText().toString().trim(),passwordtext.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {

                                    task.getResult().getUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                cp.setVisibility(View.GONE);
                                                //progressDialog.dismiss();
                                                Toasty.success(context,"Please verify your link").show();
                                            }else {
                                                Toasty.error(context,task.getException().getMessage()).show();
                                                 cp.setVisibility(View.GONE);
                                                //progressDialog.dismiss();
                                            }
                                        }
                                    });
                                    Toasty.success(context,"created").show();
                                }else
                                {                                    cp.setVisibility(View.GONE);
                                //progressDialog.dismiss();

                                    Toasty.error(context,task.getException().getLocalizedMessage()).show();
                                }
                            }
                        });
                    }catch (Exception e){
                        cp.setVisibility(View.GONE);
                        //progressDialog.dismiss();
                        Toasty.error(context,e.getMessage()).show();
                    }

                }}else{
                    cp.setVisibility(View.GONE);
                    //progressDialog.dismiss();
                    Toast.makeText(context, "errorrroroororr", Toast.LENGTH_SHORT).show();
                }
            }
        });



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