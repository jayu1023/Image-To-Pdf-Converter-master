package com.daily.image.pdf.imagetopdfconvert.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.daily.image.pdf.imagetopdfconvert.Constant.Util;
import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.databinding.ListSelcetdImageBinding;
import com.daily.image.pdf.imagetopdfconvert.model.ImageFile;

import java.util.List;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.MyClassView> {

    private final ItemClickListener mItemClickListener;
    List<ImageFile> images;
    Activity activity;
    int direPos = 0;

    public SelectedImageAdapter(List<ImageFile> images, Activity activity, ItemClickListener itemClickListener) {
        this.images = images;
        this.activity = activity;
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListSelcetdImageBinding imageViewBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_selcetd_image, parent, false);
        return new MyClassView(imageViewBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull SelectedImageAdapter.MyClassView holder, int position) {
        ImageFile file = images.get(position);

        holder.selcetdImageBinding.mImage.setClipToOutline(true);

        RequestOptions options = new RequestOptions();
        if (file.getPath().endsWith(".PNG") || file.getPath().endsWith(".png")) {
            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .into(holder.selcetdImageBinding.mImage);
        } else {
            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))
                    .into(holder.selcetdImageBinding.mImage);
        }

        holder.selcetdImageBinding.imgCrop.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }
        });

        holder.selcetdImageBinding.imgClose.setOnClickListener(v -> {
            Util.selectedImages.remove(file);
            notifyItemRemoved(position);

            LocalBroadcastManager lbm1 = LocalBroadcastManager.getInstance(activity);
            Intent localIn1;
            localIn1 = new Intent("REFRESH");
            lbm1.sendBroadcast(localIn1);
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    //Define your Interface method here
    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        private final ListSelcetdImageBinding selcetdImageBinding;

        public MyClassView(ListSelcetdImageBinding listSelcetdImageBinding) {
            super(listSelcetdImageBinding.getRoot());
            this.selcetdImageBinding = listSelcetdImageBinding;
        }
    }
}
