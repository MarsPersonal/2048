package com.lear.game2048.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.lear.game2048.view.GameDrawView;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final String TAG = "GestureListener";

    private GameDrawView mGameDrawView;

    public GestureListener(GameDrawView view) {
        this.mGameDrawView = view;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //滑屏
        float angle = MathCalculationUtils.getAngle(e1.getX(), e1.getY(), e2.getX(), e2.getY());
        mGameDrawView.setFlingDirection(angle);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}
