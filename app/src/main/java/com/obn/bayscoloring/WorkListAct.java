package com.obn.bayscoloring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;

import com.obn.bayscoloring.adabters.WorkAdabter;
import com.obn.bayscoloring.common.Common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorkListAct extends AppCompatActivity {

    private List<File> listImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_list);

        initToolbar();

        initView();

    }

    private void initView() {

        RecyclerView recyclerView = findViewById(R.id.recycle_view_work);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        listImage = getFiles();

        WorkAdabter adabter = new WorkAdabter(this, listImage  );
        recyclerView.setAdapter(adabter);

    }

    private List<File> getFiles() {

        ArrayList<File> result = new ArrayList<>();


        File folder;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + getString(R.string.app_name));
        }else {
            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + getString(R.string.app_name));
        }



        File subFolder = new File(folder, Common.ITEM_SELECTED);

        File[] files = subFolder.listFiles();

        if(files != null){
            for ( File file : files){

                if(file.getName().endsWith(".png")){
                    result.add(file);
                }


            }
        }

            return result;
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.tollbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.blue, null));

        getSupportActionBar().setTitle("Work List");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return true;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals(Common.DELETE)){
            deleteFile(item.getOrder());
            initView();
        }


        return true;
    }

    private void deleteFile(int order) {

        listImage.get(order).delete();
        listImage.remove(order);

    }


}