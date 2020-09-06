package com.lear.game2048.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

public class DisplayUtils {


    /**
     * 获取Point
     *
     * @param context 活动
     * @return Point
     */
    public static Point getDisplayPoint(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display;
        if (manager != null) {
            display = manager.getDefaultDisplay();
        } else {
            display = ((Activity) context).getWindowManager().getDefaultDisplay();
        }
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context 活动
     * @return 屏幕宽度
     */
    public static int getWidth(Context context) {
        return getDisplayPoint(context).x;
    }

    /**
     * 获取屏幕高度
     *
     * @param context 活动
     * @return 屏幕高度
     */
    public static int getHeight(Context context) {
        return getDisplayPoint(context).y;
    }

    /**
     * 按比例计算长度
     *
     * @param raw   原数据
     * @param scale 比例,最小是0.0f
     * @return 计算后的长度
     */
    public static int scale(int raw, float scale) {
        return (int) (raw * scale);
    }


}
