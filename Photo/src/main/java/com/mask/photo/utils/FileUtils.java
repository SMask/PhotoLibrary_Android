package com.mask.photo.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * FileUtils
 * Created by lishilin on 2020/03/17
 */
public class FileUtils {

    private static Date date = new Date();

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault());

    /**
     * 返回带日期的名称
     *
     * @return String
     */
    public static String getDateName() {
        return getDateName(null);
    }

    /**
     * 返回带日期的名称
     *
     * @param prefix 文件名前缀(会自动拼接 _ )
     * @return String
     */
    public static String getDateName(String prefix) {
        date.setTime(System.currentTimeMillis());
        String dateStr = dateFormat.format(date);
        if (!TextUtils.isEmpty(prefix)) {
            return prefix + "_" + dateStr;
        } else {
            return dateStr;
        }
    }

    /**
     * 获取Cache目录
     *
     * @param context context
     * @return File
     */
    public static File getCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * 获取Cache目录 图片
     *
     * @param context context
     * @return File
     */
    public static File getCachePhotoDir(Context context) {
        String dir = Environment.DIRECTORY_PICTURES;
        return new File(getCacheDir(context), dir);
    }

    /**
     * 获取Cache目录 图片压缩
     *
     * @param context context
     * @return File
     */
    public static File getCachePhotoCompressDir(Context context) {
        String dir = "Compress";
        return new File(getCachePhotoDir(context), dir);
    }

}
