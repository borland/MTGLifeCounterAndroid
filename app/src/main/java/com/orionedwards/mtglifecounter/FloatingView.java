package com.orionedwards.mtglifecounter;

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
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class FloatingView extends RelativeLayout {
    final @NonNull View mInnerView;

    public float pxToDp(float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }

    public FloatingView(Context context, @NonNull View innerView, Float cornerRadius) {
        super(context);

        mInnerView = innerView;

        float padding = pxToDp(8);
        setPadding((int)padding, (int)padding, (int)padding, (int)padding);

        LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        p.addRule(CENTER_IN_PARENT, TRUE);
        addView(innerView, p);

        float minVal = pxToDp(40);
        setMinimumWidth((int)minVal); // try to be a square
        setMinimumHeight((int)minVal);

        // setup rounded corners
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(pxToDp(cornerRadius));
        bg.setColor(Color.BLUE);
        setBackground(bg);
    }

    @TargetApi(21) // lollipop, android 5.0
    public void showInView(@NonNull RelativeLayout parent, RelativeLayout.LayoutParams layoutParams) {
        if(getParent() == null) {
            parent.addView(this, layoutParams);
            setAlpha(1f); // doesn't animate, it just pops in
            if(Build.VERSION.SDK_INT >= 21) {
                setElevation(4);
            }
        }
    }

    public void remove() {
        removeView(mInnerView);
        ((ViewGroup)getParent()).removeView(this);
    }
}
