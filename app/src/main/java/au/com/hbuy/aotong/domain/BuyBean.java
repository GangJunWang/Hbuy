package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2016/12/13--15:18.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class BuyBean implements Serializable{
    private String title;
    private String url;
    private String num;
    private String size;
    private String mark;

    public BuyBean(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
