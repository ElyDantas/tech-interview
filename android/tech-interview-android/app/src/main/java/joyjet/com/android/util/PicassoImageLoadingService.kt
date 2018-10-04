package joyjet.com.android.util

import android.content.Context
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import ss.com.bannerslider.ImageLoadingService

class PicassoImageLoadingService(var context: Context) : ImageLoadingService {

    override fun loadImage(url: String, imageView: ImageView) {
        Picasso.get()
                .load(url)
                .noFade()
                .networkPolicy(NetworkPolicy.OFFLINE) //Try to fetch image in cache first
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        imageView.fadeIn(defaultOverlay = true)
                    }

                    override fun onError(e: Exception?) {
                        Picasso.get().load(url).noFade().into(imageView, object : Callback {
                            override fun onSuccess() {
                                imageView.fadeIn(defaultOverlay = true)
                            }

                            override fun onError(e: java.lang.Exception?) {
                                imageView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))
                                imageView.fadeIn(defaultOverlay = true)
                            }

                        }) //Try again online if cache failed
                    }
                })
    }

    override fun loadImage(resource: Int, imageView: ImageView) {
        Picasso.get()
                .load(resource)
                .noFade()
                .networkPolicy(NetworkPolicy.OFFLINE) //Try to fetch image in cache first
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        imageView.fadeIn(defaultOverlay = true)
                    }

                    override fun onError(e: Exception?) {
                        Picasso.get().load(resource).noFade().into(imageView, object : Callback {
                            override fun onSuccess() {
                                imageView.fadeIn(defaultOverlay = true)
                            }

                            override fun onError(e: java.lang.Exception?) {
                                imageView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))
                                imageView.fadeIn(defaultOverlay = true)
                            }
                        }) //Try again online if cache failed
                    }
                })
    }

    override fun loadImage(url: String, placeHolder: Int, errorDrawable: Int, imageView: ImageView) {
        Picasso.get()
                .load(url)
                .noFade()
                .placeholder(placeHolder)
                .error(errorDrawable)
                .networkPolicy(NetworkPolicy.OFFLINE) //Try to fetch image in cache first
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        imageView.fadeIn(defaultOverlay = true)
                    }

                    override fun onError(e: Exception?) {
                        Picasso.get()
                                .load(url)
                                .noFade()
                                .placeholder(placeHolder)
                                .error(errorDrawable)
                                .into(imageView, object : Callback {
                                    override fun onSuccess() {
                                        imageView.fadeIn(defaultOverlay = true)
                                    }

                                    override fun onError(e: java.lang.Exception?) {
                                        imageView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))
                                        imageView.fadeIn(defaultOverlay = true)
                                    }
                                }) //Try again online if cache failed
                    }
                })
    }
}