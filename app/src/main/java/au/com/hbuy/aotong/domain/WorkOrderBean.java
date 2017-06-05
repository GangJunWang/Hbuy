package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2016/9/1--16:23.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class WorkOrderBean implements Serializable {
    private String id;
    private String no;
    private String status;
    private String waiting;
    private String list;
    private String time;
    private String ori_pkg_id;
    private String mail_no;
    private String carrier_id;
    private String icon;
    private String carrier_name;

    public String getCarrier_name() {
        return carrier_name;
    }

    public String getIcon() {
        return icon;
    }

    public String getOri_pkg_id() {
        return ori_pkg_id;
    }

    public String getList() {
        return list;
    }

    public String getWaiting() {
        return waiting;
    }

    public String getTime() {
        return time;
    }

    public String getNo() {
        return no;
    }

    public String getStatus() {
        return status;
    }

    public String getMail_no() {
        return mail_no;
    }

    public String getCarrier_id() {
        return carrier_id;
    }

    public String getId() {
        return id;
    }
}
