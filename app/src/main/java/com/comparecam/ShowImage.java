package com.comparecam;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.comparecam.R;

public class ShowImage extends AppCompatActivity {

    private ImageView imageView;
    private String finalImageURI;
    private Button detectFaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        imageView = (ImageView) findViewById(R.id.newimage);
        detectFaces = (Button) findViewById(R.id.detect_button);


        finalImageURI = getIntent().getExtras().getString(EditImageActivity.IMAGE_URI);

        if (finalImageURI != null && finalImageURI != "") {
            imageView.setImageURI(Uri.parse(finalImageURI));
        } else {
            finish();
            Toast.makeText(ShowImage.this, "Image malformed", Toast.LENGTH_SHORT).show();
        }

        detectFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalImageURI != null) {
                    Intent intent = new Intent(ShowImage.this, FaceDetectionActivity.class);
                    intent.putExtra(EditImageActivity.IMAGE_URI, finalImageURI);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
