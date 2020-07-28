package com.updown.onetaptest.network.response;

import com.google.gson.annotations.SerializedName;

public class OverlayImage {
    @SerializedName("image")
    private String image;

    public String getImageUrl() {
        return image;
    }
}
