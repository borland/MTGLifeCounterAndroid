package com.orionedwards.mtglifecounter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.BoolRes;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

public class PlayerFragment extends Fragment implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private View mView;
    private TextView mLifeTotalLabel;

    private final GestureDetectorCompat mGestureDetector;

    private LifeTotalDeltaTracker mTracker = new LifeTotalDeltaTracker();
    private String mPlayerName = "";
    private int mLifeTotal = 0;
    private Boolean mIsUpsideDown = false;
    private MtgColor mColor = MtgColor.White;

    public PlayerFragment() {
        // Required empty public constructor
        mGestureDetector = new GestureDetectorCompat(this.getContext(), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_player, container, false);
        mView.setOnTouchListener(this);

        mLifeTotalLabel = (TextView)mView.findViewById(R.id.lifeTotal);
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
        mLifeTotal = value;
        mTracker.update(value);

        mLifeTotalLabel.setText(String.format("%d",mLifeTotal));
    }

    public MtgColor getColor() {
        return mColor;
    }

    public void setColor(MtgColor value) {
        mColor = value;
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

    // ----- Gesture Detecting -----
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
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
        Log.i("onScroll", String.format("dX = %f, dY = %f", distanceX, distanceY));
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) { }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}

class HistoryTuple {
    public Date when;
    public int lifeTotal;

    HistoryTuple(int lifeTotal) {
        this.lifeTotal = lifeTotal;
        this.when = new Date();
    }
}

class LifeTotalDeltaTracker {
    final int FLOATING_VIEW_FONT_SIZE = 44;
    final double FLOATING_VIEW_TIMEOUT = 1.7;
    final double FLOATING_VIEW_HIDE_TIME = 0.25;

    private int mBaseline = 0; // we show +/- x relative to this
    private LinkedList<HistoryTuple> mHistory = new LinkedList<>();
    private View mFloatingView = null;
    private Callable mCancelPreviousDelay = null;
    private View mParent = null;

    LifeTotalDeltaTracker() {

    }

    public View getParent() {
        return mParent;
    }

    public void setParent(View parent) {
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

    private void updateUi(int lifeTotal) {

    }

}
