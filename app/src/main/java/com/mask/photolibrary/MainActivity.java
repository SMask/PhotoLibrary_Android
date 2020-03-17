package com.mask.photolibrary;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.mask.photo.interfaces.PuzzleCallback;
import com.mask.photo.interfaces.SaveBitmapCallback;
import com.mask.photo.utils.BitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View layout_root;
    private View layout_group_1;
    private View layout_group_2;
    private View layout_group_3;
    private RadioGroup rg_screenshot;
    private Button btn_screenshot;
    private RadioGroup rg_watermark;
    private EditText edt_watermark;
    private Button btn_watermark;
    private Button btn_puzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        layout_root = findViewById(R.id.layout_root);
        layout_group_1 = findViewById(R.id.layout_group_1);
        layout_group_2 = findViewById(R.id.layout_group_2);
        layout_group_3 = findViewById(R.id.layout_group_3);
        rg_screenshot = findViewById(R.id.rg_screenshot);
        btn_screenshot = findViewById(R.id.btn_screenshot);
        rg_watermark = findViewById(R.id.rg_watermark);
        edt_watermark = findViewById(R.id.edt_watermark);
        btn_watermark = findViewById(R.id.btn_watermark);
        btn_puzzle = findViewById(R.id.btn_puzzle);
    }

    private void initData() {

    }

    private void initListener() {
        btn_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doScreenshot();
            }
        });
        btn_watermark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doWatermark();
            }
        });
        btn_puzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPuzzle();
            }
        });
    }

    /**
     * 截图
     */
    private void doScreenshot() {
        View view = getCheckedView();
        String filePrefix = getPrefix();

        saveBitmapToFile(BitmapUtils.getBitmap(view), filePrefix);
    }

    /**
     * 添加水印
     */
    private void doWatermark() {
        Bitmap bitmap = BitmapUtils.getBitmap(getCheckedView());
        boolean isLeft = rg_watermark.getCheckedRadioButtonId() == R.id.rb_watermark_left;
        String text = edt_watermark.getText().toString();

        BitmapUtils.addWatermark(bitmap, 400, 250, text, Color.WHITE, 18, 36, 18, isLeft);

        String filePrefix = getPrefix() + "_水印";

        saveBitmapToFile(bitmap, filePrefix);
    }

    /**
     * 拼接图片
     */
    private void doPuzzle() {
//        String dir = "Photo";
//        File dirFile = new File(getExternalCacheDir(), dir);
//        List<File> fileList = Arrays.asList(dirFile.listFiles());
//        BitmapUtils.puzzleFile(fileList, new PuzzleCallback() {
//            @Override
//            public void onSuccess(Bitmap bitmap) {
//                super.onSuccess(bitmap);
//
//                saveBitmapToFile(bitmap, "Puzzle");
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                super.onFail(e);
//
//                e.printStackTrace();
//            }
//        });

        EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                .setFileProviderAuthority(getPackageName() + ".FileProvider")
                .setCount(22)
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        List<Uri> uriList = new ArrayList<>();
                        for (Photo photo : photos) {
                            if (photo == null) {
                                continue;
                            }
                            uriList.add(photo.uri);
                        }
                        BitmapUtils.puzzleUri(MainActivity.this, uriList, new PuzzleCallback() {
                            @Override
                            public void onSuccess(Bitmap bitmap) {
                                super.onSuccess(bitmap);

                                saveBitmapToFile(bitmap, "Puzzle");
                            }

                            @Override
                            public void onFail(Exception e) {
                                super.onFail(e);

                                e.printStackTrace();
                            }
                        });
                    }
                });
    }

    /**
     * 获取 选中View
     *
     * @return View
     */
    private View getCheckedView() {
        View view;
        switch (rg_screenshot.getCheckedRadioButtonId()) {
            case R.id.rb_view_root:
                view = layout_root;
                break;
            default:
            case R.id.rb_view_group_1:
                view = layout_group_1;
                break;
            case R.id.rb_view_group_2:
                view = layout_group_2;
                break;
            case R.id.rb_view_group_3:
                view = layout_group_3;
                break;
        }
        return view;
    }

    /**
     * 获取 前缀
     *
     * @return View
     */
    private String getPrefix() {
        String prefix;
        switch (rg_screenshot.getCheckedRadioButtonId()) {
            case R.id.rb_view_root:
                prefix = "ViewRoot";
                break;
            default:
            case R.id.rb_view_group_1:
                prefix = "ViewGroup1";
                break;
            case R.id.rb_view_group_2:
                prefix = "ViewGroup2";
                break;
            case R.id.rb_view_group_3:
                prefix = "ViewGroup3";
                break;
        }
        return prefix;
    }

    /**
     * 保存Bitmap到文件
     *
     * @param bitmap     bitmap
     * @param filePrefix 文件前缀名
     */
    private void saveBitmapToFile(Bitmap bitmap, String filePrefix) {
        BitmapUtils.saveBitmapToFile(this, bitmap, filePrefix, new SaveBitmapCallback() {
            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);

                LogUtil.i("onSuccess: " + file.getAbsolutePath());

                Toast.makeText(getApplication(), getString(R.string.content_save_bitmap_result, file.getAbsolutePath()), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(Exception e) {
                super.onFail(e);

                e.printStackTrace();
            }
        });
    }
}
