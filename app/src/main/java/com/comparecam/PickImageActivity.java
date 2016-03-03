/*
 * !
 *  *  Project     : MoveIn
 *  *  File Name   : BasicProfileActivity
 *  *  Description :
 *  *  Created by  : Darpan
 *  *  Created on  : 6/24/2015 : 4:20 PM
 *  *  Reviewed by :
 *  *  Reviewed on :
 *  *  Copyright (c) 2015 move.in. All rights reserved.
 *
 */

package com.comparecam;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


public class PickImageActivity extends Activity{

    public static final int EDIT_IMAGE = 12;
    public static final String IMAGE_PATH = "IMAGE_PATH";
    public static final String IMAGE_REMOVED = "IMAGE_REMOVED";
    private static final int GALLERY_REQUEST = 3;
    private static final int CAMERA_REQUEST = 4;
    private static final String TAG = PickImageActivity.class.getSimpleName();
    private Intent pictureActionIntent;
    private Uri mImageUri;
    private Button mCameraButtton;
    private Button mGalleryButtton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_edit_profile);

        inflateLayout();
        //All UI Component Listners
        registerListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri imageUri = null;

            if (requestCode == GALLERY_REQUEST) {

                if (data != null) {

                    imageUri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(imageUri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Intent intent = new Intent(PickImageActivity.this, EditImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(PickImageActivity.IMAGE_PATH, picturePath);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, PickImageActivity.EDIT_IMAGE);


                } else {
                    Toast.makeText(getApplicationContext(), "Could not select Image",
                            Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == CAMERA_REQUEST) {

                if (null != mImageUri) { //data.getExtras().get("data")){
                    imageUri = mImageUri;
                    //imageUri = data.getExtras().get("data");
                    Intent intent = new Intent(PickImageActivity.this, EditImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(PickImageActivity.IMAGE_PATH, imageUri.toString());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, PickImageActivity.EDIT_IMAGE);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not select image",
                            Toast.LENGTH_SHORT).show();
                }

            }
        } else if (resultCode == RESULT_CANCELED) {
           // Toast.makeText(PickImageActivity.this, "Image loading failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerListeners() {


        mCameraButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureActionIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
                imagesFolder.mkdirs(); // <----
                File image = new File(imagesFolder, "profile_image");
                mImageUri = Uri.fromFile(image);
                pictureActionIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                try {
                    startActivityForResult(pictureActionIntent,
                            CAMERA_REQUEST);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(PickImageActivity.this, "Camera is not available on this " +
                            "device.", Toast.LENGTH_SHORT).show();
                }
                //selectPhotosFromCamera();
            }
        });

        mGalleryButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pictureActionIntent = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                try {
                    startActivityForResult(pictureActionIntent, GALLERY_REQUEST);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(PickImageActivity.this, "Gallery is not available on this " +
                            "device.", Toast.LENGTH_SHORT).show();
                }
                //selectPicture();
            }
        });

    }

    private void inflateLayout() {

        mCameraButtton = (Button) findViewById(R.id.camerabutton);
        mGalleryButtton = (Button) findViewById(R.id.gallerybutton);

    }

    /*private boolean isValidImage() {
        if (null == mImageAbsolutePath || mImageAbsolutePath.equals("")) {
            Log.d(TAG, "NoImage");
        } else {
            //File imageToUpload = new File(Uri.parse(mImageAbsolutePath).getPath());
            if (new File(Uri.parse(mImageAbsolutePath).getPath()).exists()) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", mImageUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        mImageUri = savedInstanceState.getParcelable("file_uri");
    }

}
