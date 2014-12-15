package com.webmyne.riteway_driver.model;


public class AppConstants {

    //POST
    public static final String DriverArrivedNotification="http://ws-srv-net.in.webmyne.com/Applications/Android/RiteWayServices/Driver.svc/json/DriverArrivedNotification";
    public static final String DriverCurrentLocation="http://ws-srv-net.in.webmyne.com/Applications/Android/RiteWayServices/Driver.svc/json/DriverCurrentLocation";
    public static final String DriverProfile="http://ws-srv-net.in.webmyne.com/Applications/Android/RiteWayServices/Driver.svc/json/DriverProfile";
    public static final String DriverStatus="http://ws-srv-net.in.webmyne.com/Applications/Android/RiteWayServices/Driver.svc/json/DriverStatus";
    public static final String RequestedTripStatus="http://ws-srv-net.in.webmyne.com/Applications/Android/RiteWayServices/Driver.svc/json/RequestedTripStatus";
    public static final String TripCompletion="http://ws-srv-net.in.webmyne.com/Applications/Android/RiteWayServices/Driver.svc/json/TripCompletion";
    //GET
    public static final String DriverTrips="http://ws-srv-net.in.webmyne.com/Applications/Android/RiteWayServices/Driver.svc/json/DriverTrips/";
    public static final String GetDriverNotifications="http://ws-srv-net.in.webmyne.com/Applications/Android/RiteWayServices/Driver.svc/json/GetDriverNotifications/";
    public static final String DriverNotificationsStatusChanged="http://ws-srv-net.in.webmyne.com/Applications/Android/RiteWayServices/Driver.svc/json/DriverNotificationsStatusChanged/";
    //GET Current rates
    public static final String CurrentRate ="http://ws-srv-net.in.webmyne.com/Applications/Android/RiteWayServices/Driver.svc/json/CurrentRate";
    public static  boolean driverStatusBoolValue=true;
    public static final String deviceType="Android";

    // Trip Status
    public static final String tripInProgressStatus="In Progress";
    public static final String tripOnTripStatus="On Trip";
    public static final String tripCancelledByDriverStatus="Cancelled By Driver";
    public static final String tripCancelledByCustomerStatus="Canceled By Customer";
    public static final String tripAcceptStatus="Accept";
    public static final String tripSuccessStatus="Success";
    public static final String tripArrivedOnSiteStatus="On Site";
    public static final String tripStopStatus="Stop Trip";


}
