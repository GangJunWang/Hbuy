package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2016/11/30--14:18.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class PkgDetailsBean {
    private String carrier_name;
    private String mail_no;
    private String arrive_pay;
    private String open_pay;
    private String content;
    private String carrier_id;
    private PkgDetail detail;
    private String status;
    private String id;
    private String address_id;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public String getAddress_id() {
        return address_id;
    }

    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getArrive_pay() {
        return arrive_pay;
    }

    public void setArrive_pay(String arrive_pay) {
        this.arrive_pay = arrive_pay;
    }

    public String getOpen_pay() {
        return open_pay;
    }

    public void setOpen_pay(String open_pay) {
        this.open_pay = open_pay;
    }

    public String getStatus() {
        return status;
    }

    public void setCarrier_id(String carrier_id) {
        this.carrier_id = carrier_id;
    }

    public void setCarrier_name(String carrier_name) {
        this.carrier_name = carrier_name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDetail(PkgDetail detail) {
        this.detail = detail;
    }

    public void setMail_no(String mail_no) {
        this.mail_no = mail_no;
    }

    public String getCarrier_name() {
        return carrier_name;
    }

    public String getContent() {
        return content;
    }

    public PkgDetail getDetail() {
        return detail;
    }

    public String getMail_no() {
        return mail_no;
    }

    public String getCarrier_id() {
        return carrier_id;
    }
}
