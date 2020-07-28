package com.updown.onetaptest.network.response;

import com.google.gson.annotations.SerializedName;

public class OverlayImagesResponse {
    @SerializedName("NewsPaper")
    private OverlayImage newsPaper;

    @SerializedName("StayHome")
    private OverlayImage stayHome;

    public OverlayImage getNewsPaper() {
        return newsPaper;
    }

    public OverlayImage getStayHome() {
        return stayHome;
    }
}