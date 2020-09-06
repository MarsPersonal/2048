package com.lear.game2048.model;

import android.content.Context;

import com.lear.game2048.utils.SQLUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 游戏数据保存类
 * 用于缓存游戏相关数据
 * 避免直接读取SQLite
 * 加快游戏加载速度
 */
public class GameDataStorage {

    public static final String TAG = "GameDataStorage";
    public static final String KEY_ALL = "ALL";
    public static final String KEY_GAME_TYPE = "GAME_TYPE";
    public static final String KEY_GAME_SCORE = "GAME_SCORE";

    private volatile List<GameTypeModel> mGameTypeModelList = null;
    private volatile List<GameScoreModel> mGameScoreModels = null;

    private static GameDataStorage mInstance;

    private GameDataStorage() {

    }

    /**
     * 更新数据
     *
     * @param context 上下文
     */
    public void update(Context context) {
        update(context, KEY_ALL);
    }

    /**
     * 更新数据
     *
     * @param context 上下文
     * @param key     键
     */
    public void update(Context context, String key) {
        switch (key) {
            case KEY_ALL:
                mGameTypeModelList = SQLUtils.getGameTypeData(context);
                mGameScoreModels = SQLUtils.getAllScore(context);
                break;
            case KEY_GAME_TYPE:
                mGameTypeModelList = SQLUtils.getGameTypeData(context);
                break;
            case KEY_GAME_SCORE:
                mGameScoreModels = SQLUtils.getAllScore(context);
                break;
        }

    }

    /**
     * 获取游戏类型列表
     *
     * @return list
     */
    public List<GameTypeModel> getGameTypeModelList() {
        return mGameTypeModelList;
    }

    /**
     * 获取游戏分数列表
     *
     * @return list
     */
    public List<GameScoreModel> getGameScoreModels() {
        return mGameScoreModels;
    }

    /**
     * 获取游戏分数列表（单一的）
     *
     * @param name 游戏类型的名字
     *             //     * @return  list
     */
    public List<GameScoreModel> getGameScoreModels(String name) {
        final List<GameScoreModel> score = new ArrayList<>();
        for (final GameScoreModel model : getGameScoreModels()) {
            if (model.gameName.equals(name)) score.add(model);
        }

        Collections.sort(score);

        return score;
    }

    /**
     * 获取最高分
     *
     * @param name 游戏名字
     * @return 分数
     */
    public int getMaxScore(String name) {
        List<GameScoreModel> list = getGameScoreModels(name);
        return list != null && list.size() > 0 ? list.get(0).score : 0;
    }

    /**
     * 获取单例
     *
     * @return this
     */
    public static GameDataStorage getInstance() {
        if (mInstance == null) {
            synchronized (GameDataStorage.class) {
                if (mInstance == null) {
                    mInstance = new GameDataStorage();
                }
            }
        }
        return mInstance;
    }

}
