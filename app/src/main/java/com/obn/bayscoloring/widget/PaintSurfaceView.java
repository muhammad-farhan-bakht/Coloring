package com.obn.bayscoloring.widget;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.obn.bayscoloring.common.Common;
import com.obn.bayscoloring.util.FloodFill;

import java.util.ArrayList;
import java.util.List;

public class PaintSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {



    private Bitmap btmBackground, btmView, image, captureImage, originalImage, rotateImage;
    private Paint mPaint = new Paint();
    private Path mPath = new Path();
    private int ColorBackground, sizeBrush, sizeEraser;
    private float mX,mY;
    private Canvas mCanvas;
    private final int DEFFERENCE_SPACE = 4;
    private ArrayList<Bitmap> listAction = new ArrayList<>();
    private int leftImage = 50, topImage = 50;
    public static boolean toMove = false;
    private boolean toResize = false;
    private float refX, refY;
    private int xCenter, yCenter;
    private float xRotate, yRotate;
    private int angle = 0;

    private SurfaceHolder holder;
    private Thread drawThread;
    private boolean surfaceReady = false;
    private  boolean drawingActive = false;
    private static final int MAX_FRAME_TIME = (int) (1000.0 / 60.0);
    private static final String LOGTAG = "Surface view";


    private static final Object REQUEST_FOR_GET_IMAGE_FROM_GALLERY = 1002;
    private Bitmap bitmap;
    private float mPositionX, mPositionY;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    private final static float mMinZoom = 1.0f;
    private final static float mMaxZoom = 5.0f;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private Bitmap defaultBitmap = null;

    private static final int MAX_BITMAP = 10;



    public PaintSurfaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();




        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());


    }

    private void init() {

        holder = getHolder();
        holder.addCallback(this);

        sizeEraser = sizeBrush = 12;
       // colorBackground = Color.WHITE;

        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);


    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap();
        }
    return null;

    }


    public void returnLastAction() {

        if(listAction.size() > 0) {
            listAction.remove(listAction.size()-1);

            if(listAction.size() > 0) {
                btmView = listAction.get(listAction.size()-1);
            }else {
                btmView = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            }
            mCanvas = new Canvas(btmView);
            invalidate();
        }

    }







    public void undoLastAction() {
        if(bitmapList.size() > 0) {
           bitmapList.remove(bitmapList.size() - 1);
           if(bitmapList.size() > 0) {
               bitmap = bitmapList.get(bitmapList.size() - 1);
           }else {
               bitmap = Bitmap.createBitmap(defaultBitmap);
           }

            invalidate();
        }

    }

    private void addLastAction(Bitmap b){
        bitmapList.add(b);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        this.holder = surfaceHolder;

        if(drawThread != null) {

            drawingActive = false;
            try {
                drawThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        surfaceReady = true;
        startDrawThread();
    }

    public void startDrawThread() {

        if(surfaceReady && drawThread == null) {
            drawThread = new Thread(this, "Draw thread");
            drawingActive = true;
            drawThread.start();
        }

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int width, int height) {
        if(width == 0 || height == 0) {
            return;
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        stopDrawThread();
        surfaceHolder.getSurface().release();
        this.holder = null;
        surfaceReady = false;
    }

    public void stopDrawThread() {

        if(drawThread == null) {
            Log.d(LOGTAG,"DrawThread is null");
            return;
        }
        drawingActive = false;

        while(true){

            try {
                Log.d(LOGTAG,"Request last frame");
                drawThread.join(5000);
                break;
            } catch (InterruptedException e) {
                Log.e(LOGTAG,"Could not join with draw thread ");
            }

        }

        drawThread = null;

    }

    @Override
    public void run() {

        Log.d(LOGTAG,"Draw Thread started");
        long frameStartTime;
        long frameTime;

        if(Build.BRAND.equalsIgnoreCase("google")
            && Build.MANUFACTURER.equalsIgnoreCase("asus")
            && Build.MODEL.equalsIgnoreCase("Nexus 7")) {


            Log.w(LOGTAG,"Sleep 500ms (Device : Asus Nexus 7");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try{
            while(drawingActive) {
                if(holder == null) {
                    return;
                }
                frameStartTime = System.nanoTime();
                Canvas canvas = holder.lockCanvas();
                if(canvas != null){
                    try{
                        canvas.drawColor(Color.WHITE);
                        drawBitmap(canvas);
                    }finally{
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
                frameTime = (System.nanoTime() - frameStartTime) / 1000000;
                if(frameTime < MAX_FRAME_TIME) {

                    try {
                        Thread.sleep(MAX_FRAME_TIME - frameStartTime);
                    }catch (Exception e) {

                    }

                }

            }

        }catch (Exception e) {
            Log.w(LOGTAG, "Exception while locking/unlocking");
        }
        Log.d(LOGTAG, "Draw thread finnished");

    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector){

            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(mMinZoom, Math.min(mScaleFactor, mMaxZoom)); //mMinZoom yerine mScaleFactor yapınca görüntü çook zoom olup ekrandan taşıyordu
            invalidate();

            return true;
        }
    }



    public PaintSurfaceView(Context context) {
        super(context);
    }






    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);






        if (bitmap == null) {

            Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), Common.PICTURE_SELECTED);

          try{
              bitmap = Bitmap.createScaledBitmap(srcBitmap, w, h, false);
          }catch (Exception e_hata) {
              System.out.println("e_hatası oluyor: " + e_hata);
          }

            int genislik = bitmap.getWidth();
            int yukseklik = bitmap.getHeight();

            for (int i=0; i<genislik; i++){
                for (int j=0; j<yukseklik; j++){

                    int alpha = 255 - brightness(bitmap.getPixel(i, j));

                    if(alpha < 200) {
                        bitmap.setPixel(i, j, Color.WHITE);
                    }else {
                        bitmap.setPixel(i, j, Color.BLACK);
                    }

                }
            }

            if(defaultBitmap == null)
                defaultBitmap = Bitmap.createBitmap(bitmap);
        }

    }

    private int brightness(int color) {
        return (color >> 16) & 0xff;
    }



    /*SurfaceView eklenince bu kodları devre dışı bırakmak gerekti


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawBitmap(bitmap,0,0,null);
        drawBitmap(canvas);
        

    }

     */

    private void drawBitmap(Canvas canvas) {
        canvas.save();
        canvas.translate(mPositionX, mPositionY);
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

       // paint((int) event.getX(), (int)event.getY());

        mScaleDetector.onTouchEvent(event);
        switch(event.getAction()){

            case MotionEvent.ACTION_DOWN:
            refX = event.getX();
            refY = event.getY();
            paint((int) ((refX - mPositionX)/mScaleFactor), (int)((refY-mPositionY)/mScaleFactor));
            break;

            case MotionEvent.ACTION_MOVE:
                float nX = event.getX();
                float nY = event.getY();

                mPositionX += nX - refX;
                mPositionY += nY - refY;

                refX = nX;
                refY = nY;

                invalidate();

        }


        return true;
    }

    private void paint(int x, int y) {

        if (x < 0 || y < 0 || x >= bitmap.getWidth() || y >= bitmap.getHeight())
            return;


            int targetColor = bitmap.getPixel(x, y);

            if (targetColor != Color.BLACK && targetColor != Common.COLOR_SELECTED) {
                FloodFill.floodFill(bitmap, new Point(x, y), targetColor, Common.COLOR_SELECTED);
                addLastAction(Bitmap.createBitmap(getBitmap()));

                if(bitmapList.size() > MAX_BITMAP){
                    for(int i=0; i<5; i++) {
                        bitmapList.remove(i);
                    }
                }


                invalidate();

            }
        }

        public Bitmap getBitmap () {

            return bitmap;

        }

        public void newPage(){
        bitmap = Bitmap.createBitmap(defaultBitmap);
        invalidate();
        }
    }


