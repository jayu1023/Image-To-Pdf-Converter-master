package com.daily.image.pdf.imagetopdfconvert.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.daily.image.pdf.imagetopdfconvert.Constant.Util;
import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.databinding.ListImageBinding;
import com.daily.image.pdf.imagetopdfconvert.model.ImageFile;

import java.util.List;

public class AlbumChildAdapter extends RecyclerView.Adapter<AlbumChildAdapter.MyClassView> {

    private final ItemClickListener mItemClickListener;
    List<ImageFile> images;
    Activity activity;
    int direPos = 0;

    public AlbumChildAdapter(List<ImageFile> images, Activity activity, int i, ItemClickListener itemClickListener) {
        this.images = images;
        this.activity = activity;
        this.direPos = i;
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListImageBinding imageViewBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_image, parent, false);
        ViewGroup.LayoutParams params = imageViewBinding.getRoot().getLayoutParams();
        if (params != null) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            params.height = width / 3;
        }
        return new MyClassView(imageViewBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {

        ImageFile file = images.get(position);
        for (int i = 0; i < Util.selectedImages.size(); i++) {
            String path1 = Util.selectedImages.get(i).getPath();
            if (file.getPath().equals(path1)) {
                Log.e("LLL_Path: ", path1 + "  : " + file.getPath());
                holder.imageViewBinding.rlSelect.setVisibility(View.VISIBLE);
                break;
            } else {
                holder.imageViewBinding.rlSelect.setVisibility(View.GONE);
            }
        }
        holder.imageViewBinding.mImage.setClipToOutline(true);
        holder.imageViewBinding.imgSelected.setClipToOutline(true);

        RequestOptions options = new RequestOptions();
        if (file.getPath().endsWith(".PNG") || file.getPath().endsWith(".png")) {
            Glide.with(activity)
                    .load(file.getPath())
                    .placeholder(R.color.light_text_color)
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .into(holder.imageViewBinding.mImage);
        } else {
            Glide.with(activity)
                    .load(file.getPath())
                    .placeholder(R.color.light_text_color)
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))
                    .into(holder.imageViewBinding.mImage);
        }

        holder.imageViewBinding.getRoot().setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //Define your Interface method here
    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        private final ListImageBinding imageViewBinding;

        public MyClassView(ListImageBinding imageViewBinding) {
            super(imageViewBinding.getRoot());
            this.imageViewBinding = imageViewBinding;
        }
    }
}
