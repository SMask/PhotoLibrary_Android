package com.mask.photolibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

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


    }
}
