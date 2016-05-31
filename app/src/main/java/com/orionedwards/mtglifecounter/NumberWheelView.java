package com.orionedwards.mtglifecounter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import static com.orionedwards.mtglifecounter.Util.pxToDp;

public class NumberWheelView extends RelativeLayout {
    public static interface Generator {
        SpannableString generate(int x);
    }

    private static final int LINE_GAP = 5;
    private static final int OVERSHOOT_ITEM_COUNT = 1;

    private final View mInnerView;
    private final int mNumCells;
    private final float mLineHeight;
    private final float mTotalHeight;

    public NumberWheelView(Context context, float fontSize, @ColorInt int textColor, int numCells, @NonNull Generator generator) {
        super(context);
        mNumCells = numCells;

        // configure the view
        final LinearLayout inner = new LinearLayout(getContext());
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setClipChildren(false);
        inner.setClipToPadding(false);
        RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        inner.setLayoutParams(ilp);

        for(int i = 0; i < numCells + OVERSHOOT_ITEM_COUNT; i++) { // 2 more for overshoot
            TextView tv = new TextView(getContext());
            tv.setText(generator.generate(numCells - 1 - i));
            tv.setTextSize(fontSize);
            tv.setTextColor(textColor);
            tv.setSingleLine(true);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
//            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            tlp.setMargins(0, 0, 0, (int)pxToDp(context, LINE_GAP));
            tv.setLayoutParams(tlp);
            inner.addView(tv);
        }

        inner.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mTotalHeight = inner.getMeasuredHeight();
        mLineHeight = mTotalHeight / (float)(numCells + OVERSHOOT_ITEM_COUNT);

        addView(inner);
        mInnerView = inner;
    }

    public void spinWithDuration(long millis) {
        // we have to animate the layout itself, not just the visual representation
        // as android clips textViews and doesn't redraw them during visual animation

        final int newTopMargin = (int)-(mTotalHeight - mLineHeight * (OVERSHOOT_ITEM_COUNT + 1));
        Animation a = new LayoutTopMarginAnimation(mInnerView, 0, newTopMargin);
        a.setDuration(millis);
        a.setInterpolator(new OvershootInterpolator(0.6f));
        mInnerView.startAnimation(a);
    }

    static class LayoutTopMarginAnimation extends Animation {
        final int mStartValue;
        final int mDiff;
        @NonNull final View mTargetView;

        LayoutTopMarginAnimation(@NonNull View targetView, int startValue, int endValue) {
            mStartValue = startValue;
            mDiff = endValue - startValue;
            mTargetView = targetView;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mTargetView.getLayoutParams();
            lp.topMargin = mStartValue + (int)(mDiff * interpolatedTime);
            mTargetView.setLayoutParams(lp);
        }
    }
}
