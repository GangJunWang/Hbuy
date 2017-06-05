package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2017/1/6--14:08.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class PhoneBean implements Serializable {
    private String email;
    private String phone;
    private String wechat;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }
}
