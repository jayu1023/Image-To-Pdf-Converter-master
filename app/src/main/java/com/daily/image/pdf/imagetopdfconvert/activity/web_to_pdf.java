package com.daily.image.pdf.imagetopdfconvert.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;

import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityMainBinding;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityWebToPdfBinding;

import java.io.File;

import es.dmoral.toasty.Toasty;

public class web_to_pdf extends AppCompatActivity {
    private ActivityWebToPdfBinding myBinding;
    WebView printWeb;
    PrintJob printJob;

    // a boolean to check the status of printing

    boolean printBtnPressed=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       myBinding= DataBindingUtil.setContentView(web_to_pdf.this,R.layout.activity_web_to_pdf);

        myBinding.llShare.setVisibility(View.GONE);
        myBinding.llOpenWith.setVisibility(View.GONE);


        myBinding.webview.setWebViewClient(new WebViewClient()

        {

            @Override

            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);

                // initializing the printWeb Object

                printWeb=myBinding.webview;

            }

        });

      myBinding.BtnRefreshLink.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(myBinding.EditTextofweb.getText().toString().isEmpty())
              {
                  Toasty.error(getApplicationContext(),"fieldd is empty").show();

              }else
              {
                  if(myBinding.webview!=null)
                  {
                      if (Build.VERSION.SDK_INT >= 19) {
                          myBinding.webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                      }
                      myBinding.webview.clearFormData();
                      myBinding.webview.loadUrl(myBinding.EditTextofweb.getText().toString());
                  }else
                  {
                      myBinding.webview.loadUrl(myBinding.EditTextofweb.getText().toString());

                  }

              }
          }
      });

      myBinding.imgFinalClose.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(myBinding.llFinal.getVisibility()==View.VISIBLE)
              {
                  myBinding.llFinal.setVisibility(View.GONE);
                  myBinding.maincode.setVisibility(View.VISIBLE);
              }else
              {

              }
          }
      });


    myBinding.imgProcessClose.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(myBinding.llprocess.getVisibility()==View.VISIBLE)
            {
                myBinding.llprocess.setVisibility(View.GONE);
                myBinding.maincode.setVisibility(View.VISIBLE);
            }else
            {

            }
        }
    });


   myBinding.imgbackarrow.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           finish();
       }
   });

        myBinding.btnConvertWebToPdf.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                    if(myBinding.EditTextofweb.getText().toString().isEmpty()){
                        Toasty.error(getApplicationContext(),"fieldd is empty").show();
                    }else
                    {

                        if (Build.VERSION.SDK_INT >= 19) {
                            myBinding.webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                        }
                        myBinding.webview.loadUrl(myBinding.EditTextofweb.getText().toString());

//                        myBinding.EditTextofweb.setVisibility(View.GONE);
                        if(printWeb!=null)

                        {


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                                // Calling createWebPrintJob()

                                myBinding.llprocess.setVisibility(View.VISIBLE);
                                myBinding.maincode.setVisibility(View.GONE);

                               Handler mHandler = new Handler();
                              Runnable  mRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        myBinding.llprocess.setVisibility(View.GONE);
                                       myBinding.llFinal.setVisibility(View.VISIBLE);
                                        PrintTheWebPage(printWeb);
                                    }
                                };

                                // its trigger runnable after 4000 millisecond.
                                mHandler.postDelayed(mRunnable,4000);





                            }else

                            {

                                // Showing Toast message to user

                                Toast.makeText(getApplicationContext()," Not available for device below Android LOLLIPO",Toast.LENGTH_SHORT).show();

                            }

                        }

                        else

                        {

                            // Showing Toast message to user

                            Toast.makeText(getApplicationContext(), "WebPage not fully loaded", Toast.LENGTH_SHORT).show();

                        }

                    }

                    }
           });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void PrintTheWebPage(WebView printWeb) {
        // set printBtnPressed true

        printBtnPressed=true;

        // Creating  PrintManager instance

        PrintManager printManager = (PrintManager) this

                .getSystemService(Context.PRINT_SERVICE);

        // setting the name of job

        String jobName = getString(R.string.app_name) +"webpage"+printWeb.getUrl();

        // Creating  PrintDocumentAdapter instance

        PrintDocumentAdapter printAdapter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            printAdapter = printWeb.createPrintDocumentAdapter(jobName);
        }

        // Create a print job with name and adapter instance

        assert printManager != null;

        printJob = printManager.print(jobName, printAdapter,

                new PrintAttributes.Builder().build());
        System.out.print("=======>>>>"+ printJob.getInfo());


    }
}
