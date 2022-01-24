package com.daily.image.pdf.imagetopdfconvert.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daily.image.pdf.imagetopdfconvert.Constant.Util;
import com.daily.image.pdf.imagetopdfconvert.R;
import com.daily.image.pdf.imagetopdfconvert.adapter.GeneratedPdfAdapter;
import com.daily.image.pdf.imagetopdfconvert.databinding.ActivityGeneratedPdfBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import es.dmoral.toasty.Toasty;

import static android.os.Build.VERSION.SDK_INT;

public class GeneratedPdfActivity extends AppCompatActivity implements GeneratedPdfAdapter.ItemClickListener {

    ActivityGeneratedPdfBinding generatedPdfBinding;
    MyClickHandlers myClickHandlers;

    ArrayList<String> generatedPDFList = new ArrayList<>();
    GeneratedPdfAdapter generatedPdfAdapter;
    BottomSheetBehavior moreOptionBehaviour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generatedPdfBinding = DataBindingUtil.setContentView(GeneratedPdfActivity.this, R.layout.activity_generated_pdf);
        myClickHandlers = new MyClickHandlers(GeneratedPdfActivity.this);
        generatedPdfBinding.setOnClick(myClickHandlers);

        moreOptionBehaviour = BottomSheetBehavior.from(generatedPdfBinding.rlBottom);

        generatedPdfBinding.rvPdf.setLayoutManager(new LinearLayoutManager(GeneratedPdfActivity.this, LinearLayoutManager.VERTICAL, false));
        generatedPdfAdapter = new GeneratedPdfAdapter(GeneratedPdfActivity.this, this::onItemClick);
        generatedPdfBinding.rvPdf.setAdapter(generatedPdfAdapter);

        moreOptionBehaviour.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (moreOptionBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    generatedPdfBinding.imgView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        generatedPdfBinding.rvPdf.setOnClickListener(v -> {
            if (generatedPdfBinding.cvSorting.getVisibility() == View.VISIBLE) {
                generatedPdfBinding.cvSorting.setVisibility(View.GONE);
                generatedPdfBinding.backRl.setVisibility(View.GONE);
            }
        });

        getFiles();
    }

    @Override
    public void onItemClick(int position) {
        if (moreOptionBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            moreOptionBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            generatedPdfBinding.imgView.setVisibility(View.VISIBLE);
            File newFile = new File(generatedPDFList.get(position));

            StringBuilder sb = new StringBuilder();
            sb.append(Util.getSize(newFile.length()));
            sb.append(", ");
            sb.append(Util.convertTimeDateModified(newFile.lastModified()));

            generatedPdfBinding.tvNameBottom.setText(newFile.getName());
            generatedPdfBinding.tvSizeDate.setText(sb.toString());

            generatedPdfBinding.rlShare.setOnClickListener(v -> {
                moreOptionBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                if (newFile.exists()) {
                    Uri URI = FileProvider.getUriForFile(GeneratedPdfActivity.this, getApplicationContext().getPackageName() + ".provider", newFile);
                    intentShareFile.setType("application/pdf");
                    intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, URI);
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Sharing File...");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
                    startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }
            });

            generatedPdfBinding.rlRename.setOnClickListener(v -> {
                renameFile(newFile);
                moreOptionBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            });

            generatedPdfBinding.rlDelete.setOnClickListener(v -> {
                moreOptionBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                deleteFile(newFile);
            });
        } else {
            moreOptionBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void renameFile(File file) {
        final Dialog dial = new Dialog(GeneratedPdfActivity.this, android.R.style.Theme_Black);
        dial.requestWindowFeature(1);
        dial.setContentView(R.layout.dialog_rename);
        dial.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dial.setCanceledOnTouchOutside(true);

        AppCompatEditText etRename = dial.findViewById(R.id.etFileName);

        etRename.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Util.hideKeyboard(etRename,GeneratedPdfActivity.this);
                return true;
            }
            return false;
        });

        dial.findViewById(R.id.yes).setOnClickListener(view -> {
            if (etRename.getText() != null && !etRename.getText().toString().isEmpty()) {
                boolean isRename = file.renameTo(new File(file.getParentFile().getAbsolutePath(), etRename.getText().toString() + ".pdf"));
                if (isRename) {
                    Toasty.success(GeneratedPdfActivity.this, "Rename Successfully.", Toasty.LENGTH_SHORT).show();
                    runOnUiThread(() -> {
                        generatedPdfAdapter.clear();
                        getFiles();
                    });
                } else {
                    Toasty.error(GeneratedPdfActivity.this, "Something went wrong", Toasty.LENGTH_SHORT).show();
                }
            }
            dial.dismiss();
        });

        dial.findViewById(R.id.no).setOnClickListener(v -> dial.dismiss());

        dial.show();
    }

    private void deleteFile(File file) {
        final Dialog dial = new Dialog(GeneratedPdfActivity.this, android.R.style.Theme_Black);
        dial.requestWindowFeature(1);
        dial.setContentView(R.layout.dialog_rename);
        dial.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dial.setCanceledOnTouchOutside(true);

        RelativeLayout rlEdit = dial.findViewById(R.id.rlEdit);
        AppCompatTextView tvDelete = dial.findViewById(R.id.tvDelete);
        TextView yes = dial.findViewById(R.id.yes);
        yes.setText("Delete");

        TextView tvTitleDel = dial.findViewById(R.id.tvTitleDel);
        tvTitleDel.setText("Delete File");

        rlEdit.setVisibility(View.GONE);
        tvDelete.setVisibility(View.VISIBLE);

        dial.findViewById(R.id.yes).setOnClickListener(view -> {
            if (file.exists()) {
                boolean isDelete = file.delete();
                if (isDelete) {
                    Toasty.success(GeneratedPdfActivity.this, "File deleted successfully.", Toasty.LENGTH_SHORT).show();
                    runOnUiThread(() -> {
                        generatedPdfAdapter.clear();
                        getFiles();
                    });
                } else {
                    Toasty.error(GeneratedPdfActivity.this, "Something went wrong", Toasty.LENGTH_SHORT).show();
                }
            }
            dial.dismiss();
        });

        dial.findViewById(R.id.no).setOnClickListener(v -> dial.dismiss());

        dial.show();
    }

    private void getFiles() {
        File folder;
        if (SDK_INT >= Build.VERSION_CODES.R) {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            folder = new File(cw.getExternalFilesDir(Environment.DIRECTORY_DCIM), getResources().getString(R.string.app_name));
        } else {
            folder = new File(Util.BASE_PATH, getResources().getString(R.string.app_name));
        }
        if (folder.exists()) {
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                generatedPDFList.add(files[i].getPath());
            }

            if (generatedPDFList != null && generatedPDFList.size() > 0) {
                generatedPdfAdapter.addAll(generatedPDFList);
            }
        }
        if (generatedPDFList.size() > 0) {
            generatedPdfBinding.imgNoData.setVisibility(View.GONE);
            generatedPdfBinding.imgSorting.setVisibility(View.VISIBLE);
        } else {
            generatedPdfBinding.imgNoData.setVisibility(View.VISIBLE);
            generatedPdfBinding.imgSorting.setVisibility(View.GONE);
        }
    }

    public class MyClickHandlers {
        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onSortingClick(View view) {
            Log.e("LLL_Click: ", "Back");
            if (generatedPdfBinding.cvSorting.getVisibility() == View.VISIBLE) {
                generatedPdfBinding.cvSorting.setVisibility(View.GONE);
                generatedPdfBinding.backRl.setVisibility(View.GONE);
            } else {
                generatedPdfBinding.cvSorting.setVisibility(View.VISIBLE);
                generatedPdfBinding.backRl.setVisibility(View.VISIBLE);
            }
        }

        public void onBack(View view) {
            onBackPressed();
        }

        public void onNameAscending(View view) {
            Collections.sort(generatedPDFList, (mediaFileListModel, t1) -> {
                File file1 = new File(mediaFileListModel);
                File file2 = new File(t1);
                return file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase()); // Name wise
            });

            generatedPdfAdapter.notifyDataSetChanged();
            generatedPdfBinding.cvSorting.setVisibility(View.GONE);
            generatedPdfBinding.backRl.setVisibility(View.GONE);
        }

        public void onNameDescending(View view) {
            Collections.sort(generatedPDFList, (mediaFileListModel, t1) -> {
                File file1 = new File(mediaFileListModel);
                File file2 = new File(t1);
                return file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase()); // Name wise
            });
            Collections.reverse(generatedPDFList);
            generatedPdfAdapter.notifyDataSetChanged();
            generatedPdfBinding.cvSorting.setVisibility(View.GONE);
            generatedPdfBinding.backRl.setVisibility(View.GONE);
        }

        public void onDateAscending(View view) {
            // Date Ascending
            SimpleDateFormat format = new SimpleDateFormat("dd LLL yyyy");
            Collections.sort(generatedPDFList, (t2, t1) -> {
                File file = new File(t2);
                String lastModDate = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
                Date arg0Date = null;
                try {
                    arg0Date = format.parse(lastModDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                file = new File(t1);
                String lastModDate2 = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
                Date arg1Date = null;
                try {
                    arg1Date = format.parse(lastModDate2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return arg0Date.compareTo(arg1Date);
            });
            generatedPdfAdapter.notifyDataSetChanged();
            generatedPdfBinding.cvSorting.setVisibility(View.GONE);
            generatedPdfBinding.backRl.setVisibility(View.GONE);
        }

        public void onDateDescending(View view) {
            SimpleDateFormat format = new SimpleDateFormat("dd LLL yyyy");
            Collections.sort(generatedPDFList, (t2, t1) -> {
                File file = new File(t2);
                String lastModDate = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
                Date arg0Date = null;
                try {
                    arg0Date = format.parse(lastModDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                file = new File(t1);
                String lastModDate2 = new SimpleDateFormat("dd LLL yyyy").format(new Date(file.lastModified()));
                Date arg1Date = null;
                try {
                    arg1Date = format.parse(lastModDate2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return arg0Date.compareTo(arg1Date);
            });
            Collections.reverse(generatedPDFList);
            generatedPdfAdapter.notifyDataSetChanged();
            generatedPdfBinding.cvSorting.setVisibility(View.GONE);
            generatedPdfBinding.backRl.setVisibility(View.GONE);
        }

        public void onSizeAscending(View view) {
            Collections.sort(generatedPDFList, (t1, t2) -> {
                File f1 = new File(t2);
                File f2 = new File(t1);
                return Long.valueOf(f1.length()).compareTo(f2.length());
            });
            Collections.reverse(generatedPDFList);

            generatedPdfAdapter.notifyDataSetChanged();
            generatedPdfBinding.cvSorting.setVisibility(View.GONE);
            generatedPdfBinding.backRl.setVisibility(View.GONE);
        }

        public void onSizeDescending(View view) {
            Collections.sort(generatedPDFList, (t1, t2) -> {
                File f1 = new File(t2);
                File f2 = new File(t1);
                return Long.valueOf(f1.length()).compareTo(f2.length());
            });

            generatedPdfAdapter.notifyDataSetChanged();
            generatedPdfBinding.cvSorting.setVisibility(View.GONE);
            generatedPdfBinding.backRl.setVisibility(View.GONE);
        }
    }

}