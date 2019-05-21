package com.abecerra.gnssanalysis.core.utils.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.abecerra.gnssanalysis.R
import jp.wasabeef.glide.transformations.CropTransformation

fun ImageView.setImage(url: Any) {
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        )
        .into(this)
}

fun convertToCircularBitmapDrawable(url: Any, imageView: ImageView, placeHolderResourceId: Int) {
    val placeholder = BitmapFactory.decodeResource(context.resources, placeHolderResourceId)
    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, placeholder)

    circularBitmapDrawable.isCircular = true

    Glide
        .with(context)
        .load(url)
        .apply(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(
                    RequestOptions.bitmapTransform(
                        MultiTransformation<Bitmap>(
                            CropTransformation(
                                300,
                                300,
                                CropTransformation.CropType.TOP
                            ), CircleCrop()
                        )
                    )
                )
                .error(circularBitmapDrawable)
                .placeholder(circularBitmapDrawable)
        )
        //     .transition(DrawableTransitionOptions.withCrossFade())
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

        })
        .into(imageView)
}
