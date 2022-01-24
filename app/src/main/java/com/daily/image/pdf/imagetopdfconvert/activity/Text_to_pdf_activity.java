package com.daily.image.pdf.imagetopdfconvert.activity;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import com.daily.image.pdf.imagetopdfconvert.Constant.Util;
import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityTextToPdf2Binding;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Text_to_pdf_activity extends AppCompatActivity  {

    boolean isconversinoisrunning=false;
    ActivityTextToPdf2Binding binidng;
    String orientation;
    LongOperation longOperation;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binidng = DataBindingUtil.setContentView(Text_to_pdf_activity.this, R.layout.activity_text_to_pdf2);



        binidng.btnConvertTextToPdf.setBackgroundColor(getResources().getColor(R.color.teal_200));




        binidng.btnConvertTextToPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TextToPDFConverter textToPDFConverter=new TextToPDFConverter("mypdf");

                try {

                    getdialog();



                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binidng.imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
        binidng.imgFinalClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binidng.maincode.getVisibility()==View.GONE&& binidng.llFinal.getVisibility()==View.VISIBLE)
                {

                    binidng.maincode.setVisibility(View.VISIBLE);
                    binidng.llFinal.setVisibility(View.GONE);
                }
            }
        });


        binidng.imgProcessClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isconversinoisrunning)
                {
                    longOperation.cancel(false);
                    isconversinoisrunning=false;
                    if(binidng.llprocess.getVisibility()==View.VISIBLE&&binidng.maincode.getVisibility()==View.GONE)
                    {

                        binidng.maincode.setVisibility(View.VISIBLE);
                        binidng.llprocess.setVisibility(View.GONE);
                    }else
                    {
                        Log.e("hello", "onClick: errro si s svfjhvfjf " );
                    }
                }else
                {
                    Log.e("hello", "onClick: errro si s svfjhvfjf " );
                }
            }
        });

        // drawer and back button to close drawer
//        drawerLayout = findViewById(R.id.my_drawer_layout);
//        actionBarDrawerToggle= new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
//
//
//        // pass the Open and Close toggle for the drawer layout listener
//        // to toggle the button
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//
//        // to make the Navigation drawer icon always appear on the action bar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getdialog(){

        ArrayList<String> list = new ArrayList<>();
        list.add("Original");
        list.add("Landscape");
        list.add("Portrait");



        final Dialog dial = new Dialog(this, android.R.style.Theme_Black);
        dial.requestWindowFeature(1);
        dial.setContentView(R.layout.dialog_create_pdf);
        dial.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dial.setCanceledOnTouchOutside(true);

        SwitchMaterial switchMargin, switchProtect;
//        SeekBar seekBarCompress;
        RelativeLayout rlProtectPass;
        RelativeLayout rlcompress;
        TextView tvOk, tvCancel;
        EditText etFileName, etPassword;
        AppCompatSpinner spinnerOrientation;

//        switchCompress = dial.findViewById(R.id.switchCompress);
        switchMargin = dial.findViewById(R.id.switchMargin);
        switchProtect = dial.findViewById(R.id.switchProtect);

        etFileName = dial.findViewById(R.id.etFileName);
        etPassword = dial.findViewById(R.id.etPassword);

        rlcompress=dial.findViewById(R.id.rlCompress);
        rlcompress.setVisibility(View.GONE);
//        seekBarCompress = dial.findViewById(R.id.seekBarCompress);
        rlProtectPass = dial.findViewById(R.id.rlProtectPass);

        spinnerOrientation = dial.findViewById(R.id.spinner);

        tvOk = dial.findViewById(R.id.tvOk);
        tvCancel = dial.findViewById(R.id.tvCancel);

        etFileName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Util.hideKeyboard(etFileName,this);
                return true;
            }
            return false;
        });

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Util.hideKeyboard(etPassword,this);
                return true;
            }
            return false;
        });

        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, list);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);

        spinnerOrientation.setAdapter(adapter);
        spinnerOrientation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orientation =list.get(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                orientation = list.get(0).toString();
            }
        });
//
//        switchCompress.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                seekBarCompress.setVisibility(View.VISIBLE);
//
//            } else {
//                seekBarCompress.setVisibility(View.GONE);
//
//            }
//        });

        switchProtect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rlProtectPass.setVisibility(View.VISIBLE);
            } else {
                rlProtectPass.setVisibility(View.GONE);
            }
        });

        tvOk.setOnClickListener(v -> {
            if (!etFileName.getText().toString().isEmpty()) {
                if (switchProtect.isChecked()) {
                    if (!etPassword.getText().toString().isEmpty()) {
                        longOperation = new LongOperation(etFileName.getText().toString(), switchMargin.isChecked(), switchProtect.isChecked(), etPassword.getText().toString(), orientation);
                        longOperation.execute();
                        if (dial.isShowing())
                            dial.dismiss();
                    } else {
                        Toasty.error(Text_to_pdf_activity.this, "Please enter password.", Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    longOperation = new LongOperation(etFileName.getText().toString(), switchMargin.isChecked(), switchProtect.isChecked(), etPassword.getText().toString(), orientation);
                    longOperation.execute();
                    if (dial.isShowing())
                        dial.dismiss();
                }
            } else {
                Toasty.error(Text_to_pdf_activity.this, "Please enter file name", Toasty.LENGTH_SHORT).show();
            }
        });

        tvCancel.setOnClickListener(v -> {
            if (dial.isShowing())
                dial.dismiss();
        });

        dial.show();
    }








   private  final class LongOperation extends AsyncTask<Void, Integer, File> {

        String fileName;

        boolean isMargin;
        boolean isPassword;
        String password, orientation;

        public LongOperation(String fileName,  boolean isMargin, boolean isPassword, String password, String orientation) {
            this.fileName = fileName;

            this.isMargin = isMargin;
            this.isPassword = isPassword;
            this.password = password;
            this.orientation = orientation;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isconversinoisrunning=true;
            binidng.progress.setMax(100);
            binidng.maincode.setVisibility(View.GONE);
            binidng.llprocess.setVisibility(View.VISIBLE);
        }

        @Override
        protected File doInBackground(Void... params) {



            publishProgress(50);
            Document document = new Document();
            FileOutputStream fileOutputStream = null;
            File file1;
            if (SDK_INT >= Build.VERSION_CODES.R) {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                file1 = new File(cw.getExternalFilesDir(Environment.DIRECTORY_DCIM), getResources().getString(R.string.app_name));
            } else {
                file1 = new File(Util.BASE_PATH, getResources().getString(R.string.app_name));
            }
            if (!file1.exists()) {
                file1.mkdirs();
            }

            File file = new File(file1.getAbsolutePath(), fileName + ".pdf");

            try {
                if (orientation.equals("Portrait")) {
                    if (isMargin)
                        document = new Document(PageSize.A4, 38.0f, 38.0f, 50.0f, 38.0f);
                    else
                        document = new Document(PageSize.A4, 0f, 0f, 0f, 0f);
                } else if (orientation.equals("Landscape")) {
                    if (isMargin)
                        document = new Document(PageSize.A4_LANDSCAPE, 38.0f, 38.0f, 50.0f, 38.0f);
                    else
                        document = new Document(PageSize.A4_LANDSCAPE, 0f, 0f, 0f, 0f);

                } else {
                    if (isMargin)
                        document = new Document(PageSize.A4, 38.0f, 38.0f, 50.0f, 38.0f);
                    else
                        document = new Document(PageSize.A4, 0f, 0f, 0f, 0f);
                }
                fileOutputStream = new FileOutputStream(file.getAbsolutePath());
                PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream); //  Change pdf's name.

                if (isPassword) {

                    writer.setEncryption(password.getBytes(), Util.MASTER_PASSWORD.getBytes(),
                            PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY,
                            PdfWriter.ENCRYPTION_AES_128);
                    Log.v("Stage " + BuildConfig.VERSION_NAME, "Set Encryption");
                }
            } catch (Exception e) {
                Log.e("LLL_error1: ", e.getMessage());
                e.printStackTrace();
            }
            document.open();




           publishProgress(70);

            try {

                document.add(new Paragraph(binidng.EditTextofText.getText().toString()));
                publishProgress(85);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            document.close();
            publishProgress(100);
            return file;




//            while (true) {
//                if (i < Util.selectedImages.size()) {
//                    try {
//                        Log.e("LLL_Selected: ", Util.selectedImages.get(i).getPath());
//                        Image image = Image.getInstance(Util.selectedImages.get(i).getPath());  // Change image's name and extension.
//                        float scale = ((document.getPageSize().getWidth() - document.leftMargin()
//                                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
//                        image.scalePercent(scale);
//                        image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
//                        image.setAbsolutePosition((document.getPageSize().getWidth() - image.getScaledWidth()) / 2.0f,
//                                (document.getPageSize().getHeight() - image.getScaledHeight()) / 2.0f);
//                        double qualityMod = compressProgress * 0.09;
//                        image.setCompressionLevel((int) qualityMod);
//
//                        document.add(image);
//                        document.newPage();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.e("LLL_error: ", e.getMessage());
//                    }
//                } else {
//                    publishProgress(i);
//                    i++;
//                }
//            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            runOnUiThread(() -> {
                binidng.progress.setProgress(((values[0])) );
                binidng.tvProgress.setText(((values[0] + 1) * 100)  + "%");
                StringBuilder sb = new StringBuilder();
                sb.append("Processing images (");
                sb.append(values[0] + 1);
                sb.append("/");
                sb.append(Util.selectedImages.size());
                sb.append(")");
                binidng.tvCount.setText(sb.toString());
            });
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            Log.e("LLL_Receive: ", "Done");
            Toasty.success(Text_to_pdf_activity.this, "Pdf Created successfully.", Toasty.LENGTH_SHORT).show();
            Util.selectedImages.clear();

            binidng.llprocess.setVisibility(View.GONE);
            binidng.llFinal.setVisibility(View.VISIBLE);
            //binidng.maincode.setVisibility(View.VISIBLE);
            runOnUiThread(() -> {
                Log.e("LLL_File: ", String.valueOf(file.exists()));
                StringBuilder sb1 = new StringBuilder();
                sb1.append(file.getName());
                binidng.tvPdfName.setText(sb1.toString());
                StringBuilder sb = new StringBuilder();
                sb.append(Util.getSize(file.length()));
                sb.append(", ");
                sb.append(Util.convertTimeDateModified(file.lastModified()));
                binidng.tvSizeDate.setText(sb.toString());
                isconversinoisrunning=false;
                binidng.llOpenWith.setOnClickListener(v -> {
                    try
                    {
                        Intent viewPdf = new Intent(Intent.ACTION_VIEW);
                        viewPdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri URI = FileProvider.getUriForFile(Text_to_pdf_activity.this, getApplicationContext().getPackageName() + ".provider", file);
                        viewPdf.setDataAndType(URI, "application/pdf");
                        viewPdf.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(viewPdf);
                    }catch (Exception e)
                    {
                        final Dialog dialog = new Dialog(Text_to_pdf_activity.this, android.R.style.Theme_Black);
                        dialog.requestWindowFeature(1);
                        dialog.setContentView(R.layout.customalertdialog_warning);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        dialog.setCanceledOnTouchOutside(true);


                        AppCompatButton btncancel=dialog.findViewById(R.id.btn_dialog_cancel);
                        AppCompatButton btnok=dialog.findViewById(R.id.btn_dialog_go);
                        btnok.setOnClickListener(view->{
                            Intent playstor_viewpdf=new Intent(Intent.ACTION_VIEW,Uri.parse("www.playstore.com/"));
                            playstor_viewpdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            playstor_viewpdf.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            playstor_viewpdf.putExtra(Intent.EXTRA_TEXT, "Installing Pdf Reader...");
                            startActivity(Intent.createChooser(playstor_viewpdf,"Install Pdf Reader"));


                        });
                        btncancel.setOnClickListener(view->{
                            dialog.dismiss();
                        });

                        dialog.show();



                    }

                });

                binidng.llShare.setOnClickListener(v -> {
                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                    if (file.exists()) {
                        Uri URI = FileProvider.getUriForFile(Text_to_pdf_activity.this, getApplicationContext().getPackageName() + ".provider", file);
                        intentShareFile.setType("application/pdf");
                        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, URI);
                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                "Sharing File...");
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                        startActivity(Intent.createChooser(intentShareFile, "Share File"));
                    }
                });
            });
        }
    }
}



//   PdfWriter writer = PdfWriter.getInstance(document, file);
//
//        writer.setEncryption(USER_PASSWORD.getBytes(),
//                OWNER_PASSWORD.getBytes(), PdfWriter.ALLOW_PRINTING,
//
//
//                PdfWriter.ENCRYPTION_AES_128);


//TODO:-modify existing pdf
// try
//         {
//         //Read file using PdfReader
//         PdfReader pdfReader = new PdfReader("HelloWorld.pdf");
//
//         //Modify file using PdfReader
//         PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream("HelloWorld-modified.pdf"));
//
//         Image image = Image.getInstance("temp.jpg");
//         image.scaleAbsolute(100, 50);
//         image.setAbsolutePosition(100f, 700f);
//
//         for(int i=1; i&lt;= pdfReader.getNumberOfPages(); i++)
//         {
//         PdfContentByte content = pdfStamper.getUnderContent(i);
//         content.addImage(image);
//         }
//
//         pdfStamper.close();
//
//         } catch (IOException e) {
//         e.printStackTrace();
//         } catch (DocumentException e) {
//         e.printStackTrace();
//         }



//TODO:-web to pdf transfer

//TODO:_first step=> intilize  web view:

//webView = findViewById(R.id.webView);
//webView.loadUrl("https://www.androidmads.info/");
//webView.setWebViewClient(new WebViewClient());

//TODO:-second Step:-Create a pdf print adapter with print attributes what we need.
//String fileName = String.format("%s.pdf", new SimpleDateFormat("dd_MM_yyyyHH_mm_ss", Locale.US).format(new Date()));
//            final PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(fileName);
//            PrintAttributes printAttributes = new PrintAttributes.Builder()
//                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//                    .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
//                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
//                    .build();

//TODO;-Third Step=>Call your extension method with the callback as shown below.
//new PdfPrint(printAttributes).print(
//                    printAdapter,
//                    file,
//                    fileName,
//                    new PdfPrint.CallbackPrint() {
//                        @Override
//                        public void onSuccess(String path) {
//                        }
//                        @Override
//                        public void onFailure(Exception ex) {
//                        }
//                    });

//TODO:-setp_4=> save pdf
//
// private void savePdf() {
//        if(!allowSave)
//            return;
//        allowSave = false;
//        textView.setVisibility(View.VISIBLE);
//        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == PERMISSION_GRANTED) {
//            String fileName = String.format("%s.pdf", new SimpleDateFormat("dd_MM_yyyyHH_mm_ss", Locale.US).format(new Date()));
//            final PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(fileName);
//            PrintAttributes printAttributes = new PrintAttributes.Builder()
//                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//                    .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
//                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
//                    .build();
//            final File file = Environment.getExternalStorageDirectory();
//            new PdfPrint(printAttributes).print(
//                    printAdapter,
//                    file,
//                    fileName,
//                    new PdfPrint.CallbackPrint() {
//                        @Override
//                        public void onSuccess(String path) {
//                            textView.setVisibility(View.GONE);
//                            allowSave = true;
//                            Toast.makeText(getApplicationContext(),
//                                    String.format("Your file is saved in %s", path),
//                                    Toast.LENGTH_LONG).show();
//                        }
//
//                        @Override
//                        public void onFailure(Exception ex) {
//                            textView.setVisibility(View.GONE);
//                            allowSave = true;
//                            Toast.makeText(getApplicationContext(),
//                                    String.format("Exception while saving the file and the exception is %s", ex.getMessage()),
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    });
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
//        }
//    }



