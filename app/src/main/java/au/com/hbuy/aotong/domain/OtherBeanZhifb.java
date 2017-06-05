package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2017/1/15--18:14.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class OtherBeanZhifb implements Serializable {
    private String country_id;
    private String bank_name;
    private String account_name;
    private String account_bsb;
    private String country_name;
    private String account_no;
    private String money_name;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoney_name() {
        return money_name;
    }

    public void setMoney_name(String money_name) {
        this.money_name = money_name;
    }

    private String money;
    public String getAccount_bsb() {
        return account_bsb;
    }

    public void setAccount_bsb(String account_bsb) {
        this.account_bsb = account_bsb;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
