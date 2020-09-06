package com.lear.game2048.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * 游戏类型模型
 * 保存一个游戏类型的类
 */
public class GameTypeModel implements Serializable {

    //经典模式
    public static final int MODE_ClASSIC = 0;

    //显示文字
    public static final int DISPLAY_TEXT = 0;
    //显示图片
    public static final int DISPLAY_IMG = 1;


    private int id = -1;
    //对外显示名字
    private String name;
    //游戏模式
    private int mode = -1;
    //显示类型
    private int displayType = -1;
    //显示内容,是Json字符串,需要转化成文本或url
    private String content;
    //棋盘大小
    private int checkerBoardMax;
    //最大等级
    private int maxLevel;

    public GameTypeModel() {
    }

    public int getId() {
        return id;
    }

    public GameTypeModel setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GameTypeModel setName(String name) {
        this.name = name;
        return this;
    }

    public int getMode() {
        return mode;
    }

    public GameTypeModel setMode(int mode) {
        this.mode = mode;
        return this;

    }

    public int getDisplayType() {
        return displayType;
    }

    public GameTypeModel setDisplayType(int displayType) {
        this.displayType = displayType;
        return this;

    }

    public String getContent() {
        return content;
    }

    public GameTypeModel setContent(String content) {
        this.content = content;
        return this;

    }

    public int getCheckerBoardMax() {
        return checkerBoardMax;
    }

    public GameTypeModel setCheckerBoardMax(int checkerBoardMax) {
        this.checkerBoardMax = checkerBoardMax;
        return this;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public GameTypeModel setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("id：").append(getId()).append("\n");

        str.append("显示类型：");
        if (getDisplayType() == DISPLAY_IMG) str.append("图片");
        else if (getDisplayType() == DISPLAY_TEXT) str.append("文字");
        else str.append("未知");
        str.append("\n");

        str.append("游戏内容：").append(getContent());
        str.append("\n");

        str.append("游戏棋盘：").append(getCheckerBoardMax());
        return str.toString();
    }

}
