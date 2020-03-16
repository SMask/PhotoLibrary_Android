package com.mask.photolibrary;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mask.photo.interfaces.SaveBitmapCallback;
import com.mask.photo.utils.BitmapUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg_screenshot;
    private Button btn_screenshot;
    private View layout_root;
    private View layout_group_1;
    private View layout_group_2;
    private View layout_group_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        rg_screenshot = findViewById(R.id.rg_screenshot);
        btn_screenshot = findViewById(R.id.btn_screenshot);
        layout_root = findViewById(R.id.layout_root);
        layout_group_1 = findViewById(R.id.layout_group_1);
        layout_group_2 = findViewById(R.id.layout_group_2);
        layout_group_3 = findViewById(R.id.layout_group_3);
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
    }

    /**
     * 截图
     */
    private void doScreenshot() {
        View view;
        String filePrefix;
        switch (rg_screenshot.getCheckedRadioButtonId()) {
            case R.id.rb_view_root:
                view = layout_root;
                filePrefix = "ViewRoot";
                break;
            default:
            case R.id.rb_view_group_1:
                view = layout_group_1;
                filePrefix = "ViewGroup1";
                break;
            case R.id.rb_view_group_2:
                view = layout_group_2;
                filePrefix = "ViewGroup2";
                break;
            case R.id.rb_view_group_3:
                view = layout_group_3;
                filePrefix = "ViewGroup3";
                break;
        }

        BitmapUtils.saveBitmapToFile(this, BitmapUtils.getBitmap(view), filePrefix, new SaveBitmapCallback() {
            @Override
            public void onSuccess(File file) {
                super.onSuccess(file);

                LogUtil.i("onSuccess: " + file.getAbsolutePath());

                Toast.makeText(getApplication(), getString(R.string.content_screenshot_result, file.getAbsolutePath()), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(Exception e) {
                super.onFail(e);

                e.printStackTrace();
            }
        });
    }
}
