package com.poseidonapp.snackBar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

open class SnackbarLayout : LinearLayout {
    private var mMaxWidth = Integer.MAX_VALUE
    private var mMaxHeight = Integer.MAX_VALUE

    constructor(context: Context) : super(context) {}

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(context, attrs, defStyle) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        // Adjust width as necessary
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        if (mMaxWidth < width) {
            val mode = View.MeasureSpec.getMode(widthMeasureSpec)
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mMaxWidth, mode)
        }
        // Adjust height as necessary
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        if (mMaxHeight < height) {
            val mode = View.MeasureSpec.getMode(heightMeasureSpec)
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mMaxHeight, mode)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setMaxWidth(maxWidth: Int) {
        mMaxWidth = maxWidth
        requestLayout()
    }

    fun setMaxHeight(maxHeight: Int) {
        mMaxHeight = maxHeight
        requestLayout()
    }

}
