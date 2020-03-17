package com.mask.photo.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.mask.photo.interfaces.PuzzleCallback;
import com.mask.photo.interfaces.SaveBitmapCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Bitmap Utils
 * Created by lishilin on 2020/03/16
 */
public class BitmapUtils {

    /**
     * 回收Bitmap
     *
     * @param bitmap Bitmap
     */
    public static void recycle(Bitmap bitmap) {
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        }
    }

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
        if (bitmap == null) {
            callback.onFail(new NullPointerException("Bitmap不能为null"));
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建保存路径
                File dirFile = FileUtils.getCachePhotoDir(activity);
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
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 添加水印到Bitmap
     *
     * @param bitmap     bitmap
     * @param baseWidth  基础数据(用于计算等比例后的真实数据)
     * @param baseHeight 基础数据(用于计算等比例后的真实数据)
     * @param text       水印文本
     * @param textColor  水印颜色
     * @param textSize   水印文字大小
     * @param offsetX    水印X轴偏移量
     * @param offsetY    水印Y轴偏移量
     * @param isLeft     是否在左边
     */
    public static void addWatermark(Bitmap bitmap, float baseWidth, float baseHeight, String text, int textColor, float textSize, float offsetX, float offsetY, boolean isLeft) {
        if (bitmap == null) {
            return;
        }

        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        if (TextUtils.isEmpty(text)) {
            return;
        }

        // 计算成比例的数据(相对宽高取最小值)
        float ratioWidth = width / baseWidth;
        float ratioHeight = height / baseHeight;
        float ratio = Math.min(ratioWidth, ratioHeight);
        textSize = textSize * ratio;
        offsetX = offsetX * ratio;
        offsetY = offsetY * ratio;

        // 绘制水印文字
        Canvas canvas = new Canvas(bitmap);
        Paint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);

        float textX;
        if (isLeft) {
            textPaint.setTextAlign(Paint.Align.LEFT);

            textX = offsetX;
        } else {
            textPaint.setTextAlign(Paint.Align.RIGHT);

            textX = width - offsetX;
        }
        float textY = height - offsetY - rect.height() - rect.top;
        canvas.drawText(text, textX, textY, textPaint);
    }

    /**
     * 拼接图片
     *
     * @param fileList fileList
     * @param callback 回调
     */
    public static void puzzleFile(final List<File> fileList, final PuzzleCallback callback) {
        if (fileList == null || fileList.isEmpty()) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Bitmap> bitmapList = new ArrayList<>();
                for (File file : fileList) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if (bitmap == null) {
                        continue;
                    }
                    bitmapList.add(bitmap);
                }

                callback.onSuccess(puzzleBitmap(bitmapList));
            }
        }).start();
    }

    /**
     * 拼接图片
     *
     * @param activity activity
     * @param uriList  uriList
     * @param callback callback
     */
    public static void puzzleUri(final Activity activity, final List<Uri> uriList, final PuzzleCallback callback) {
        if (uriList == null || uriList.isEmpty()) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Bitmap> bitmapList = new ArrayList<>();
                for (Uri uri : uriList) {
                    Bitmap bitmap = null;
                    try (InputStream inputStream = activity.getContentResolver().openInputStream(uri)) {
                        bitmap = BitmapFactory.decodeStream(inputStream);
                    } catch (Exception ignored) {
                    }
                    if (bitmap == null) {
                        continue;
                    }
                    bitmapList.add(bitmap);
                }

                callback.onSuccess(puzzleBitmap(bitmapList));
            }
        }).start();
    }

    /**
     * 拼接图片
     *
     * @param bitmapList bitmapList
     */
    public static Bitmap puzzleBitmap(List<Bitmap> bitmapList) {
        if (bitmapList == null || bitmapList.isEmpty()) {
            return null;
        }

        // 计算画布宽高
        int width = 0;
        int height = 0;
        for (Bitmap bitmap : bitmapList) {
            width = Math.max(width, bitmap.getWidth());
            height += bitmap.getHeight();
        }

        // 创建画布
        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);

        // 拼接图片
        int top = 0;
        for (Bitmap bitmap : bitmapList) {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            float left = (width - bitmapWidth) / 2.0f;
            canvas.drawBitmap(bitmap, left, top, null);

            top += bitmapHeight;
        }

        return resultBitmap;
    }

}
