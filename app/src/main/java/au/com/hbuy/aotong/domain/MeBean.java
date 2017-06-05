package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2017/2/28--10:48.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class MeBean {
    private String packing;
    private String dealing;
    private String wait_order;
    private String wait_pay;
    private String valid_coupon;
    private String user_balance;

    public String getUser_balance() {
        return user_balance;
    }

    public void setUser_balance(String user_balance) {
        this.user_balance = user_balance;
    }

    public String getValid_coupon() {
        return valid_coupon;
    }

    public void setValid_coupon(String valid_coupon) {
        this.valid_coupon = valid_coupon;
    }

    public String getDealing() {
        return dealing;
    }

    public void setDealing(String dealing) {
        this.dealing = dealing;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public String getWait_order() {
        return wait_order;
    }

    public void setWait_order(String wait_order) {
        this.wait_order = wait_order;
    }

    public String getWait_pay() {
        return wait_pay;
    }

    public void setWait_pay(String wait_pay) {
        this.wait_pay = wait_pay;
    }
}
