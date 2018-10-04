package joyjet.com.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.gson.Gson
import joyjet.com.android.adapter.CategoriesAdapter
import joyjet.com.android.model.Article
import joyjet.com.android.util.joyjetRepository
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class FavoriteActivity : BaseActivity() {

    private lateinit var favoritesAdapter: CategoriesAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setSupportActionBar(toolbar_layout)
        super.onCreateDrawer("FavoriteActivity")
        initComponents()
        initFavorites()
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView.adapter = null
    }

    private fun initComponents() {
        toolbar_layout.setTitle(R.string.title_activity_favorite)
        recyclerView = findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this@FavoriteActivity)
    }

    private fun initFavorites() {
        doAsync {
            var favorites = getFeed(joyjetRepository.fetchFavorites())
            favorites = favorites.filter { it is Article }.toMutableList()
            uiThread {
                favoritesAdapter = CategoriesAdapter(favorites, this@FavoriteActivity)
                recyclerView.adapter = favoritesAdapter
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SHOW_ARTICLE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.getStringExtra(EXTRA_ARTICLE)?.let { it ->

                    doAsync {
                        val article = Gson().fromJson(it, Article::class.java)
                        joyjetRepository.update(article)

                        uiThread {
                            favoritesAdapter.removeArticle(article)
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        startFeedActivity()
    }
}