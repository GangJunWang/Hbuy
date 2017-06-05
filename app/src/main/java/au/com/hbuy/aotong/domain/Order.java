package au.com.hbuy.aotong.domain;

import android.animation.ObjectAnimator;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangwei on 2016/7/26--14:15.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class Order implements Serializable {
    private String id;
    private String mail_no;
    private String carrier_id;
    private String carrier_code;
    private String time;
    private String carrier_name;
    private String weight;
    private String icon;
    private String content;
    private String pkg_number;
    private String status;
    private String open_pay;   //0 已查看   非0 付款2块
    private ExpressDetails detail;
    private boolean isChecked;
    private Scheme scheme;
    private String money;
    private String no;
    private String volume;
    private String freight_pay;
    private String weight_img;
    private String is_international;

    public String getIs_international() {
        return is_international;
    }

    public void setIs_international(String is_international) {
        this.is_international = is_international;
    }

    public String getWeight_img() {
        return weight_img;
    }

    public void setWeight_img(String weight_img) {
        this.weight_img = weight_img;
    }

    public String getFreight_pay() {
        return freight_pay;
    }

    public String getVolume() {
        return volume;
    }

    private String destination_country_id;

    public String getDestination_country_id() {
        return destination_country_id;
    }

    public void setDestination_country_id(String destination_country_id) {
        this.destination_country_id = destination_country_id;
    }

    public ExpressDetails getDetail() {
        return detail;
    }

    private String type;

    public String getMoney() {
        return money;
    }

    public String getNo() {
        return no;
    }

    public String getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    public String getPkg_number() {
        return pkg_number;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public String getWeight() {
        return weight;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getCarrier_code() {
        return carrier_code;
    }

    public void setCarrier_code(String carrier_code) {
        this.carrier_code = carrier_code;
    }

    public String getCarrier_id() {
        return carrier_id;
    }

    public void setCarrier_id(String carrier_id) {
        this.carrier_id = carrier_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOpen_pay() {
        return open_pay;
    }

    public String getMail_no() {
        return mail_no;
    }

    public void setMail_no(String mail_no) {
        this.mail_no = mail_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }
    public String getCarrier_name() {
        return carrier_name;
    }

    public static class ExpressDetails implements Serializable {
       private OriginInfo original_info;
        private OriginInfo destination_info;

        public OriginInfo getDestination_info() {
            return destination_info;
        }

        public void setDestination_info(OriginInfo destination_info) {
            this.destination_info = destination_info;
        }

        public OriginInfo getOriginal_info() {
            return original_info;
        }

        public void setOriginal_info(OriginInfo original_info) {
            this.original_info = original_info;
        }
    }

    public static class OriginInfo implements Serializable {
        private List<DataInfo> data;

        public List<DataInfo> getData() {
            return data;
        }

        public void setData(List<DataInfo> data) {
            this.data = data;
        }

        public static class DataInfo {
            private String time;
            private String content;
            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }
        }
    }
}
