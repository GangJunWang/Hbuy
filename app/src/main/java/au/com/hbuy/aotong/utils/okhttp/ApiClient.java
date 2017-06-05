package au.com.hbuy.aotong.utils.okhttp;

import android.content.Context;
import android.text.TextUtils;

import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.HasParamsable;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.persistentcookiejar.ClearableCookieJar;
import au.com.hbuy.aotong.utils.persistentcookiejar.PersistentCookieJar;
import au.com.hbuy.aotong.utils.persistentcookiejar.cache.SetCookieCache;
import au.com.hbuy.aotong.utils.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * API 调用工具类
 */
public class ApiClient {
    private static ApiClient single = null;
    private static ClearableCookieJar cookieJar = null;

    public static ApiClient getInstance(Context context) {
        if (null == single) {
            single = new ApiClient(context);
        }
        return single;
    }

    public ApiClient(Context context) {

        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        KLog.d(cookieJar.toString());

        OkHttpClient httpClient = new OkHttpClient.Builder()
                // 读写请求超时
                .connectTimeout(ConfigConstants.CONN_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(ConfigConstants.READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(ConfigConstants.WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)
                       /* .cookieJar(new CookieJar() {
                            @Override
                            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                                KLog.d(url.toString() + "cooikes = " + cookies);
                            }

                            @Override
                            public List<Cookie> loadForRequest(HttpUrl url) {
                                return new ArrayList<Cookie>();
                            }
                        })*/
                        // 失败不重试
//                .retryOnConnectionFailure(false)
                .build();
        OkHttpUtils.initClient(httpClient);
    }

    // 设置请求参数
    private static void setParams(OkHttpRequestBuilder builder, Map<String, String> params) {
        // 添加 cookies
//        if (!TextUtils.isEmpty(GlobalApp.getCookie())) {
//            builder.addHeader("cookie", GlobalApp.getCookie());
//        }
        // 添加参数对
        if (params != null && !params.isEmpty()) {
            if (builder instanceof HasParamsable)
                ((HasParamsable) builder).params(params);
        }
    }

    /**
     * Http GET 同步请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应内容
     * @throws IOException
     */
    public static Response get(String url, Map<String, String> params) throws IOException {
        GetBuilder builder = OkHttpUtils.get()
                .url(url);
        setParams(builder, params);
        return builder.tag(url).build().execute();
    }

    /**
     * Http GET 异步请求
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param callback 响应回调
     */
    public static void get(String url, Map<String, String> params, Callback callback) {
        GetBuilder builder = OkHttpUtils.get()
                .url(url);
        setParams(builder, params);
        builder.tag(url).build().execute(callback);
    }

    /**
     * Http GET 异步请求(无参数)
     *
     * @param url      请求地址
     * @param callback 响应回调
     */
    public static void get(String url, Callback callback) {
        KLog.d(url);
        get(url, null, callback);
    }

    /**
     * Http POST 参数异步请求
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param callback 响应回调
     */
    public static void postForm(String url, HashMap<String, String> params, Callback callback) {

        KLog.d(url);
        KLog.d(params);
        PostFormBuilder builder = OkHttpUtils.post()
                .url(url);
        setParams(builder, params);
        builder.tag(url).build().execute(callback);
    }

    /**
     * Http POST 无参数异步请求
     *
     * @param url      请求地址
     * @param callback 响应回调
     */
    public static void postForm(String url, Callback callback) {
        postForm(url, null, callback);
    }
    /**
     * 上传文件 异步
     * @param url      请求地址
     * @param file     上传文件
     * @param params   请求参数
     * @param callBack 响应回调
     */
    public static void postFile(String url, File file, HashMap<String, String> params, Callback callBack) {
        OkHttpRequestBuilder builder = OkHttpUtils.postFile()
                .url(url)
                .file(file);
        setParams(builder, params);
        builder.tag(url).build().execute(callBack);
    }

    /**
     * 表单形式异步上传文件
     *
     * @param url      请求地址
     * @param file     上传文件
     * @param name     表单中的name
     * @param fileName 文件名
     * @param params   请求参数
     * @param callback 响应回调
     */
    public static void postFormFile(String url, File file, String name, String fileName, HashMap<String, String> params, Callback callback) {
        PostFormBuilder builder = OkHttpUtils.post()
                .url(url)
                .addFile(name, fileName, file);
        setParams(builder, params);
        builder.tag(url).build().execute(callback);
    }

    /**
     * Http POST 参数异步请求
     * post字节数组
     *
     * @param url      请求地址
     * @param tag      请求标记
     * @param params   请求参数
     * @param callback 响应回调  Callback
     */
    public static void postByteArray(String url, String tag, HashMap<String, String> params, String key, String fileName, byte[] content, Callback callback) {
        PostBytesFormBuilder postBytesFormBuilder = new PostBytesFormBuilder();
        OkHttpRequestBuilder builder = postBytesFormBuilder
                .addByteArray(key, fileName, content)
                .url(url)
                .tag(tag);
        setParams(builder, params);
        builder.build()
                .connTimeOut(100000L)
                .readTimeOut(100000L)
                .writeTimeOut(100000L)
                .execute(callback);
    }

    /**
     * 取消请求
     *
     * @param tag 这个请求的url
     */
    public static void cancel(String tag) {
        if (TextUtils.isEmpty(tag)) return;
        OkHttpUtils.getInstance().cancelTag(tag);
    }

  /*  public static void loadFile(final String url, final String path, final String filename) {
        File file =new File(path);
        if  (!file .exists()  && !file .isDirectory())
        {
            file .mkdir();
        }
        Request request = new Request.Builder().url(url).build();
        OkHttpClient mOkHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
        mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(path + "/" + filename);
                    if(!file.exists())
                    {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                    }
                    fos.flush();
                } catch (Exception e) {
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }*/
}
