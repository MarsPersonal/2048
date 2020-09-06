package com.lear.game2048.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.lear.game2048.R;

import java.util.Iterator;
import java.util.List;

public class DrawableUtils {
    public static final String TAG = "DrawableUtils";
    private static final int[] COLOR = {Color.RED, Color.BLUE, Color.rgb(255,125,0),
            Color.GRAY, Color.BLACK, Color.MAGENTA};
    private static int POINTER = -1;

    private static int[] BG_COLOR;

    /**
     * 设置颜色
     *
     * @param list View类型的列表
     */
    public static void setBgColor(List<View> list) {
        Iterator<View> it = list.iterator();
        GradientDrawable drawable;
        while (it.hasNext()) {
            drawable = (GradientDrawable) it.next().getBackground();
            setColor(drawable, next());
        }
    }

    /**
     * 设置Drawable颜色
     * 使用此方法修改Drawable颜色
     * 可以防止影响到从Drawable加载的所有实例
     *
     * @param drawable GradientDrawable
     * @param color    颜色
     */
    public static void setColor(GradientDrawable drawable, int color) {
        drawable.mutate();
        drawable.setColor(color);
    }

    /**
     * 重设指针
     */
    public static void reset() {
        POINTER = -1;
    }

    /**
     * 获取下一个颜色
     *
     * @return int
     */
    public static int next() {
        POINTER++;
        if (POINTER >= COLOR.length)
            POINTER = 0;
        return COLOR[POINTER];
    }

    public static int getItemBgColor(int i) {
        if (i < 0) i = 0;
        if (i >= COLOR.length) i %= COLOR.length;
        return COLOR[i];
    }

    /**
     * 获取背景颜色
     *
     * @param context 上下文
     * @return int[]
     */
    public static int[] getBgColor(Context context) {
        if (BG_COLOR == null || BG_COLOR.length == 0) {
            Resources res = context.getResources();
            int size = 22;
            BG_COLOR = new int[size];
            BG_COLOR[0] = res.getColor(R.color.bg_0);
            BG_COLOR[1] = res.getColor(R.color.bg_1);
            BG_COLOR[2] = res.getColor(R.color.bg_2);
            BG_COLOR[3] = res.getColor(R.color.bg_3);
            BG_COLOR[4] = res.getColor(R.color.bg_4);
            BG_COLOR[5] = res.getColor(R.color.bg_5);
            BG_COLOR[6] = res.getColor(R.color.bg_6);
            BG_COLOR[7] = res.getColor(R.color.bg_7);
            BG_COLOR[8] = res.getColor(R.color.bg_8);
            BG_COLOR[9] = res.getColor(R.color.bg_9);
            BG_COLOR[10] = res.getColor(R.color.bg_10);
            BG_COLOR[11] = res.getColor(R.color.bg_11);
            BG_COLOR[12] = res.getColor(R.color.bg_12);
            BG_COLOR[13] = res.getColor(R.color.bg_13);
            BG_COLOR[14] = res.getColor(R.color.bg_14);
            BG_COLOR[15] = res.getColor(R.color.bg_15);
            BG_COLOR[16] = res.getColor(R.color.bg_16);
            BG_COLOR[17] = res.getColor(R.color.bg_17);
            BG_COLOR[18] = res.getColor(R.color.bg_18);
            BG_COLOR[19] = res.getColor(R.color.bg_19);
            BG_COLOR[20] = res.getColor(R.color.bg_20);
            BG_COLOR[21] = res.getColor(R.color.bg_21);
        }
        return BG_COLOR;
    }
}
