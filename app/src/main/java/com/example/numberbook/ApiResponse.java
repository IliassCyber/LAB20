package com.example.numberbook;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("success")
    private boolean isOperationSuccessful;
    
    @SerializedName("message")
    private String responseMessage;

    public boolean isSuccessful() {
        return isOperationSuccessful;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
