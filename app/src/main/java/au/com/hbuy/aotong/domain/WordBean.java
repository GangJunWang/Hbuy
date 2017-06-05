package au.com.hbuy.aotong.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangwei on 2017/1/3--10:54.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class WordBean implements Serializable {
    private String no;
    private String status;
    private String time;
    private String receiver;
    private String country;
    private String city;
    private String address;
    private String phone;
    private String[] pictures;
    private String waiting;
    private List<BeanBy> pkgs;
    private List<UpckBean> pack_pkgs;   //打包完成  的运费列表等信息

    public List<UpckBean> getPack_pkgs() {
        return pack_pkgs;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String[] getPictures() {
        return pictures;
    }

    public void setPictures(String[] pictures) {
        this.pictures = pictures;
    }

    public List<BeanBy> getPkgs() {
        return pkgs;
    }

    public void setPkgs(List<BeanBy> pkgs) {
        this.pkgs = pkgs;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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

    public String getWaiting() {
        return waiting;
    }

    public void setWaiting(String waiting) {
        this.waiting = waiting;
    }

    public class BeanBy implements Serializable {
        private String mail_no;
        private String carrier_name;
        private String open_pay;
        private String arrive_pay;

        public String getArrive_pay() {
            return arrive_pay;
        }

        public void setArrive_pay(String arrive_pay) {
            this.arrive_pay = arrive_pay;
        }

        public String getCarrier_name() {
            return carrier_name;
        }

        public void setCarrier_name(String carrier_name) {
            this.carrier_name = carrier_name;
        }

        public String getMail_no() {
            return mail_no;
        }

        public void setMail_no(String mail_no) {
            this.mail_no = mail_no;
        }

        public String getOpen_pay() {
            return open_pay;
        }

        public void setOpen_pay(String open_pay) {
            this.open_pay = open_pay;
        }
    }

    public class UpckBean implements Serializable {
        private String volume;
        private String weight;
        private String freight_pay;
        private String degauss_pay;
        private String extra_pay;
        private String carrier_name;
        private String package_pay;

        public String getCarrier_name() {
            return carrier_name;
        }

        public void setCarrier_name(String carrier_name) {
            this.carrier_name = carrier_name;
        }

        public String getDegauss_pay() {
            return degauss_pay;
        }

        public void setDegauss_pay(String degauss_pay) {
            this.degauss_pay = degauss_pay;
        }

        public String getExtra_pay() {
            return extra_pay;
        }

        public void setExtra_pay(String extra_pay) {
            this.extra_pay = extra_pay;
        }

        public String getFreight_pay() {
            return freight_pay;
        }

        public void setFreight_pay(String freight_pay) {
            this.freight_pay = freight_pay;
        }

        public String getPackage_pay() {
            return package_pay;
        }

        public void setPackage_pay(String package_pay) {
            this.package_pay = package_pay;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }
    }
}
