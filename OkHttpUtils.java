package com.uiot.video.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.uiot.video.utils.sign.Encrypt_Tea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OkHttpUtils 采用OkHttp封装框架短连接请求,http://square.github.io/okhttp与https://github.com/square/okhttp
 *
 * @author susanyuan
 * @date 2016/9/23 17:44
 */
public class OkHttpUtils {

    private static final long DEFAULT_MILLISECONDS = 10000;
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final String TAG = "OkHttpUtils";
    private static OkHttpUtils mInstance;
    private final Handler mDelivery;
    private OkHttpClient mOkHttpClient;

    private OkHttpUtils() {
        mOkHttpClient = new OkHttpClient();
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 设置超时，毫秒，请求之前设置
     *
     * @param readTimeOut  读取超时，如小于等于0，则默认10000毫秒
     * @param writeTimeOut 写入超时，如小于等于0，则默认10000毫秒
     * @param connTimeOut  连接超时，如小于等于0，则默认10000毫秒
     */
    public static void setTimeout(long readTimeOut, long writeTimeOut, long connTimeOut) {
        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {
            readTimeOut = readTimeOut > 0 ? readTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            getInstance()._setTimeout(readTimeOut, writeTimeOut, connTimeOut);
        }
    }

    /**
     * 同步的Get请求
     *
     * @param url URL
     * @return Response
     * @throws IOException
     */
    public static Response getAsyn(String url) throws IOException {
        return getInstance()._getAsyn(url);
    }

    /**
     * 同步的Get请求
     *
     * @param url URL
     * @return 字符串
     * @throws IOException
     */
    public static String getAsString(String url) throws IOException {
        return getInstance()._getAsString(url);
    }

    /**
     * 异步的Get请求
     *
     * @param url       URL
     * @param callback  回调
     * @param isDecrypt 是否需要解密
     */
    public static void getAsyn(String url, boolean isDecrypt, Callback callback) {
        getInstance()._getAsyn(url, callback, isDecrypt);
    }

    /**
     * 同步的Post请求
     *
     * @param url    URL
     * @param params post的参数
     * @return Response
     * @throws IOException
     */
    public static Response post(String url, Param... params) throws IOException {
        return getInstance()._post(url, params);
    }

    /**
     * 同步的Post请求
     *
     * @param url    URL
     * @param params post的参数
     * @return 字符串
     * @throws IOException
     */
    public static String postAsString(String url, Param... params) throws IOException {
        return getInstance()._postAsString(url, params);
    }

    /**
     * 异步的Post请求
     *
     * @param url       URL
     * @param callback  异步回调
     * @param isDecrypt 是否需要解密
     * @param params    post的参数， Param...
     */
    public static void postAsyn(String url, boolean isDecrypt, Callback callback, Param... params) {
        getInstance()._postAsyn(url, callback, isDecrypt, params);
    }

    /**
     * 异步的Post请求
     *
     * @param url       URL
     * @param callback  异步回调
     * @param isDecrypt 是否需要解密
     * @param params    post的参数，此是使用Map
     */
    public static void postAsyn(String url, boolean isDecrypt, Map<String, String> params, Callback callback) {
        getInstance()._postAsyn(url, callback, isDecrypt, params);
    }

    /**
     * 异步的Post请求，采用Post Streaming方式
     *
     * @param url       URL
     * @param callback  异步回调
     * @param isDecrypt 是否需要解密
     * @param strBody   post的stream的字符串
     */
    public static void postStreamAsyn(String url, boolean isDecrypt, String strBody, Callback callback) {
        getInstance()._postAsyn(url, callback, isDecrypt, strBody);
    }


    //*************对外公布的方法************

    /**
     * 得到OkHttpClient实例
     *
     * @return OkHttpClient
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    private void _setTimeout(long readTimeOut, long writeTimeOut, long connTimeOut) {
        mOkHttpClient.newBuilder()
                .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 同步的Get请求
     *
     * @param url URL
     * @return Response
     */
    private Response _getAsyn(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        return execute;
    }

    /**
     * 同步的Get请求
     *
     * @param url URL
     * @return 字符串
     */
    private String _getAsString(String url) throws IOException {
        Response execute = _getAsyn(url);
        return execute.body().string();
    }

    /**
     * 异步的get请求
     *
     * @param url      URL
     * @param callback Callback
     */
    private void _getAsyn(String url, Callback callback, boolean isDecrypt) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request, isDecrypt);
    }

    /**
     * 同步的Post请求
     *
     * @param url    URL
     * @param params post的参数
     * @return
     */
    private Response _post(String url, Param... params) throws IOException {
        Request request = buildPostRequest(url, params);
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }

    /**
     * 同步的Post请求
     *
     * @param url    URL
     * @param params post的参数
     * @return 字符串
     */
    private String _postAsString(String url, Param... params) throws IOException {
        Response response = _post(url, params);
        return response.body().string();
    }

    /**
     * 异步的post请求
     *
     * @param url      URL
     * @param callback Callback
     * @param params   Param...
     */
    private void _postAsyn(String url, Callback callback, boolean isDecrypt, Param... params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request, isDecrypt);
    }

    /**
     * 异步的post请求
     *
     * @param url      URL
     * @param callback Callback
     * @param params   Map
     */
    private void _postAsyn(String url, Callback callback, boolean isDecrypt, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(callback, request, isDecrypt);
    }

    /**
     * 异步的post请求
     *
     * @param url       URL
     * @param callback  Callback
     * @param isDecrypt 是否需要解密
     * @param strBody   post的stream的字符串
     */
    private void _postAsyn(String url, Callback callback, boolean isDecrypt, String strBody) {
        Request request = buildPostRequest(url, isDecrypt, strBody);
        deliveryResult(callback, request, isDecrypt);
    }

    /**
     * 异步下载文件，注意，URL为文件地址，下载后回调的对象为File
     *
     * @param url          下载文件地址
     * @param destFileDir  本地文件存储的文件夹
     * @param destFileName 文件名
     * @param callback     Callback
     */
    private void _downloadAsyn(final String url, final String destFileDir, final String destFileName, final Callback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Callback finalCallback = callback;
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                sendFailResultCallback(call, e, finalCallback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    File file = saveFile(response, destFileDir, destFileName);
                    //如果下载文件成功，第一个参数为文件
                    sendSuccessResultCallback(file, response, callback);
                } catch (IOException e) {
                    sendFailResultCallback(call, e, finalCallback);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 异步下载图片，注意，URL为图片地址，下载后回调的对象为Bitmap
     *
     * @param url
     * @param callback
     */
    private void _downloadImageAsyn(final String url, final Callback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Callback finalCallback = callback;
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                sendFailResultCallback(call, e, finalCallback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                //如果下载文件成功，第一个参数为文件
                sendSuccessResultCallback(bitmap, response, callback);
            }
        });
    }

    /**
     * 异步下载文件，注意：URL为文件地址，下载后回调的对象为File
     *
     * @param url          下载文件地址
     * @param destFileDir  本地文件存储的文件夹
     * @param destFileName 文件名
     * @param callback     Callback
     */
    public static void downloadAsyn(String url, String destFileDir, String destFileName, Callback callback) {
        getInstance()._downloadAsyn(url, destFileDir, destFileName, callback);
    }

    /**
     * 异步下载图片，注意：URL为图片地址，下载后回调的对象为Bitmap
     *
     * @param url
     * @param callback
     */
    public static void downloadImageAsyn(String url, Callback callback) {
        getInstance()._downloadImageAsyn(url, callback);
    }

    public File saveFile(Response response, String destFileDir, String destFileName) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
//            final long total = response.body().contentLength();
//            long sum = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
//                sum += len;
//                final long finalSum = sum;
//                mDelivery.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        callback.inProgress(finalSum * 1.0f / total, total);
//                    }
//                });
            }
            fos.flush();
            return file;

        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private Request buildPostRequest(String url, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private Request buildPostRequest(String url, boolean isDecrypt, String strBody) {

        LogMsg.i(TAG, "url=" + url + "\nstrBody=" + strBody);
        //json
//        MediaType JSON =MediaType.parse("application/json; charset=utf-8");
//        RequestBody requestBody = RequestBody.create(JSON,json);

        //文件
//        File file = new File("README.md");
//        RequestBody.create(MEDIA_TYPE_STREAM, file)

        final byte[] data = strBody.toString().getBytes();
        if (isDecrypt) {
            /**********TEA加密***********/
            Encrypt_Tea.tea_encrypt_fun(data, data.length);
        }

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, data);

        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private void deliveryResult(Callback callback, Request request, final boolean isDecrypt) {
        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;

        final Callback finalCallback = callback;
        mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailResultCallback(call, e, finalCallback);
                LogMsg.e(TAG, "OkHttp_onFailure:" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String string;
                    if (isDecrypt) {
                        byte[] bytes = response.body().bytes();
                        /*******************TEA解密*********************/
                        Encrypt_Tea.tea_decrypt_fun(bytes, bytes.length);
                        string = new String(bytes);
                    } else {
                        string = response.body().string();
//                            Object o = finalCallback.parseNetworkResponse(response);
                    }
                    sendSuccessResultCallback(string, response, finalCallback);
                    LogMsg.i(TAG, "OkHttp_code:" + response.code() + " onResponse:" + string);
                } catch (Exception e) {
                    sendFailResultCallback(call, e, finalCallback);
                    LogMsg.e(TAG, "OkHttp_onResponse:" + e.toString());
                }
            }
        });
    }

    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback) {
        if (callback == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e);
            }
        });
    }

    public void sendSuccessResultCallback(final Object o, final Response response, final Callback callback) {
        if (callback == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(o, response);
            }
        });
    }

    private Param[] map2Params(Map<String, String> params) {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    public static class Param {
        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public abstract static class Callback<T> {
        /**
         * UI Thread
         *
         * @param progress
         */
//        public void inProgress(float progress) {
//
//        }

        public static Callback CALLBACK_DEFAULT = new Callback() {

//            @Override
//            public Object parseNetworkResponse(Response response) throws Exception {
//                return null;
//            }

            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(Object object, Response response) {
            }
        };

        /**
         * Thread Pool Thread
         */
//        public abstract T parseNetworkResponse(Response response) throws Exception;
        public abstract void onError(Call call, Exception e);

        public abstract void onResponse(Object object, Response response);
    }
}
