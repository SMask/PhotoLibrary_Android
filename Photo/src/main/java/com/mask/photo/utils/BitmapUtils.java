package com.mask.photo.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.mask.photo.interfaces.SaveBitmapCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Bitmap Utils
 * Created by lishilin on 2020/03/16
 */
public class BitmapUtils {

    /**
     * 获取View Bitmap
     *
     * @param view view
     * @return Bitmap
     */
    public static Bitmap getBitmap(View view) {
        int width = view.getWidth();
        int height = view.getHeight();

        // getDrawingCache()获取Bitmap方法
//        view.setDrawingCacheEnabled(true);
//        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
//        view.destroyDrawingCache();
//        view.setDrawingCacheEnabled(false);

        // draw(canvas)获取Bitmap方法
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    /**
     * 保存Bitmap到文件
     *
     * @param activity   activity
     * @param bitmap     bitmap
     * @param filePrefix 文件前缀名(文件最终名称格式为：前缀名+自动生成的唯一数字字符+.png)
     * @param callback   回调
     */
    public static void saveBitmapToFile(final Activity activity, final Bitmap bitmap, final String filePrefix, final SaveBitmapCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建保存路径
                String dir = "Photo";
                File dirFile = new File(activity.getExternalCacheDir(), dir);
                boolean mkdirs = dirFile.mkdirs();

                FileOutputStream fos = null;
                try {
                    // 创建保存文件
                    final File outFile = File.createTempFile(filePrefix + "_", ".png", dirFile);

                    // 保存文件
                    fos = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(outFile);
                        }
                    });
                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail(e);
                        }
                    });
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

}
