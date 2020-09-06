package com.lear.game2048.utils;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

class GestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final String TAG = "GestureListener";

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        //单击
        Log.i(TAG, "onSingleTapUp");
        return false;
    }
}