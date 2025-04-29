package com.poseidonapp.ui.extensions

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.CountDownTimer
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.poseidonapp.BuildConfig
import com.poseidonapp.R
import com.poseidonapp.utils.Const
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*



//change status bar color
fun AppCompatActivity.setStatusBarColor(color: Int, showLight: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window = this.window

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color


    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (showLight) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.setDecorFitsSystemWindows(true)
        }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val view = this.window.decorView
        view.let {
            if (showLight) {
                it.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                it.systemUiVisibility =
                    it.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() //set status text  light
            }
        }
    }
}

//change TextView color
fun AppCompatTextView.setColor(color: Int) {
    this.setTextColor(ContextCompat.getColor(this.context!!, color))
}

//Create spannable TextView
fun AppCompatTextView.setSpanString(
    spanText: String,
    start: Int,
    end: Int = spanText.length,
    color: Int = R.color.Black,
    showBold: Boolean = false,
    onSpanClick: () -> Unit,
) {
    val ss = SpannableString(spanText)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color)
        }
    }
    ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (showBold) {
        ss.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text = ss
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = ContextCompat.getColor(this@setSpanString.context, R.color.transparent)

}

//Create spannable AppCompatCheckBox
fun AppCompatCheckBox.setSpanString(
    spanText: String,
    start: Int,
    end: Int = spanText.length,
    color: Int = R.color.Black,
    showBold: Boolean = false,
    onSpanClick: () -> Unit,
) {
    val ss = SpannableString(spanText)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color)
        }
    }
    ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (showBold) {
        ss.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text = ss
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = ContextCompat.getColor(this@setSpanString.context, R.color.transparent)

}

fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
    removeObserver(observer)
    observe(owner, observer)
}


fun AppCompatTextView.slideLeft(
    isAnimating: Boolean,
    secondTV: TextView,
    onUpdate: (isAnimating: Boolean) -> Unit,
    onSlide: () -> Unit,
) {
    if (isAnimating) return
    onUpdate(true)
    val xDisplacement: Float = getRelativeX() - secondTV.getRelativeX()
    if (xDisplacement < 0) {
        onUpdate(false)
        return
    }
    secondTV.animate().xBy(xDisplacement).yBy(0f)
    this.animate().xBy(-xDisplacement).yBy(0f)
    secondTV.animate().duration = 150
    this.animate().duration = 150
    val animDuration: Long = secondTV.animate().duration
    object : CountDownTimer(animDuration + 10, animDuration + 10) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            onUpdate(false)
            onSlide()
        }
    }.start()
}

//returns x-pos relative to root layout
fun View.getRelativeX(): Float {
    return if (parent === rootView) x else x + (parent as View).getRelativeX()
}

fun AppCompatTextView.slideRight(
    isAnimating: Boolean,
    firstTV: TextView,
    onUpdate: (isAnimating: Boolean) -> Unit,
    onSlide: () -> Unit,
) {
    if (isAnimating) return
    onUpdate(true)
    val xDisplacement: Float = firstTV.getRelativeX() - getRelativeX()
    if (xDisplacement > 0) {
        onUpdate(false)
        return
    }
    this.animate().xBy(xDisplacement).yBy(0f)
    firstTV.animate().xBy(-xDisplacement).yBy(0f)
    this.animate().duration = 200
    firstTV.animate().duration = 200
    val animDuration: Long = this.animate().duration
    object : CountDownTimer(animDuration + 10, animDuration + 10) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            onUpdate(false)
            onSlide()
        }
    }.start()
}

// TextWatcher Extension
fun AppCompatEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })


}


fun AppCompatEditText.isBlank(): Boolean {
    return this.text!!.isEmpty()
}

fun AppCompatEditText.getLength(): Int {
    return this.text!!.toString().length
}

fun AppCompatEditText.checkString(): String {
    return this.text!!.toString().trim()
}

fun setPhoneNumberLength(mobileET: AppCompatEditText, countryCode: String) {
    val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
    val isoCode: String = phoneNumberUtil.getRegionCodeForCountryCode(countryCode.toInt())
    val numberIs: String =
        java.lang.String.valueOf(phoneNumberUtil.getExampleNumber(isoCode).nationalNumber)
    val phoneLength = numberIs.length
    mobileET.filters = arrayOf<InputFilter>(
        InputFilter.LengthFilter(phoneLength)
    )
}

fun EditText.setMaxLength(maxLength: Int) {
    filters = arrayOf(InputFilter.LengthFilter(maxLength))
}

fun View.visibleView(visible: Boolean) {
    this.visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}


@SuppressLint("ClickableViewAccessibility")
fun EditText.showHidePasswordDARK() {
    val START = 0
    val TOP = 1
    val END = 2
    val BOTTOM = 3
    this.setOnTouchListener(View.OnTouchListener { v, event ->

        if (event.action == MotionEvent.ACTION_UP) {

            if (event.rawX >= this.right - (this.compoundDrawables[END].bounds.width() + this.paddingRight)) { // your action here

                if (transformationMethod is PasswordTransformationMethod) {
                    transformationMethod = HideReturnsTransformationMethod()
                    this.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        this.compoundDrawables[START],
                        this.compoundDrawables[TOP],
                        ContextCompat.getDrawable(this.context, R.drawable.on),
                        this.compoundDrawables[BOTTOM]
                    )
                } else {
                    transformationMethod = PasswordTransformationMethod()
                    this.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        this.compoundDrawables[START],
                        this.compoundDrawables[TOP],
                        ContextCompat.getDrawable(this.context, R.drawable.off),
                        this.compoundDrawables[BOTTOM]
                    )

                }

                requestFocus()
                setSelection(text.toString().length)
                return@OnTouchListener true
            }

        }
        false
    })
}


@SuppressLint("ClickableViewAccessibility")
fun EditText.showHidePassword() {
    val START = 0
    val TOP = 1
    val END = 2
    val BOTTOM = 3
    this.setOnTouchListener(View.OnTouchListener { v, event ->

        if (event.action == MotionEvent.ACTION_UP) {

            if (event.rawX >= this.right - (this.compoundDrawables[END].bounds.width() + this.paddingRight)) { // your action here

                if (transformationMethod is PasswordTransformationMethod) {
                    transformationMethod = HideReturnsTransformationMethod()
                    this.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        this.compoundDrawables[START],
                        this.compoundDrawables[TOP],
                        ContextCompat.getDrawable(this.context, R.drawable.on),
                        this.compoundDrawables[BOTTOM]
                    )
                } else {
                    transformationMethod = PasswordTransformationMethod()
                    this.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        this.compoundDrawables[START],
                        this.compoundDrawables[TOP],
                        ContextCompat.getDrawable(this.context,R.drawable.off),
                        this.compoundDrawables[BOTTOM]
                    )

                }

                requestFocus()
                setSelection(text.toString().length)
                return@OnTouchListener true
            }

        }
        false
    })
}

var EMOJI_FILTER =
    InputFilter { source, start, end, dest, dstart, dend ->
        for (index in start until end) {
            val type = Character.getType(source[index])
            if (type == Character.SURROGATE.toInt() || type == Character.NON_SPACING_MARK.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                return@InputFilter ""
            }
        }
        null
    }

var SYMBOL_FILTER =
    InputFilter { source, start, end, dest, dstart, dend ->
        for (index in start until end) {
            val type = Character.getType(source[index])
            if (type == Character.SURROGATE.toInt() || type == Character.CURRENCY_SYMBOL.toInt()
                || type == Character.MODIFIER_SYMBOL.toInt() || type == Character.MATH_SYMBOL.toInt()
                || type == Character.OTHER_NUMBER.toInt() || type == Character.NON_SPACING_MARK.toInt()
                || type == Character.OTHER_SYMBOL.toInt() || type == Character.DECIMAL_DIGIT_NUMBER.toInt()
            ) {
                return@InputFilter ""
            }
        }
        null
    }

fun changeDateFormatNew(dateString: String): String {
    if (dateString.isEmpty()) {
        return ""
    }
    val inputDateFormat = SimpleDateFormat(Const.SIMPLE_DATE_TIME_WITH_SEC, Locale.getDefault())
    var date = Date()
    try {
        date = inputDateFormat.parse(dateString)
    } catch (e: ParseException) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }

    val outputDateFormat = SimpleDateFormat(Const.OUTPUT_DATE_TIME_WITH_SEC, Locale.getDefault())
    return outputDateFormat.format(date)
}

fun Date?.getStringDate(format: String? = Const.YYYY_MM_DD): String {
    if (this == null || format == null || format.isEmpty()) {
        return ""
    }
    val outputDateFormat = SimpleDateFormat(format, Locale.getDefault())
    return outputDateFormat.format(this)
}


fun String?.createDate(sourceFormat: String = Const.YYYY_MM_DD): Date {
    if (this.isNullOrEmpty()) {
        return Date()
    }
    val inputDateFromat = SimpleDateFormat(sourceFormat, Locale.getDefault())
    var date = Date()
    try {
        date = inputDateFromat.parse(this)
    } catch (e: ParseException) {
        handleException(e)
    }

    return date
}
fun log(title: String, message: String) {
    if (BuildConfig.DEBUG) {
        Log.e(title, message)
    }
}

fun handelException(e: Exception) {
    if (BuildConfig.DEBUG) {
        e.printStackTrace()
    }
}

fun handleException(e: Exception) {
    if (BuildConfig.DEBUG) {
        e.printStackTrace()
    }
}
