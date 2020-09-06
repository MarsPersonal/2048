package com.lear.game2048.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.ref.WeakReference;


public class SharedPreferencesUtils {

    public static final String TAG = "SharedPreferencesUtils";

    public static final String NAME_APP_CONFIG = "app_config";
    public static final String KEY_APP_LOAD = "key_app_load";
    public static final String KEY_USER_NAME = "USER_NAME";
    public static final String KEY_MUSIC_SWITCH = "MUSIC_SWITCH";
    public static final String KEY_SOUND_EFFECT_SWITCH = "KEY_SOUND_EFFECT_SWITCH";
    private static final String KEY_HIGH_FRAME_RATE_MODE_SWITCH = "HIGH_FRAME_RATE_MODE_SWITCH";

    private static SharedPreferencesUtils instance;

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static SharedPreferencesUtils getInstance() {
        if (instance == null) {
            synchronized (SharedPreferencesUtils.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 获取SharedPreferences
     *
     * @param context 上下文
     * @param name    表名
     * @return SharedPreferences
     */
    public SharedPreferences getSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 获取应用配置SharedPreferences
     *
     * @param context 上下文
     * @return SharedPreferences
     */
    public SharedPreferences getAppConfig(Context context) {
        return getSharedPreferences(context, NAME_APP_CONFIG);
    }

    /**
     * 判断是否是第一次加载
     *
     * @param context 上下文
     * @return 如果应用是第一次加载，就返回true
     */
    public boolean isFirstLoad(Context context) {
        return getAppConfig(context).getBoolean(KEY_APP_LOAD, true);
    }

    /**
     * 设置是否是第一次加载
     *
     * @param context     上下文
     * @param isFirstLoad boolean
     */
    public void setFirstLoad(Context context, boolean isFirstLoad) {
        getAppConfig(context).edit()
                .putBoolean(KEY_APP_LOAD, isFirstLoad)
                .apply();

    }

    /**
     * 设置用户名
     *
     * @param context 上下文
     * @param name    用户名
     */
    public void setUserName(Context context, String name) {
        getAppConfig(context).edit()
                .putString(KEY_USER_NAME, name)
                .apply();

//        Log.i(TAG, "setUserName: " + getUserName(context));
    }

    /**
     * 获取用户名
     *
     * @param context 上下文
     * @return 用户名
     */
    public String getUserName(Context context) {
        return getAppConfig(context).getString(KEY_USER_NAME, "");
    }

    /**
     * 设置音乐开关
     *
     * @param context   上下文
     * @param isChecked 是否打开
     */
    public void setMusicSwitch(Context context, boolean isChecked) {
        getAppConfig(context).edit()
                .putBoolean(KEY_MUSIC_SWITCH, isChecked)
                .apply();
    }

    /**
     * 获取音乐开关
     *
     * @param context 上下文
     * @return 如果音乐打开则返回true
     */
    public boolean getMusicSwitch(Context context) {
        return getAppConfig(context).getBoolean(KEY_MUSIC_SWITCH, true);
    }

    /**
     * 设置音效开关
     * @param context   上下文
     * @param isChecked 是否选中
     */
    public void setSoundEffectSwitch(Context context, boolean isChecked) {
        getAppConfig(context).edit()
                .putBoolean(KEY_SOUND_EFFECT_SWITCH, isChecked)
                .apply();
    }

    /**
     * 获取音效开关
     *
     * @param context 上下文
     * @return 如果开启则返回true
     */
    public boolean getSoundEffectSwitch(Context context) {
        return getAppConfig(context).getBoolean(KEY_SOUND_EFFECT_SWITCH, true);
    }

    /**
     * 设置高帧率模式
     *
     * @param context   上下文
     * @param isChecked 是否打开
     */
    public void setHighFrameRateModeSwitch(Context context, boolean isChecked) {
        getAppConfig(context).edit()
                .putBoolean(KEY_HIGH_FRAME_RATE_MODE_SWITCH, isChecked)
                .apply();
    }

    /**
     * 获取高帧率模式开关
     *
     * @param context 上下文
     * @return 如果高帧率模式打开则返回true
     */
    public boolean getHighFrameRateModeSwitch(Context context) {
        return getAppConfig(context)
                .getBoolean(KEY_HIGH_FRAME_RATE_MODE_SWITCH, true);
    }
}
