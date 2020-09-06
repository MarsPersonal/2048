package com.lear.game2048.sql;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lear.game2048.R;

import java.lang.ref.WeakReference;
import java.util.Objects;

import androidx.annotation.Nullable;

/**
 * 数据库帮助类
 */
public class SQLHelper extends SQLiteOpenHelper {

    public static final String TAG = "SQLHelper";
    //版本号
    private static final int VERSION = 1;

    private static WeakReference<Context> sContext;
    private static SQLHelper mInstance = null;
    private SQLiteDatabase mDB;


    private SQLHelper(@Nullable Context context, @Nullable String name,
                      @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.i(TAG, "SQLHelper: 创建");
    }

    /**
     * 获取单例
     *
     * @param context 上下文
     * @return SQLHelper
     */
    public static synchronized SQLHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SQLHelper.class) {
                if (mInstance == null) {
                    mInstance = new SQLHelper(context,
                            context.getResources().getString(R.string.sql_databases_name),
                            null, VERSION);
                }
            }
        }
        sContext = new WeakReference<>(context);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Resources res = sContext.get().getResources();

        //创建游戏表
        db.execSQL(res.getString(R.string.sql_create_game_table));

        //创建排名表
        db.execSQL(res.getString(R.string.sql_create_leader_board_table));

        Log.i(TAG, "onCreate: 数据表创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 获取数据库
     *
     * @return SQLiteDatabase
     */
    public synchronized SQLiteDatabase getDatabase() {
        if (mDB == null) {
            synchronized (SQLHelper.class) {
                if (mDB == null) {
                    mDB = getWritableDatabase();
                }
            }
        }
        return mDB;
    }

}
