package com.updown.onetaptest;

import android.app.Application;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.updown.onetaptest.network.request.GetImageRequest;
import com.updown.onetaptest.network.request.GetOverlayImagesRequest;
import com.updown.onetaptest.network.response.IResponse;
import com.updown.onetaptest.network.response.OverlayImagesResponse;

import java.io.File;

import okhttp3.ResponseBody;

public class CameraViewModel extends AndroidViewModel implements IResponse<OverlayImagesResponse> {

    public CameraViewModel(@NonNull Application application) {
        super(application);
    }

    public void getImages() {
        new GetOverlayImagesRequest().setListener(this).start();
    }

    @Override
    public void onSuccess(OverlayImagesResponse result) {
        retrieveImage(result.getNewsPaper().getImageUrl(), 0);
        retrieveImage(result.getStayHome().getImageUrl(), 1);
    }

    @Override
    public void onError(String message, Throwable t) {

    }


    private void retrieveImage(String imageUrl, int id) {
        new GetImageRequest(imageUrl).setListener(new IResponse<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                // display the image data in a ImageView or save it
                BitmapFactory.decodeStream(result.byteStream());
                File f = getFileToSave();
                f.

            }

            @Override
            public void onError(String message, Throwable t) {

            }
        }).start();
    }

    private File getFileToSave() {
        File[] externalMediaDirs = getApplication().getExternalMediaDirs();
        File mediaDir = null;
        if (externalMediaDirs != null && externalMediaDirs.length > 0) {
            mediaDir = new File(externalMediaDirs[0], Constants.FileNameTemplate);
            mediaDir.mkdir();
        }
        if (mediaDir != null && mediaDir.exists())
            return mediaDir;
        else return getApplication().getFilesDir();
    }
}
