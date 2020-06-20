package cn.jiguang.jmlinkdemo.scene.params;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.jiguang.jmlinkdemo.BaseActivity;
import cn.jiguang.jmlinkdemo.R;
import cn.jiguang.jmlinkdemo.ShareDialog;
import cn.jiguang.jmlinkdemo.common.Constants;
import cn.jiguang.jmlinkdemo.helper.UserInfoHelper;
import cn.jiguang.jmlinkdemo.model.UserInfo;
import cn.jiguang.jmlinkdemo.network.HttpClient;
import cn.jiguang.jmlinkdemo.utils.SPHelper;
import cn.jiguang.jmlinkdemo.utils.dialog.ConfirmDialog;
import cn.jiguang.jmlinkdemo.utils.dialog.LoadDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Game extends BaseActivity implements View.OnClickListener {
    private static final String TITLE = "游戏邀请";
    private static final String TEXT = "欢迎使用极光魔链";
    private static final String URL = "https://demo-test.jmlk.co/#/page6";
    private LoadDialog loadDialog;
    private static final int TYPE_GET_MEMBER = 1;
    private static final int TYPE_CREATE_ROOM_AND_JOIN = 2;
    private static final int TYPE_JOIN_ROOM = 3;
    private long mRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        loadDialog = new LoadDialog(Game.this, false, "");
        initTitle(R.id.toolbar, TITLE, true, R.drawable.refresh_selector, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialog.show();
                refreshRoomMember();
            }
        });
        initView();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        final long roomId = bundle != null ? bundle.getLong("room_id") : 0L;
        final long originRoomId = SPHelper.getRoomId();
        Log.e("69523", "roomId:" + roomId);
        Log.e("69523", "originRoomId:" + originRoomId);
        if (roomId != 0) {
            if (originRoomId != 0 && originRoomId != roomId) {
                new ConfirmDialog(this, "您已经在游戏房间，确认要退出游戏并加入新的游戏房间吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadDialog.show();
                        mRoomId = roomId;
                        joinRoom();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadDialog.show();
                        mRoomId = originRoomId;
                        refreshRoomMember();
                    }
                }).show();
            } else if (originRoomId == 0) {
                String username = bundle.getString("username");
                new ConfirmDialog(this, username + "邀请你加入房间， 是否加入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadDialog.show();
                        mRoomId = roomId;
                        joinRoom();
                    }
                }, null).show();
            }
        } else if (originRoomId != 0){
            mRoomId = originRoomId;
            loadDialog.show();
            Log.e("69523", "refreshRoomMemberrefreshRoomMember");
            refreshRoomMember();
        }
        findViewById(R.id.create).setOnClickListener(this);
        findViewById(R.id.invite).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create:
                loadDialog.show();
                createAndJoinRoom();
                break;
            case R.id.invite:
                new ShareDialog(Game.this, TITLE, TEXT, URL + "?type=3&scene=6&uid=" + UserInfoHelper.getMyInfo().getUserId()
                        + "&username=" + Uri.encode(UserInfoHelper.getMyInfo().getUsername()) + "&room_id=" + mRoomId).show();
                break;

        }
    }

    private void joinRoom() {
        try {
            JSONObject body = new JSONObject();
            body.put("room_id", mRoomId);
            body.put("uid", UserInfoHelper.getMyInfo().getUserId());
            body.put("username", UserInfoHelper.getMyInfo().getUsername());
            HttpClient.sendPost(Constants.HOST + Constants.GAME_JOIN_ROOM, body.toString(),
                    new MyCallback(Game.this, TYPE_JOIN_ROOM));
        } catch (Exception e) {
            e.printStackTrace();
            dismiss();
        }
    }

    private void createAndJoinRoom() {
        HttpClient.sendGet(Constants.HOST + Constants.GAME_CREATE_ROOM, new MyCallback(Game.this, TYPE_CREATE_ROOM_AND_JOIN));
    }

    private void refreshRoomMember() {
        if (mRoomId != 0) {
            Log.e("69523", "aaaaaaaaaaaaaaaaaaaa");
            HttpClient.sendGet(Constants.HOST + Constants.GAME_GET_MEMBER + "?room_id=" + mRoomId,
                    new MyCallback(Game.this, TYPE_GET_MEMBER));
        } else {
            dismiss();
        }
    }

    private void dismiss() {
        if (loadDialog != null) {
            loadDialog.dismiss();
        }
    }

    private static class MyCallback implements Callback {
        private int type;
        private WeakReference<Activity> activityWeakReference;

        MyCallback(Activity activity, int type) {
            activityWeakReference = new WeakReference<>(activity);
            this.type = type;
        }

        @Override
        public void onFailure(@Nullable Call call, @Nullable IOException e) {
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                ((Game) activity).dismiss();
            }
            if (e != null) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResponse(@Nullable Call call, @Nullable Response response) {
            switch (type) {
                case TYPE_GET_MEMBER:
                    Log.e("69523", "TYPE_GET_MEMBERTYPE_GET_MEMBERTYPE_GET_MEMBER");
                    handleGetMember(response);
                    break;
                case TYPE_CREATE_ROOM_AND_JOIN:
                    handleCreateAndJoinRoom(response);
                    break;
                case TYPE_JOIN_ROOM:
                    handleJoinRoom(response);
                    break;
                default:
                    break;
            }
        }

        private void handleJoinRoom(Response response) {
            final Activity activity = activityWeakReference.get();
            if (response != null && response.isSuccessful() && response.body() != null && activity != null) {
                try {
                    JSONObject result = new JSONObject(response.body().string());
                    final int status = result.getInt("status");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String text;
                            if (status == 0) {
                                SPHelper.setRoomId(((Game) activity).mRoomId);
                                text = "您已加入游戏";
                            } else if (status == 1){
                                SPHelper.setRoomId(((Game) activity).mRoomId);
                                text = "加入房间失败,房间人数已满";
                            } else {
                                text = "加入房间失败";
                            }
                            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 不管加入是否成功.刷新成员
            if (activity != null) {
                ((Game) activity).refreshRoomMember();
            }
        }

        private void handleCreateAndJoinRoom(Response response) {
            final Activity activity = activityWeakReference.get();
            if (response != null && response.isSuccessful() && response.body() != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    final UserInfo myInfo = UserInfoHelper.getMyInfo();
                    long roomId = jsonObject.getLong("room_id");
                    if (activity != null) {
                        ((Game) activity).mRoomId = roomId;
                    }
                    JSONObject body = new JSONObject();
                    body.put("room_id", roomId);
                    body.put("uid", myInfo.getUserId());
                    body.put("username", myInfo.getUsername());
                    Response result = HttpClient.sendPostSync(Constants.HOST + Constants.GAME_JOIN_ROOM, body.toString());
                    if (result.isSuccessful() && result.body() != null) {
                        int status = new JSONObject(result.body().string()).getInt("status");
                        if (status == 0 && activity != null) {
                            SPHelper.setRoomId(roomId);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ImageView) activity.findViewById(R.id.user1_avatar)).setImageResource(myInfo.getAvatar());
                                    ((TextView) activity.findViewById(R.id.user1_name)).setText("我");
                                    ((Game) activity).dismiss();
                                }
                            });
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (activity != null) {
                ((Game) activity).dismiss();
            }
        }

        private void handleGetMember(final Response response) {
            final Activity activity = activityWeakReference.get();
            if (response != null && response.isSuccessful() && response.body() != null && activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Log.e("69523", "json:" + jsonObject.toString());
                            int status = jsonObject.getInt("status");
                            if (status == 0) {
                                JSONArray users = jsonObject.getJSONArray("users");
                                int length = users.length();
                                UserInfo[] userInfos = new UserInfo[length];
                                for (int i = 0; i < length; i++) {
                                    userInfos[i] = new UserInfo(users.getJSONObject(i).getLong("uid"),
                                            users.getJSONObject(i).getString("username"));
                                }
                                switch (length) {
                                    case 3:
                                        ((ImageView) activity.findViewById(R.id.user3_avatar)).setImageResource(userInfos[2].getAvatar());
                                        ((TextView) activity.findViewById(R.id.user3_name)).setText(getName(userInfos[2]));
                                    case 2:
                                        ((ImageView) activity.findViewById(R.id.user2_avatar)).setImageResource(userInfos[1].getAvatar());
                                        ((TextView) activity.findViewById(R.id.user2_name)).setText(getName(userInfos[1]));
                                    case 1:
                                        ((ImageView) activity.findViewById(R.id.user1_avatar)).setImageResource(userInfos[0].getAvatar());
                                        ((TextView) activity.findViewById(R.id.user1_name)).setText(getName(userInfos[0]));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ((Game) activity).dismiss();
                    }
                });
            } else {
                if (activity != null) {
                    ((Game) activity).dismiss();
                }
            }
        }
    }

    private static String getName(UserInfo userInfo) {
        if (UserInfoHelper.getMyInfo().getUserId() == userInfo.getUserId()) {
            return "我";
        }
        return userInfo.getUsername();
    }
}
