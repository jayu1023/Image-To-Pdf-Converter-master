package com.daily.image.pdf.imagetopdfconvert.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.daily.image.pdf.imagetopdfconvert.Constant.Util;
import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.databinding.ListGeneratedPdfBinding;

import java.io.File;
import java.util.ArrayList;

public class GeneratedPdfAdapter extends RecyclerView.Adapter<GeneratedPdfAdapter.MyClassView> {

    private final ItemClickListener mItemClickListener;
    ArrayList<String> fileList = new ArrayList<>();
    Activity activity;

    public GeneratedPdfAdapter(Activity activity, ItemClickListener itemClickListener) {
        this.activity = activity;
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListGeneratedPdfBinding generatedPdfBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_generated_pdf, parent, false);
        return new MyClassView(generatedPdfBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GeneratedPdfAdapter.MyClassView holder, int position) {
        File file = new File(fileList.get(position));
        holder.generatedPdfBinding.tvName.setText(file.getName());

        StringBuilder sb = new StringBuilder();
        sb.append(Util.getSize(file.length()));
        sb.append(", ");
        sb.append(Util.convertTimeDateModified(file.lastModified()));

        holder.generatedPdfBinding.tvSizeDate.setText(sb.toString());

        holder.generatedPdfBinding.imgMore.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }
        });

        holder.generatedPdfBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewPdf = new Intent(Intent.ACTION_VIEW);
                viewPdf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri URI = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
                viewPdf.setDataAndType(URI, "application/pdf");
                viewPdf.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(viewPdf);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void addAll(ArrayList<String> fileList) {
        this.fileList = fileList;
        notifyDataSetChanged();
    }

    public void clear() {
        this.fileList.clear();
        notifyDataSetChanged();
    }

    //Define your Interface method here
    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        private final ListGeneratedPdfBinding generatedPdfBinding;

        public MyClassView(ListGeneratedPdfBinding generatedPdfBinding) {
            super(generatedPdfBinding.getRoot());
            this.generatedPdfBinding = generatedPdfBinding;
        }
    }

}
