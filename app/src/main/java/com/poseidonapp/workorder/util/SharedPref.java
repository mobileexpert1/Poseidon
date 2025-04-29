package com.poseidonapp.workorder.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    public static final String startDay="start_day";

    public static final String languageSelected="languageSelected";


    public static final String firesprinkler = "firesprinkler";

    private final SharedPreferences pref;

    private static SharedPref mInstance;

    public SharedPref(Context context) {
        mInstance = this;
        pref = context.getSharedPreferences("WorkOrder", 0);
    }


    public void setboolen(String name, boolean value){
        pref.edit().putBoolean(name,value).commit();
    }

    public boolean getboolen(String name) {
        return pref.getBoolean(name, false);
    }


    public void clear() {
        pref.edit().clear().commit();
    }

    //score updated today
    public void setScoreUpdated(boolean sprinker){
        //scoreUpdated
        pref.edit().putBoolean(firesprinkler,sprinker).commit();
    }

    public boolean getScoreUpdated(){
        return pref.getBoolean(firesprinkler,true);
    }


    public void setString(String name, String value){
        pref.edit().putString(name,value).commit();
    }

    public String getString(String name){
        return pref.getString(name,"");
    }

    public String getLanguageSelected(){
        return pref.getString(languageSelected, "En");
    }
    public void setLanguageSelected(String setLang){
        pref.edit().putString(languageSelected, setLang).commit();
    }


}