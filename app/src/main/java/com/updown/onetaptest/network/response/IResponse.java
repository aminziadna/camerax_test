package com.updown.onetaptest.network.response;

public interface IResponse<T> {

    void onSuccess(T result);
    void onError(String message, Throwable t);

}

