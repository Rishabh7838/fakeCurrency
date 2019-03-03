package com.example.rish.detect_currency.services;

import com.example.rish.detect_currency.Modal.LoginResponse;
import com.example.rish.detect_currency.Modal.RegistrationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistrationService {
    @POST("./login/register")
    Call<RegistrationResponse> registerUser(@Body() RegistrationResponse registerResponse);


}
