package com.lear.game2048.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lear.game2048.architecture.ISimpleFragment;
import com.lear.game2048.architecture.SingleShowFrame;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";
    private ISimpleFragment mFrag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mFrag = new SingleShowFrame(this, onViewId());
    }

    /**
     * 获取Fragment显示的view的id
     *
     * @return int
     */
    protected abstract int onViewId();

    public ISimpleFragment getFrag() {
        return mFrag;
    }

}
