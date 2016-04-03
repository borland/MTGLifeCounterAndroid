package com.orionedwards.mtglifecounter;


import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

class HistoryTuple {
    public Date when;
    public int lifeTotal;

    HistoryTuple(int lifeTotal) {
        this.lifeTotal = lifeTotal;
        this.when = new Date();
    }
}

public class LifeTotalDeltaTracker {
    final int STANDARD_MARGIN = 16; // dp?
    final float FLOATING_VIEW_FONT_SIZE = 44f;
    final double FLOATING_VIEW_TIMEOUT = 1.7;
    final double FLOATING_VIEW_HIDE_TIME = 0.25;

    private final @NonNull Context mContext;
    private int mBaseline = 0; // we show +/- x relative to this
    private LinkedList<HistoryTuple> mHistory = new LinkedList<>();
    private FloatingView mFloatingView = null;
    private final TextView mLabel;
    private Runnable mCancelPreviousDelay = null;
    private RelativeLayout mParent = null;

    LifeTotalDeltaTracker(@NonNull Context context) {
        mContext = context;
        mLabel = new TextView(context);
        mLabel.setTextSize(FLOATING_VIEW_FONT_SIZE);
        mLabel.setTextColor(Color.WHITE);
    }

    public RelativeLayout getParent() {
        return mParent;
    }

    public void setParent(RelativeLayout parent) {
        mParent = parent;
    }

    public void update(int lifeTotal) {
        if(!mHistory.isEmpty()) {
            HistoryTuple t = mHistory.getLast();
            if(t.lifeTotal == lifeTotal) {
                return; // no point recording a duplicate
            }
        }

        HistoryTuple nt = new HistoryTuple(lifeTotal);
        mHistory.addLast(nt);
        updateUi(lifeTotal);
    }

    public void reset(int lifeTotal) {
        mHistory.clear();
        mBaseline = lifeTotal;
        updateUi(lifeTotal);
    }

    public static Runnable delay(double seconds, @NonNull final Runnable callback) {
        final Handler handler = new Handler();
        final boolean[] capture = { false };

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(capture[0] == true) {
                    return; // canceled
                }
                callback.run();
            }
        }, (long) (seconds * 1000));

        // return a Runnable which cancels the delayed action if it hasn't already run
        return new Runnable() {
            @Override
            public void run() {
                capture[0] = true;
            }
        };
    }

    private void updateUi(int lifeTotal) {
        String symbol = (lifeTotal - mBaseline >= 0) ? "+" : "";
        mLabel.setText(String.format(Locale.getDefault(), "%s%d", symbol, lifeTotal - mBaseline));
        showOrExtendView();
    }

    private void showOrExtendView() {
        if(mParent != null && mFloatingView == null && mHistory.size() > 1) {
            FloatingView fv = new FloatingView(mContext, mLabel, FLOATING_VIEW_FONT_SIZE / 5);
            fv.setBackgroundColor(Color.BLUE);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            params.setMargins(STANDARD_MARGIN,STANDARD_MARGIN,STANDARD_MARGIN,STANDARD_MARGIN);

            fv.showInView(mParent, params);

            mFloatingView = fv;
        }

        if(mCancelPreviousDelay != null) {
            mCancelPreviousDelay.run();
        }
        mCancelPreviousDelay = delay(FLOATING_VIEW_TIMEOUT, new Runnable() {
            @Override
            public void run() {
                if(mHistory.size() < 1) {
                    return;
                }
                HistoryTuple t = mHistory.getLast();
                mBaseline = t.lifeTotal;

                mHistory.clear();

                if(mFloatingView != null) {
                    // Animate this away with FLOATING_VIEW_HIDE_TIME

                    mFloatingView.setAlpha(0);
                    mFloatingView.remove();
                    mFloatingView = null;
                }
            }
        });
    }
}