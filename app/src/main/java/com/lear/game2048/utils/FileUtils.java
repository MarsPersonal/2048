package com.lear.game2048.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author: song
 * created on : 2020/8/6 18:56
 * description:文件工具类
 */
public class FileUtils {

    public static final String TAG = "FileUtils";
    public static final String KEY_IMAGES = "IMAGES";

    /**
     * 获取游戏图片数据文件夹路径
     *
     * @param context 上下文
     * @return 如果存在则返回路径，不存在则返回null
     */
    public static String getGameImageDataDirectoryPath(Context context) {

        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final File dir = context.getExternalFilesDir("");

            if (dir != null) file = new File(dir.getAbsolutePath(), KEY_IMAGES);
            else file = new File(context.getFilesDir().getAbsolutePath(), KEY_IMAGES);

        } else {
            file = new File(context.getFilesDir().getAbsolutePath(), KEY_IMAGES);
        }

        /*如果文件不存在且不是文件夹，则创建文件夹，如果失败则返回null*/
        if ((!file.exists() || !file.isDirectory()) && !file.mkdir()) {
            return null;
        }

        return file.getAbsolutePath();
    }

    /**
     * 保存游戏图片文件
     *
     * @param context 上下文
     * @param name    文件名
     * @param bitmap  图片
     * @return File文件
     */
    @NonNull
    public static File saveGameImageFile(Context context, String name, Bitmap bitmap) throws IOException {
        File file = new File(getGameImageDataDirectoryPath(context), name);
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        return file;
    }

}
