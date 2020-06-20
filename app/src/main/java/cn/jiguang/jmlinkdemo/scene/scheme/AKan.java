package cn.jiguang.jmlinkdemo.scene.scheme;

import cn.jiguang.jmlinkdemo.R;
import cn.jiguang.jmlinkdemo.ShareDialog;
import cn.jiguang.jmlinkdemo.BaseActivity;

import android.os.Bundle;
import android.view.View;


public class AKan extends BaseActivity {
    private static final String TITLE = "爱看视频";
    private static final String TEXT = "欢迎使用极光魔链";
    private static final String URL = "https://demo-test.jmlk.co/#/page4?type=1&scene=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akan);
        initTitle(R.id.toolbar, TITLE, true, R.drawable.share_selector, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareDialog(AKan.this, TITLE, TEXT, URL).show();
            }
        });
    }
}
