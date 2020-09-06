package com.lear.game2048.utils;

/**
 * 数学计算类
 */
public class MathCalculationUtils {

    //方向
    public static final int DIRECTION_TOP = 0;
    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_BOTTOM = 2;
    public static final int DIRECTION_LEFT = 3;

    /**
     * 获取两个点之间的角度
     *
     * @param x1 点1的x坐标
     * @param y1 点1的y坐标
     * @param x2 点2的x坐标
     * @param y2 点2的y坐标
     * @return 角度
     */
    public static float getAngle(float x1, float y1, float x2, float y2) {
        double radian = Math.atan2((y2 - y1), (x2 - x1));
        return (float) (radian * (180 / Math.PI));
    }

    /**
     * 获取方向
     *
     * @param angle 角度，范围是0~180与-0~-180
     * @return int
     */
    public static int getDirection(float angle) {
        angle += 180;

        if (angle >= 45 && angle < 135) return DIRECTION_TOP;
        if (angle >= 135 && angle < 225) return DIRECTION_RIGHT;
        if (angle >= 225 && angle < 315) return DIRECTION_BOTTOM;
        return DIRECTION_LEFT;
    }


}
