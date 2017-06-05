package au.com.hbuy.aotong.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangwei on 2016/9/30--14:17.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class OrderBean implements Serializable{
    private String page_all;
    private String page;
    private List<Order> data;

    public String getPage() {
        return page;
    }

    public List<Order> getData() {
        return data;
    }

    public String getPage_all() {
        return page_all;
    }
}
