package com.dodi.disasteralert.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferences {

    public static SharedPreferences preferences;
    public static String loginKey = "loginstatus6";
    public static String tokenKey = "token";
    public static String userIdKey = "userId";
    public static String emailKey = "useremail";
    public static String nameKey = "username";
    public static String passKey = "userpassword";
    public static String phoneNKey = "phoneNumber";
    public static String addressKey = "useraddress";
    public static String userBirthKey = "userBirthDay";
    public static String userFavWord = "userFavWord";

    public static String getData(String key, Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }

    public static boolean getDataBool(String key, Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

    public static void setData(String key, String value, Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(key, value).apply();
    }

    public static void setDataBool(String key, Boolean value, Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(key, value).apply();
    }
}
