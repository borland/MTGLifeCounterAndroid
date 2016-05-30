package com.orionedwards.mtglifecounter;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.concurrent.Callable;

import static com.orionedwards.mtglifecounter.Util.pxToDp;

public class FloatingView extends RelativeLayout {
    public static final long
            DEFAULT_CALLBACK_MILLIS = 2200,
            DEFAULT_PAUSE_MILLIS = 1400,
            DEFAULT_FADE_MILLIS = 600;

    public interface BeforeShowCallback {
        void beforeShow(long callbackDurationMillis);
    }

    final @NonNull View mInnerView;

    private @Nullable BeforeShowCallback mBeforeShow;
    private @Nullable Runnable mBeforePause;

    public FloatingView(Context context, @NonNull View innerView, Float cornerRadius) {
        super(context);

        mInnerView = innerView;

        float padding = pxToDp(context, 8);
        setPadding((int)padding, (int)padding, (int)padding, (int)padding);

        LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        p.addRule(CENTER_IN_PARENT, TRUE);
        addView(innerView, p);

        float minVal = pxToDp(context, 40);
        setMinimumWidth((int)minVal); // try to be a square
        setMinimumHeight((int)minVal);

        // setup rounded corners
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(pxToDp(context, cornerRadius));
        bg.setColor(Color.BLUE);
        setBackground(bg);
    }

    public void setBeforeShow(BeforeShowCallback value) {
        mBeforeShow = value;
    }

    public void setBeforePause(Runnable value) {
        mBeforePause = value;
    }

    @TargetApi(21) // lollipop, android 5.0 needed for setElevation only
    private void setElevationIfAvailable() {
        if(Build.VERSION.SDK_INT >= 21) {
            setElevation(4);
        }
    }

    public void showInView(@NonNull RelativeLayout parent, @NonNull RelativeLayout.LayoutParams layoutParams) {
        showInView(parent, layoutParams, DEFAULT_CALLBACK_MILLIS, DEFAULT_PAUSE_MILLIS, DEFAULT_FADE_MILLIS);
    }

    public void showInView(@NonNull final RelativeLayout parent, @NonNull final RelativeLayout.LayoutParams layoutParams, long callbackDurationMillis, long pauseDurationMillis, long fadeDurationMillis) {
        if(getParent() != null) {
            return; // already parented, animation mustn't have finished or something
        }

        parent.addView(this, layoutParams);
        setAlpha(1f); // doesn't animate, it just pops in
        setElevationIfAvailable();

        if(mBeforeShow != null) {
            mBeforeShow.beforeShow(callbackDurationMillis);
        }

        if(mBeforePause != null){
            Util.delay(callbackDurationMillis, mBeforePause);
        }

        final FloatingView self = this;
        this.animate()
                .alpha(0)
                .setStartDelay(callbackDurationMillis + pauseDurationMillis)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        parent.removeView(self);
                    }
                })
                .start();
    }

    public void remove() {
        removeView(mInnerView);
        ((ViewGroup)getParent()).removeView(this);
    }
}
