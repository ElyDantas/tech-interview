package joyjet.com.android.adapter

import android.app.Activity
import android.os.Build
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import joyjet.com.android.ArticleActivity
import joyjet.com.android.BaseActivity.Companion.EXTRA_ARTICLE
import joyjet.com.android.BaseActivity.Companion.SHOW_ARTICLE_REQUEST_CODE
import joyjet.com.android.R
import joyjet.com.android.database.DatabaseHelper
import joyjet.com.android.database.JoyjetRepository
import joyjet.com.android.model.Article
import joyjet.com.android.model.Category
import joyjet.com.android.util.fadeIn
import kotlinx.android.synthetic.main.article_item.view.*
import kotlinx.android.synthetic.main.section_item.view.*
import org.jetbrains.anko.intentFor
import ss.com.bannerslider.Slider
import ss.com.bannerslider.adapters.SliderAdapter
import ss.com.bannerslider.viewholder.ImageSlideViewHolder


class CategoriesAdapter(private var feed: MutableList<Any>, private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var defaultGalery = arrayListOf("https://firebasestorage.googleapis.com/v0/b/swipecleaner.appspot.com/o/naom_585c43cd5fe8a.jpg?alt=media&token=21f7e14e-34ac-42fc-a225-33aef888878d",
            "https://firebasestorage.googleapis.com/v0/b/swipecleaner.appspot.com/o/iss-1030777_960_720.jpg?alt=media&token=484187ae-eb18-43b9-930e-6db8845343d6",
            "https://firebasestorage.googleapis.com/v0/b/swipecleaner.appspot.com/o/Space%20Shuttle.jpg?alt=media&token=50f35bd9-0cb1-46b5-971f-f5ac07c7e8d0")

    // Section view
    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val sectionLayout: LinearLayout = itemView.sectionLayout
    }

    // Article view
    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.descriptionTextView
        val carousel: Slider = itemView.carousel
        val leftImageView: ImageView = itemView.leftImageView
        val rightImageView: ImageView = itemView.rightImageView
        val articleView: View = itemView.articleView
        lateinit var carouselAdapter: CarouselAdapter
    }

    override fun getItemViewType(position: Int): Int {
        return when (feed[position]) {
            is Category -> {
                R.layout.section_item
            }
            is Article -> {
                R.layout.article_item
            }
            else ->
                super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(activity).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.section_item -> SectionViewHolder(view)
            R.layout.article_item -> ArticleViewHolder(view)
            else -> {
                ArticleViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val element = feed[position]

        when (holder.itemViewType) {
            R.layout.section_item -> {

                /** Set up category section */
                val section: SectionViewHolder = holder as SectionViewHolder
                section.sectionLayout.fadeIn(400)
                section.titleTextView.text = (element as Category).category

            }
            R.layout.article_item -> {

                val row: ArticleViewHolder = holder as ArticleViewHolder
                val article = (element as Article)

                /** Image test with fast response*/
                //article.galery = defaultGalery

                row.carouselAdapter = CarouselAdapter(article.galery)
                row.carousel.setAdapter(row.carouselAdapter)
                row.carousel.setSelectedSlide(article.carouselIndex)
                row.titleTextView.text = article.title
                row.descriptionTextView.text = article.description

                /** Update object and slide position*/
                row.leftImageView.setOnClickListener {
                    row.carousel.setSelectedSlide(getPreviousSlide(row.carousel.selectedSlidePosition, row.carousel.sliderAdapter.itemCount), false)
                    (feed[row.adapterPosition] as Article).carouselIndex = row.carousel.selectedSlidePosition
                }
                row.rightImageView.setOnClickListener {
                    row.carousel.setSelectedSlide(getNextSlide(row.carousel.selectedSlidePosition, row.carousel.sliderAdapter.itemCount), false)
                    (feed[row.adapterPosition] as Article).carouselIndex = row.carousel.selectedSlidePosition
                }

                /** Show article acitivy*/
                row.articleView.setOnClickListener {

                    if (row.adapterPosition < 0 || row.adapterPosition > feed.size - 1) return@setOnClickListener

                    /** Prepare article extra as json string*/
                    val extra: String = Gson().toJson(feed[row.adapterPosition])

                    /** Start activity with or without animation*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, row.carousel, ArticleActivity.VIEW_NAME_HEADER_IMAGE)
                        startActivityForResult(activity, activity.intentFor<ArticleActivity>(EXTRA_ARTICLE to extra), SHOW_ARTICLE_REQUEST_CODE, activityOptions.toBundle())
                    } else {
                        startActivityForResult(activity, activity.intentFor<ArticleActivity>(EXTRA_ARTICLE to extra), SHOW_ARTICLE_REQUEST_CODE, null)
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                }
            }
        }
    }

    private fun getPreviousSlide(position: Int, size: Int): Int {
        return if (position == 0) {
            size - 1
        } else {
            position - 1
        }
    }

    private fun getNextSlide(position: Int, size: Int): Int {
        return if (position == size - 1) {
            0
        } else {
            position + 1
        }
    }

    fun removeArticle(article: Article) {
        feed.forEachIndexed { index, it ->
            if (it is Article) {
                if (article.title == it.title && article.category == it.category) {
                    feed.remove(it)
                    notifyItemRemoved(index)
                    return
                }
            }
        }
    }

    fun update(article: Article) {
        feed.forEachIndexed { index, it ->
            if (it is Article) {
                if (article.title == it.title && article.category == it.category) {
                    notifyItemChanged(index, article)
                    return
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return feed.size
    }
}