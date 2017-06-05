package au.com.hbuy.aotong.utils.okhttp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import au.com.hbuy.aotong.domain.User;

/**
 * Http 响应体，
 * 适用服务器端大部分如下的json结构体:
 * <pre>
 * {
 *  "Code":Code
 *  "data":T
 * }
 * </pre>
 * Code -> Resp 系统码,
 * T -> 对应Json结构体
 *
 *
 * 接口要用陀峰命名法，这里因为服务器命名不规范，所以使用SerializedName序列化名字
 * @param <T>
 */
public class Resp<T> implements Serializable {
    private String noCooike;

    public String getNoCooike() {
        return noCooike;
    }

    public void setNoCooike(String noCooike) {
        this.noCooike = noCooike;
    }

    /**
     * 服务器端响应码 返回状态 1 成功 0 失败
     */
    @SerializedName("status")
    public String status;

    @SerializedName("code")
    public String code;
    /**
     * 响应数据内容
     *//*
    @SerializedName("msg")
    public T data;
*/

    @SerializedName("access_token")
    public String access_token;

    @SerializedName("openid")
    public String openid;

    @SerializedName("nickname")
    public String nickname;

    @SerializedName("unionid")
    public String unionid;

    @SerializedName("headimgurl")
    public String headimgurl;
    @SerializedName("sex")
    public String sex;

    @SerializedName("msg")
    public String msg;

    //default address
    @SerializedName("id")
    public String id;

    @SerializedName("receiver")
    public String receiver;

    @SerializedName("phone")
    public String phone;

    @SerializedName("country")
    public String country;

    @SerializedName("city")
    public String city;

    @SerializedName("address")
    public String address;

    @SerializedName("time")
    public String time;

    @SerializedName("pictures")
    public String[] pictures;

    @SerializedName("pack_pkgs")
    public T pack_pkgs;

    //系统响应code

    /**
     * 任务返回值——成功
     */
    public static final int RESPONSE_SUCCESS = 1;
    /**
     * 任务返回值——成功
     */
    public static final int CREATE_ACCOUNT = 2;
    /**
     * 任务返回值——失败
     */
    public static final int RESPONSE_FAIL = 0;
    /**
     * 任务返回值——旧密码不正确
     */
    public static final int OLDPWD_FAIL = -1;
    /**
     * 任务返回值——新密码二次输入不正确
     */
    public static final int NEWNOTTOW_FAIL = -2;
    /**
     * 任务返回值——参数获取失败
     */
    public static final int RESPONSE_ILLEGAL_PARAMETER = 9;

    /**
     * 任务返回值——提交参数个数不匹配
     */
    public static final int RESPONSE_UNMATCH_PARAM_COUNT = 33;
    /**
     * 任务返回值——服务器内部未知错误
     */
    public static final int RESPONSE_UNKNOWN_ERROR = 500;
}
