package com.example.rish.detect_currency.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegistrationResponse {

    @SerializedName("IsSuccessfull")
    @Expose
    private String  IsSuccessfull;

    public String getIsSuccessfull() {
        return IsSuccessfull;
    }

    public void setIsSuccessfull(String isSuccessfull) {
        IsSuccessfull = isSuccessfull;
    }
}
