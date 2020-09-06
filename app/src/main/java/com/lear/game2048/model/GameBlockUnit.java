package com.lear.game2048.model;


/**
 * 游戏方块单元
 */
public class GameBlockUnit {

    public static final int STATUS_VOID = 0;        //空的
    public static final int STATUS_NEW = 1;         //新的
    public static final int STATUS_EXISTS = 2;      //存在
    public static final int STATUS_UP = 3;          //升级

    public int status;                  //状态
    public int level;                   //等级

    public int baseX, baseY;            //基础X坐标与基础y坐标
    public int toX, toY;                //目标w坐标与目标y坐标
    public int width, height;
    public float scale;                 //放大系数,最小是0
    public int offset;                  //偏移,移动，缩放共用

    public GameBlockUnit() {
        reset();
    }

    /**
     * 清理
     */
    public void clear() {
        scale = 1.0f;
        toX = toY = 0;
        offset = 0;
    }

    /**
     * 重置
     */
    public void reset() {
        status = STATUS_VOID;
        level = 0;
        baseX = baseY = width = height = 0;
        scale = 1.0f;
        toX = toY = 0;
        offset = 0;
    }
}
