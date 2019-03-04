package com.orionedwards.mtglifecounter

import android.content.Context
import android.support.annotation.ColorInt
import android.text.SpannableString
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.orionedwards.mtglifecounter.Util.pxToDp

class NumberWheelView(
        context: Context,
        fontSize: Float,
        @ColorInt textColor: Int,
        numCells: Int,
        generator: (Int) -> SpannableString) : RelativeLayout(context) {

    private val mInnerView: View
    private val mLineHeight: Float
    private val mTotalHeight: Float

    constructor(context: Context) : this(context, 12f, 1, 1, { SpannableString("") })

    init {

        // configure the view
        val inner = LinearLayout(getContext())
        inner.orientation = LinearLayout.VERTICAL
        inner.clipChildren = false
        inner.clipToPadding = false
        val ilp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        inner.layoutParams = ilp

        for (i in 0 until numCells + OVERSHOOT_ITEM_COUNT) { // 2 more for overshoot
            val tv = TextView(getContext())
            tv.text = generator(numCells - 1 - i)
            tv.textSize = fontSize
            tv.setTextColor(textColor)
            tv.setSingleLine(true)
            tv.gravity = Gravity.CENTER_HORIZONTAL
            //            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            val tlp = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            tlp.setMargins(0, 0, 0, pxToDp(context, LINE_GAP.toFloat()).toInt())
            tv.layoutParams = tlp
            inner.addView(tv)
        }

        inner.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        mTotalHeight = inner.measuredHeight.toFloat()
        mLineHeight = mTotalHeight / (numCells + OVERSHOOT_ITEM_COUNT).toFloat()

        addView(inner)
        mInnerView = inner
    }

    fun spinWithDuration(millis: Long) {
        // we have to animate the layout itself, not just the visual representation
        // as android clips textViews and doesn't redraw them during visual animation

        val newTopMargin = (-(mTotalHeight - mLineHeight * (OVERSHOOT_ITEM_COUNT + 1))).toInt()
        val a = LayoutTopMarginAnimation(mInnerView, 0, newTopMargin)
        a.duration = millis
        a.interpolator = OvershootInterpolator(0.6f)
        mInnerView.startAnimation(a)
    }

    internal class LayoutTopMarginAnimation(
            private val targetView: View,
            private val startValue: Int,
            endValue: Int) : Animation()
    {
        private val diff: Int = endValue - startValue

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val lp = targetView.layoutParams as RelativeLayout.LayoutParams
            lp.topMargin = startValue + (diff * interpolatedTime).toInt()
            targetView.layoutParams = lp
        }
    }

    companion object {

        private const val LINE_GAP = 5
        private const val OVERSHOOT_ITEM_COUNT = 1
    }
}
