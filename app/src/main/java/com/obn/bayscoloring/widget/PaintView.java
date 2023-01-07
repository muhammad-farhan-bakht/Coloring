package com.obn.bayscoloring.widget;

import static com.obn.bayscoloring.widget.PaintSurfaceView.toMove;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.obn.bayscoloring.R;
import com.obn.bayscoloring.common.Common;
import com.obn.bayscoloring.util.FloodFill;

import java.util.ArrayList;
import java.util.List;



public class PaintView extends View {

   // Bitmap bitmap;
    private float mPositionX, mPositionY;
    private float refX, refY;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    private final static float mMinZoom = 1.0f;
    private final static float mMaxZoom = 5.0f;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private Bitmap defaultBitmap = null;



        Drawable drawable = getResources().getDrawable(R.drawable.ic_baseline_next_plan_24);
        PaintView mPaintView;




     //  mPaint.setAntiAlias(true);
     //  mPaint.setDither(true);
     //  mPaint.setStyle(Paint.Style.STROKE);
     //  mPaint.setStrokeCap(Paint.Cap.ROUND);
     //  mPaint..setStrokeJoin(Paint.Join.ROUND);
     //  mPaint.setStrokeWidth(toPx(sizeBrush));

    //because is .xml
   // Drawable drawable = getResources().getDrawable(R.drawable.ic_baseline_rotate_right_24);
   // rotateImage = drawableToBitmap(drawable);

    //.png
  //  captureImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_photo_camera_24);










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


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector){

            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(mMinZoom, Math.min(mScaleFactor, mMaxZoom)); //mMinZoom yerine mScaleFactor yapınca görüntü çook zoom olup ekrandan taşıyordu
            invalidate();

            return true;
        }
    }



    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

      //  btmBackground = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
     //   btmView = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
      //  mCanvas = new Canvas(btmView);


        if (bitmap == null) {

            Bitmap srcBitmap = null;

            if(Common.IMAGE_FROM_GALLERY != null) {
                srcBitmap = Common.IMAGE_FROM_GALLERY;
            }else {
                srcBitmap = BitmapFactory.decodeResource(getResources(), Common.PICTURE_SELECTED);
            }


            bitmap = Bitmap.createScaledBitmap(srcBitmap, w, h, false);

            for (int i=0; i<bitmap.getWidth();i++){
                for (int j=0; j<bitmap.getHeight();j++){
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

 //       canvas.drawColor(colorBackground);
 //     canvas.drawBitmap(btmBackground,0,0,null);
//
 //       if(image != null && toMove) {
//
 //           drawImage(canvas);
 //           xCenter = leftImage + image.getWidth()/2 - captureImage().getWidth/2;
 //           yCenter = topImage + image.getHeight()/2 - captureImage().getHeight/2;
//
 //           xRotate = leftImage + image.getWidth() + toPx(10);
 //           yRotate = topImage - toPx(10);
 //           canvas.drawBitmap(rotateImage, xRotate, yRotate, null);
 //           canvas.drawBitmap(captureImage, xCenter, yCenter, null);
//
 //       }
 //       canvas.drawBitmap(btmView, 0, 0, null);

        

    }




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

            if (targetColor != Color.BLACK) {
                FloodFill.floodFill(bitmap, new Point(x, y), targetColor, Common.COLOR_SELECTED);
                addLastAction(Bitmap.createBitmap(getBitmap()));
                invalidate();

            }
        }

        public Bitmap getBitmap () {

            return bitmap;

        }

        public Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

    private void drawImage(Canvas canvas){

     //   Matrix matrix = new Matrix();
     //   matrix.setRotate(angle, image.getWidth()/2, image.getHeight()/2);
     //   matrix.postTranslate(leftImage, topImage);
     //   canvas.drawBitmap(image, matrix, null);
    }


    private float toPx (int sizeBrush) {
        return sizeBrush*(getResources().getDisplayMetrics().density);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {

        if(drawable instanceof BitmapDrawable){
            return((BitmapDrawable) drawable).getBitmap();
        }
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            drawable.setBounds(0, 0, c.getWidth(), c.getHeight());
            drawable.draw(c);

            return bitmap;
    }



    }


