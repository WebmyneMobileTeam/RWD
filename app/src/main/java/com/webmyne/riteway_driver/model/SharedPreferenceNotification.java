package com.webmyne.riteway_driver.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;
import com.webmyne.riteway_driver.notifications.DriverNotification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SharedPreferenceNotification {

	public static final String PREF_NAME = "SHARED_DATA_NOTIFICATION";
	public static final String PREF_VALUE = "shared_values_for_notifications";
    List<DriverNotification> driverNotifications =new ArrayList<DriverNotification>();
	public SharedPreferenceNotification() {
		super();
	}


    public void clearNotification(Context context) {
        SharedPreferences sharedPref;
        Editor editor;
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        driverNotifications.clear();
        editor.clear();
        editor.commit();
    }
    public void saveNotification(Context context, DriverNotification driverNotification) {
        SharedPreferences sharedPref;
        Editor editor;
        if (driverNotifications == null) {
            driverNotifications = new ArrayList<DriverNotification>();
        }
        driverNotifications.add(driverNotification);
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(driverNotifications);
        editor.putString(PREF_VALUE, jsonFavorites);
//        Log.e(" list:", jsonFavorites + "");
        editor.commit();
    }

    public ArrayList<DriverNotification> loadNotification(Context context) {
        SharedPreferences sharePref;
        List<DriverNotification> notificationList;
        sharePref = context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        String jsonFavorites = sharePref.getString(PREF_VALUE, null);
        Gson gson = new Gson();
        DriverNotification[] favoriteItems = gson.fromJson(jsonFavorites,DriverNotification[].class);
        notificationList = new ArrayList<DriverNotification>(Arrays.asList(favoriteItems));
//        Log.e(" array", notificationList + "");
        return (ArrayList<DriverNotification>) notificationList;
    }

}
