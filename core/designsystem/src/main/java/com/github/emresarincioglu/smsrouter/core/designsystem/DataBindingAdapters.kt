package com.github.emresarincioglu.smsrouter.core.designsystem

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@SuppressLint("CheckResult")
@BindingAdapter("app:remoteSrc", "app:defaultImage", requireAll = false)
fun ImageView.loadImageWithGlide(model: Any?, defaultImage: Drawable?) {

    Glide.with(context)
        .load(model)
        .centerCrop()
        .apply {
            defaultImage?.let {
                fallback(it)
            }
        }
        .into(this)
}