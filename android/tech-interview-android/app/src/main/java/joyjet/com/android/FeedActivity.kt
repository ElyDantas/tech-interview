package joyjet.com.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import com.google.gson.Gson
import joyjet.com.android.Joyjet.Companion.categoryType
import joyjet.com.android.adapter.CategoriesAdapter
import joyjet.com.android.model.Article
import joyjet.com.android.model.Category
import joyjet.com.android.util.joyjetRepository
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class FeedActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var feedAdapter: CategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        super.onCreateDrawer("FeedActivity")
        initComponents()
        initFeed()
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView.adapter = null
    }

    private fun initComponents() {
        recyclerView = findViewById(R.id.feedRecyclerView)
        toolbar_layout.setTitle(R.string.digital_space)
        toolbar_layout.titleMarginBottom = 32
        recyclerView.hasFixedSize()
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerView.itemAnimator.changeDuration = 0
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun initFeed() {

        // Check extras from SplashScreen
        val data = intent.getStringExtra(EXTRA_CATEGORIES)
        if (data != null) {

            val feed = getFeed(Gson().fromJson(data, categoryType) as ArrayList<Category>)
            setAdapter(feed)

        } else {

            // Init feed using database records
            doAsync {
                val feed = getFeed(joyjetRepository.fetchAllCategories())
                uiThread { setAdapter(feed) }
            }

        }
    }

    /**
     * Reload recyclerview adapter
     * @param List of articles && categories
     */
    private fun setAdapter(feed: MutableList<Any>) {
        feedAdapter = CategoriesAdapter(feed, this@FeedActivity)
        recyclerView.adapter = feedAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            SHOW_ARTICLE_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) return

                // Retrieve article changed from article activity
                data?.getStringExtra(EXTRA_ARTICLE)?.let {

                    doAsync {
                        val article = Gson().fromJson(it, Article::class.java)
                        joyjetRepository.update(article)

                        uiThread {
                            feedAdapter.update(article)
                        }
                    }
                }
            }
        }
    }
}
