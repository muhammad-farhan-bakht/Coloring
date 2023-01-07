package com.obn.bayscoloring;


//import androidx.annotation.Nullable;
import static java.lang.System.out;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.obn.bayscoloring.common.Common;
import com.obn.bayscoloring.widget.PaintSurfaceView;
import com.obn.bayscoloring.widget.PaintView;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Permission;
import java.util.UUID;

public class PaintActivity extends AppCompatActivity implements SpectrumPalette.OnColorSelectedListener {

    private static final int PERMISSION_REQUEST = 10001  ;
    PaintSurfaceView paintView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        initToolbar();

        SpectrumPalette spectrumPalette = findViewById(R.id.palette);
        spectrumPalette.setOnColorSelectedListener(this);
        paintView = findViewById(R.id.paint_view);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Common.IMAGE_FROM_GALLERY = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        paintView.startDrawThread();
    }

    @Override
    protected void onPause() {
        paintView.stopDrawThread();

        super.onPause();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.tollbar); //Gerekirse typecasting ile (Toolbar) eklersin
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        }catch (Exception e1){
            out.println("HATA " + e1.getMessage());

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_save){
            showDialogForSave();

        }else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialogForSave() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED){ requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
    }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Save picture ?");

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialogInterface.dismiss();
                }
            });

            builder.show();



        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onColorSelected(int color) {
        Common.COLOR_SELECTED = color;

    }

    private void save() throws IOException {

        Bitmap bitmap = paintView.getBitmap();

        String file_name = "paint_" + UUID.randomUUID().toString().substring(0,5) + ".png";
        OutputStream outputStream;
        boolean saved;
        File folder;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //folder = new File(Environment.getExternalStorageDirectory()+File.separator+Environment.DIRECTORY_PICTURES+File.separator+getString(R.string.app_name));
            folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+File.separator+getString(R.string.app_name));

        }else{

            folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +File.separator+getString(R.string.app_name));
        }




        if(!folder.exists())
            folder.mkdirs();


        File subFolder = new File(folder, Common.ITEM_SELECTED);

        if(!subFolder.exists())
            subFolder.mkdirs();

        File image = new File (subFolder+File.separator+file_name);
        Uri imageUri = Uri.fromFile(image);


      //  try {
      //      FileOutputStream out = new FileOutputStream(subFolder+File.separator+file_name);
      //      bitmap.compress(Bitmap.CompressFormat.PNG,100, out); //Dosyayı PNG uzantılı olarak kaydet
      //  } catch (FileNotFoundException e) {
      //      e.printStackTrace();
      //  }
//

        outputStream = new FileOutputStream(image);
        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,file_name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+File.separator+getString(R.string.app_name));
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            outputStream = resolver.openOutputStream(uri);
            saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        }else {
            sendPictureToGallery(imageUri);
        }

        if(saved)
            Toast.makeText(this, "Picture saved", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Error while saving the picture", Toast.LENGTH_SHORT).show();

        outputStream.flush();
        outputStream.close();
        finish();


    }

    private void sendPictureToGallery(Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        sendBroadcast(intent);
    }

    public void selectColor(View view) {

        ColorPickerDialogBuilder.with(this)
                .initialColor(Common.COLOR_SELECTED)
                .setTitle("Select Color")
                .density(12)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .setPositiveButton("OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
                        Common.COLOR_SELECTED = lastSelectedColor;
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).build().show();

    }

    public void undoLastAction(View view) {
        ImageButton button = (ImageButton) view;
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                paintView.newPage();
                return true;
            }
        });

        paintView.undoLastAction();

    }
}