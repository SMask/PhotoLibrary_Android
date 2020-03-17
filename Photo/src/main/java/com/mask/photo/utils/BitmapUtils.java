package com.mask.photo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import androidx.exifinterface.media.ExifInterface;

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
                // 创建保存文件
                final File outFile = new File(dirFile, FileUtils.getDateName(filePrefix) + ".png");

                // 保存文件
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
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
     * 旋转Bitmap
     *
     * @param bitmap bitmap
     * @param file   file
     * @return Bitmap
     */
    public static Bitmap adjustOrientation(Bitmap bitmap, File file) {
        if (file == null) {
            return bitmap;
        }

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file);
        } catch (Exception ignored) {
        }
        if (exif == null) {
            return bitmap;
        }

        return adjustOrientation(bitmap, exif);
    }

    /**
     * 旋转Bitmap
     *
     * @param context context
     * @param bitmap  bitmap
     * @param uri     uri
     * @return Bitmap
     */
    public static Bitmap adjustOrientation(Context context, Bitmap bitmap, Uri uri) {
        if (uri == null) {
            return bitmap;
        }

        ExifInterface exif = null;
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                exif = new ExifInterface(inputStream);
            }
        } catch (Exception ignored) {
        }
        if (exif == null) {
            return bitmap;
        }

        return adjustOrientation(bitmap, exif);
    }

    /**
     * 旋转Bitmap
     *
     * @param bitmap bitmap
     * @param exif   ExifInterface
     * @return Bitmap
     */
    public static Bitmap adjustOrientation(Bitmap bitmap, ExifInterface exif) {
        if (exif == null) {
            return bitmap;
        }

        // 计算旋转角度
        int angle;
        int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch (ori) {
            default:
                angle = 0;
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
        }
        if (angle == 0) {
            return bitmap;
        }

        // 旋转图片
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        recycle(bitmap);
        return result;
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
                    bitmap = adjustOrientation(bitmap, file);
                    bitmapList.add(bitmap);
                }

                callback.onSuccess(puzzleBitmap(bitmapList));
            }
        }).start();
    }

    /**
     * 拼接图片
     *
     * @param context  context
     * @param uriList  uriList
     * @param callback callback
     */
    public static void puzzleUri(final Context context, final List<Uri> uriList, final PuzzleCallback callback) {
        if (uriList == null || uriList.isEmpty()) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Bitmap> bitmapList = new ArrayList<>();
                for (Uri uri : uriList) {
                    Bitmap bitmap = null;
                    try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
                        bitmap = BitmapFactory.decodeStream(inputStream);
                    } catch (Exception ignored) {
                    }
                    if (bitmap == null) {
                        continue;
                    }
                    bitmap = adjustOrientation(context, bitmap, uri);
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
