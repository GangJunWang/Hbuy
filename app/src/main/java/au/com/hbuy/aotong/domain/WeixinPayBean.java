package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2016/8/25--17:10.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class WeixinPayBean {
    private String noncestr;
    private String timestamp;
    private String prepayid;
    private String sign;

    public String getNoncestr() {
        return noncestr;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public String getSign() {
        return sign;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
