package com.webmyne.riteway_driver.my_orders;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 19-11-2014.
 */
public class CurrentRate {

    @SerializedName("Rate")
    public String Rate;

    @SerializedName("TripFee")
    public String TripFee;

    @SerializedName("TimeInterval")
    public String TimeInterval;



    public CurrentRate() {
    }

    public CurrentRate(String rate, String tripFee) {
        Rate = rate;
        TripFee = tripFee;
    }
}
