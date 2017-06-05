package au.com.hbuy.aotong.utils.okhttp;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;
import com.zhy.http.okhttp.callback.Callback;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.hbuy.aotong.LoginActivity;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.FileUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Http 请求响应回调,对应的Resp Json 结构
 */
public abstract class RespGetTypeback extends Callback<RespModifyMsg> {
    private Gson gson;
    private Context mContext;
    private CustomProgressDialog progressDialog;

    public RespGetTypeback(Context context) {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
        this.mContext = context;
    }

    @Override
    public RespModifyMsg parseNetworkResponse(Response response, int id) throws Exception {
        RespModifyMsg resp = null;
        String string = FileUtils.inputStream2String(response.body().byteStream());
        KLog.d(string);
        if (string.equals("{\"status\":0,\"msg\":\"Access Denied\"}")) {
            return null;
        }
        try {
            resp = gson.fromJson(trim(string), RespModifyMsg.class);
        } catch (Exception e) {
            // 错误信息的结构holy shit
            e.printStackTrace();
            resp = gson.fromJson(string, new TypeToken<Resp<ErrorMsg>>() {
            }.getType());
        }
        return resp;
    }

    @Override
    public void onResponse(RespModifyMsg response, int id) {
        try {
            String status = response.type;
            KLog.d(response.status);
            if (StringUtils.toInt(status) > 0) {
                onBusiness(response.type);
            } else {
                onFail(response.status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        }
    }

    @Override
    /* hide */
       public void onError(Call call, Exception e, int id) {
       // e.printStackTrace();
        AppUtils.stopProgressDialog(progressDialog);
        // 取消了请求
        if (call.isCanceled()) {
            return;
        }

        // 服务端和客户端json结构定义不一致 -> 到 parseNetworkResponse定位一下错误
        if (e instanceof JsonSyntaxException) {
            Toast.makeText(mContext, "服务器返回数据异常", Toast.LENGTH_LONG).show(); // 临时用来处理服务器端报的错
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
     //       ShowToastUtils.toast(Context, "连接超时"); // 临时用来处理服务器端报的错
            return;
        }

        // 网络异常
        if (e instanceof UnknownHostException || e instanceof SocketException) {
         //   ShowToastUtils.toast(Context, "服务器不在线"); // 临时用来处理服务器端报的错
            return;
        }
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
     *
     */
    public abstract void onSuccess();

    /**
     * 响应成功
     *
     */
    public void onBusiness() {
      //  ShowToastUtils.toast(mContext, "为了你的信息安全,请重新登录.");
      //  AppUtils.showActivity(mContext, LoginActivity.class);
      //  mContext.finish();
    }
    public abstract void onFail(String str);

    /**
     * 响应成功
     *
     */
    public void onBusiness(String str) {
    }
}
