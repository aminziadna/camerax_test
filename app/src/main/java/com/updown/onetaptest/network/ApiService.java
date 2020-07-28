package com.updown.onetaptest.network;


import com.updown.onetaptest.network.response.OverlayImagesResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface ApiService {
    @GET("/cdn2/test/images.json")
    @Headers("Content-Type: application/json")
    Call<OverlayImagesResponse> getImages();

    @GET
    Call<ResponseBody> getImageByUrl(@Url String url);
}
