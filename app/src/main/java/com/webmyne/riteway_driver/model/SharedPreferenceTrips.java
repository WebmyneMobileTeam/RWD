package com.webmyne.riteway_driver.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;
import com.webmyne.riteway_driver.my_orders.Trip;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SharedPreferenceTrips {

	public static final String PREF_NAME = "SHARED_DATA_TRIPS";
	public static final String PREF_VALUE = "shared_values_for_trips";
    List<Trip> tripArrayList =new ArrayList<Trip>();
	public SharedPreferenceTrips() {
		super();
	}


    public void clearTrip(Context context) {
        SharedPreferences sharedPref;
        Editor editor;
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        tripArrayList.clear();
        editor.clear();
        editor.commit();
    }
    public void saveTrip(Context context, Trip trips) {
        SharedPreferences sharedPref;
        Editor editor;
        if (tripArrayList == null) {
            tripArrayList = new ArrayList<Trip>();
        }
        tripArrayList.add(trips);
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(tripArrayList);
        editor.putString(PREF_VALUE, jsonFavorites);
//        Log.e("trip list:", jsonFavorites + "");
        editor.commit();
    }

    public ArrayList<Trip> loadTrip(Context context) {
        SharedPreferences sharePref;
        List<Trip> tripList;
        sharePref = context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        String jsonFavorites = sharePref.getString(PREF_VALUE, null);
        Gson gson = new Gson();
        Trip[] favoriteItems = gson.fromJson(jsonFavorites,Trip[].class);
        tripList = new ArrayList<Trip>(Arrays.asList(favoriteItems));
//        Log.e("teacher array", tripList + "");
        return (ArrayList<Trip>) tripList;
    }

}
