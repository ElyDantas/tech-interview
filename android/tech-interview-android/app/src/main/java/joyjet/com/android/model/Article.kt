package joyjet.com.android.model

import com.google.gson.annotations.SerializedName
import joyjet.com.android.adapter.CarouselAdapter
import ss.com.bannerslider.Slider

class Article(

        @SerializedName("id")
        var id: Long,

        @SerializedName("id_category")
        var id_category: Long,

        @SerializedName("title")
        val title: String = "",

        @SerializedName("description")
        val description: String = "",

        @SerializedName("galery")
        var galery: List<String> = listOf(),

        @SerializedName("category")
        var category: String = "",

        @SerializedName("favorite")
        var favorite: Boolean = false,

        @SerializedName("carouselIndex")
        var carouselIndex: Int = 0) {

    @Transient
    var carouselAdapter: CarouselAdapter? = null

    companion object {
        @Transient
        val COLUMN_ID = "id"
        @Transient
        val COLUMN_FOREIGN_KEY = "id_category"
        @Transient
        val TABLE_NAME = "articles"
        @Transient
        val COLUMN_TITLE = "title"
        @Transient
        val COLUMN_DESCRIPTION = "description"
        @Transient
        val COLUMN_GALERY = "galery"
        @Transient
        val COLUMN_CATEGORY = "category"
        @Transient
        val COLUMN_FAVORITE = "favorite"
        @Transient
        val COLUMN_CAROUSEL_INDEX = "carouselIndex"
    }

}