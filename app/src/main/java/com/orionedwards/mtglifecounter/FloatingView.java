package com.orionedwards.mtglifecounter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class FloatingView extends RelativeLayout {
    final @NonNull View mInnerView;

    public FloatingView(Context context, @NonNull View innerView, Float cornerRadius) {
        super(context);

        mInnerView = innerView;

        setPadding(8,8,8,8);

        LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        p.addRule(CENTER_IN_PARENT, TRUE);
        addView(innerView, p);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void showInView(@NonNull RelativeLayout parent, RelativeLayout.LayoutParams layoutParams) {
        if(getParent() == null) {
            parent.addView(this, layoutParams);
            setAlpha(1f); // doesn't animate, it just pops in
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setElevation(4);
            }
        }
    }

    public void remove() {
        removeView(mInnerView);
        ((ViewGroup)getParent()).removeView(this);
    }
}
