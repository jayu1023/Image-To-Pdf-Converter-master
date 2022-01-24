package com.daily.image.pdf.imagetopdfconvert.activity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.daily.image.pdf.imagetopdfconvert.BuildConfig;
import com.daily.image.pdf.imagetopdfconvert.Constant.Util;
import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.adapter.SelectedImageAdapter;
import com.daily.image.pdf.imagetopdfconvert.callback.MyItemTouchHelperCallback;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivitySelectedBinding;
import com.daily.image.pdf.imagetopdfconvert.interfaces.CallbackItemTouch;
import com.daily.image.pdf.imagetopdfconvert.model.ImageFile;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static android.os.Build.VERSION.SDK_INT;
import static com.daily.image.pdf.imagetopdfconvert.activity.MainActivity.getPathFromUri;

public class SelectedActivity extends AppCompatActivity implements CallbackItemTouch {

    private static final int PICK_PDF_FILE = 102;
    private final int CAMERA_PIC_REQUEST = 100;

    ActivitySelectedBinding selectedBinding;
    MyClickHandlers myClickHandlers;
    SelectedImageAdapter selectedImageAdapter;
    ContentValues values;
    Uri imageUri;
    Uri imageUri1;
    float compressProgress;
    String orientation = "Original";
    LongOperation longOperation;
    private boolean isClosed = false;
    private boolean isFABOpen = false;
    private int positionRemove = 0;

    private static Bitmap getBitmapCollection(ArrayList<Bitmap> art) {
        long start = System.currentTimeMillis();
        int max_width = art.get(0).getWidth();
        for (Bitmap b : art) if (max_width < b.getWidth()) max_width = b.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(max_width, max_width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(max_width / 100);
        paint.setColor(0xffffffff);
        switch (art.size()) {
            case 2:
                canvas.drawBitmap(art.get(1), null, new Rect(0, 0, max_width, max_width), null);
                canvas.drawBitmap(art.get(0), null, new Rect(-max_width / 2, 0, max_width / 2, max_width), null);
                canvas.drawLine(max_width / 2, 0, max_width / 2, max_width, paint);
                break;
            case 3:
                canvas.drawBitmap(art.get(0), null, new Rect(-max_width / 4, 0, 3 * max_width / 4, max_width), null);
                canvas.drawBitmap(art.get(1), null, new Rect(max_width / 2, 0, max_width, max_width / 2), null);
                canvas.drawBitmap(art.get(2), null, new Rect(max_width / 2, max_width / 2, max_width, max_width), null);
                canvas.drawLine(max_width / 2, 0, max_width / 2, max_width, paint);
                canvas.drawLine(max_width / 2, max_width / 2, max_width, max_width / 2, paint);
                break;
            case 4:
                canvas.drawBitmap(art.get(0), null, new Rect(0, 0, max_width / 2, max_width / 2), null);
                canvas.drawBitmap(art.get(1), null, new Rect(max_width / 2, 0, max_width, max_width / 2), null);
                canvas.drawBitmap(art.get(2), null, new Rect(0, max_width / 2, max_width / 2, max_width), null);
                canvas.drawBitmap(art.get(3), null, new Rect(max_width / 2, max_width / 2, max_width, max_width), null);
                canvas.drawLine(max_width / 2, 0, max_width / 2, max_width, paint);
                canvas.drawLine(0, max_width / 2, max_width, max_width / 2, paint);
                break;
            default:

                float w = (float) (Math.sqrt(2) / 2 * max_width);
                float b = (float) (max_width / Math.sqrt(5));

                float d = (float) (max_width * (0.5f - 1 / Math.sqrt(10)));
                float deg = 45;

                for (int i = 0; i < 5; i++) {
                    canvas.save();
                    switch (i) {
                        case 0:
                            canvas.translate(max_width / 2, max_width / 2);
                            canvas.rotate(deg);
                            // b = (float) (max_width*Math.sqrt(2/5f));
                            canvas.drawBitmap(art.get(0), null, new RectF(-b / 2, -b / 2, b / 2, b / 2), null);
                            break;
                        case 1:
                            canvas.translate(d, 0);
                            canvas.rotate(deg);
                            canvas.drawBitmap(art.get(i), null, new RectF(-w / 2, -w / 2, w / 2, w / 2), null);
                            paint.setAntiAlias(true);
                            canvas.drawLine(w / 2, -w / 2, w / 2, w / 2, paint);
                            break;
                        case 2:
                            canvas.translate(max_width, d);
                            canvas.rotate(deg);
                            canvas.drawBitmap(art.get(i), null, new RectF(-w / 2, -w / 2, w / 2, w / 2), null);
                            paint.setAntiAlias(true);
                            canvas.drawLine(-w / 2, w / 2, w / 2, w / 2, paint);
                            break;
                        case 3:
                            canvas.translate(max_width - d, max_width);
                            canvas.rotate(deg);
                            canvas.drawBitmap(art.get(i), null, new RectF(-w / 2, -w / 2, w / 2, w / 2), null);
                            paint.setAntiAlias(true);
                            canvas.drawLine(-w / 2, -w / 2, -w / 2, w / 2, paint);
                            break;
                        case 4:
                            canvas.translate(0, max_width - d);
                            canvas.rotate(deg);
                            canvas.drawBitmap(art.get(i), null, new RectF(-w / 2, -w / 2, w / 2, w / 2), null);
                            paint.setAntiAlias(true);
                            canvas.drawLine(-w / 2, -w / 2, w / 2, -w / 2, paint);
                            break;
                    }
                    canvas.restore();
                }
        }
        Log.d("TAG", "getBitmapCollection: smalltime = " + (System.currentTimeMillis() - start));
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedBinding = DataBindingUtil.setContentView(SelectedActivity.this, R.layout.activity_selected);
        myClickHandlers = new MyClickHandlers(SelectedActivity.this);
        selectedBinding.setOnClick(myClickHandlers);
        isClosed = getIntent().getBooleanExtra("isClosed", false);

        selectedBinding.rvAlbumImage.setLayoutManager(new GridLayoutManager(SelectedActivity.this, 3));
        selectedImageAdapter = new SelectedImageAdapter(Util.selectedImages, SelectedActivity.this, new SelectedImageAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("LLL_URI: ", Uri.fromFile(new File(Util.selectedImages.get(position).getPath())) + "");
                positionRemove = position;
                CropImage.activity(Uri.fromFile(new File(Util.selectedImages.get(position).getPath())))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SelectedActivity.this);
            }
        });
        selectedBinding.rvAlbumImage.setAdapter(selectedImageAdapter);

        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(this);// create MyItemTouchHelperCallback
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback); // Create ItemTouchHelper and pass with parameter the MyItemTouchHelperCallback
        touchHelper.attachToRecyclerView(selectedBinding.rvAlbumImage);

        selectedBinding.imgAdd.setOnClickListener(v -> {
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });

        selectedBinding.imgDone.setOnClickListener(v -> pdfSettingDialog());

        selectedBinding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void itemTouchOnMove(int oldPosition, int newPosition) {
        Util.selectedImages.add(newPosition, Util.selectedImages.remove(oldPosition));// change position
        selectedImageAdapter.notifyDataSetChanged();
    }

    private void showFABMenu() {
        isFABOpen = true;
        selectedBinding.imgMyFiles.setVisibility(View.VISIBLE);
        selectedBinding.imgGallery.setVisibility(View.VISIBLE);
        selectedBinding.imgCamera.setVisibility(View.VISIBLE);
        selectedBinding.fabBGLayout.setVisibility(View.VISIBLE);
        selectedBinding.imgAdd.animate().rotationBy(135);
        selectedBinding.imgMyFiles.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        selectedBinding.imgGallery.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        selectedBinding.imgCamera.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        selectedBinding.fabBGLayout.setVisibility(View.GONE);
        selectedBinding.imgAdd.animate().rotation(0);
        selectedBinding.imgCamera.animate().translationY(0);
        selectedBinding.imgGallery.animate().translationY(0);
        selectedBinding.imgMyFiles.animate().translationY(0);
        selectedBinding.imgMyFiles.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    selectedBinding.imgCamera.setVisibility(View.GONE);
                    selectedBinding.imgGallery.setVisibility(View.GONE);
                    selectedBinding.imgMyFiles.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isFABOpen) {
            closeFABMenu();
        } else {
            if (isClosed) {
                Intent intent = new Intent(SelectedActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, imageUri1);
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageUri = getImageUri(getApplicationContext(), photo);
            if (imageUri != null) {
                try {
                    String imageurl = Util.getRealPathFromURI(SelectedActivity.this, imageUri);
                    Log.e("LLL_Path: ", imageurl);

                    ImageFile img = new ImageFile();
                    File file = new File(imageurl);

                    img.setId(1);
                    img.setName(file.getName());
                    img.setPath(file.getAbsolutePath());
                    img.setSize(file.length());
                    img.setBucketName(file.getParentFile().getName());
                    img.setDate(Util.convertTimeDateModified(file.lastModified()));

                    img.setSelected(true);
                    Util.selectedImages.add(img);

                    selectedImageAdapter.notifyItemInserted(Util.selectedImages.size() - 1);

                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toasty.error(SelectedActivity.this, "Null", Toasty.LENGTH_LONG).show();
            }
        } else if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK) {
            File file = new File(Objects.requireNonNull(getPathFromUri(SelectedActivity.this, data.getData())));
            Log.e("LLL_Path: ", file.getAbsolutePath());

            ImageFile img = new ImageFile();

            img.setId(1);
            img.setName(file.getName());
            img.setPath(file.getAbsolutePath());
            img.setSize(file.length());
            img.setBucketName(file.getParentFile().getName());
            img.setDate(Util.convertTimeDateModified(file.lastModified()));

            img.setSelected(true);
            Util.selectedImages.add(img);

            selectedImageAdapter.notifyItemInserted(Util.selectedImages.size() - 1);

            System.gc();
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                File file = new File(Objects.requireNonNull(getPathFromUri(SelectedActivity.this, resultUri)));
                Log.e("LLL_Path: ", file.getAbsolutePath());

                ImageFile img = new ImageFile();

                img.setId(1);
                img.setName(file.getName());
                img.setPath(file.getAbsolutePath());
                img.setSize(file.length());
                img.setBucketName(file.getParentFile().getName());
                img.setDate(Util.convertTimeDateModified(file.lastModified()));

                img.setSelected(true);
                Util.selectedImages.remove(positionRemove);
                selectedImageAdapter.notifyItemRemoved(positionRemove);
                Util.selectedImages.add(positionRemove, img);

                selectedImageAdapter.notifyItemInserted(positionRemove);

                System.gc();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("LLLL_Error: ", error.getLocalizedMessage());
            }
        }
    }

    private void pdfSettingDialog() {

        ArrayList<String> list = new ArrayList<>();
        list.add("Original");
        list.add("Landscape");
        list.add("Portrait");

        final Dialog dial = new Dialog(SelectedActivity.this, android.R.style.Theme_Black);
        dial.requestWindowFeature(1);
        dial.setContentView(R.layout.dialog_create_pdf);
        dial.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dial.setCanceledOnTouchOutside(true);

        SwitchMaterial switchCompress, switchMargin, switchProtect;
        SeekBar seekBarCompress;
        RelativeLayout rlProtectPass;
        TextView tvOk, tvCancel;
        EditText etFileName, etPassword;
        AppCompatSpinner spinnerOrientation;

        switchCompress = dial.findViewById(R.id.switchCompress);
        switchMargin = dial.findViewById(R.id.switchMargin);
        switchProtect = dial.findViewById(R.id.switchProtect);

        etFileName = dial.findViewById(R.id.etFileName);
        etPassword = dial.findViewById(R.id.etPassword);

        seekBarCompress = dial.findViewById(R.id.seekBarCompress);
        rlProtectPass = dial.findViewById(R.id.rlProtectPass);

        spinnerOrientation = dial.findViewById(R.id.spinner);

        tvOk = dial.findViewById(R.id.tvOk);
        tvCancel = dial.findViewById(R.id.tvCancel);

        etFileName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Util.hideKeyboard(etFileName,SelectedActivity.this);
                return true;
            }
            return false;
        });

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Util.hideKeyboard(etPassword,SelectedActivity.this);
                return true;
            }
            return false;
        });

        final ArrayAdapter adapter = new ArrayAdapter(SelectedActivity.this, R.layout.spinner_item, list);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);

        spinnerOrientation.setAdapter(adapter);
        spinnerOrientation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orientation = list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                orientation = list.get(0);
            }
        });

        switchCompress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                seekBarCompress.setVisibility(View.VISIBLE);
                compressProgress = seekBarCompress.getProgress();
            } else {
                seekBarCompress.setVisibility(View.GONE);
                compressProgress = 0f;
            }
        });

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
                        longOperation = new LongOperation(etFileName.getText().toString(), compressProgress, switchMargin.isChecked(), switchProtect.isChecked(), etPassword.getText().toString(), orientation);
                        longOperation.execute();
                        if (dial.isShowing())
                            dial.dismiss();
                    } else {
                        Toasty.error(SelectedActivity.this, "Please enter password.", Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    longOperation = new LongOperation(etFileName.getText().toString(), compressProgress, switchMargin.isChecked(), switchProtect.isChecked(), etPassword.getText().toString(), orientation);
                    longOperation.execute();
                    if (dial.isShowing())
                        dial.dismiss();
                }
            } else {
                Toasty.error(SelectedActivity.this, "Please enter file name", Toasty.LENGTH_SHORT).show();
            }
        });

        tvCancel.setOnClickListener(v -> {
            if (dial.isShowing())
                dial.dismiss();
        });

        dial.show();
    }

    private final class LongOperation extends AsyncTask<Void, Integer, File> {

        String fileName;
        float compressProgress;
        boolean isMargin;
        boolean isPassword;
        String password, orientation;

        public LongOperation(String fileName, float compressProgress, boolean isMargin, boolean isPassword, String password, String orientation) {
            this.fileName = fileName;
            this.compressProgress = compressProgress;
            this.isMargin = isMargin;
            this.isPassword = isPassword;
            this.password = password;
            this.orientation = orientation;
            selectedBinding.codMain.setVisibility(View.GONE);
            selectedBinding.llProcess.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            selectedBinding.progress.setMax(100);
        }

        @Override
        protected File doInBackground(Void... params) {

            ArrayList<Bitmap> art = new ArrayList<>();
            for (int i = 0; i < Util.selectedImages.size(); i++) {
                Bitmap bitmap = BitmapFactory.decodeFile(Util.selectedImages.get(i).getPath());
                if (bitmap != null) art.add(bitmap);
                if (art.size() == 6) break;
            }

            Bitmap ret;
            switch (art.size()) {
                case 0:
                    ret = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pdf);
                    break;
                case 1:
                    ret = art.get(0);
                    break;
                default:
                    ret = getBitmapCollection(art);
            }

            runOnUiThread(() -> {
                RequestOptions options = new RequestOptions();
                Glide.with(SelectedActivity.this)
                        .load(ret)
                        .apply(options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))
                        .into(selectedBinding.imgArt);
            });

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

            Log.e("LLL_Image: ", String.valueOf(Util.selectedImages.size()));
            int i = 0;
            while (true) {
                if (i < Util.selectedImages.size()) {
                    try {
                        Log.e("LLL_Selected: ", Util.selectedImages.get(i).getPath());
                        Image image = Image.getInstance(Util.selectedImages.get(i).getPath());  // Change image's name and extension.
                        float scale = ((document.getPageSize().getWidth() - document.leftMargin()
                                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                        image.scalePercent(scale);
                        image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                        image.setAbsolutePosition((document.getPageSize().getWidth() - image.getScaledWidth()) / 2.0f,
                                (document.getPageSize().getHeight() - image.getScaledHeight()) / 2.0f);
                        double qualityMod = compressProgress * 0.09;
                        image.setCompressionLevel((int) qualityMod);

                        document.add(image);
                        document.newPage();

                        publishProgress(i);
                        i++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("LLL_error: ", e.getMessage());
                    }
                } else {
                    document.close();
                    return file;
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            runOnUiThread(() -> {
                selectedBinding.progress.setProgress(((values[0] + 1) * 100) / Util.selectedImages.size());
                selectedBinding.tvProgress.setText(((values[0] + 1) * 100) / Util.selectedImages.size() + "%");
                StringBuilder sb = new StringBuilder();
                sb.append("Processing images (");
                sb.append(values[0] + 1);
                sb.append("/");
                sb.append(Util.selectedImages.size());
                sb.append(")");
                selectedBinding.tvCount.setText(sb.toString());
            });
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            Log.e("LLL_Receive: ", "Done");
            Toasty.success(SelectedActivity.this, "Pdf Created successfully.", Toasty.LENGTH_SHORT).show();
            Util.selectedImages.clear();
            selectedImageAdapter.notifyDataSetChanged();
            selectedBinding.llProcess.setVisibility(View.GONE);
            selectedBinding.llFinal.setVisibility(View.VISIBLE);
            runOnUiThread(() -> {
                Log.e("LLL_File: ", String.valueOf(file.exists()));
                StringBuilder sb1 = new StringBuilder();
                sb1.append(file.getName());
                selectedBinding.tvPdfName.setText(sb1.toString());
                StringBuilder sb = new StringBuilder();
                sb.append(Util.getSize(file.length()));
                sb.append(", ");
                sb.append(Util.convertTimeDateModified(file.lastModified()));
                selectedBinding.tvSizeDate.setText(sb.toString());
                selectedBinding.llOpenWith.setOnClickListener(v -> {
                    Intent viewPdf = new Intent(Intent.ACTION_VIEW);
                    viewPdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri URI = FileProvider.getUriForFile(SelectedActivity.this, getApplicationContext().getPackageName() + ".provider", file);
                    viewPdf.setDataAndType(URI, "application/pdf");
                    viewPdf.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(viewPdf);
                });

                selectedBinding.llShare.setOnClickListener(v -> {
                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                    if (file.exists()) {
                        Uri URI = FileProvider.getUriForFile(SelectedActivity.this, getApplicationContext().getPackageName() + ".provider", file);
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

    public class MyClickHandlers {
        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onGallery(View view) {
            closeFABMenu();
            Intent intent = new Intent(SelectedActivity.this, AlbumActivity.class);
            startActivity(intent);
            finish();
        }

        public void onCamera(View view) {
            closeFABMenu();
//            values = new ContentValues();
//            values.put(MediaStore.Images.Media.TITLE, "New Picture");
//            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//            imageUri = getContentResolver().insert(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//            startActivityForResult(intent, CAMERA_PIC_REQUEST);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }

        public void onFileManager(View view) {
            closeFABMenu();
            openFile();
        }

        public void cancelProcess(View view) {
            if (longOperation != null)
                longOperation.cancel(true);
            Util.selectedImages.clear();
            Intent intent = new Intent(SelectedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        public void onBackFinal(View view) {
            Util.selectedImages.clear();
            Intent intent = new Intent(SelectedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        public void onBack(View view) {
            onBackPressed();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }
}