package cn.jiguang.jmlinkdemo.network;


import java.io.IOException;

import cn.jiguang.jmlinkdemo.common.Constants;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    private static OkHttpClient okHttpClient = new OkHttpClient();
    public static void sendGet(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendPost(String url, String jsonBody, Callback callback) {
        RequestBody requestBody = RequestBody.create(Constants.JSON, jsonBody);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static Response sendPostSync(String url, String jsonBody) throws IOException {
        RequestBody requestBody = RequestBody.create(Constants.JSON, jsonBody);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return okHttpClient.newCall(request).execute();
    }
}
