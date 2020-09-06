package com.lear.game2048.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;

import com.lear.game2048.R;
import com.lear.game2048.model.GameTypeModel;

public class DataConversion {
    public static final String TAG = "DataConversion";

    /**
     * GameTypeModel转化成ContentValues
     *
     * @param context 上下文
     * @param game    GameTypeModel
     * @return ContentValues
     */
    public static ContentValues toContentValues(Context context, GameTypeModel game) {
        Resources res = context.getResources();
        ContentValues values = new ContentValues();

        if (game.getName() == null || game.getMode() == -1 || game.getDisplayType() == -1
                || game.getContent() == null || game.getCheckerBoardMax() == -1)
            throw new NullPointerException();


        values.put(res.getString(R.string.key_name), game.getName());
        values.put(res.getString(R.string.key_mode), game.getMode());
        values.put(res.getString(R.string.key_display_type), game.getDisplayType());
        values.put(res.getString(R.string.key_content), game.getContent());
        values.put(res.getString(R.string.key_check_board_max), game.getCheckerBoardMax());
        values.put(res.getString(R.string.key_max_level), game.getMaxLevel());
        return values;
    }

    /**
     * Cursor转化成GameTypeModel
     *
     * @param context 上下文
     * @param cursor  游标
     * @return GameTypeModel
     */
    public static GameTypeModel toGameTypeMode(Context context, Cursor cursor) {
        Resources res = context.getResources();
        GameTypeModel model = new GameTypeModel();
        model.setId(cursor.getInt(cursor.getColumnIndex(res.getString(R.string.key_id))));
        model.setName(cursor.getString(cursor.getColumnIndex(res.getString(R.string.key_name))));
        model.setMode(cursor.getInt(cursor.getColumnIndex(res.getString(R.string.key_mode))));
        model.setDisplayType(cursor.getInt(cursor.getColumnIndex(res.getString(R.string.key_display_type))));
        model.setContent(cursor.getString(cursor.getColumnIndex(res.getString(R.string.key_content))));
        model.setCheckerBoardMax(cursor.getInt(cursor.getColumnIndex(res.getString(R.string.key_check_board_max))));
        model.setMaxLevel(cursor.getInt(cursor.getColumnIndex(res.getString(R.string.key_max_level))));
        return model;
    }
}
