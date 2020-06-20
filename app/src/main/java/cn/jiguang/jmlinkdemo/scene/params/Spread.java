package cn.jiguang.jmlinkdemo.scene.params;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.jiguang.jmlinkdemo.helper.UserInfoHelper;
import cn.jiguang.jmlinkdemo.BaseActivity;
import cn.jiguang.jmlinkdemo.R;
import cn.jiguang.jmlinkdemo.common.Constants;
import cn.jiguang.jmlinkdemo.network.HttpClient;
import cn.jiguang.jmlinkdemo.utils.AndroidUtils;
import cn.jiguang.jmlinkdemo.utils.QRCodeUtil;
import cn.jiguang.jmlinkdemo.utils.dialog.LoadDialog;
import cn.jiguang.jmlinkdemo.model.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Spread extends BaseActivity {
    private static final String TAG = "Spread";
    private static final String TITLE = "地推";
    private LoadDialog loadDialog;
    private static final int TYPE_REPORT = 1;
    private static final int TYPE_GET_NUMBER = 2;
    private static final String URL = "https://arguys.jmlk.co/AAlq";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spread);
        loadDialog = new LoadDialog(Spread.this, false, "");
        initTitle(R.id.toolbar, TITLE, true, R.drawable.refresh_selector, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialog.show();
                refreshNumber(UserInfoHelper.getMyInfo());
            }
        });
        initView();
    }

    private void initView() {
        UserInfo myInfo = UserInfoHelper.getMyInfo();
        if (myInfo != null) {
            ((ImageView) findViewById(R.id.user_avatar)).setImageResource(myInfo.getAvatar());
            ((TextView) findViewById(R.id.user_name)).setText(myInfo.getUsername());
            String id = "ID: " + myInfo.getUserId();
            ((TextView) findViewById(R.id.user_id)).setText(id);
            String url = URL + "?type=3&scene=5&uid=" + myInfo.getUserId() + "&username=" + myInfo.getUsername();
            float desnity = AndroidUtils.getDesnity(this);
            int width = (int) (180 * desnity + 0.5f);
            int height = (int) (180 * desnity + 0.5f);
            ((ImageView) findViewById(R.id.image)).setImageBitmap(QRCodeUtil.createQRCodeBitmap(url, width, height));
            refreshNumber(myInfo);
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.size() > 1) {
                long uid = bundle.getLong("uid");
                if (uid != 0 && uid != myInfo.getUserId()) {
                    report(uid);
                    Toast.makeText(this, "你通过扫描" + bundle.getString("username") + "的二维码下载魔链APP", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void report(long uid) {
        try {
            JSONObject postJson = new JSONObject();
            postJson.put("uid", uid);
            String body = postJson.toString();
            HttpClient.sendPost(Constants.HOST + Constants.SPREAD_REPORT, body, new MyCallback(Spread.this, TYPE_REPORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismiss() {
        if (loadDialog != null) {
            loadDialog.dismiss();
        }
    }

    private void refreshNumber(UserInfo userInfo) {
        if (userInfo != null) {
            HttpClient.sendGet(Constants.HOST + Constants.SPREAD_GET_COUNT + "?uid=" + userInfo.getUserId(),
                    new MyCallback(Spread.this, TYPE_GET_NUMBER));
        } else {
            dismiss();
        }
    }

    private static class MyCallback implements Callback {
        WeakReference<Activity> activityWeakReference;
        int type;
        MyCallback(Activity activity, int type) {
            activityWeakReference = new WeakReference<>(activity);
            this.type = type;
        }
        @Override
        public void onFailure(@Nullable Call call, @Nullable IOException e) {
            if (type == TYPE_GET_NUMBER) {
                Activity activity = activityWeakReference.get();
                if (activity != null) {
                    ((Spread)activity).loadDialog.dismiss();
                }
            }
            if (e != null) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResponse(@Nullable Call call, final Response response) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "type:" + type + ", code:" + response.code());
            }
            if (type == TYPE_GET_NUMBER) {
                final Activity activity = activityWeakReference.get();
                if (response.isSuccessful() && activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String data = response.body() != null ? response.body().toString() : null;
                            if (data != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    int count = jsonObject.getInt("count");
                                    ((TextView) activity.findViewById(R.id.number)).setText(count);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            ((Spread)activity).dismiss();
                        }
                    });
                } else {
                    if (activity != null) {
                        ((Spread)activity).dismiss();
                    }
                }
            }
        }
    }
}
