package joyjet.com.android

import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import joyjet.com.android.R.id.drawer_layout
import joyjet.com.android.R.id.nav_view
import joyjet.com.android.model.Category
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.startActivity

open class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    companion object {
        const val EXTRA_FEED = "EXTRA_FEED"
        const val EXTRA_CATEGORIES = "EXTRA_CATEGORIES"
        const val EXTRA_ARTICLE = "EXTRA_ARTICLE"
        const val EXTRA_BACK_FROM_FAVORITES = "EXTRA_BACK_FROM_FAVORITES"
        const val RECYCLERVIEW_STATE = "RECYCLERVIEW_STATE"
        const val SHOW_FAVORITES_REQUEST_CODE = 101
        const val SHOW_ARTICLE_REQUEST_CODE = 100
    }

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var lightFlags = 0
    private var grayFlags = 0
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    lateinit var activityType: String

    fun onCreateDrawer(activityType: String) {
        this.activityType = activityType

        // Init views
        drawerLayout = findViewById(drawer_layout)
        navigationView = findViewById(nav_view)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        // Init Listeners
        drawerLayout.addDrawerListener(drawerToggle)
        drawerLayout.addDrawerListener(this)
        navigationView.setNavigationItemSelectedListener(this)

        // Set up custom parameters
        drawerToggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.black)
        drawerToggle.drawerArrowDrawable.gapSize = 16.0f
        drawerToggle.drawerArrowDrawable.barLength = 80.0f
        drawerToggle.drawerArrowDrawable.barThickness = 8.0f
        drawerToggle.syncState()

        // Set up status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            lightFlags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            grayFlags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun startFeedActivity() {
        if (activityType == "FeedActivity") return
        startActivity<FeedActivity>()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    fun startFavoritesActivity() {
        if (activityType == "FavoritesActivity") return
        startActivity<FavoriteActivity>()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.nav_home -> {
                Handler().postDelayed({
                    Runnable {

                        startFeedActivity() // Launch feed aticity

                    }.run()
                }, 300)
            }
            R.id.nav_favorites -> {
                Handler().postDelayed({
                    Runnable {

                        startFavoritesActivity() // Launch favorites activity

                    }.run()
                }, 300)
            }
        }
        return false
    }

    override fun onDrawerClosed(drawerView: View) {
        lightStatusBar()
    }

    override fun onDrawerOpened(drawerView: View) {
        transparentStatusBar()
    }

    override fun onDrawerStateChanged(newState: Int) {}

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

    private fun transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = lightFlags
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun lightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = grayFlags
            window.statusBarColor = Color.WHITE
        }
    }

    fun getFeed(categories: ArrayList<Category>): MutableList<Any> {
        val feedList = ArrayList<Any>()

        categories.forEach { category ->
            feedList.add(category)

            category.articles.forEach { article ->
                article.category = category.category
                feedList.add(article)
            }

        }
        return feedList.toMutableList()
    }
}