package com.updown.onetaptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    static String TAG = "MainActivity";

    static int REQUEST_CODE_PERMISSIONS = 10;
    static String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Preview preview = null;
    private ImageCapture imageCapture = null;
    private ImageAnalysis imageAnalysis = null;
    private Camera camera = null;
    private PreviewView viewFinder;
    private CameraViewModel cameraViewModel;
    private File outputDirectory;
    private ExecutorService cameraExecutor;

    private ImageView newsPaperImageView;
    private ImageView stayHomeImageView;
    private ImageView overlayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stayHomeImageView = findViewById(R.id.imageview_stayhome);
        newsPaperImageView = findViewById(R.id.imageview_newspaper);
        overlayImage = findViewById(R.id.overlay);

        newsPaperImageView.setOnClickListener(v -> {
            overlayImage.setScaleType(ImageView.ScaleType.FIT_XY);
            overlayImage.setImageDrawable(newsPaperImageView.getDrawable());
        });
        stayHomeImageView.setOnClickListener(v -> {
            overlayImage.setScaleType(ImageView.ScaleType.FIT_XY);
            overlayImage.setImageDrawable(stayHomeImageView.getDrawable());
        });


        cameraViewModel = ViewModelProviders.of(this).get(CameraViewModel.class);
        cameraViewModel.imagesDataLiveData.observe(this, new Observer<CameraViewModel.ImagesData>() {
            @Override
            public void onChanged(CameraViewModel.ImagesData imagesData) {
                Glide.with(getBaseContext())
                        .load(imagesData.newsPaperUrl)
                        .into(newsPaperImageView);
                Glide.with(getBaseContext())
                        .load(imagesData.stayHomeUrl)
                        .into(stayHomeImageView);
            }
        });
        cameraViewModel.getImages();
        viewFinder = findViewById(R.id.viewFinder);

        if (isAllPermissionsGranted()) {
            startCamera();
        } else {
            //request permissions
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        findViewById(R.id.camera_capture_button).setOnClickListener(v -> {
            takePhoto();
        });

        outputDirectory = getOutputDirectory();

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private File getOutputDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    preview = new Preview.Builder().build();

                    imageCapture = new ImageCapture.Builder().build();
                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
                    cameraProvider.unbindAll();
                    camera = cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, preview, imageCapture);
                    preview.setSurfaceProvider(viewFinder.createSurfaceProvider());
                } catch (Exception e) {
                    Log.e(TAG, "Use case binding failed", e);
                }

            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null)
            return;
        File imageFile = new File(outputDirectory, new SimpleDateFormat(Constants.FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg");
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(imageFile).build();
        imageCapture.takePicture(options, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                ///out of time -- merge two images into one
//                Bitmap bottomImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//                Bitmap topImage = overlayImage.getDrawable().;

//                Canvas comboImage = new Canvas(bottomImage);
//                comboImage.drawBitmap(topImage, 0f, 0f, null);

                // currently saving image from the camera
                Uri uri = Uri.fromFile(imageFile);
                Toast.makeText(getBaseContext(), "Image saved to :" + uri, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
            }
        });
    }

    private boolean isAllPermissionsGranted() {
        boolean isAllPermissionGranted = true;
        for (String permission : REQUIRED_PERMISSIONS) {
            isAllPermissionGranted &= ContextCompat.checkSelfPermission(
                    getBaseContext(), permission) == PackageManager.PERMISSION_GRANTED;
        }
        return isAllPermissionGranted;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (isAllPermissionsGranted()) {
                startCamera();
            } else {
                // show popup to inform the user why we need the Permissions

            }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}