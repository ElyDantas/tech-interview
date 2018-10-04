package joyjet.com.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import joyjet.com.android.BaseActivity.Companion.EXTRA_ARTICLE
import joyjet.com.android.model.Article
import kotlinx.android.synthetic.main.activity_article.*
import kotlinx.android.synthetic.main.activity_article.view.*

class ArticleActivity : AppCompatActivity() {
    companion object {
        const val VIEW_NAME_HEADER_IMAGE = "detail:header:image"
    }

    private lateinit var headerImage: ImageView
    private lateinit var article: Article
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        setSupportActionBar(toolbar)
        initComponents()
        initArticle()
    }

    private fun initComponents() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.title = ""
        headerImage = collapsingToolbar.headerImageView
        ViewCompat.setTransitionName(headerImage, VIEW_NAME_HEADER_IMAGE)
    }

    private fun initArticle() {

        // Reetrieve article from extras
        article = Gson().fromJson(intent.getStringExtra(EXTRA_ARTICLE), Article::class.java)

        // Set up article data in the ui
        titleTextView.text = article.title
        descriptionTextView.text = article.description
        categoryTextView.text = article.category
        isFavorite = article.favorite

        // Load header image
        loadHeaderImage(article.galery[article.carouselIndex])

    }

    override fun onBackPressed() {
        returnResult()
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_article, menu)
        menu.findItem(R.id.action_favorite).icon = if (article.favorite) ContextCompat.getDrawable(this, R.drawable.ic_star_filled) else ContextCompat.getDrawable(this, R.drawable.ic_star)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorite -> {

                /** Favorite star logic */

                if (isFavorite) {
                    isFavorite = false
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_star)
                } else {
                    isFavorite = true
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_star_filled)
                }

            }
            android.R.id.home -> {
                returnResult()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /** Return uppdated article or not to parent activity*/
    private fun returnResult() {
        if (article.favorite != isFavorite) {
            article.favorite = isFavorite // Update article
            val intent = Intent()
            intent.putExtra(EXTRA_ARTICLE, Gson().toJson(article))
            setResult(Activity.RESULT_OK, intent) // Send ir back
        }
        finish()
    }

    /** Load image into imageview using picasso */
    private fun loadHeaderImage(url: String) {
        Picasso.get()
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE) //Try to fetch image in cache first
                .into(headerImage, object : Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception?) {
                        Picasso.get().load(url).into(headerImage, null) //Try again online if cache failed
                    }
                })
    }
}

