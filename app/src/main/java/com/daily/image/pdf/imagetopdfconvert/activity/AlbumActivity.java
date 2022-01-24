package com.daily.image.pdf.imagetopdfconvert.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daily.image.pdf.imagetopdfconvert.Constant.Util;
import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.adapter.AlbumListAdapter;
import com.daily.image.pdf.imagetopdfconvert.base.BaseActivity;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityAlbumBinding;
import com.daily.image.pdf.imagetopdfconvert.model.Directory;
import com.daily.image.pdf.imagetopdfconvert.model.ImageFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.MediaColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.ORIENTATION;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;

public class AlbumActivity extends BaseActivity {

    private static final String TAG = AlbumActivity.class.getSimpleName();
    private final List<ImageFile> directoryList = new ArrayList<>();
    AlbumListAdapter albumListAdapter;
    private ActivityAlbumBinding albumBinding;
    private boolean isLoaded = false;

    @Override
    public void permissionGranted() {
        if (!isLoaded) {
            isLoaded = true;
            new loadImages().execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumBinding = DataBindingUtil.setContentView(AlbumActivity.this, R.layout.activity_album);
        albumListAdapter = new AlbumListAdapter(new ArrayList<>(), AlbumActivity.this);

        albumBinding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void stopAnim() {
        albumBinding.rlLoader.setVisibility(View.GONE);
    }

    private void startAnim() {
        albumBinding.rlLoader.setVisibility(View.VISIBLE);
    }

    class loadImages extends AsyncTask<Void, Void, List<Directory<ImageFile>>> {

        @SuppressLint("StaticFieldLeak")
        List<ImageFile> list1 = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnim();
        }

        @Override
        protected List<Directory<ImageFile>> doInBackground(Void... voids) {

            String[] FILE_PROJECTION = {
                    //Base File
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.TITLE,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    DATE_ADDED,
                    MediaStore.Images.Media.ORIENTATION
            };

            String selection = MediaStore.MediaColumns.MIME_TYPE + "=? or " + MediaStore.MediaColumns.MIME_TYPE + "=? or " + MediaStore.MediaColumns.MIME_TYPE + "=? or " + MediaStore.MediaColumns.MIME_TYPE + "=?";

            String[] selectionArgs;
            selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg", "image/gif"};

            Cursor data = getContentResolver().query(MediaStore.Files.getContentUri("external"),
                    FILE_PROJECTION,
                    selection,
                    selectionArgs,
                    DATE_ADDED + " DESC");

            List<Directory<ImageFile>> directories = new ArrayList<>();
            List<Directory> directories1 = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                //Create a File instance
                ImageFile img = new ImageFile();

                File file = new File(data.getString(data.getColumnIndexOrThrow(DATA)));
                if (file.exists()) {

                    img.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                    img.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                    img.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                    img.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                    img.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
                    img.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
                    img.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));
                    img.setOrientation(data.getInt(data.getColumnIndexOrThrow(ORIENTATION)));

                    if (!img.getBucketName().startsWith(".")) {
                        //Create a Directory
                        Directory<ImageFile> directory = new Directory<>();
                        directory.setId(img.getBucketId());
                        directory.setName(img.getBucketName());
                        directory.setPath(Util.extractPathWithoutSeparator(img.getPath()));

                        if (!directories1.contains(directory)) {
                            directory.addFile(img);
                            directories.add(directory);
                            directories1.add(directory);
                        } else {
                            directories.get(directories.indexOf(directory)).addFile(img);
                        }
                        directoryList.add(img);
                    }
                }
            }

            return directories;
        }

        @Override
        protected void onPostExecute(List<Directory<ImageFile>> directories) {
            super.onPostExecute(directories);
            Log.d(TAG, "onPostExecute: " + "done");

            runOnUiThread(() -> {
                albumBinding.rvImages.setLayoutManager(new LinearLayoutManager(AlbumActivity.this, RecyclerView.VERTICAL, false));
                albumBinding.rvImages.setLayoutAnimation(null);

                albumBinding.rvImages.setAdapter(albumListAdapter);
                albumListAdapter.clearData();

                albumListAdapter.addAll(directories);
            });
            stopAnim();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Util.selectedImages.clear();
        Intent intent = new Intent(AlbumActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}