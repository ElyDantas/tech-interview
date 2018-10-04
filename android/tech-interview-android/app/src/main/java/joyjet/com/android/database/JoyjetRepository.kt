package joyjet.com.android.database

import android.content.Context
import com.google.gson.Gson
import joyjet.com.android.Joyjet.Companion.stringArrayListType
import joyjet.com.android.model.Article
import joyjet.com.android.model.Category
import joyjet.com.android.util.database
import joyjet.com.android.util.fromLongtoInt
import joyjet.com.android.util.toInt
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update

class JoyjetRepository(val context: Context) {

    /**
     * Fetch categories in database
     * Return categories list
     */
    fun fetchAllCategories(): ArrayList<Category> = context.database.use {
        val categories = ArrayList<Category>()

        select(Category.TABLE_NAME, Category.COLUMN_ID, Category.COLUMN_CATEGORY)
                .parseList(object : MapRowParser<List<Category>> {
                    override fun parseRow(columns: Map<String, Any?>): List<Category> {

                        val category = getCategory(columns)
                        category.articles = fetchArticles(category.id)
                        categories.add(category)
                        return categories
                    }
                })

        categories

    }

    /**
     * Fetch articles with specified foreignkey
     * Return articles or favorited articles
     * @param foreignKey
     * @param favorites
     */
    fun fetchArticles(foreignKey: Long, favorites: Boolean = false): ArrayList<Article> = context.database.use {
        val articles = ArrayList<Article>()

        if (favorites) {

            select(Article.TABLE_NAME)
                    .whereArgs("id_category  = {foreignKey} AND favorite  = 1", "foreignKey" to foreignKey.toString())
                    .parseList(object : MapRowParser<List<Article>> {
                        override fun parseRow(columns: Map<String, Any?>): List<Article> {

                            articles.add(getArticle(columns))

                            return articles
                        }
                    })

        } else {

            select(Article.TABLE_NAME)
                    .columns(Article.COLUMN_ID, Article.COLUMN_CATEGORY, Article.COLUMN_TITLE, Article.COLUMN_FOREIGN_KEY, Article.COLUMN_DESCRIPTION, Article.COLUMN_GALERY, Article.COLUMN_FAVORITE, Article.COLUMN_CAROUSEL_INDEX)
                    .whereSimple(Article.COLUMN_FOREIGN_KEY + " = ?", foreignKey.toString())
                    .parseList(object : MapRowParser<ArrayList<Article>> {
                        override fun parseRow(columns: Map<String, Any?>): ArrayList<Article> {

                            articles.add(getArticle(columns))

                            return articles
                        }

                    })
        }

        print(articles)
        articles
    }

    /**
     * Fetch categories that contains favorited articles
     * Return favorited articles per categories
     */
    fun fetchFavorites(): ArrayList<Category> = context.database.use {
        val categories = ArrayList<Category>()

        select(Category.TABLE_NAME, Category.COLUMN_ID, Category.COLUMN_CATEGORY)
                .parseList(object : MapRowParser<List<Category>> {
                    override fun parseRow(columns: Map<String, Any?>): List<Category> {

                        val category = Category(
                                columns.getValue(Category.COLUMN_ID) as Long,
                                columns.getValue(Category.COLUMN_CATEGORY) as String
                                , arrayListOf()
                        )
                        category.articles = fetchArticles(category.id, true)
                        categories.add(category)
                        return categories
                    }
                })

        categories
    }

    /**
     * Insert categories in database
     * Return with each respective primary key value
     * @param Categories list
     */
    fun insertAll(categories: ArrayList<Category>): ArrayList<Category> = context.database.use {
        categories.forEach { category ->
            insertAll(category)
        }
        categories
    }

    /**
     * Insert category in database
     * Return object with primary key value
     * @param Category object
     */
    private fun insertAll(category: Category): Category = context.database.use {
        category.id = insert(Category.TABLE_NAME, Category.COLUMN_CATEGORY to category.category)
        category.articles.forEach { article ->
            article.category = category.category
            article.id_category = category.id
            article.id = insertAll(article)
        }
        category
    }

    /**
     * Insert article in database
     * Return object with primary key value
     * @param Article  object
     */
    private fun insertAll(article: Article): Long = context.database.use {
        insert(Article.TABLE_NAME,
                Article.COLUMN_TITLE to article.title,
                Article.COLUMN_DESCRIPTION to article.description,
                Article.COLUMN_GALERY to (Gson().toJson(article.galery)
                        ?: Gson().toJson(arrayListOf(""))),
                Article.COLUMN_CATEGORY to article.category,
                Article.COLUMN_FAVORITE to article.favorite.toInt(),
                Article.COLUMN_CAROUSEL_INDEX to article.carouselIndex,
                Article.COLUMN_FOREIGN_KEY to article.id_category
        )
    }

    /**
     * Update article in database
     * @param Article object
     */
    fun update(article: Article) = context.database.use {

        val updateResult = update(Article.TABLE_NAME, Article.COLUMN_TITLE to article.title,
                Article.COLUMN_DESCRIPTION to article.description,
                Article.COLUMN_GALERY to (Gson().toJson(article.galery)
                        ?: Gson().toJson(arrayListOf(""))),
                Article.COLUMN_CATEGORY to article.category,
                Article.COLUMN_FAVORITE to article.favorite.toInt(),
                Article.COLUMN_CAROUSEL_INDEX to article.carouselIndex,
                Article.COLUMN_FOREIGN_KEY to article.id_category
        )
                .whereSimple("id = ?", article.id.toString())
                .exec()

        println("ROWS AFFECTED BY UPDATE -> $updateResult")
    }

    /**
     * Update articles in database
     * @param Articles list
     */
    fun updateAll(articles: ArrayList<Article>) = context.database.use {
        articles.forEach { update(it) }
    }

    /** Return pair value map for articles entity */
    fun getArticle(columns: Map<String, Any?>): Article {
        return Article(
                columns.getValue(Article.COLUMN_ID) as Long,
                columns.getValue(Article.COLUMN_FOREIGN_KEY) as Long,
                columns.getValue(Article.COLUMN_TITLE) as String,
                columns.getValue(Article.COLUMN_DESCRIPTION) as String,
                getStringArray(columns.getValue(Article.COLUMN_GALERY) as String? ?: ""),
                columns.getValue(Article.COLUMN_CATEGORY) as String,
                ((columns.getValue(Article.COLUMN_FAVORITE))?.fromLongtoInt()
                        ?: 0) == 1,
                (columns.getValue(Article.COLUMN_CAROUSEL_INDEX))?.fromLongtoInt()
                        ?: 0
        )
    }

    /** Return pair value map for category entity */
    fun getCategory(columns: Map<String, Any?>): Category {
        return Category(
                columns.getValue(Category.COLUMN_ID) as Long,
                columns.getValue(Category.COLUMN_CATEGORY) as String
                , arrayListOf()
        )
    }

    private fun getStringArray(json: String?): ArrayList<String> {
        json?.let {
            return Gson().fromJson(it, stringArrayListType)
        }
        return arrayListOf("")
    }

}