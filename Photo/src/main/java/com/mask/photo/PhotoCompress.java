package com.mask.photo;

import android.app.Activity;
import android.text.TextUtils;

import com.mask.photo.interfaces.CompressCallback;
import com.mask.photo.utils.FileUtils;

import java.io.File;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * 图片压缩
 * Create By lishilin On 2020/03/17
 */
public class PhotoCompress {

    /**
     * 私有构造方法(防止创建对象)
     */
    private PhotoCompress() {
        super();
    }

    /**
     * 加载设置项
     */
    public static class Option {

        private final Activity activity;

        private File dirFile;// 保存路径

        private List pathList;// 源路径
        private int minSize = 1024;// 最小压缩大小，小于此大小则不压缩(单位为K)
        private CompressCallback callback;// 回调

        /**
         * 初始化
         *
         * @param activity activity
         */
        private Option(Activity activity) {
            this.activity = activity;
            this.dirFile = FileUtils.getCachePhotoCompressDir(activity);
        }

        /**
         * 源路径(Uri目前有Bug，拿不到真实的文件路径)
         *
         * @param sourcePath pathList(String, File, Uri)
         * @return Option
         */
        public Option sourcePath(List sourcePath) {
            this.pathList = sourcePath;
            return this;
        }

        /**
         * 最小压缩大小，小于此大小则不压缩(单位为K)
         *
         * @param minSize minSize
         * @return Option
         */
        public Option minSize(int minSize) {
            this.minSize = minSize;
            return this;
        }

        /**
         * 启动
         *
         * @param callback callback
         */
        public void start(CompressCallback callback) {
            this.callback = callback;
            start();
        }

        /**
         * 启动
         */
        public void start() {
            Luban.with(activity)
                    .load(pathList)
                    .ignoreBy(minSize)
                    .setTargetDir(dirFile.getAbsolutePath())
                    .setFocusAlpha(true)
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    })
                    .setRenameListener(new OnRenameListener() {
                        @Override
                        public String rename(String filePath) {
                            File sourceFile = new File(filePath);
                            String name = sourceFile.getName();// 文件名
                            String suffix = ".png";// 文件后缀名
                            int index = name.lastIndexOf(".");
                            if (index > 0) {
                                suffix = name.substring(index);
                                name = name.substring(0, index);
                            }
                            return FileUtils.getDateName(name + "_compress") + suffix;
                        }
                    })
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            boolean mkdirs = dirFile.mkdirs();
                            if (callback != null) {
                                callback.onStart();
                            }
                        }

                        @Override
                        public void onSuccess(File file) {
                            if (callback != null) {
                                callback.onSuccess(file);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (callback != null) {
                                callback.onFail(e);
                            }
                        }
                    })
                    .launch();
        }
    }

    /**
     * 初始化
     *
     * @param activity activity
     * @return Option
     */
    public static Option with(Activity activity) {
        return new Option(activity);
    }

}
