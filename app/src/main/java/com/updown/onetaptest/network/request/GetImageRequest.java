package com.updown.onetaptest.network.request;

import com.updown.onetaptest.network.ApiService;

import okhttp3.ResponseBody;

public class GetImageRequest extends BaseController<ResponseBody> {

    private String imageUrl;

    public GetImageRequest(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public void start() {
        buildRetrofit().create(ApiService.class).getImageByUrl(imageUrl)
                .enqueue(this);
    }
}
