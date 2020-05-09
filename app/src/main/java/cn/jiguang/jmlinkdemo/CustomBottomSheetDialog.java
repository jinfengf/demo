package cn.jiguang.jmlinkdemo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.NonNull;

public class CustomBottomSheetDialog extends BottomSheetDialog {

    public CustomBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(1000, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
