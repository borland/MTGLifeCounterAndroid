package com.orionedwards.mtglifecounter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

import java.util.Locale;

public class DiceRollView {

    public static FloatingView create(final Context context, final int num, final boolean winner) {

        NumberWheelView.Generator generator = new NumberWheelView.Generator() {
            @Override
            public SpannableString generate(int x) {
                if(x == 0) {
                    SpannableString content = new SpannableString(String.format(Locale.getDefault(), "%d", num));
                    if (num == 6 || num == 9) {
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    }
                    return content;
                }

                return new SpannableString(String.format(Locale.getDefault(), "%d", RandomGen.next(20) + 1));
            }
        };

        final NumberWheelView numberView = new NumberWheelView(context, 110, context.getResources().getColor(R.color.white), 30, generator);

        final FloatingView fv = new FloatingView(context, numberView, 120f / 5);
        fv.setBeforeShow(new FloatingView.BeforeShowCallback() {
            @Override
            public void beforeShow(long callbackDurationMillis) {
                numberView.spinWithDuration(callbackDurationMillis - 250);
            }
        });

        if(winner) {
            fv.setBeforePause(new Runnable() {
                @Override
                public void run() {
                    // make it gold and TODO "pulse"
                    fv.setBackgroundColor(Color.YELLOW);
                }
            });
        }
        return fv;
    }

}
