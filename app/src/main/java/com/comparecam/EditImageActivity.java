package com.comparecam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;


/**
 * Created by darpan.jain on 7/7/2015.
 */
public class EditImageActivity extends Activity {
    private static final String TAG = "EditImageActivity";
    private String imagePath;
    private ImageView backImageView;
    private TextView doneLableTextView;
    private Uri mImageUri;
    private RelativeLayout mRotateImageRightRL;
    private Bitmap mFinalImage;
    private Uri mFinalUri;
    private CropImageView mCropImageView;
    private RelativeLayout mRotateImageLeftRL;

    public static final String IMAGE_URI ="IMAGE_URI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_edit_image);
        initView();
        initData();
        initListners();

        //beginCrop(mImageUri);

        final CropImageView cropImageView = mCropImageView;

        final ViewTreeObserver vto = cropImageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
               /* LayerDrawable ld = (LayerDrawable) cropImageView.getBackground();
                ld.setLayerInset(1, 0, cropImageView.getHeight() / 2, 0, 0);
                ViewTreeObserver obs = cropImageView.getViewTreeObserver();*/
                final ViewTreeObserver vt = mCropImageView.getViewTreeObserver();
                beginCrop(mImageUri);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    vt.removeOnGlobalLayoutListener(this);
                } else {
                    vt.removeGlobalOnLayoutListener(this);
                }
            }

        });
    }

    private void initView() {

        backImageView = (ImageView) findViewById(R.id.back_icon);
        doneLableTextView = (TextView) findViewById(R.id.done);
        mRotateImageLeftRL = (RelativeLayout) findViewById(R.id.relativeLayoutRotateLeft);
        mRotateImageRightRL = (RelativeLayout) findViewById(R.id.relativeLayoutRotateRight);
        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);
        mCropImageView.setAspectRatio(1, 1);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setGuidelines(2);
    }

    private void initListners() {
        doneLableTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

Intent intent = new Intent(EditImageActivity.this, ShowImage.class);
                try {
                    mFinalImage = mCropImageView.getCroppedImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mFinalImage != null) {
                    File imageFile = new File(getCacheDir(), "finalImage");
                    OutputStream fos = null;
                    try {
                        fos = new FileOutputStream(imageFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (fos != null) {
                        mFinalImage.compress(Bitmap.CompressFormat.JPEG, 30, fos);
                    }

                    intent.putExtra(IMAGE_URI, Uri.fromFile(imageFile).toString());
                    startActivity(intent);
                }

                finish();
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        mRotateImageLeftRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rotateImage();
                mCropImageView.rotateImage(270);
            }
        });

        mRotateImageRightRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rotateImage();
                mCropImageView.rotateImage(90);
            }
        });
    }

    private void initData() {
        try {
            imagePath = getIntent().getExtras().getString(PickImageActivity.IMAGE_PATH);
            mImageUri = Uri.parse(imagePath);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void beginCrop(Uri source) {
        Bitmap bitmap = null;
        int targetHeight;
        int targetWidth;
        // try {

        // down sizing image as it throws OutOfMemory Exception for larger

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(source.getPath());
            //options.inSampleSize = 3;
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();

            targetHeight = mCropImageView.getHeight();
            targetWidth = mCropImageView.getWidth();

            if (height / width > 1) {
                targetWidth = width * targetHeight / height; // eg 1280
            } else {
                targetHeight = height * targetWidth / width; //eg 960
            }
            //bitmap = BitmapFactory.decodeFile(source.getPath(), options);
            Log.d(TAG, "height " + targetHeight + " width " + targetWidth);

            /*ExifInterface ei = new ExifInterface(source.toString());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int rotation = 0;
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }


            Log.d(TAG, "rotation " + rotation);
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, targetWidth, targetHeight,
                    matrix, true);*/
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, targetWidth, targetHeight);
            //bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
            mCropImageView.setImageBitmap(bitmap);
            //mCropImageView.rotateImage(rotation);

        } catch (Exception e) {
            Toast.makeText(EditImageActivity.this, "Image was too large, select another image.",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}
