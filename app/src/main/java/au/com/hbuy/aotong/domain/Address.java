package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2016/8/2--14:56.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class Address implements Serializable {
    private String id;
    private String receiver;
    private String phone;
    private String country;
    private String city;
    private String address;
    private String zip;
    private String is_default;
    private String phonecode;
    private String name;

    public String getPhonecode() {
        return phonecode;
    }

    public String getName() {
        return name;
    }

    public Address() {

    }
    public Address(String receiver, String phone, String address, String city, String country, String id, String is_default, String zip, String phonecode) {
        this.receiver = receiver;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
        this.id = id;
        this.is_default = is_default;
        this.zip = zip;
        this.phonecode = phonecode;
    }

    public String getZip() {
        return zip;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getIs_default() {
        return is_default;
    }

    public String getId() {
        return id;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }
}
