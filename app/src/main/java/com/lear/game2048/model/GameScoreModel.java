package com.lear.game2048.model;

import androidx.annotation.NonNull;

public class GameScoreModel implements Comparable<GameScoreModel> {

    public String userName;
    public String gameName;
    public int score;
    public String date;

    public GameScoreModel() {
        userName = gameName = date = "";
        score = 0;
    }

    public GameScoreModel(String userName, String gameName, int score, String date) {
        this.userName = userName;
        this.gameName = gameName;
        this.score = score;
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "用户名：" + userName + "\t游戏类型：" + gameName +
                "\t得分：" + score + "\t日期：" + date;
    }

    @Override
    public int compareTo(GameScoreModel o) {
        return Integer.compare(o.score, this.score);
    }
}
