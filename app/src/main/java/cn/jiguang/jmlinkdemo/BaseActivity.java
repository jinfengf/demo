package cn.jiguang.jmlinkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle();
    }

    private void initTitle() {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //指定自定义标题栏的布局文件
        setContentView(R.layout.titlebar);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.titlebar);
//获取自定义标题栏的TextView控件并设置内容为传递过来的字符串
        TextView textView = (TextView) findViewById(R.id.mytitle);
        textView.setText("一链拉起");
        //设置返回按钮的点击事件
        ImageButton titleBackBtn = (ImageButton) findViewById(R.id.bt_back);
        titleBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //调用系统的返回按键的点击事件
                finish();
            }
        });
    }
}
