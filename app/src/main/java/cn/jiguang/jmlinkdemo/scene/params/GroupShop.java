package cn.jiguang.jmlinkdemo.scene.params;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.jiguang.jmlinkdemo.helper.UserInfoHelper;
import cn.jiguang.jmlinkdemo.BaseActivity;
import cn.jiguang.jmlinkdemo.R;
import cn.jiguang.jmlinkdemo.ShareDialog;
import cn.jiguang.jmlinkdemo.common.Constants;
import cn.jiguang.jmlinkdemo.network.HttpClient;
import cn.jiguang.jmlinkdemo.utils.SPHelper;
import cn.jiguang.jmlinkdemo.utils.dialog.ConfirmDialog;
import cn.jiguang.jmlinkdemo.utils.dialog.LoadDialog;
import cn.jiguang.jmlinkdemo.model.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GroupShop extends BaseActivity {
    private static final String TITLE = "拼团邀请";
    private static final String TEXT = "欢迎使用极光魔链";
    private static final String URL = "https://demo-test.jmlk.co/#/page1";
    private LoadDialog loadDialog;
    private static final int TYPE_JOIN_GROUP = 1;
    private static final int TYPE_GET_GROUP_MEMBER = 2;
    private static final int TYPE_CREATE_AND_JOIN = 3;
    private List<UserInfo> mdatas = new ArrayList<>();
    private long mGroupId;
    private GroupAdapter madapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_shop);
        loadDialog = new LoadDialog(GroupShop.this, false, "");
        initTitle(R.id.toolbar, TITLE, true, R.drawable.refresh_selector, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialog.show();
            }
        });
        RecyclerView recyclerView = findViewById(R.id.group_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        madapter = new GroupAdapter();
        recyclerView.setAdapter(madapter);
        initData();
        findViewById(R.id.createOrInvtite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGroupId != 0) {
                    String url = URL + "?type=3&scene=7&uid=" + UserInfoHelper.getMyInfo().getUserId() +
                            "&username=" + UserInfoHelper.getMyInfo().getUsername() + "&group_id=" + mGroupId;
                    new ShareDialog(GroupShop.this, TITLE, TEXT, url).show();
                } else {
                    loadDialog.show();
                    createAndJoin();
                }
            }
        });
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            final long groupId = bundle.getLong("group_id");
            final long originGroupId = SPHelper.getGroupId();
            if (groupId != 0) {
                if (originGroupId != groupId && originGroupId != 0) {
                    new ConfirmDialog(this, "您已在评团中，是否加入其他拼团", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGroupId = groupId;
                            refreshButton();
                            loadDialog.show();
                            joinGroup();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGroupId = originGroupId;
                            refreshButton();
                            refreshGroup();
                        }
                    }).show();
                } else if (originGroupId == 0){
                    String userName = bundle.getString("username");
                    new ConfirmDialog(this, userName + "邀请你加入拼团，是否加入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGroupId = groupId;
                            refreshButton();
                            loadDialog.show();
                            joinGroup();
                        }
                    }, null);
                }
            } else if (originGroupId != 0){
                mGroupId = originGroupId;
                refreshButton();
                loadDialog.show();
                refreshGroup();
            }
        }
    }

    private void refreshButton() {
        if (mGroupId != 0) {
            ((TextView) findViewById(R.id.createOrInvtite)).setText("邀请其他人加");
        }
    }

    private void dismiss() {
        if (loadDialog != null) {
            loadDialog.dismiss();
        }
    }

    private void updateData(List<UserInfo> userInfos) {
        mdatas.clear();
        mdatas.addAll(userInfos);
        if (madapter != null) {
            madapter.notifyDataSetChanged();
        }
    }

    private void createAndJoin() {
        HttpClient.sendGet(Constants.HOST + Constants.GROUP_CREATE, new MyCallback(GroupShop.this,
                TYPE_CREATE_AND_JOIN));
    }

    private void refreshGroup() {
        if (mGroupId != 0) {
            HttpClient.sendGet(Constants.HOST + Constants.GROUP_GET_MEMBER + "?group_id=" + mGroupId,
                    new MyCallback(GroupShop.this, TYPE_GET_GROUP_MEMBER));
        } else {
            dismiss();
        }
    }

    private void joinGroup() {
        try {
            JSONObject body = new JSONObject();
            body.put("group_id", mGroupId);
            body.put("uid", UserInfoHelper.getMyInfo().getUserId());
            body.put("username", UserInfoHelper.getMyInfo().getUsername());
            HttpClient.sendPost(Constants.HOST + Constants.GROUP_JOIN, body.toString(),
                    new MyCallback(GroupShop.this, TYPE_JOIN_GROUP));
        } catch (Exception e) {
            e.printStackTrace();
            dismiss();
        }
    }

    static class MyCallback implements Callback {
        WeakReference<Activity> weakReference;
        int type;
        MyCallback(Activity activity, int type) {
            weakReference = new WeakReference<>(activity);
            this.type = type;
        }
        @Override
        public void onFailure(@Nullable Call call, @Nullable IOException e) {
            Activity activity = weakReference.get();
            if (activity != null) {
                ((GroupShop) activity).dismiss();
            }
            if (e != null) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResponse(@Nullable Call call, @Nullable Response response) {
            switch (type) {
                case TYPE_JOIN_GROUP:
                    handleJoinGroup(response);
                    break;
                case TYPE_GET_GROUP_MEMBER:
                    handleGetGroupMember(response);
                    break;
                case TYPE_CREATE_AND_JOIN:
                    handleCreateAndJoin(response);
                    break;

            }
        }

        private void handleCreateAndJoin(Response response) {
            final Activity activity = weakReference.get();
            if (response != null && response.isSuccessful() && response.body() != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    long groupId = jsonObject.getLong("group_id");
                    SPHelper.setGroupId(groupId);
                    if (activity != null) {
                        ((GroupShop) activity).mGroupId = groupId;
                        JSONObject body = new JSONObject();
                        body.put("group_id", groupId);
                        body.put("uid", UserInfoHelper.getMyInfo().getUserId());
                        body.put("username", UserInfoHelper.getMyInfo().getUsername());
                        Response result = HttpClient.sendPostSync(Constants.HOST + Constants.GROUP_JOIN, body.toString());
                        if (result.isSuccessful() && result.body() != null) {
                            JSONObject object = new JSONObject(result.body().string());
                            if (object.getInt("status") == 0) {
                                SPHelper.setGroupId(groupId);
                                final List<UserInfo> userInfos = Collections.singletonList(UserInfoHelper.getMyInfo());
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((GroupShop) activity).updateData(userInfos);
                                        ((GroupShop) activity).dismiss();
                                    }
                                });
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (activity != null) {
                    ((GroupShop) activity).dismiss();
                }
            }
        }

        private void handleGetGroupMember(Response response) {
            Activity activity = weakReference.get();
            if (response != null && response.isSuccessful() && response.body() != null && activity != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getInt("status") == 0) {
                        JSONArray array = jsonObject.getJSONArray("users");
                        List<UserInfo> userInfos = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            userInfos.add(new UserInfo(object.getLong("uid"), object.getString("username")));
                        }
                        ((GroupShop) activity).updateData(userInfos);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (activity != null) {
                ((GroupShop) activity).dismiss();
            }
        }


        private void handleJoinGroup(Response response) {
            Activity activity = weakReference.get();
            if (response != null && response.isSuccessful() && response.body() != null && activity != null) {
                try {
                    JSONObject result = new JSONObject(response.body().string());
                    int status = result.getInt("status");
                    if (status == 0) {
                        SPHelper.setGroupId(((GroupShop) activity).mGroupId);
                        Toast.makeText(activity, "您已加入评团", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "加入评团失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 不管加入是否成功.刷新成员
            if (activity != null) {
                ((GroupShop) activity).refreshGroup();
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.group_user_avatar);
            userName = itemView.findViewById(R.id.group_user_name);
        }
    }

    private class GroupAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(
                    GroupShop.this).inflate(R.layout.group_user_item, parent,
                    false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.userAvatar.setImageResource(mdatas.get(0).getAvatar());
            holder.userName.setText(mdatas.get(0).getUsername());
        }

        @Override
        public int getItemCount() {
            return mdatas.size();
        }
    }
}
