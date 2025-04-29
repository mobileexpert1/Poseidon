package com.poseidonapp.ui.extensions

import androidx.core.content.ContextCompat
import android.widget.TextView
import android.widget.Toast
import com.poseidonapp.BuildConfig
import com.poseidonapp.R
import com.poseidonapp.ui.base.BaseActivity

import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun getMessage(json: JSONObject, baseActivity: BaseActivity) {
    var string: String
    string = baseActivity.getString(R.string.success_msg)
    if (json.has("message")) {
        string = json.getString("message")
    }
    showToastMain(baseActivity, string)
}

fun getErrorMsg(json: JSONObject, baseActivity: BaseActivity) {
    var string: String
    string = baseActivity.getString(R.string.soemthing_went_wrong)
    if (json.has("error")) {
        string = json.getString("error")
    }
    showToastMain(baseActivity, string)
}

fun showToastMain(baseActivity: BaseActivity, string: String) {
    Toast.makeText(baseActivity, string, Toast.LENGTH_SHORT).show()
}

fun TextView.setColor(baseActivity: BaseActivity?, lightGrey: Int) {
    this.setTextColor(ContextCompat.getColor(baseActivity!!, lightGrey))
}



fun changeDateFormat(dateString: String): String {
    if (dateString.isEmpty()) {
        return ""
    }
    val inputDateFormat = SimpleDateFormat("yyyy-MM-DD HH:MM:SS", Locale.getDefault())
    var date = Date()
    try {
        date = inputDateFormat.parse(dateString)
    } catch (e: ParseException) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }

    val outputDateFormat = SimpleDateFormat("EEE, MMM d, ''yy h:mm a", Locale.getDefault())
    return outputDateFormat.format(date)
}




