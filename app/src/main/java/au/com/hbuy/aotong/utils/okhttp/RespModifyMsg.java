package au.com.hbuy.aotong.utils.okhttp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

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
 */
public class RespModifyMsg implements Serializable {

    /**
     * 服务器端响应码 返回状态 1 成功 0 失败
     */
    @SerializedName("status")
    public String status;

    @SerializedName("msg")
    public String msg;

    @SerializedName("id")
    public String id;

    @SerializedName("code")
    public String code;

    @SerializedName("type")
    public String type;


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

}
