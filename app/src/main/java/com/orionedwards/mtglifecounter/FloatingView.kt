package com.orionedwards.mtglifecounter

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.orionedwards.mtglifecounter.Util.delay
import com.orionedwards.mtglifecounter.Util.pxToDp

class FloatingView(
        context: Context,
        private val innerView: View,
        cornerRadius: Float?) : RelativeLayout(context)
{

    var beforeShow: ((callbackDurationMillis: Long) -> Unit)? = null
    var beforePause: (() -> Unit)? = null

    // standard "context only" constructor to satisfy lint
    constructor(context: Context) : this(context, View(context), null)

    init {
        val padding = pxToDp(context, 8f)
        setPadding(padding.toInt(), padding.toInt(), padding.toInt(), padding.toInt())

        val p = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        p.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        addView(innerView, p)

        val minVal = pxToDp(context, 40f)
        minimumWidth = minVal.toInt() // try to be a square
        minimumHeight = minVal.toInt()

        // setup rounded corners
        val bg = GradientDrawable()
        bg.cornerRadius = pxToDp(context, cornerRadius!!)
        bg.setColor(Color.BLUE)
        background = bg
    }

    override fun setBackgroundColor(color: Int) {
        // custom background drawable for rounded cornders
        val bg = background as GradientDrawable
        bg.setColor(color)
    }

    fun pulse() {
        this.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(200)
                .start()
    }

    @TargetApi(21) // lollipop, android 5.0 needed for setElevation only
    private fun setElevationIfAvailable() {
        if (Build.VERSION.SDK_INT >= 21) {
            elevation = 20f
        }
    }

    @JvmOverloads
    fun showInView(parent: RelativeLayout, layoutParams: LayoutParams, callbackDurationMillis: Long = DEFAULT_CALLBACK_MILLIS, pauseDurationMillis: Long = DEFAULT_PAUSE_MILLIS) {
        if (getParent() != null) {
            return  // already parented, animation mustn't have finished or something
        }

        parent.addView(this, layoutParams)
        alpha = 1f // doesn't animate, it just pops in
        setElevationIfAvailable()

        beforeShow?.invoke(callbackDurationMillis)

        beforePause?.let{
            delay(callbackDurationMillis, Runnable(it))
        }

        val self = this
        this.animate()
                .alpha(0f)
                .setStartDelay(callbackDurationMillis + pauseDurationMillis)
                .withEndAction { parent.removeView(self) }
                .start()
    }

    fun remove() {
        removeView(innerView)
        val parent = parent as ViewGroup
        parent.removeView(this)
    }

    companion object {
        const val DEFAULT_CALLBACK_MILLIS: Long = 2200
        const val DEFAULT_PAUSE_MILLIS: Long = 1400
    }
}
