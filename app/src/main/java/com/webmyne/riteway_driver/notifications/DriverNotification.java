package com.webmyne.riteway_driver.notifications;

import com.google.gson.annotations.SerializedName;


public class DriverNotification {

    @SerializedName("Date")
    public String Date;
    @SerializedName("DriverID")
    public String DriverID;
    @SerializedName("Message")
    public String Message;
    @SerializedName("NotificationID")
    public String NotificationID;
    @SerializedName("Status")
    public String notificationStatus;
    @SerializedName("Time")
    public String notificationTime;
    @SerializedName("Title")
    public String Title;
    @SerializedName("TripID")
    public String TripID;

    public DriverNotification() {
    }

    public DriverNotification(String date, String driverID, String message, String notificationID, String notificationStatus, String notificationTime, String title, String tripID) {
        Date = date;
        DriverID = driverID;
        Message = message;
        NotificationID = notificationID;
        this.notificationStatus = notificationStatus;
        this.notificationTime = notificationTime;
        Title = title;
        TripID = tripID;
    }
}
