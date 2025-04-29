package com.poseidonapp.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPref(val context: Context) {
    var pref: SharedPreferences
    var edit: SharedPreferences.Editor



    init{
        pref=context.getSharedPreferences(Const.SHARED_PREF,0)
        edit=pref.edit()

    }

    public fun setString(key : String,value : String){
        edit.putString(key,value)
        edit.commit()
    }

    public fun getString(key : String):String{
        return pref.getString(key,"").toString()
    }

     fun setToken(key : String,value : String){
        edit.putString(key,value)
        edit.commit()
    }

    public fun getToken(key : String):String{
        return pref.getString(key,"").toString()
    }


    public fun setInt(key : String,value : Int){
        edit.putInt(key,value)
        edit.commit()
    }

    public fun getInt(key : String):Int{
        return pref.getInt(key,0)
    }


    public fun setBoolean(key : String,value : Boolean){
        edit.putBoolean(key,value)
        edit.commit()
    }

    public fun getBoolean(key : String):Boolean{
        return pref.getBoolean(key,false)
    }


    public fun getBooleanEstimatedTime(key : String):Boolean{
        return pref.getBoolean(key,true)
    }

    fun clearPref(){
        edit.clear().commit()
    }

    fun getLanguageSelected(): String? {
        return pref.getString(com.poseidonapp.workorder.util.SharedPref.languageSelected, "DARK")
    }

    fun setLanguageSelected(setLang: String?) {
        pref.edit().putString(com.poseidonapp.workorder.util.SharedPref.languageSelected, setLang).commit()
    }


    companion object {
      var  token = ""
    }
}

