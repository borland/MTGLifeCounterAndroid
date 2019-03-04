package com.orionedwards.mtglifecounter


import android.content.Context
import android.graphics.Color
import android.widget.RelativeLayout
import android.widget.TextView
import java.util.*

internal data class HistoryTuple(val lifeTotal: Int)

class LifeTotalDeltaTracker internal constructor(
        private val mContext: Context)
{

    private var mBaseline = 0 // we show +/- x relative to this
    private val mHistory = LinkedList<HistoryTuple>()
    private var mFloatingView: FloatingView? = null
    private val mLabel: TextView = TextView(mContext)
    private var mCancelPreviousDelay: Runnable? = null
    var parent: RelativeLayout? = null

    init {
        mLabel.textSize = FLOATING_VIEW_FONT_SIZE
        mLabel.setTextColor(Color.WHITE)
    }

    fun update(lifeTotal: Int) {
        if (!mHistory.isEmpty()) {
            val t = mHistory.last
            if (t.lifeTotal == lifeTotal) {
                return  // no point recording a duplicate
            }
        }

        val nt = HistoryTuple(lifeTotal)
        mHistory.addLast(nt)
        updateUi(lifeTotal)
    }

    fun reset(lifeTotal: Int) {
        mHistory.clear()
        mBaseline = lifeTotal
        updateUi(lifeTotal)
    }

    private fun updateUi(lifeTotal: Int) {
        val symbol = if (lifeTotal - mBaseline >= 0) "+" else ""
        mLabel.text = String.format(Locale.getDefault(), "%s%d", symbol, lifeTotal - mBaseline)
        showOrExtendView()
    }

    private fun showOrExtendView() {
        if (parent != null && mFloatingView == null && mHistory.size > 1) {
            val fv = FloatingView(mContext, mLabel, FLOATING_VIEW_FONT_SIZE / 5)

            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
            params.setMargins(STANDARD_MARGIN, STANDARD_MARGIN, STANDARD_MARGIN, STANDARD_MARGIN)

            fv.showInView(parent!!, params)

            mFloatingView = fv
        }

        if (mCancelPreviousDelay != null) {
            mCancelPreviousDelay!!.run()
        }
        mCancelPreviousDelay = Util.delay(FLOATING_VIEW_TIMEOUT_MILLIS, Runnable {
            if (mHistory.size < 1) {
                return@Runnable
            }
            val t = mHistory.last
            mBaseline = t.lifeTotal

            mHistory.clear()

            mFloatingView?.let { floatingView ->
                floatingView.animate().alpha(0f).setDuration(FLOATING_VIEW_HIDE_MILLIS).withEndAction {
                    floatingView.remove()
                    mFloatingView = null
                }
            }
        })
    }

    companion object {
        private const val STANDARD_MARGIN = 16 // dp?
        private const val FLOATING_VIEW_FONT_SIZE = 44f
        private const val FLOATING_VIEW_TIMEOUT_MILLIS: Long = 1700
        private const val FLOATING_VIEW_HIDE_MILLIS: Long = 250
    }
}
