package com.comparecam;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FaceDetectionActivity extends Activity {
    private String finalImageURI;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        finalImageURI = getIntent().getExtras().getString(EditImageActivity.IMAGE_URI);

        if (finalImageURI != null && finalImageURI != "") {
            setContentView(new MyView(this));

        } else {
            finish();
            Toast.makeText(FaceDetectionActivity.this, "Image malformed", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyView extends View {
        private Bitmap myBitmap;
        private int width, height;
        private FaceDetector.Face[] detectedFaces;
        private int NUMBER_OF_FACES = 10;
        private FaceDetector faceDetector;
        private int NUMBER_OF_FACE_DETECTED;
        private float eyeDistance;

        public MyView(Context context) {
            super(context);
            BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
            bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            //myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.celebrity, bitmapFatoryOptions);
            myBitmap = BitmapFactory.decodeFile(Uri.parse(finalImageURI).getPath(), bitmapFatoryOptions);

           /* FileInputStream fis = null;
            try {
                fis = new FileInputStream(new File(getRealPathFromURI(Uri.parse(finalImageURI))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            myBitmap = BitmapFactory.decodeStream(fis, null,  bitmapFatoryOptions);
*/
          /*  try {
               myBitmap =  MediaStore.Images.Media.getBitmap(FaceDetectionActivity.this.getContentResolver(), Uri.parse(finalImageURI)  );
            //BitmapFactory.decodeFile()
            } catch (IOException e) {
                e.printStackTrace();
            }*/


            if(myBitmap!=null) {
                width = myBitmap.getWidth();
                height = myBitmap.getHeight();
                detectedFaces = new FaceDetector.Face[NUMBER_OF_FACES];
                faceDetector = new FaceDetector(width, height, NUMBER_OF_FACES);
                NUMBER_OF_FACE_DETECTED = faceDetector.findFaces(myBitmap, detectedFaces);
            }else{
                Toast.makeText(FaceDetectionActivity.this, "Please try again in some time", Toast.LENGTH_SHORT).show();
            }
        }

        private String getRealPathFromURI(Uri contentUri) {
            String[] proj = { MediaStore.Images.Media.DATA };
            CursorLoader loader = new CursorLoader(FaceDetectionActivity.this, contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(myBitmap, 0, 0, null);
            Paint myPaint = new Paint();
            myPaint.setColor(Color.GREEN);
            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(3);

            for (int count = 0; count < NUMBER_OF_FACE_DETECTED; count++) {
                Face face = detectedFaces[count];
                PointF midPoint = new PointF();
                face.getMidPoint(midPoint);

                eyeDistance = face.eyesDistance();
                canvas.drawRect(midPoint.x - eyeDistance, midPoint.y - eyeDistance, midPoint.x + eyeDistance, midPoint.y + eyeDistance, myPaint);
            }
        }

    }
}