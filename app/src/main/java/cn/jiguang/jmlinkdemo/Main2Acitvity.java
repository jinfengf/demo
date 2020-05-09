package cn.jiguang.jmlinkdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class Main2Acitvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_acitvity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("69523", "Main2Acitvity onDestroy");
    }
}
