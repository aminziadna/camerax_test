package com.updown.onetaptest;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.updown.onetaptest.network.request.GetOverlayImagesRequest;
import com.updown.onetaptest.network.response.IResponse;
import com.updown.onetaptest.network.response.OverlayImagesResponse;

public class CameraViewModel extends ViewModel implements IResponse<OverlayImagesResponse> {

    public MutableLiveData<ImagesData> imagesDataLiveData = new MutableLiveData<>();

    public void getImages() {
        new GetOverlayImagesRequest().setListener(this).start();
    }

    @Override
    public void onSuccess(OverlayImagesResponse result) {
        imagesDataLiveData.postValue(new ImagesData(result.getNewsPaper().getImageUrl(), result.getStayHome().getImageUrl()));
    }

    @Override
    public void onError(String message, Throwable t) {

    }

    class ImagesData {
        String newsPaperUrl;
        String stayHomeUrl;

        public ImagesData(String newsPaperUrl, String stayHomeUrl) {
            this.newsPaperUrl = newsPaperUrl;
            this.stayHomeUrl = stayHomeUrl;
        }
    }
}
