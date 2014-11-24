package com.webmyne.riteway_driver.home;

import com.google.gson.annotations.SerializedName;


public class DriverProfile {


    @SerializedName("Active")
    public String Active;
    @SerializedName("CompanyID")
    public String CompanyID;
    @SerializedName("DriverID")
    public String DriverID;
    @SerializedName("FirstName")
    public String FirstName;
    @SerializedName("LastName")
    public String LastName;
    @SerializedName("Response")
    public String Response;
    @SerializedName("Webmyne_DeviceType")
    public String Webmyne_DeviceType;
    @SerializedName("Webmyne_DriverIMEI_Number")
    public String Webmyne_DriverIMEI_Number;
    @SerializedName("Webmyne_Latitude")
    public String Webmyne_Latitude;
    @SerializedName("Webmyne_Longitude")
    public String Webmyne_Longitude;
    @SerializedName("Webmyne_NotificationID")
    public String Webmyne_NotificationID;

    public DriverProfile() {
    }

    public DriverProfile(String active, String companyID, String driverID, String firstName, String lastName, String response, String webmyne_DeviceType, String webmyne_DriverIMEI_Number, String webmyne_Latitude, String webmyne_Longitude, String webmyne_NotificationID) {
        Active = active;
        CompanyID = companyID;
        DriverID = driverID;
        FirstName = firstName;
        LastName = lastName;
        Response = response;
        Webmyne_DeviceType = webmyne_DeviceType;
        Webmyne_DriverIMEI_Number = webmyne_DriverIMEI_Number;
        Webmyne_Latitude = webmyne_Latitude;
        Webmyne_Longitude = webmyne_Longitude;
        Webmyne_NotificationID = webmyne_NotificationID;
    }

}
