package com.poseidonapp.ui.base


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.poseidonapp.AllContext
import com.poseidonapp.BuildConfig
import com.poseidonapp.snackBar.Snackbar
import com.poseidonapp.snackBar.SnackbarManager
import com.poseidonapp.snackBar.SnackbarType
import com.poseidonapp.ui.activities.LoginActivity
import com.poseidonapp.ui.activities.MainActivity
import com.poseidonapp.utils.CircularProgressDialog
import com.poseidonapp.utils.Const
import com.poseidonapp.utils.SharedPref
import java.util.*

open class BaseActivity : AppCompatActivity(), View.OnClickListener {

    var progressBarPB: CircularProgressDialog?=null
    lateinit var sharedPref: SharedPref
    public var reqCode: Int = 0

    private val uniqueDeviceId: String
        @SuppressLint("HardwareIds")
        get() = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        progressBarPB = CircularProgressDialog(this)
        progressBarPB!!.setCancelable(false)
        sharedPref = SharedPref(this)
        AllContext.setContext(this)
        sharedPref.setString(Const.DEVICE_TOKEN, uniqueDeviceId)
    }


    fun isInternetAvail(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun showToast(msg: String) {
        SnackbarManager.create(
            Snackbar.with(this).duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                .type(SnackbarType.MULTI_LINE)
                .text(msg)
        )!!.show()
        /*Toast.makeText(
            this , msg, Toast.LENGTH_SHORT
        ).show()*/
    }

    fun showToastLong(msg: String) {
        SnackbarManager.create(
            Snackbar.with(this).duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                .type(SnackbarType.MULTI_LINE)
                .text(msg)
        )!!.show()
        /*Toast.makeText(
            this , msg, Toast.LENGTH_SHORT
        ).show()*/
    }

    fun dismissSnackbar() {
        // Check if a Snackbar is currently being displayed and dismiss it
        SnackbarManager.dismiss()
    }

    fun getDeviceToken(): String {
        return if (sharedPref.getString(Const.DEVICE_TOKEN).isEmpty()) {
            return uniqueDeviceId
        } else {
            sharedPref.getString(Const.DEVICE_TOKEN)
        }
    }

    fun gotoLoginSignUpActivity(is_logout: Int = Const.NO) {
        val intent = Intent(this@BaseActivity, LoginActivity::class.java)
        intent.putExtra(Const.IS_LOGOUT, is_logout)
        startActivity(intent)
//        finish()
    }

    fun gotMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    fun log(string: String) {
        if (BuildConfig.DEBUG) {
            Log.e("BaseActivity", string)
        }
    }


    fun log(title: String, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.e(title, message ?: "")
        }
    }


    fun showToastOne(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }


    override fun onClick(v: View) {

    }

    open fun handelException(e: java.lang.Exception) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        //checkDate(Const.DATE_CHECK)
    }


}
