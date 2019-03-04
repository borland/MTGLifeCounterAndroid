package com.orionedwards.mtglifecounter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.app.Fragment
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_player.view.*
import java.util.*

class PlayerFragment : Fragment(), View.OnTouchListener, GestureDetector.OnGestureListener {

    var rootView: RelativeLayout? = null
        private set

    private lateinit var mLifeTotalLabel: TextView
    private lateinit var mPlusButtonLabel: TextView
    private lateinit var mMinusButtonLabel: TextView

    private lateinit var mGestureDetector: GestureDetectorCompat // final after onCreateView
    private lateinit var mTracker: LifeTotalDeltaTracker // final after onCreateView

    private var mLifeTotal = 0
    private var mIsUpsideDown: Boolean? = false
    var color = MtgColor.White
        set(value) {
            if (color == value) {
                return
            }

            if (color == MtgColor.White && value != MtgColor.White) {
                textColor = Color.WHITE
            } else if (color != MtgColor.White && value == MtgColor.White) {
                textColor = Color.BLACK
            }

            field = value
            rootView?.let {
                setBackgroundGradient(it)
            }
        }

    private var mLastTouchDownX = 0f
    private var mLastTouchDownY = 0f
    private var mLastTouchDownColorOrdinal = 0
    private var mLastTouchDownLifeTotal = 0
    private var mLockScrolling = LOCK_NONE

    // ----- Logic -----

    var lifeTotal: Int
        get() = mLifeTotal
        set(value) {
            if (value == mLifeTotal) {
                return
            }
            mLifeTotal = value
            mTracker.update(value)

            mLifeTotalLabel.text = String.format(Locale.getDefault(), "%d", mLifeTotal)
        }

    var isUpsideDown: Boolean
        get() = mIsUpsideDown!!

        set(value) {
            mIsUpsideDown = value
            if (value) {
                rootView!!.rotation = 180f
            }
        }


    // all states
    // that color
    private var textColor: Int
        @ColorInt
        get() = mLifeTotalLabel.currentTextColor

        set(@ColorInt value) {
            if (rootView == null) { // view not loaded yet
                return
            }

            val csl = ColorStateList(
                    arrayOf(intArrayOf()),
                    intArrayOf(value))

            mLifeTotalLabel.setTextColor(csl)
            mPlusButtonLabel.setTextColor(csl)
            mMinusButtonLabel.setTextColor(csl)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val values = MtgColor.values()
        val idx = RandomGen.next(values.size)
        color = values[idx]
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_player, container, false) as RelativeLayout
        view.setOnTouchListener(this)
        setBackgroundGradient(view)
        rootView = view

        mLifeTotalLabel = view.lifeTotal
        mPlusButtonLabel = view.plus
        mMinusButtonLabel = view.minus

        mGestureDetector = GestureDetectorCompat(context, this)
        val tracker = LifeTotalDeltaTracker(context!!)
        tracker.parent = view

        mTracker = tracker

        return rootView
    }

    /** Sets the lifetotal to `value` without triggering any change trackers  */
    fun resetLifeTotal(value: Int) {
        mLifeTotal = value
        mTracker.reset(value)

        mLifeTotalLabel.text = String.format(Locale.getDefault(), "%d", mLifeTotal)
    }

    // ----- Gesture Detecting -----
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean {
        mLastTouchDownX = e.x
        mLastTouchDownY = e.y
        mLastTouchDownColorOrdinal = color.ordinal
        mLastTouchDownLifeTotal = lifeTotal
        mLockScrolling = LOCK_NONE
        return true
    }

    override fun onShowPress(e: MotionEvent) {}

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val rotation = activity!!.windowManager.defaultDisplay.rotation

        val up = when (rotation) {
            // phone in portrait - +/- buttons are on the side of the text
            Surface.ROTATION_0, Surface.ROTATION_180 -> e.x > rootView!!.measuredWidth / 2

            // landscape, +/- buttons are above and below the text
            else -> e.y < rootView!!.measuredHeight / 2
        }

        if (up) {
            lifeTotal += 1
        } else {
            lifeTotal -= 1
        }

        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        Log.d("onScroll", String.format("e2.x = %f, e2.y = %f", e2.x, e2.y))

        // these algorithm was based off iOS which scales differently
        val density = resources.displayMetrics.density

        val verticalPanDivisor = 7 * density
        val dy = mLastTouchDownY - e2.y // invert the math as pushing up should increase the life total
        if (mLockScrolling != LOCK_HORIZONTAL && (dy < -verticalPanDivisor || dy > verticalPanDivisor)) { // vert pan greater than threshold
            mLockScrolling = LOCK_VERTICAL

            val translateBy = (dy / verticalPanDivisor).toInt()
            lifeTotal = mLastTouchDownLifeTotal + translateBy
        }

        val horizontalPanDivisor = 20 * density
        val dx = e2.x - mLastTouchDownX
        if (mLockScrolling != LOCK_VERTICAL && (dx < -horizontalPanDivisor || dx > horizontalPanDivisor)) { // horz pan greater than threshold
            mLockScrolling = LOCK_HORIZONTAL

            val values = MtgColor.values()
            val translateBy = (dx / horizontalPanDivisor).toInt()
            var newOrdinal = (mLastTouchDownColorOrdinal + translateBy) % values.size
            if (newOrdinal < 0) {
                newOrdinal += values.size
            }
            color = values[newOrdinal]
        }

        return true
    }

    override fun onLongPress(e: MotionEvent) {}

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    // ----- Background -----

    private fun setBackgroundGradient(v: View) {

        val sf = object : ShapeDrawable.ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                val colorSrc = color
                val color2 = colorSrc.lookupColor(MtgColor.PRIMARY)
                val color1 = colorSrc.lookupColor(MtgColor.SECONDARY)

                val sz = Math.max(30, Math.max(width, height))
                val radius = sz.toFloat() * 1.3.toFloat()

                return RadialGradient(0f, 0f, radius, color1, color2, Shader.TileMode.CLAMP)
            }
        }
        val p = PaintDrawable()
        p.shape = RectShape()
        p.shaderFactory = sf

        @Suppress("DEPRECATION")
        v.setBackgroundDrawable(p)
    }

    companion object {

        // to lock in the scrolling
        internal const val LOCK_NONE = 0
        internal const val LOCK_HORIZONTAL = 1
        internal const val LOCK_VERTICAL = 2
    }
}
