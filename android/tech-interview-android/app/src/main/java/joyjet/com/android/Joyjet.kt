package joyjet.com.android

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import joyjet.com.android.Joyjet.Companion.JOYJET_GET_CATEGORIES
import joyjet.com.android.model.Article
import joyjet.com.android.model.Category
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class Joyjet {

    companion object {
        const val JOYJET_DATABASE = "JOYJET_DATABASE"
        const val JOYJET_BASE_URL = "https://cdn.joyjet.com/tech-interview/"
        const val JOYJET_GET_CATEGORIES = "mobile-test-one.json"
        val stringArrayListType = object : TypeToken<ArrayList<String>>() {}.type!!
        val articleType = object : TypeToken<List<Article>>() {}.type
        val categoryType = object : TypeToken<List<Category>>() {}.type
    }

    private val retrofitInstance = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .baseUrl(JOYJET_BASE_URL)
            .build()

    fun categoryService(): CategoryService = retrofitInstance.create(CategoryService::class.java)

}

interface CategoryService {
    @GET(JOYJET_GET_CATEGORIES)
    fun getCategories(): Observable<ArrayList<Category>>
}