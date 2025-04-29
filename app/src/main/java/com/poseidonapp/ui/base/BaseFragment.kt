package com.poseidonapp.ui.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.poseidonapp.database.DatabaseHelper
import com.poseidonapp.utils.CircularProgressDialog
import com.poseidonapp.utils.SharedPref
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.regex.Matcher
import java.util.regex.Pattern

open class BaseFragment : Fragment(), AdapterView.OnItemClickListener, View.OnClickListener,
    AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    var baseActivity: BaseActivity? = null
    lateinit var sharedPref: SharedPref
    var _email: String = ""
    var progressBarPB: CircularProgressDialog?=null
    lateinit var dbHelper: DatabaseHelper
    var initialHashMap = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = activity as BaseActivity?
        baseFragment = BaseFragment()
        sharedPref = SharedPref(requireContext())

        progressBarPB = CircularProgressDialog(context)
        dbHelper = DatabaseHelper(requireContext())

    }


    fun alertDialog(message: String) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(message)
        dialog.setPositiveButton(
            "OK"
        ) { dialog, which -> }
        val alertDialog = dialog.create()
        alertDialog.show()
    }

    fun convertCameraImageBase64(path: String?): String {
        var base64Image = ""
        try {
            val imageFile = path?.let { File(it) }
            var rotate = 0
            try {
                val imageFile = path?.let { File(it) }
                val exif = ExifInterface(
                    imageFile!!.absolutePath
                )
                val orientation: Int = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            val matrix = Matrix()
            matrix.postRotate(rotate.toFloat())

            val bmp = BitmapFactory.decodeStream(FileInputStream(imageFile), null, null)
            val correctBmp = Bitmap.createBitmap(bmp!!, 0, 0, bmp.width, bmp.height, matrix, true)
            val baos = ByteArrayOutputStream()
            correctBmp.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val b = baos.toByteArray()
            base64Image = Base64.encodeToString(b, Base64.DEFAULT)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return base64Image

    }


    companion object {
        var baseActivity: BaseActivity? = null
        var baseFragment: BaseFragment? = null
    }


    fun convertImageToBase64String(bitmap: Bitmap): String {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(
            Bitmap.CompressFormat.PNG,
            60,
            outputStream
        ) // Adjust quality as needed
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64String: String): Bitmap? {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onClick(v: View) {

    }

    fun isInternetAvail(): Boolean {

        val cm =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    fun showToast(msg: String) {
        baseActivity!!.showToast(msg)
    }

    fun showToastOne(s: String) {
        baseActivity!!.showToastOne(s)
    }

    fun showToastLong(s: String) {
        baseActivity!!.showToastLong(s)
    }

    fun showShortToast(message: String) {
        if (!isAdded) {
            return
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun isValidPassword(password: String?): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^" +
                "(?=.*[@#$%^&+=])" +  // at least 1 special character
                "(?=\\S+$)" +  // no white spaces
                ".{8,}" +  // at least 8 characters
                "$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun isEmailValid(email: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Basic phone number validation example
        // This example checks if the phone number contains only digits and has a minimum length of 7
        return phoneNumber.matches(Regex("[0-9]+")) && phoneNumber.length >= 7
    }

    fun isValidPostalCode(postalCode: String): Boolean {
        // Basic postal code validation example
        // This example checks if the postal code has a valid format (e.g., "12345" or "A1B 2C3")
        return postalCode.matches(Regex("[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d"))
    }

    fun log(s: String) {
        baseActivity!!.log(s)
    }


    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {

    }


}
