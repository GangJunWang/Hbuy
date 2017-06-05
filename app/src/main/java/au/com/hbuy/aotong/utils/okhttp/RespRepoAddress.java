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
 * 接口要用陀峰命名法，这里因为服务器命名不规范，所以使用SerializedName序列化名字
 * @param <T>
 */
public class RespRepoAddress<T> implements Serializable {
    @SerializedName("code")
    public String code;
    @SerializedName("address")
    public T address;
}
