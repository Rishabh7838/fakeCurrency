package com.example.rish.detect_currency.services;

import com.example.rish.detect_currency.Modal.LoginResponse;
import com.example.rish.detect_currency.Modal.QueryResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface LoginService {

        @POST("/login")
            //Call<String> detectNote(@Field("value") String title);
        Call<LoginResponse> loginUser (@Body() LoginResponse loginResponse);
}
