package joyjet.com.android.model

import android.widget.TextView
import com.google.gson.annotations.SerializedName
import joyjet.com.android.R

data class Category(@SerializedName("id") var id: Long,
                         @SerializedName("category") val category: String = "",
                         @SerializedName("items") var articles: ArrayList<Article> = arrayListOf()){

    companion object {
        @Transient
        val COLUMN_ID = "id"
        @Transient
        val TABLE_NAME = "categories"
        @Transient
        val COLUMN_CATEGORY = "category"
    }
}