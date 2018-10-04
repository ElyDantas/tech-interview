package joyjet.com.android.adapter

import ss.com.bannerslider.adapters.SliderAdapter
import ss.com.bannerslider.viewholder.ImageSlideViewHolder

class CarouselAdapter(private val galery: List<String>) : SliderAdapter() {

    override fun getItemCount(): Int {
        return galery.size
    }

    override fun onBindImageSlide(position: Int, viewHolder: ImageSlideViewHolder) {
        viewHolder.bindImageSlide(galery[position])
    }

}