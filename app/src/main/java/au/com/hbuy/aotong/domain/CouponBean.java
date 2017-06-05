package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2016/11/28--13:36.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class CouponBean {
    private String title;
    private String content;
    private String type;
    private String value;
    private String condition;
    private String time;
    private String is_expire;
    private String is_used;
    private String is_del;
    private String expire;
    private String id;
    private String ordertype;  //0  通用  1 转运  2 代购

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getIs_del() {
        return is_del;
    }

    public String getIs_expire() {
        return is_expire;
    }

    public String getIs_used() {
        return is_used;
    }

    public String getTime() {
        return time;
    }

    public String getCondition() {
        return condition;
    }

    public String getContent() {
        return content;
    }

    public String getExpire() {
        return expire;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }
}
