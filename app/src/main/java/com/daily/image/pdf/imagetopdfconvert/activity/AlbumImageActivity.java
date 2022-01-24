package com.daily.image.pdf.imagetopdfconvert.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.daily.image.pdf.imagetopdfconvert.Constant.Util;
import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.adapter.AlbumChildAdapter;
import com.daily.image.pdf.imagetopdfconvert.base.BaseActivity;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityAlbumImageBinding;
import com.daily.image.pdf.imagetopdfconvert.model.Directory;
import com.daily.image.pdf.imagetopdfconvert.model.ImageFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.MediaColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.ORIENTATION;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;

public class AlbumImageActivity extends BaseActivity {


    private static final String TAG = AlbumImageActivity.class.getSimpleName();
    ActivityAlbumImageBinding albumImageBinding;

    private List<ImageFile> imageFiles = new ArrayList<>();
    private AlbumChildAdapter albumChildAdapter;
    private boolean isLoaded = false;
    private RefreshReceiver refreshReceiver;

    @Override
    public void permissionGranted() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumImageBinding = DataBindingUtil.setContentView(AlbumImageActivity.this, R.layout.activity_album_image);
        albumImageBinding.setDirName(getIntent().getStringExtra("DirName"));

        refreshReceiver = new RefreshReceiver();
        LocalBroadcastManager.getInstance(AlbumImageActivity.this).registerReceiver(refreshReceiver,
                new IntentFilter("REFRESH"));

        albumImageBinding.rlCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlbumImageActivity.this, SelectedActivity.class);
                intent.putExtra("isClosed", false);
                startActivity(intent);
            }
        });

        albumImageBinding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onResume() {
        if (!AlbumImageActivity.this.isDestroyed()) {
            super.onResume();
            if (!isLoaded) {
                boolean isGranted = EasyPermissions.hasPermissions(this, perms);
                if (isGranted) {
                    isLoaded = true;
                    if (Util.selectedImages.size() > 0) {
                        albumImageBinding.rlCount.setVisibility(View.VISIBLE);
                        String count = "Count " + Util.selectedImages.size();
                        albumImageBinding.tvCount.setText(count);
                    } else {
                        albumImageBinding.rlCount.setVisibility(View.GONE);
                    }
                    new LoadImages(AlbumImageActivity.this).execute();
                }
            } else {
                if (albumChildAdapter != null)
                    albumChildAdapter.notifyDataSetChanged();
            }
        }
    }

    private void stopAnim() {
        albumImageBinding.rlLoader.setVisibility(View.GONE);
    }

    private void startAnim() {
        albumImageBinding.rlLoader.setVisibility(View.VISIBLE);
    }

    private class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isLoaded = false;
            onResume();
        }
    }

    class LoadImages extends AsyncTask<Void, Void, List<Directory<ImageFile>>> {

        @SuppressLint("StaticFieldLeak")
        FragmentActivity fragmentActivity;
        List<ImageFile> list1 = new ArrayList<>();

        public LoadImages(FragmentActivity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnim();
            albumImageBinding.rvAlbumImage.setVisibility(View.GONE);
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

            Cursor data = fragmentActivity.getContentResolver().query(MediaStore.Files.getContentUri("external"),
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

                    for (int i = 0; i < Util.selectedImages.size(); i++) {
                        String path1 = Util.selectedImages.get(i).getPath();
                        if (img.getPath().equals(path1)) {
                            img.setSelected(true);
                        }
                    }
                }

                imageFiles.add(img);
            }

            return directories;
        }

        @Override
        protected void onPostExecute(List<Directory<ImageFile>> directories) {
            super.onPostExecute(directories);
            Log.d(TAG, "onPostExecute: " + "done");
            albumImageBinding.rvAlbumImage.setVisibility(View.VISIBLE);
            imageFiles.clear();
            for (int i = 0; i < directories.size(); i++) {
                if (albumImageBinding.getDirName().equals(directories.get(i).getName())) {
                    imageFiles = directories.get(i).getFiles();
                    int finalI = i;
                    fragmentActivity.runOnUiThread(() -> {
                        Log.e("LLL_Date: ", albumImageBinding.getDirName() + "  Directory: " + directories.get(finalI).getName() + " Size: " + directories.get(finalI).getFiles().size());
                        albumImageBinding.rvAlbumImage.setLayoutManager(new GridLayoutManager(AlbumImageActivity.this, 3));
                        albumChildAdapter = new AlbumChildAdapter(imageFiles, AlbumImageActivity.this, finalI, new AlbumChildAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                ImageFile file = imageFiles.get(position);
                                if (file.isSelected()) {
                                    Log.e("LLL_Path_Select: ", file.isSelected() + "   Path: " + file.getPath());
                                    file.setSelected(false);
                                    Util.selectedImages.remove(file);
                                } else {
                                    file.setSelected(true);
                                    Util.selectedImages.add(file);
                                }
                                albumChildAdapter.notifyItemChanged(position);
                                if (Util.selectedImages.size() > 0) {
                                    albumImageBinding.rlCount.setVisibility(View.VISIBLE);
                                    String count = "Count " + Util.selectedImages.size();
                                    albumImageBinding.tvCount.setText(count);
                                } else {
                                    albumImageBinding.rlCount.setVisibility(View.GONE);
                                }
                                Log.e("LLL_Count: ", String.valueOf(Util.selectedImages.size()));
                            }
                        });
                        albumImageBinding.rvAlbumImage.setAdapter(albumChildAdapter);
                    });
                }
            }

            stopAnim();
        }
    }

}