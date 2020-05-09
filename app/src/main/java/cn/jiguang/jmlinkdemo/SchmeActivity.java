package cn.jiguang.jmlinkdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SchmeActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schme);
        findViewById(R.id.chooseScenes).setOnClickListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Log.e("69523", "getDesnity:" + getDesnity(this));
    }

    public static float getDesnity(Context context) {
        float desnity = 0;
        try {
            desnity = context.getResources().getDisplayMetrics().density;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return desnity;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.e("69523", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa;" + item.getItemId());
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseScenes:
                BottomSheetDialog dialog = new BottomSheetDialog(SchmeActivity.this);
                View view = getLayoutInflater().inflate(R.layout.choose_scenes, null);
                ((TextView) view.findViewById(R.id.description)).setText(R.string.schemeScenesDesc);
                view.findViewById(R.id.sceneAK).setOnClickListener(this);
                view.findViewById(R.id.schemeXS).setOnClickListener(this);
                view.findViewById(R.id.cancel).setOnClickListener(this);
                dialog.setContentView(view);
                dialog.show();
                break;
            case R.id.sceneAK:
                Log.e("69523", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                break;
            case R.id.schemeXS:
                Log.e("69523", "bbbbbbbbbbbbbbbbbbbb");
                break;
            default:
        }
    }
}
