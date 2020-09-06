package com.lear.game2048.utils;

/**
 * 点击工具类
 */
public class PressedUtils {

    //时间差
    private static final long[] sTimeDifference = {0L, 0L};

    /**
     * 是否在短时间内按下返回按键两次
     *
     * @return 如果在500毫秒内按下返回按钮两次则返回true
     */
    public static boolean isPressedBackTwice() {
        return isPressedBackTwice(500);
    }

    /**
     * 是否在短时间内按下返回按键两次
     *
     * @param time 时间范围，单位毫秒
     * @return 如果在time毫秒内按下返回按钮两次则返回true
     */
    public static boolean isPressedBackTwice(int time) {
        sTimeDifference[0] = sTimeDifference[1];
        sTimeDifference[1] = System.currentTimeMillis();

        return (sTimeDifference[1] - sTimeDifference[0]) <= time;
    }

}
