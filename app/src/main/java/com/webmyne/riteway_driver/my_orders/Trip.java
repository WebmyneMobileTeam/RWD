package com.webmyne.riteway_driver.my_orders;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dhruvil on 20-10-2014.
 */
public class Trip {

    @SerializedName("TripID")
    public String TripID;
    @SerializedName("CustomerID")
    public String CustomerID;
    @SerializedName("DriverID")
    public String DriverID;
    @SerializedName("PickupLatitude")
    public String PickupLatitude;
    @SerializedName("PickupLongitude")
    public String PickupLongitude;
    @SerializedName("PickupAddress")
    public String PickupAddress;
    @SerializedName("PickupNote")
    public String PickupNote;
    @SerializedName("DropoffLatitude")
    public String DropoffLatitude;
    @SerializedName("DropoffLongitude")
    public String DropoffLongitude;
    @SerializedName("DropOffAddress")
    public String DropOffAddress;
    @SerializedName("PickupTime")
    public String PickupTime;
    @SerializedName("TripDate")
    public String TripDate;
    @SerializedName("TipPercentage")
    public String TipPercentage;
    @SerializedName("TripFare")
    public String TripFare;
    @SerializedName("TripFee")
    public String TripFee;
    @SerializedName("TripDistance")
    public String TripDistance;
    @SerializedName("PaymentType")
    public String PaymentType;
    @SerializedName("TripStatus")
    public String TripStatus;
    @SerializedName("CustomerName")
    public String CustomerName;
    @SerializedName("DriverName")
    public String DriverName;
    @SerializedName("CustomerNotificationID")
    public String CustomerNotificationID;
    @SerializedName("DriverNotificationID")
    public String DriverNotificationID;
    @SerializedName("CustomerEmail")
    public String CustomerEmail;
    @SerializedName("CustomerMobile")
    public String CustomerMobile;
    @SerializedName("isCustomerFeedbackGiven")
    public String isCustomerFeedbackGiven;
    @SerializedName("isDriverFeedbackGiven")
    public String isDriverFeedbackGiven;



    public Trip() {
    }

    public Trip(String tripID, String customerID, String driverID, String pickupLatitude, String pickupLongitude, String pickupAddress, String pickupNote, String dropoffLatitude, String dropoffLongitude, String dropOffAddress, String pickupTime, String tripDate, String tipPercentage, String tripFare, String tripFee, String tripDistance, String paymentType, String tripStatus, String customerName, String driverName, String customerNotificationID, String driverNotificationID, String customerEmail, String customerMobile, String isCustomerFeedbackGiven, String isDriverFeedbackGiven) {
        TripID = tripID;
        CustomerID = customerID;
        DriverID = driverID;
        PickupLatitude = pickupLatitude;
        PickupLongitude = pickupLongitude;
        PickupAddress = pickupAddress;
        PickupNote = pickupNote;
        DropoffLatitude = dropoffLatitude;
        DropoffLongitude = dropoffLongitude;
        DropOffAddress = dropOffAddress;
        PickupTime = pickupTime;
        TripDate = tripDate;
        TipPercentage = tipPercentage;
        TripFare = tripFare;
        TripFee = tripFee;
        TripDistance = tripDistance;
        PaymentType = paymentType;
        TripStatus = tripStatus;
        CustomerName = customerName;
        DriverName = driverName;
        CustomerNotificationID = customerNotificationID;
        DriverNotificationID = driverNotificationID;
        CustomerEmail = customerEmail;
        CustomerMobile = customerMobile;
        this.isCustomerFeedbackGiven = isCustomerFeedbackGiven;
        this.isDriverFeedbackGiven = isDriverFeedbackGiven;
    }
}
