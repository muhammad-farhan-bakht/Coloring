package com.obn.bayscoloring;


import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.obn.bayscoloring.common.Common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListFilesAct extends AppCompatActivity {

    List<File> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

        initToolbar();
        initViews();
    }

    private void initViews() {

    }

    private List<File> loadFile() {

        ArrayList<File> inFiles = new ArrayList<>();

        File parendDir;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parendDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + getString(R.string.app_name));
        }else {
            parendDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + getString(R.string.app_name));
        }

        File[] files = parendDir.listFiles();

        for(File file : files) {
            if (file.getName().endsWith(".png"))
                inFiles.add(file);
        }

        TextView textView = findViewById(R.id.status_empty);
        if(files.length > 0) {
            textView.setVisibility(View.GONE);
        }else {
            textView.setVisibility(View.VISIBLE);
        }

        return inFiles;
    }


    private void initToolbar() {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return true;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals((Common.DELETE))) {
            deleteFile(item.getOrder());
            initViews();
        }
        return true;
    }

    private void deleteFile(int order) {
        fileList.get(order).delete();
        fileList.remove(order);
    }
}
