package au.com.hbuy.aotong.utils.okhttp;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.robin.lazy.cache.CacheLoaderManager;
import com.socks.library.KLog;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.hbuy.aotong.LoginActivity;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.FileUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Http 请求响应回调,对应的Resp Json 结构
 */
public abstract class RespRepoAddressCallback<T> extends Callback<RespRepoAddress<T>> {
    private Gson gson;
    private Activity mContext;

    public RespRepoAddressCallback(Activity context) {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
        if (null != context) {
            this.mContext = context;
        }
    }

    @Override
    public RespRepoAddress parseNetworkResponse(Response response, int id) throws Exception {
        RespRepoAddress resp = null;
        String string = FileUtils.inputStream2String(response.body().byteStream());
        KLog.d(string);
        if (string.equals("{\"status\":0,\"msg\":\"Access Denied\"}")) {
            return null;
        }
        if (AppUtils.isGoodJson(string)) {
                CacheLoaderManager.getInstance().saveString("repoAddress", string, 24 * 60);
        }

        try {
            KLog.d();
            resp = gson.fromJson(trim(string), getType());
        } catch (Exception e) {
            // 错误信息的结构holy shit
            KLog.d();


            e.printStackTrace();
            resp = gson.fromJson(string, new TypeToken<Resp<ErrorMsg>>() {
            }.getType());
        }
        return resp;
    }

    @Override
    public void onResponse(RespRepoAddress<T> response, int id) {
        if (null == response) {
            onBusiness();
            return;
        }
        try {
            KLog.d();
            if (response.code.equals("1"))
            onSuccess(response.address);
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        }
    }

    @Override
    /* hide */
    public void onError(Call call, Exception e, int id) {
        // e.printStackTrace();

        // 取消了请求
        if (call.isCanceled()) {
            return;
        }

        // 服务端和客户端json结构定义不一致 -> 到 parseNetworkResponse定位一下错误
        if (e instanceof JsonSyntaxException) {
      //      ShowToastUtils.toast(mContext, "服务器数据异常"); // 临时用来处理服务器端报的错
            return;
        }
        if (e instanceof RuntimeException) {
            // (response.code() >= 400 && response.code() <= 599) 特殊处理了
//            SToast.show(e.getMessage());
            //Toast.makeText(ConfigConstants.appContext, R.string.app_run_error, Toast.LENGTH_LONG).show(); // 临时用来处理服务器端报的错
            return;
        }
        onError(e);
    }

    /**
     * 请求操作失败
     *
     * @param e
     */
    public void onError(Exception e) {
        e.printStackTrace();

        // json 解析语法出错
        if (e instanceof SocketTimeoutException) {
          //  ShowToastUtils.toast(mContext, "连接超时"); // 临时用来处理服务器端报的错
            return;
        }

        // 网络异常
        if (e instanceof UnknownHostException || e instanceof SocketException) {
           // ShowToastUtils.toast(mContext, "服务器不在线"); // 临时用来处理服务器端报的错
            return;
        }
    }

    /**
     * 解析实体对应的类型
     * （特殊的需要可以重写该方法）
     *
     * @return Type
     */
    private Type getType() {
        // 当前对象泛型超类->RespCallback
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            // RespCallback可以不传泛型类型
            throw new RuntimeException("Missing type parameter.");
        }

        // 带参数的Type
        ParameterizedType parameter = (ParameterizedType) superclass;
        // 取得泛型类型对应的类型，即得到 T 对应的类型
        Type _type = $Gson$Types.canonicalize(parameter.getActualTypeArguments()[0]);
        return $Gson$Types.newParameterizedTypeWithOwner(null, RespRepoAddress.class, _type);
    }

    // 处理字符串
    private static String trim(String source) {
        return getJsonString(source);
    }

    /**
     * 处理jsonp
     */
    private static String getJsonString(String source) {
        String result = source;

        if (!TextUtils.isEmpty(result)) {
            if (result.startsWith("var adConfigs =")) {
                result = result.substring("var adConfigs = [ ".length(), result.length() - 2);
            }
            Pattern pattern = Pattern.compile("^\\s*callback\\s*\\((.*)\\s*\\)\\s*$");
            Matcher m = pattern.matcher(source);
            if (m.find() && m.groupCount() > 0) {
                result = m.group(1);
            }

            // 兼容处理 远程下载接口返回JSON多加的 （） 问题
            pattern = Pattern.compile("^\\s*\\((.*)\\s*\\)\\s*$");
            m = pattern.matcher(source);
            if (m.find() && m.groupCount() > 0) {
                result = m.group(1);
            }

//            json = m.group(1);

            // 兼容处理一些小运营商ISP劫持，修改json数据的问题
            if (result.contains(JSON_FILTER_SIGN)) {
                result = result.replaceAll(JSON_FILTER_REGEX, "");
            }
        }

        return result;
    }

    private static final String JSON_FILTER_SIGN = "<script>_guanggao_pub";
    private static final String JSON_FILTER_REGEX = "<script>_guanggao_pub.*?<\\/script>";

    /**
     * 响应成功
     */
    public abstract void onSuccess(T t);

    /**
     * 响应成功
     */

    public abstract void onFail(String str);


    public void onBusiness() {
        if (null != mContext)
        AppUtils.cookieInvalid(mContext);
    }
}
