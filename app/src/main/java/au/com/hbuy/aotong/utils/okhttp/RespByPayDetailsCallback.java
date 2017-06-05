package au.com.hbuy.aotong.utils.okhttp;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.hbuy.aotong.LoginActivity;
import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.domain.Extra;
import au.com.hbuy.aotong.domain.OrderDetails;
import au.com.hbuy.aotong.domain.OrderDetailsBean;
import au.com.hbuy.aotong.domain.PkgBean;
import au.com.hbuy.aotong.domain.WaitPayOrder;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.FileUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Http 请求响应回调,对应的Resp Json 结构
 */
public abstract class RespByPayDetailsCallback<T> extends Callback<OrderDetailsBean> {
    private Activity mContext;
    private CustomProgressDialog progressDialog;

    public RespByPayDetailsCallback(Activity context) {
        this.mContext = context;
    }

    public RespByPayDetailsCallback(Activity context, CustomProgressDialog progressDialog) {
        this.mContext = context;
        this.progressDialog = progressDialog;
    }

    @Override
    public OrderDetailsBean parseNetworkResponse(Response response, int id) throws Exception {
        OrderDetailsBean respByOrder = null;
        String string = FileUtils.inputStream2String(response.body().byteStream());
        /*string = "{\n" +
                "    \"no\": \"1000000000000104\",\n" +
                "    \"status\": \"1\",\n" +
                "    \"time\": \"2016-12-06 10:13:17\",\n" +
                "    \"money\": \"3.00\",\n" +
                "    \"payment\": \"0\",\n" +
                "    \"type\": \"1\",\n" +
                "    \"coupon\": \"0\",\n" +
                "    \"use_balance\": \"0\",\n" +
                "    \"list\": {\n" +
                "        \"1011000000000082\": {\n" +
                "            \"pkgs\": {\n" +
                "                \"1\": {\n" +
                "                    \"pkg_id\": \"213\",\n" +
                "                    \"carrier_id\": \"6\",\n" +
                "                    \"carrier_name\": \"国际EMS\",\n" +
                "                    \"volume\": \"8x8x8\",\n" +
                "                    \"weight\": \"70\",\n" +
                "                    \"money\": \"1.00\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"extra\": {\n" +
                "                \"open_pay\": \"0.00\",\n" +
                "                \"arrive_pay\": \"0.00\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"1011000000000085\": {\n" +
                "            \"pkgs\": {\n" +
                "                \"2\": {\n" +
                "                    \"pkg_id\": \"215\",\n" +
                "                    \"carrier_id\": \"4\",\n" +
                "                    \"carrier_name\": \"TNT\",\n" +
                "                    \"volume\": \"323x2x3\",\n" +
                "                    \"weight\": \"33\",\n" +
                "                    \"money\": \"1.00\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"extra\": {\n" +
                "                \"open_pay\": \"0.00\",\n" +
                "                \"arrive_pay\": \"1.00\"\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"time_left\": \"1799999998518751509\"\n" +
                "}";
                */
        KLog.d(string);
        if (string.equals("{\"status\":0,\"msg\":\"Access Denied\"}")) {
            return null;
        }
        try {
            respByOrder = JSON.parseObject(trim(string), OrderDetailsBean.class);
            KLog.d(respByOrder.getList());
            JSONObject object = new JSONObject(respByOrder.getList());
            Iterator keys = object.keys();
            List<OrderDetails> list = new ArrayList<>();
            while(keys.hasNext()){
                String key = (String) keys.next();
                KLog.d(key);
                OrderDetails order = new OrderDetails();
                order.setId(key);
                String content = object.getString(key);
                KLog.d(content);
                Extra ee = JSON.parseObject(content, Extra.class);
                KLog.d(ee);
               /* order.setExtra(ee);
                List<PkgBean> pkg = new ArrayList<>();
                JSONObject o = new JSONObject(ee.getPkgs());
                Iterator ks = o.keys();
                while(ks.hasNext()){
                    String k = (String) ks.next();
                    KLog.d(k);
                    String c = o.getString(k);
                    PkgBean a = JSON.parseObject(c, PkgBean.class);
                    pkg.add(a);
                }*/
                order.setPkgBeanList(ee.getPkgs());
                order.setExtraBean(ee.getExtra());
                list.add(order);
            }
            respByOrder.setOrderList(list);
        } catch (Exception e) {
            e.printStackTrace();
            return new OrderDetailsBean("sb");
        }
        return respByOrder;
    }

    @Override
    public void onResponse(OrderDetailsBean response, int id) {
        if (null == response) {
            onBusiness();
            return;
        }
        if ("sb".equals(response.getStatus())) {
            onFail();
            return;
        }
        try {
            onSuccess(response);
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        }
    }

    @Override
    /* hide */
    public void onError(Call call, Exception e, int id) {
        AppUtils.stopProgressDialog(progressDialog);
        onFail();
        // 取消了请求
        if (call.isCanceled()) {
            return;
        }

        // 服务端和客户端json结构定义不一致 -> 到 parseNetworkResponse定位一下错误
        if (e instanceof JsonSyntaxException) {
            ShowToastUtils.toast(mContext, "服务器数据异常");
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
        // json 解析语法出错
        if (e instanceof JsonSyntaxException) {
            ShowToastUtils.toast(mContext, "json解析错误"); // 临时用来处理服务器端报的错
            return;
        }

        // 网络异常
        if (e instanceof UnknownHostException || e instanceof SocketException) {
            ShowToastUtils.toast(mContext, "服务器不在线"); // 临时用来处理服务器端报的错
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
     */
    public abstract void onSuccess(OrderDetailsBean t);

    public abstract void onFail();

    public void onBusiness() {
        AppUtils.cookieInvalid(mContext);
    }
}
