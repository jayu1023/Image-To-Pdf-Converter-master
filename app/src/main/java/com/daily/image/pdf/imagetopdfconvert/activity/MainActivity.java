package com.daily.image.pdf.imagetopdfconvert.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import com.daily.image.pdf.imagetopdfconvert.Constant.Util;
import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.base.BaseActivity;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityMainBinding;
import com.daily.image.pdf.imagetopdfconvert.model.ImageFile;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class MainActivity extends BaseActivity {

    public DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView btnopendrawer;

    private static final int PICK_PDF_FILE = 102;
    private final int CAMERA_PIC_REQUEST = 100;
    MyClickHandlers myClickHandlers;
    ContentValues values;
    Uri imageUri;
    Uri imageUri1;
    private ActivityMainBinding mainBinding;
    TextView uname;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return "";
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    public void permissionGranted() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        myClickHandlers = new MyClickHandlers(MainActivity.this);
        mainBinding.setOnClick(myClickHandlers);
        Util.selectedImages = new ArrayList<>();


        navigationView = (NavigationView) findViewById(R.id.side_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnopendrawer = (ImageView) findViewById(R.id.btnopendrawer);
        uname = navigationView.findViewById(R.id.txtusernm);

//        uname.setText("Dora");
        

//    startActivity(new Intent(this,Text_to_pdf_activity.class));

        btnopendrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                drawerLayout.openDrawer(Gravity.RIGHT);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.side_home:
                        Toast.makeText(MainActivity.this, "home selected", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        return true;
                    case R.id.side_search:
                        Toast.makeText(MainActivity.this, "search selected", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("LLL_Code: ", requestCode + "  : " + resultCode);
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageUri = getImageUri(getApplicationContext(), photo);
            if (imageUri != null) {
                try {
                    String imageurl = Util.getRealPathFromURI(MainActivity.this, imageUri);
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

                    Log.e("LLL_Select_Path: ", String.valueOf(img.isSelected()));

                    Intent intent = new Intent(MainActivity.this, SelectedActivity.class);
                    intent.putExtra("isClosed", true);
                    startActivity(intent);
                    finish();

                    System.gc();
                    values.clear();
                    values = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toasty.error(MainActivity.this, "Null", Toasty.LENGTH_LONG).show();
            }
        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_CANCELED) {
            Log.e("LLL_Result_Cancel: ", "Cancel");
            values.clear();
            values = null;
        } else if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK) {
            File file = new File(Objects.requireNonNull(getPathFromUri(MainActivity.this, data.getData())));
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

            Intent intent = new Intent(MainActivity.this, SelectedActivity.class);
            intent.putExtra("isClosed", true);
            startActivity(intent);
            finish();

            System.gc();
        }
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, imageUri1);
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Util.selectedImages.clear();
        finishAffinity();
    }

    public class MyClickHandlers {
        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onGallery(View view) {
            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
            startActivity(intent);
            finish();
        }

        public void onCamera(View view) {
//            values = new ContentValues();
//            values.put(MediaStore.Images.Media.TITLE, "New Picture");
//            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (values != null) {
//                imageUri = getContentResolver().insert(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//            }
//            startActivityForResult(intent, CAMERA_PIC_REQUEST);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }

        public void onFileManager(View view) {
            openFile();
        }

        public void onGeneratedPDF(View view) {
            Intent intent = new Intent(MainActivity.this, GeneratedPdfActivity.class);
            startActivity(intent);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }
}