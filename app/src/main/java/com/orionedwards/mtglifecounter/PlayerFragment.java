package com.orionedwards.mtglifecounter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.Callable;

public class PlayerFragment extends Fragment implements View.OnTouchListener, GestureDetector.OnGestureListener {

    // to lock in the scrolling
    static final int LOCK_NONE = 0;
    static final int LOCK_HORIZONTAL = 1;
    static final int LOCK_VERTICAL = 2;

    private RelativeLayout mView;
    private TextView mLifeTotalLabel;
    private TextView mPlusButtonLabel;
    private TextView mMinusButtonLabel;

    private GestureDetectorCompat mGestureDetector; // final after onCreateView
    private LifeTotalDeltaTracker mTracker; // final after onCreateView

    private String mPlayerName = "";
    private int mLifeTotal = 0;
    private Boolean mIsUpsideDown = false;
    private MtgColor mColor = MtgColor.White;

    private float mLastTouchDownX = 0;
    private float mLastTouchDownY = 0;
    private int mLastTouchDownColorOrdinal = 0;
    private int mLastTouchDownLifeTotal = 0;
    private int mLockScrolling = LOCK_NONE;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public RelativeLayout getRootView() {
        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MtgColor[] values = MtgColor.values();
        int idx = RandomGen.INSTANCE.next(values.length);
        setColor(values[idx]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (RelativeLayout)inflater.inflate(R.layout.fragment_player, container, false);
        mView.setOnTouchListener(this);
        SetBackgroundGradient(mView);

        mLifeTotalLabel = (TextView)mView.findViewById(R.id.lifeTotal);
        mPlusButtonLabel = (TextView)mView.findViewById(R.id.plus);
        mMinusButtonLabel = (TextView)mView.findViewById(R.id.minus);

        mGestureDetector = new GestureDetectorCompat(getContext(), this);
        mTracker = new LifeTotalDeltaTracker(getContext());
        mTracker.setParent((RelativeLayout)mView);

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // ----- Logic -----

    public int getLifeTotal() {
        return mLifeTotal;
    }

    public void setLifeTotal(int value) {
        if(value == mLifeTotal) {
            return;
        }
        mLifeTotal = value;
        mTracker.update(value);

        mLifeTotalLabel.setText(String.format(Locale.getDefault(), "%d", mLifeTotal));
    }

    public MtgColor getColor() {
        return mColor;
    }

    public void setColor(MtgColor value) {
        if(mColor == value) {
            return;
        }

        if(mColor.equals(MtgColor.White) && !value.equals(MtgColor.White)) {
            setTextColor(Color.WHITE);
        } else if(!mColor.equals(MtgColor.White) && value.equals(MtgColor.White)) {
            setTextColor(Color.BLACK);
        }

        mColor = value;
        if(mView != null) {
            SetBackgroundGradient(mView);
        }
    }

    public boolean getIsUpsideDown() {
        return mIsUpsideDown;
    }

    public void setIsUpsideDown(boolean value) {
        mIsUpsideDown = value;
        if(value) {
            mView.setRotation(180);
        }
    }

    @ColorInt
    public int getTextColor() {
        return mLifeTotalLabel.getCurrentTextColor();
    }

    public void setTextColor(@ColorInt int value) {
        if(mLifeTotalLabel == null || mPlusButtonLabel == null || mMinusButtonLabel == null) {
            return; // view not loaded yet
        }

        ColorStateList csl = new ColorStateList(new int [] [] {
                new int [] {} // all states
        },
                new int [] {
                        value // that color
                });

        mLifeTotalLabel.setTextColor(csl);
        mPlusButtonLabel.setTextColor(csl);
        mMinusButtonLabel.setTextColor(csl);
    }

    /** Sets the lifetotal to `value` without triggering any change trackers */
    public void resetLifeTotal(int value) {
        mLifeTotal = value;
        mTracker.reset(value);

        mLifeTotalLabel.setText(String.format(Locale.getDefault(), "%d", mLifeTotal));
    }

    // ----- Gesture Detecting -----
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mLastTouchDownX = e.getX();
        mLastTouchDownY = e.getY();
        mLastTouchDownColorOrdinal = getColor().ordinal();
        mLastTouchDownLifeTotal = getLifeTotal();
        mLockScrolling = LOCK_NONE;
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

        boolean up = true;
        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180: // phone in portrait - +/- buttons are on the side of the text
                up = e.getX() > (mView.getMeasuredWidth() / 2);
                break;

            default: // landscape, +/- buttons are above and below the text
                up = e.getY() < (mView.getMeasuredHeight() / 2);
                break;
        }

        if(up) {
            setLifeTotal(getLifeTotal() + 1);
        } else {
            setLifeTotal(getLifeTotal() - 1);
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("onScroll", String.format("e2.x = %f, e2.y = %f", e2.getX(), e2.getY()));

        // these algorithm was based off iOS which scales differently
        final float density = getResources().getDisplayMetrics().density;

        final float verticalPanDivisor = 7 * density;
        final float dy = mLastTouchDownY - e2.getY(); // invert the math as pushing up should increase the life total
        if(mLockScrolling != LOCK_HORIZONTAL && (dy < -verticalPanDivisor|| dy > verticalPanDivisor)) { // vert pan greater than threshold
            mLockScrolling = LOCK_VERTICAL;

            int translateBy = (int)(dy / verticalPanDivisor);
            setLifeTotal(mLastTouchDownLifeTotal + translateBy);
        }

        final float horizontalPanDivisor = 20 * density;
        float dx = e2.getX() - mLastTouchDownX;
        if(mLockScrolling != LOCK_VERTICAL && (dx < -horizontalPanDivisor || dx > horizontalPanDivisor)) { // horz pan greater than threshold
            mLockScrolling = LOCK_HORIZONTAL;

            MtgColor[] values = MtgColor.values();
            int translateBy = (int)(dx / horizontalPanDivisor);
            int newOrdinal = (mLastTouchDownColorOrdinal + translateBy) % values.length;
            if (newOrdinal < 0) {
                newOrdinal += values.length;
            }
            setColor(values[newOrdinal]);
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) { }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    // ----- Background -----

    private void SetBackgroundGradient(View v) {
        final View view = v;

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                MtgColor colorSrc = getColor();
                int color2 = colorSrc.lookupColor(MtgColor.PRIMARY);
                int color1 = colorSrc.lookupColor(MtgColor.SECONDARY);

                int sz = Math.max(30, Math.max(width, height));
                float radius = (float)sz * (float)1.3;

                RadialGradient rg = new RadialGradient(0, 0, radius, color1, color2, Shader.TileMode.CLAMP);

                return rg;
            }
        };
        PaintDrawable p = new PaintDrawable();
        p.setShape(new RectShape());
        p.setShaderFactory(sf);
        view.setBackgroundDrawable(p);
    }
}
