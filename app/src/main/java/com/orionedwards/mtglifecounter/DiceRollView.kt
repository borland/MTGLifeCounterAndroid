package com.orionedwards.mtglifecounter

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.UnderlineSpan

import java.util.Locale

object DiceRollView {

    fun create(context: Context, fontSize: Int, num: Int, winner: Boolean): FloatingView {

        val generator = NumberWheelView.Generator { x ->
            if (x == 0) {
                val content = SpannableString(String.format(Locale.getDefault(), "%d", num))
                if (num == 6 || num == 9) {
                    content.setSpan(UnderlineSpan(), 0, content.length, 0)
                }
                return@Generator content
            }

            SpannableString(String.format(Locale.getDefault(), "%d", RandomGen.next(20) + 1))
        }

        val numberView = NumberWheelView(context,
                fontSize.toFloat(), // fontSize
                Color.WHITE, // textColor
                30, // numCells
                generator) // cellGenerator

        val cornerRadius = Util.pxToDp(context, 6f)
        val fv = FloatingView(context, numberView, cornerRadius)
        fv.beforeShow = { callbackDurationMillis -> numberView.spinWithDuration(callbackDurationMillis - 250) }

        if (winner) {
            fv.beforePause = {
                // make it gold pulse
                fv.setBackgroundColor(context.resources.getColor(R.color.winnerBackground))
                fv.pulse()
            }
        }
        return fv
    }

}
