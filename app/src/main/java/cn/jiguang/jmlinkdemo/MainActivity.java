package cn.jiguang.jmlinkdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
//        findViewById(R.id.openScheme).setOnClickListener(this);
//        findViewById(R.id.replay).setOnClickListener(this);
//        findViewById(R.id.params).setOnClickListener(this);
//        findViewById(R.id.models).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        Context context = MainActivity.this.getApplicationContext();
        switch (v.getId()) {
//            case R.id.openScheme:
//                Log.e("60523", "ooooooooooooooooooooooo");
//                intent.setClass(context, SchmeActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.replay:
//                break;
//            case R.id.params:
//                break;
//            case R.id.models:
//                break;
        }
    }
}
