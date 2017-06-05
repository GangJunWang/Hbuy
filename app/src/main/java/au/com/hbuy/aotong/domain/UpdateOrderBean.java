package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2017/1/17--20:21.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class UpdateOrderBean implements Serializable{
    private String coupon_id;
    private String use_balance;
    private String real_money;
    private String user_balance;

    public String getUser_balance() {
        return user_balance;
    }

    public void setUser_balance(String user_balance) {
        this.user_balance = user_balance;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getReal_money() {
        return real_money;
    }

    public void setReal_money(String real_money) {
        this.real_money = real_money;
    }

    public String getUse_balance() {
        return use_balance;
    }

    public void setUse_balance(String use_balance) {
        this.use_balance = use_balance;
    }
}
