package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2017/1/14--16:20.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class Good implements Serializable {
    private String id;
    private String img;
    private String title;
    private String money;
    private String num;
    private String freight;
    private String prop;
    private String type;
    private String helpbuy_type;
    private String link;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHelpbuy_type() {
        return helpbuy_type;
    }

    public void setHelpbuy_type(String helpbuy_type) {
        this.helpbuy_type = helpbuy_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}