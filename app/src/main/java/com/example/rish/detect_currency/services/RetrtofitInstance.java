package com.example.rish.detect_currency.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrtofitInstance {
    private static Retrofit retrofit = null;
    private static String  BaseUrl = "http://18.188.20.76:3000/";

    public static SendPhotoService getService(){


        if(retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


        }
        return retrofit.create(SendPhotoService.class);
    }
}
