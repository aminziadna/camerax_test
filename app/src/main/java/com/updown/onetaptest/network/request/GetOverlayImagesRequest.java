package com.updown.onetaptest.network.request;


import com.updown.onetaptest.network.ApiService;
import com.updown.onetaptest.network.response.OverlayImagesResponse;

public class GetOverlayImagesRequest extends BaseController<OverlayImagesResponse> {

    @Override
    public void start() {
        buildRetrofit().create(ApiService.class).getImages()
                .enqueue(this);
    }
}