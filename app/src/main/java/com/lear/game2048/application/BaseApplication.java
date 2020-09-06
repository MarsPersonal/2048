package com.lear.game2048.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.lear.game2048.model.GameDataStorage;
import com.lear.game2048.utils.SQLUtils;
import com.lear.game2048.utils.SharedPreferencesUtils;

import androidx.annotation.NonNull;

public class BaseApplication extends Application {

    public static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        final SharedPreferencesUtils sp = SharedPreferencesUtils.getInstance();
        final boolean first = sp.isFirstLoad(this);
        final Context context = this;

        //获取数据
        new Thread(() -> {
            if (first) {
                sp.setFirstLoad(context, false);
                SQLUtils.initGame(context);
            }

            GameDataStorage.getInstance().update(context);
        }).start();

        //让字体大小不跟随系统设置
        getResources().getDisplayMetrics().scaledDensity = getResources().getDisplayMetrics().density;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate: 调试用");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged: 系统配置修改");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "onLowMemory: 回收内存");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i(TAG, "onTrimMemory: 内存等级 - " + level);
    }

}
