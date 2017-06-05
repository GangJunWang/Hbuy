package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2016/9/13--14:57.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class PkgBean {
    private String open_pay; //看包裹费
    private String arrive_pay; //到付
    private String money;   //快递运费
    private String carrier_name;
    private String id;
    private String volume;
    private String weight;
    private String freight_pay;
    private String extra_pay;
    private String degauss_pay;
    private String package_pay;

    public String getPackage_pay() {
        return package_pay;
    }

    public void setPackage_pay(String package_pay) {
        this.package_pay = package_pay;
    }

    public String getDegauss_pay() {
        return degauss_pay;
    }

    public void setDegauss_pay(String degauss_pay) {
        this.degauss_pay = degauss_pay;
    }

    public void setExtra_pay(String extra_pay) {
        this.extra_pay = extra_pay;
    }

    public void setFreight_pay(String freight_pay) {
        this.freight_pay = freight_pay;
    }

    public String getExtra_pay() {
        return extra_pay;
    }

    public String getFreight_pay() {
        return freight_pay;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public String getArrive_pay() {
        return arrive_pay;
    }

    public String getCarrier_name() {
        return carrier_name;
    }

    public String getMoney() {
        return money;
    }

    public String getOpen_pay() {
        return open_pay;
    }

    public void setArrive_pay(String arrive_pay) {
        this.arrive_pay = arrive_pay;
    }

    public void setCarrier_name(String carrier_name) {
        this.carrier_name = carrier_name;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setOpen_pay(String open_pay) {
        this.open_pay = open_pay;
    }
}
