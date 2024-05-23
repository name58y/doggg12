package com.example.applicationdog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class ResultActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    TextView result, confidence;
    ImageView photoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        photoImageView = findViewById(R.id.photoImageView);
        Button backToMainButton = findViewById(R.id.btnCamera);
        Button takePictureButton = findViewById(R.id.btnCamera);

        String breed = getIntent().getStringExtra("breed");
        String confidenceText = getIntent().getStringExtra("confidence");
        Bitmap image = getIntent().getParcelableExtra("image");
        if (result != null) {
            result.setText(breed);
        }
        if (confidence != null) {
            confidence.setText(confidenceText);
        }
        if (image != null) {
            photoImageView.setImageBitmap(image);
        }

        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }

    public void takePicture(View view) {
        openCamera();
    }

    private void openCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            photoImageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, 224, 224, false);

            DogBreedUtil.processImageAndDisplayResults(this, image, result, confidence);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
