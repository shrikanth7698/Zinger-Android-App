package com.food.ordering.zinger.utils

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import ss.com.bannerslider.ImageLoadingService

class PicassoImageLoadingService(var context: Context) : ImageLoadingService {
    override fun loadImage(url: String, imageView: ImageView) {
        Picasso.get().load(url).into(imageView)
    }
    override fun loadImage(resource: Int, imageView: ImageView) {
        Picasso.get().load(resource).into(imageView)
    }
    override fun loadImage(url: String, placeHolder: Int, errorDrawable: Int, imageView: ImageView) {
        Picasso.get().load(url).placeholder(placeHolder).error(errorDrawable).into(imageView)
    }
}