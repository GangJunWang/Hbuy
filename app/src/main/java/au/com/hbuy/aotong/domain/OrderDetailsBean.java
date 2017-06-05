package au.com.hbuy.aotong.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangwei on 2016/12/9--14:34.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class OrderDetailsBean implements Serializable {
    private String no;
    private String status;
    private String time;
    private String money;
    private String type;
    private String real_money;  //实付金额
    public String getReal_money() {
        return real_money;
    }

    public void setReal_money(String real_money) {
        this.real_money = real_money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String list;
    private String coupon;    //>0 该订单已使用优惠卷 0没有使用 显示可使用的张数
    private String user_balance;   //余额
    private String use_balance;   //该订单已使用的金额

    public String getUse_balance() {
        return use_balance;
    }

    public void setUse_balance(String use_balance) {
        this.use_balance = use_balance;
    }

    private String available_coupon;   //可用的优惠卷
    public OrderDetailsBean() {
    }
    public OrderDetailsBean(String status) {
        this.status = status;
    }

    public String getAvailable_coupon() {
        return available_coupon;
    }

    public void setAvailable_coupon(String available_coupon) {
        this.available_coupon = available_coupon;
    }

    public String getUser_balance() {
        return user_balance;
    }

    public void setUser_balance(String user_balance) {
        this.user_balance = user_balance;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getCoupon() {
        return coupon;
    }

    private String payment;
    private List<OrderDetails> orderList;

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }


    public List<OrderDetails> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderDetails> orderList) {
        this.orderList = orderList;
    }


    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
