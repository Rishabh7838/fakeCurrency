package com.example.rish.detect_currency.services;

import com.example.rish.detect_currency.Modal.LoginResponse;
import com.example.rish.detect_currency.Modal.QueryResponse;
import com.example.rish.detect_currency.Modal.RegistrationResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SendPhotoService {
    @Multipart
    @POST(".")
    //Call<String> detectNote(@Field("value") String title);
    Call<QueryResponse> detectNote (@Part MultipartBody.Part filePart);


    @POST("/login")
        //Call<String> detectNote(@Field("value") String title);
    Call<LoginResponse> loginUser (@Body() HashMap<String, String> user);


    @POST("/login/register")
    Call<RegistrationResponse> registerUser(@Body() HashMap<String, String> user);

    @Multipart
    @POST("/login/upload")
    Call<RegistrationResponse> uploadphoto (@Part MultipartBody.Part filePart);
}
