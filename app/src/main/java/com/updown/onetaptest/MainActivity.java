package com.updown.onetaptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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
    private TextureView textureView;
    private CameraViewModel cameraViewModel;
    private File outputDirectory;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraViewModel = ViewModelProviders.of(this).get(CameraViewModel.class);
        cameraViewModel.getImages();
        viewFinder = findViewById(R.id.viewFinder);
        textureView = findViewById(R.id.textureView);

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
                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
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