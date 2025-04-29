package com.poseidonapp.utils


import android.content.Context
import android.net.ConnectivityManager
import android.net.ParseException
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

//    fun getSimpleFormatTime(time:String):String{  // 2022-11-24 01:20:43.037
//        val sdfBase = SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS")
//        val sdfActual = SimpleDateFormat("hh:mm a")
//        val dateTime: Date
//
//        var actualTime = ""
//
//        try {
//            dateTime = sdfBase.parse(time)
//            Log.e("call","@@@ 345 "+sdfActual.format(dateTime))
//            actualTime = sdfActual.format(dateTime)
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//        return actualTime.toString()
//    }


    fun getSimpleFormatTime(time:String):String{
        val sdfBase = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a")  // 11/30/2022 3:25:44 AM
        val sdfActual = SimpleDateFormat("dd MMM yyyy, hh:mm")
        val dateTime: Date

        var actualTime = ""

        try {
            dateTime = sdfBase.parse(time)
            Log.e("call","@@@ 345 "+sdfActual.format(dateTime))
            actualTime = sdfActual.format(dateTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return actualTime.toString()
    }

    fun convertTimestampToCurrentTime(timestamp: Long): String? {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("hh:mm a")
        sdf.timeZone = TimeZone.getDefault() // Set the timezone to the device's local timezone
        return sdf.format(date)
    }

}