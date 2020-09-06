package com.lear.game2048.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.lear.game2048.R;
import com.lear.game2048.model.GameLeaderBoard;
import com.lear.game2048.model.GameScoreModel;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.sql.SQLHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * sql工具类
 * 负责增删改查
 */
public class SQLUtils {

    public static final String TAG = "SQLUtils";

    /**
     * 获取单例
     *
     * @param context 上下文
     * @return SQLHelper
     */
    public static SQLHelper getSQLHelper(Context context) {
        return SQLHelper.getInstance(context);
    }

    /**
     * 获取所有游戏
     *
     * @param context 上下文
     * @return List
     */
    public static List<GameTypeModel> getGameTypeData(Context context) {
        List<GameTypeModel> list = new ArrayList<>();
        Resources resources = context.getResources();
        SQLiteDatabase db = getSQLHelper(context).getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(resources.getString(R.string.sql_table_game), new String[]{"*"},
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                GameTypeModel model = DataConversion.toGameTypeMode(context, cursor);
                list.add(model);
            }

        } finally {
            close(db, cursor);
        }

        return list;
    }

    /**
     * 获取所有游戏的排行榜
     *
     * @param context 上下文
     * @return List
     */
    public static List<GameLeaderBoard> getGameLeaderBoard(Context context) {
        return getGameLeaderBoard(context, null);
    }

    /**
     * 获取某个游戏的排行榜
     *
     * @param context  上下文
     * @param gameName 内容
     * @return List
     */
    public static List<GameLeaderBoard> getGameLeaderBoard(Context context, @Nullable String gameName) {

        List<GameLeaderBoard> list = new ArrayList<>();
        Resources res = context.getResources();
        SQLiteDatabase db = SQLHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = null;

        String selectWhere;
        String[] selectArgs;
        if (gameName != null) {
            selectWhere = res.getString(R.string.key_game_name) + "=?";
            selectArgs = new String[]{gameName};
        } else {
            selectWhere = "";
            selectArgs = null;
        }

        try {
            cursor = db.query(res.getString(R.string.sql_table_leader_board), new String[]{"*"},
                    selectWhere, selectArgs, null, null
                    , res.getString(R.string.key_fraction));

            while (cursor.moveToNext()) {
                GameLeaderBoard lb = new GameLeaderBoard();
                lb.setId(getInt(cursor, res.getString(R.string.key_id)));
                lb.setUserName(getString(cursor, res.getString(R.string.key_user_name)));
                lb.setGameName(getString(cursor, res.getString(R.string.key_game_name)));
                lb.setFraction(getInt(cursor, res.getString(R.string.key_fraction)));
                lb.setDate(getString(cursor, res.getString(R.string.key_date)));
            }

        } finally {
            close(db, cursor);
        }

        return list;
    }

    /**
     * 获取所有游戏名
     *
     * @param context 上下文
     * @return list
     */
    public static List<String> getGameNames(Context context) {
        List<GameTypeModel> models = getGameTypeData(context);
        List<String> strings = new ArrayList<>();
        for (GameTypeModel model : models) strings.add(model.getName());
        return strings;
    }

    /**
     * 插入新游戏
     *
     * @param context 上下文
     * @param game    游戏
     * @return 插入id
     */
    public static long insertNewGame(Context context, @NonNull GameTypeModel game) {
        long id;
        Resources res = context.getResources();
        SQLiteDatabase db = SQLHelper.getInstance(context).getDatabase();

        ContentValues values = DataConversion.toContentValues(context, game);
        db.beginTransaction();
        try {
            id = db.insert(res.getString(R.string.sql_table_game), null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close(db, null);
        }

        return id;
    }

    /**
     * 插入默认游戏
     * 只能用于初始化
     *
     * @param context 上下文
     * @param games   游戏组
     * @return 影响行数
     */
    private static long initInsertDefaultGame(Context context, @NonNull List<GameTypeModel> games) {
        long number = 0;
        Resources res = context.getResources();
        SQLiteDatabase db = SQLHelper.getInstance(context).getDatabase();

        //开启事务
        db.beginTransaction();
        try {
            for (GameTypeModel game : games) {
                ContentValues value = DataConversion.toContentValues(context, game);
                long i = db.insert(res.getString(R.string.sql_table_game),
                        null, value);
                if ((i != -1)) {
                    number++;
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception ignored) {
            return -1;
        } finally {
            //结束事务
            db.endTransaction();
            close(db, null);
        }
        return number;
    }

    /**
     * 删除游戏
     *
     * @param context 上下文
     * @param game    游戏
     * @return 影响行数
     */
    public static int deleteGame(Context context, @NonNull GameTypeModel game) {
        Resources res = context.getResources();
        SQLiteDatabase db = SQLHelper.getInstance(context).getDatabase();
        int number = -1;
        if (game.getId() == -1) return number;
        db.beginTransaction();
        try {
            number = db.delete(res.getString(R.string.sql_table_game),
                    "id=?", new String[]{String.valueOf(game.getId())});
            int s = deleteScore(context, game.getName());
            if (s == -1) return -1;
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close(db, null);
        }
        return number;

    }

    /**
     * 修改游戏
     *
     * @param context 上下文
     * @param game    游戏
     * @return 影响行数
     */
    public static long updateGame(Context context, @NonNull GameTypeModel game) {
        Resources res = context.getResources();
        SQLiteDatabase db = SQLHelper.getInstance(context).getDatabase();
        long number;
        db.beginTransaction();
        try {
            number = db.update(res.getString(R.string.sql_table_game),
                    DataConversion.toContentValues(context, game), "id=?",
                    new String[]{String.valueOf(game.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close(db, null);
        }
        return number;

    }

    /**
     * 插入新的分数
     *
     * @param context 上下文
     * @param game    游戏类型
     * @param score   分数
     * @return 返回插入的id，如果插入失败则返回-1，并回滚
     */
    @SuppressLint("SimpleDateFormat")
    public static long insertNewScore(Context context, @NonNull GameTypeModel game, String userName, int score) {
        SQLiteDatabase db = SQLHelper.getInstance(context).getDatabase();
        long id;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("user_name", userName);
            values.put("game_name", game.getName());
            values.put("fraction", score);
            values.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            id = db.insert(context.getString(R.string.sql_table_leader_board), null, values);
            if (id == -1) return -1;

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close(db, null);
        }
        return id;

    }

    /**
     * 获取所有得分
     *
     * @param context 上下文
     * @return 所有得分
     */
    public static List<GameScoreModel> getAllScore(Context context) {
        Log.i(TAG, "getAllScore: 调用");
        List<GameScoreModel> list = new ArrayList<>();

        SQLiteDatabase db = SQLHelper.getInstance(context).getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(context.getString(R.string.sql_select_all_score), null);
            if (cursor == null) return list;

            while (cursor.moveToNext()) {
                String userName = cursor.getString(cursor.getColumnIndex(context.getString(R.string.key_user_name)));
                String gameName = cursor.getString(cursor.getColumnIndex(context.getString(R.string.key_game_name)));
                int score = cursor.getInt(cursor.getColumnIndex(context.getString(R.string.key_fraction)));
                String date = cursor.getString(cursor.getColumnIndex(context.getString(R.string.key_date)));
                final GameScoreModel model = new GameScoreModel(userName, gameName, score, date);
                list.add(model);
            }

        } finally {
            close(db, cursor);
        }

        return list;
    }

    /**
     * 删除分数
     *
     * @param context 上下文
     * @param name    游戏名
     * @return 影响行数
     */
    public static int deleteScore(Context context, String name) {
        int line;
        Resources resources = context.getResources();
        SQLiteDatabase db = SQLHelper.getInstance(context).getDatabase();
        db.beginTransaction();
        try {
            line = db.delete(resources.getString(R.string.sql_table_leader_board), "?=?",
                    new String[]{resources.getString(R.string.key_game_name), name});
            if (line == -1) return line;
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close(db, null);
        }
        return line;
    }

    /**
     * 初始化游戏
     *
     * @param context 上下文
     */
    public static void initGame(Context context) {
        List<GameTypeModel> list = new ArrayList<>();
        Resources res = context.getResources();

        //经典模式
        JsonObject classicJson = new JsonObject();
        for (int i = 1; i < 12; i++) {
            classicJson.addProperty(String.valueOf(i), String.valueOf((int) Math.pow(2, i)));
        }

        GameTypeModel classic = new GameTypeModel();
        classic.setMode(GameTypeModel.MODE_ClASSIC)
                .setDisplayType(GameTypeModel.DISPLAY_TEXT)
                .setName(res.getString(R.string.mode_sclassic))
                .setCheckerBoardMax(4)
                .setContent(classicJson.toString())
                .setMaxLevel(11);
        list.add(classic);

        //图片模式
        JsonObject pictureJson = new JsonObject();
        AssetManager manager = context.getAssets();
        try {
            InputStream is = manager.open("picture_mode_default_image.jpg");
            String path = FileUtils.getGameImageDataDirectoryPath(context);
            File file = new File(path, "picture_mode_default_image.png");
            if (path != null && (file.exists() || file.createNewFile())) {

                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(bytes)) != -1) {
                    fos.write(bytes, 0, byteCount);
                }

                fos.flush();
                fos.close();
                is.close();
//                manager.close();

                pictureJson.addProperty("bitmap", file.getAbsolutePath());
                for (int i = 0; i < 12; i++) {
                    final JsonObject object = new JsonObject();
                    object.addProperty("left", 0);
                    object.addProperty("top", String.valueOf(i * 150));
                    object.addProperty("right", 150);
                    object.addProperty("bottom", ((i + 1) * 150));

                    pictureJson.addProperty(String.valueOf((i + 1)), object.toString());
                }

                GameTypeModel pictureMode = new GameTypeModel();
                pictureMode.setMode(GameTypeModel.MODE_ClASSIC)
                        .setDisplayType(GameTypeModel.DISPLAY_IMG)
                        .setName(res.getString(R.string.mode_picture))
                        .setCheckerBoardMax(4)
                        .setContent(pictureJson.toString())
                        .setMaxLevel(12);

                list.add(pictureMode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        initInsertDefaultGame(context, list);
    }

    /**
     * 关闭数据库连接与游标
     *
     * @param db     数据库连接
     * @param cursor 游戏
     */
    public static void close(@Nullable SQLiteDatabase db, @Nullable Cursor cursor) {
//        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }

    public static int getInt(Cursor cursor, String key) {
        return cursor.getInt(cursor.getColumnIndex(key));
    }

    public static String getString(Cursor cursor, String key) {
        return cursor.getString(cursor.getColumnIndex(key));
    }

}
