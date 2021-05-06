package com.partime.user.helpers;

/**
 * Created by vipra on 7/3/17.
 */

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JavaRetrofitClient {

    private static Retrofit retrofit = null;

    /*private OkHttpClient getRequestHeader() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        httpClient.setReadTimeout(30, TimeUnit.SECONDS);

        return httpClient;
    }*/

    public static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
