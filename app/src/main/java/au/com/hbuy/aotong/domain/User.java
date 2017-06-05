package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2016/7/26--14:15.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class User implements Serializable {
    private String uid;
    private String username;
    private String passwd;
    private String avator;
    private String email;
    private String imtoken;
    private String kf_id;
    private String gender;
    private String country;
    private String city;
    private String phonecode;
    private String phone;
    private String isvip;
    private String unionid;

    public String getIsvip() {
        return isvip;
    }

    public void setIsvip(String isvip) {
        this.isvip = isvip;
    }

    public String getUnionid() {
        return unionid;
    }

    public String getPhonecode() {
        return phonecode;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getCountry() {
        return country;
    }

    public String getGender() {

        return gender;
    }

    public String getCity() {
        return city;
    }

    public String getAvator() {
        return avator;
    }

    public String getKf_id() {
        return kf_id;
    }

    public void setKf_id(String kf_id) {
        this.kf_id = kf_id;
    }

    public String getImtoken() {
        return imtoken;
    }

    public void setImtoken(String imtoken) {
        this.imtoken = imtoken;
    }

    @Override
    public String toString() {
        return "User{" +
                "username=" + username +
                ", pwd=" + passwd + ", email = " + email + ", imtoken = " + "}";
    }
}
