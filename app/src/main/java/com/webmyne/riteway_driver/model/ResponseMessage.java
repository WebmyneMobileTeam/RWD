package com.webmyne.riteway_driver.model;

import com.google.gson.annotations.SerializedName;


public class ResponseMessage {

    @SerializedName("Response")
    public String Response;

    public ResponseMessage() {
    }

    public ResponseMessage(String response) {
        Response = response;
    }
}
