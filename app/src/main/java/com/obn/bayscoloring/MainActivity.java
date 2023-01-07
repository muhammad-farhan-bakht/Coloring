package com.obn.bayscoloring;

//DONE youtube kanalı: https://www.youtube.com/@hazemourari4049/videos

//DONE video tarifi 1: https://www.youtube.com/watch?v=Kfz5blPMa9M (31.12.2019)
//DONE video tarifi 2: https://www.youtube.com/watch?v=Xea6NN1X-2Y (02.01.2020)
//DONE video tarifi 3: https://www.youtube.com/watch?v=kRZt7kXMpUk (02.01.2020)
//DONE video tarifi 4: https://www.youtube.com/watch?v=mLrgRs0IzmE (03.01.2020)
//DONE video tarifi 5: https://www.youtube.com/watch?v=HkL9Zjd_re8 (04.01.2020)
//DONE video tarifi 6: https://www.youtube.com/watch?v=rzq9Uk3EJFY (05.01.2020)

//DONE video tarifi 7: https://www.youtube.com/watch?v=PHOQKRzZ4ws (13.02.2020)
//DONE video tarifi 8: https://www.youtube.com/watch?v=jrGYTONJq9w (13.02.2020)
//DONE video tarifi 9: https://www.youtube.com/watch?v=IBQj5zwJbNg (13.02.2020)

//DONE video tarifi 10: https://www.youtube.com/watch?v=66B2tc2kuow (20.02.2020)
//DONE video tarifi 11: https://www.youtube.com/watch?v=q_eQI4fuVOY (06.02.2020)
//DONE video tarifi 12: https://www.youtube.com/watch?v=XXvupm4SNWA (21.02.2020)
//DONE video tarifi 13: https://www.youtube.com/watch?v=RZorskvPIrs (05.03.2020)
//DONE video tarifi 14: https://www.youtube.com/watch?v=xRhc_SewBYA (05.03.2020)
//DONE video tarifi 15: https://www.youtube.com/watch?v=NnX09lTnZRY (27.02.2020)
//DONE video tarifi 16: https://www.youtube.com/watch?v=TjRpOX2uwWc (17.01.2020)
//DONE video tarifi 17: https://www.youtube.com/watch?v=iwQmAoBvygE (16.02.2020) Fix error get image from gallery android studio

//DONE video tarifi 18: https://www.youtube.com/watch?v=PmQxFm0Fd8g (16.02.2020) fix error image not show in gallery android studio




import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.obn.bayscoloring.adabters.ImageAdabter;
import com.obn.bayscoloring.common.Common;
import com.obn.bayscoloring.widget.PaintView;


import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

import kotlin.text.MatchNamedGroupCollection;


public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSION = 1001 ;
    private static final int PICK_IMAGE = 1111;
    private static final int REQUEST_FOR_GET_IMAGE_FROM_GALLERY = 1002 ;
    Button buttonImageUpload;
    ImageView imageView;
    Intent intent_me;
    private Bitmap image;
    PaintView mPaintView;
    int colorBackground, colorBrush;
    int brushSize, eraserSize;

    RecyclerView recyclerView;
    ImageAdabter adabter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTollbar();
        initView();
        //initTools();


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FOR_GET_IMAGE_FROM_GALLERY);
        } else{
            getImage();
        }




    }

    private void initView() {
        recyclerView = findViewById(R.id.recycle_view_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adabter = new ImageAdabter(this);
        recyclerView.setAdapter(adabter);



    }

    private void initTollbar() {

        Toolbar toolbar = findViewById(R.id.tollbar);
        setSupportActionBar(toolbar);
        int toolbar_item_color = ResourcesCompat.getColor(getResources(),R.color.blue,null);
        toolbar.setTitleTextColor(toolbar_item_color);
        toolbar.setSubtitleTextColor(toolbar_item_color);
        getSupportActionBar().setTitle("My Pictures");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getImageFromGallery(View view) {

    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

        requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
    }else {
        getImage();
    }

    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          if(requestCode == REQUEST_PERMISSION ){
              try {
                  saveBitmap();
              } catch (IOException e) {
                  e.printStackTrace();
              }
        }else if(requestCode == REQUEST_FOR_GET_IMAGE_FROM_GALLERY) {
              getImage();

          } else if(requestCode == REQUEST_FOR_GET_IMAGE_FROM_GALLERY){
              getImage();
          }

        }else {
            Toast.makeText(this, "You need accept permission.", Toast.LENGTH_SHORT).show();

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

















    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PICK_IMAGE && data != null && resultCode == RESULT_OK) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


                try {
                   ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r");
                   Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor());
                    //mPaintView.setImage(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }else {
                Uri pickedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

               // mPaintView.setImage(bitmap);
                cursor.close();
            }


          //  Common.IMAGE_FROM_GALLERY = bitmap;
            Common.ITEM_SELECTED = "0";
            startActivity(new Intent(this, PaintActivity.class));


        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showImages(View view) {
        Common.ITEM_SELECTED = "0";
        startActivity(new Intent(this, WorkListAct.class));


    }



    //gereksizce kodlar ama lazım olabilir


//@Override
//public void onSelected(String name) {

    //   switch (name) {

       //    case Common.BRUSH:
       //    mPaintView.toMove = false;
       //    mPaintView.disableEraser();
       //    mPaintView.invalidate();
       //    showDialogSize(false);
       //    break;
//
       //    case Common.ERASER:
       //    mPaintView.enableEraser();
       //    showDialogSize(true);
       //    break;
//
       //    case Common.RETURN:
       //    mPaintView.returnLastAction();
       //    break;
//
       //    case Common.BACKGROUND:
       //    updateColor(name);
       //    break;
//
       //    case Common.COLORS:
       //    updateColor(name);
      ////    break;

        //   case Common.IMAGE:

    //       if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    //       != PackageManager.PERMISSION_GRANTED) {
    //           requestPermissions(new String[] {
    //                   Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FOR_GET_IMAGE_FROM_GALLERY);
    //           }else {
    //           getImage();

    //           break;

    //       }
    //  }











private void updateColor(final String name) {

}
private void showDialogSize(final boolean isEraser) {

}

private void initTools() {

}


public void finishPaint (View view) {
        finish();
    }

    public void shareApp(View view) {

    }

    public void showFiles(View view) {
        startActivity((new Intent(this, ListFilesAct.class)));
    }

    public void saveFile(View view) {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }else {
            try {
                saveBitmap();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }






    }
    private void saveBitmap() throws IOException {

        Bitmap bitmap = mPaintView.getBitmap();
        String file_name = UUID.randomUUID() + ".png";
        OutputStream outputStream;
        boolean saved;
        File folder;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

            folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ File.separator+getString(R.string.app_name));

        }else {
            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ File.separator+getString(R.string.app_name));

        }



        if(!folder.exists()){
            folder.mkdirs();
        }


        File image = new File(folder+File.separator+file_name);
        Uri imageUri = Uri.fromFile(image);

            outputStream = new FileOutputStream(image);
            saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    ContentResolver resolver = getContentResolver();
    ContentValues contentValues = new ContentValues();
    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, file_name);
    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_name));
    Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    outputStream = resolver.openOutputStream(uri);
    saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);


} else {

sendPictureToGallery(imageUri);

}

if(saved)
    Toast.makeText(this, "picture saved", Toast.LENGTH_SHORT).show();
else
    Toast.makeText(this, "picture not saved", Toast.LENGTH_SHORT).show();


        outputStream.flush();
        outputStream.close();






    }

    private void sendPictureToGallery(Uri imageUri) {

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        sendBroadcast(intent);

    }







}

