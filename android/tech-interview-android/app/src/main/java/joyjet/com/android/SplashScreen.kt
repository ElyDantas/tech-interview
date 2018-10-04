package joyjet.com.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import joyjet.com.android.model.Category
import joyjet.com.android.util.joyjetRepository
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        doAsync {

            if (!isOnline()) {
                Toast.makeText(this@SplashScreen, "No internet connection", Toast.LENGTH_LONG).show()
                return@doAsync
            }

            Joyjet().categoryService().getCategories()
                    .observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler())
                    .doOnError { it.message?.let { it1 -> error(it1) } }
                    .subscribe { it ->

                        val categories = joyjetRepository.insertAll(it)

                        uiThread {
                            startActivity(intentFor<FeedActivity>().putExtra(BaseActivity.EXTRA_CATEGORIES, Gson().toJson(categories)))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                        }
                    }
        }
    }

    private fun isOnline(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockaddr = InetSocketAddress("8.8.8.8", 53)
            sock.connect(sockaddr, timeoutMs)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }
    }
}
