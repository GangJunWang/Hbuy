package au.com.hbuy.aotong.utils.okhttp;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.domain.Order;

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
public class RespInform<T> implements Serializable {
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

    @SerializedName("pkg_number")
    public String pkg_number;

    @SerializedName("waiting_pkg")
    public String waiting_pkg;

    /**
     * 任务返回值——成功
     */
    public static final int RESPONSE_SUCCESS = 1;
    /**
     * 任务返回值——失败
     */
    public static final int RESPONSE_FAIL = 0;

    /**
     * 响应数据内容
     */
    @SerializedName("data")
    public T data;


    @SerializedName("detail")
    public Order detail;

    /**
     * 响应数据内容
     */
    @SerializedName("carrier_id")
    public String carrier_id;

    @SerializedName("mail_no")
    public String mail_no;

    @SerializedName("content")
    public String content;

    @SerializedName("page_all")
    public String page_all;

    @SerializedName("time")
    public String time;

    @SerializedName("money")
    public String money;

    @SerializedName("pkgs")
    public T pkgs;

    @SerializedName("address")
    public Address address;
    @Override
    public String toString() {
        return "Resp{" +
                "code=" + status +
                ", data=" + '\'' +
                '}';
    }
}
