package com.webmyne.riteway_driver.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 12-11-2014.
 */
public class ResponseMessage {

    @SerializedName("Response")
    public String Response;

    public ResponseMessage() {
    }

    public ResponseMessage(String response) {
        Response = response;
    }
}
