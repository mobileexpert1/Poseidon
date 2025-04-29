package com.poseidonapp.ui.extensions

import android.graphics.Color
import android.net.Uri
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.bumptech.glide.signature.ObjectKey

import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.poseidonapp.utils.Const
import de.hdodenhof.circleimageview.CircleImageView

fun ImageView.loadFromUrl(url: String, defaultImage: Int) {
    if (!url.isNullOrEmpty()) {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.setColorSchemeColors(Color.BLACK)
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setTint(Color.BLACK)
        circularProgressDrawable.start()

        Glide.with(this.context)
            .load(url).signature(ObjectKey(System.currentTimeMillis()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(circularProgressDrawable)
            .error(defaultImage)
            .skipMemoryCache(true)
            .into(this)

    } else {
        this.setImageResource(defaultImage)
    }

}

fun SimpleDraweeView.loadImageURI(imageIV: SimpleDraweeView, imagePath: String) {
    val roundingParams = RoundingParams.fromCornersRadius(20f)
    val uri: Uri = Uri.parse((Const.IMAGE_URL + imagePath) ?: "")
    imageIV.setImageURI(uri)
    imageIV.hierarchy.roundingParams = roundingParams
//    imageIV.hierarchy.setProgressBarImage(R.mipmap.app_logo)
//    imageIV.hierarchy.setPlaceholderImage(R.drawable.deafult)
}

fun CircleImageView.loadFromUrl(url: String, defaultImage: Int) {
    if (url.isNotEmpty()) {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.setColorSchemeColors(Color.BLACK)
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setTint(Color.BLACK)
        circularProgressDrawable.start()

        Glide.with(this.context)
            .load(url).signature(ObjectKey(System.currentTimeMillis()))
            // .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(defaultImage)
            .error(defaultImage)
            .skipMemoryCache(true)
            .into(this)

    } else {
        this.setImageResource(defaultImage)
    }

}


fun ImageView.loadImageUrlWhiteLoader(url: String? = "", defaultImage: Int) {
    if (!url.isNullOrEmpty()) {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.setColorSchemeColors(Color.BLACK)
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setTint(Color.BLACK)
        circularProgressDrawable.start()

        Glide.with(this.context)
            .load(url).signature(ObjectKey(System.currentTimeMillis()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(circularProgressDrawable)
            .error(defaultImage)
            .skipMemoryCache(true)
            .into(this)

    } else {
        this.setImageResource(defaultImage)
    }
}