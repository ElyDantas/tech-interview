package joyjet.com.android.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import joyjet.com.android.Joyjet
import joyjet.com.android.model.Article
import joyjet.com.android.model.Category
import org.jetbrains.anko.db.*

/**
 * Database class, created just beacause i am confortable in using it for every project
 *             * Temporary behavior, recreated every app start *
 */
class DatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, Joyjet.JOYJET_DATABASE, null, 1) {

    companion object {
        private var instance: DatabaseHelper? = null

        @Synchronized
        fun Instance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(database: SQLiteDatabase) {
        database.createTable(
                Article.TABLE_NAME, true,
                Article.COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                Article.COLUMN_TITLE to TEXT + NOT_NULL,
                Article.COLUMN_DESCRIPTION to TEXT + NOT_NULL,
                Article.COLUMN_GALERY to TEXT + NOT_NULL,
                Article.COLUMN_CATEGORY to TEXT + NOT_NULL,
                Article.COLUMN_FAVORITE to INTEGER + NOT_NULL,
                Article.COLUMN_CAROUSEL_INDEX to INTEGER + NOT_NULL,
                Article.COLUMN_FOREIGN_KEY to INTEGER + NOT_NULL,
                FOREIGN_KEY(Article.COLUMN_FOREIGN_KEY, Category.TABLE_NAME, Category.COLUMN_ID)
        )

        database.createTable(Category.TABLE_NAME, true,
                Category.COLUMN_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT, Category.COLUMN_CATEGORY to TEXT
        )
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.dropTable(Article.TABLE_NAME, true)
        database.dropTable(Category.TABLE_NAME, true)
    }
}
