package joyjet.com.android

import android.app.Application
import android.content.Context
import joyjet.com.android.database.DatabaseHelper
import joyjet.com.android.model.Article
import joyjet.com.android.model.Category
import joyjet.com.android.util.PicassoImageLoadingService
import org.jetbrains.anko.db.*
import ss.com.bannerslider.Slider

class JoyjetApplication : Application() {

    val Context.database: DatabaseHelper get() = DatabaseHelper.Instance(this)

    override fun onCreate() {
        super.onCreate()

        initTemporaryDatabase()
        Slider.init(PicassoImageLoadingService(this))
    }

    private fun initTemporaryDatabase() {
        database.writableDatabase.execSQL("DROP TABLE IF EXISTS " + Article.TABLE_NAME)
        database.writableDatabase.execSQL("DROP TABLE IF EXISTS " + Category.TABLE_NAME)
        database.use {
            createTable(
                    Article.TABLE_NAME, true,
                    Article.COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                    Article.COLUMN_TITLE to TEXT + NOT_NULL,
                    Article.COLUMN_DESCRIPTION to TEXT,
                    Article.COLUMN_GALERY to TEXT,
                    Article.COLUMN_CATEGORY to TEXT,
                    Article.COLUMN_FAVORITE to INTEGER,
                    Article.COLUMN_CAROUSEL_INDEX to INTEGER,
                    Article.COLUMN_FOREIGN_KEY to INTEGER,
                    FOREIGN_KEY(Article.COLUMN_FOREIGN_KEY, Category.TABLE_NAME, Category.COLUMN_ID)
            )

            createTable(Category.TABLE_NAME, true,
                    Category.COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT, Category.COLUMN_CATEGORY to TEXT
            )
        }
    }
}