package com.updown.onetaptest.network.request;

import com.google.gson.GsonBuilder;
import com.updown.onetaptest.Constants;
import com.updown.onetaptest.network.response.IResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseController<T> implements Callback<T> {

    private static Retrofit sRetrofit;
    private IResponse<T> mListener;
    private Call<T> mCall;

    private Object mExtra;

    public abstract void start();

    static {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.followRedirects(false);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        sRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.Server_Url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
    }

    protected Retrofit buildRetrofit() {
        return sRetrofit;
    }

    protected Object getExtra() {
        return mExtra;
    }

    public BaseController<T> setExtra(Object extra) {
        mExtra = extra;
        return this;
    }

    public BaseController<T> setListener(IResponse<T> listener) {
        mListener = listener;
        return this;
    }

    public void cancel() {
        mListener = null;

        if (mCall != null) {
            mCall.cancel();
        }
        mCall = null;
    }

    public final boolean isExecuted() {
        return mCall != null && mCall.isExecuted();
    }

    /**
     * Override methods in subclasses should call the super method at the end of their work
     *
     * @param call
     * @param response
     */
    @Override
//    @CallSuper
    public void onResponse(Call<T> call, Response<T> response) {
        mListener = null;
        mCall = null;
    }

    /**
     * Override methods in subclasses should call the super method at the end of their work
     *
     * @param call
     * @param t
     */
    @Override
//    @CallSuper
    public void onFailure(Call<T> call, Throwable t) {
        mListener = null;
        mCall = null;
    }

}
