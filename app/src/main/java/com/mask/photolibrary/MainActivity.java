package com.mask.photolibrary;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mask.photo.interfaces.SaveBitmapCallback;
import com.mask.photo.utils.BitmapUtils;

import java.io.File;

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
