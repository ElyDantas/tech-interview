package joyjet.com.android.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import joyjet.com.android.R
import joyjet.com.android.database.DatabaseHelper
import joyjet.com.android.database.JoyjetRepository

fun Boolean.toInt() = if (this) 1 else 0

fun Any.fromLongtoInt() = (this as Long).toInt()

fun View.fadeIn(duration: Long = 600, colorOverlay: String? = null, resourceOverlay: Int? = null, defaultOverlay: Boolean = false, startAlpha: Float = 0f, finishAlpha: Float = 1f) {
    this.alpha = startAlpha
    colorOverlay?.let { this.setBackgroundColor(Color.parseColor(it)) }
    resourceOverlay?.let { this.setBackgroundResource(it) }
    if (defaultOverlay) {
        this.setBackgroundResource(R.color.colorTransparentOverlay)
    }
    this.animate().setDuration(duration).alpha(finishAlpha).start()
}

val Context.database: DatabaseHelper get() = DatabaseHelper.Instance(this)

val Activity.joyjetRepository: JoyjetRepository
    get() {
        return JoyjetRepository(this)
    }