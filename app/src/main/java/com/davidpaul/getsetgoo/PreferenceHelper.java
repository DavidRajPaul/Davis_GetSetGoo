package com.davidpaul.getsetgoo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by David Paul on 8/20/2016.
 */
public class PreferenceHelper {
    private static PreferenceHelper preferenceHelper;
    private SharedPreferences sharedPreferences;

    public static PreferenceHelper getInstance(Context context, String prefName) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferenceHelper(context, prefName);
        }
        return preferenceHelper;
    }

    PreferenceHelper(Context context, String prefName) {
        sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public void setdata(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getData(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }

    public void clearData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
