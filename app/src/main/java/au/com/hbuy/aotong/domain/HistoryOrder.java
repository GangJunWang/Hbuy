package au.com.hbuy.aotong.domain;

import java.util.List;

/**
 * Created by yangwei on 2017/1/7--14:39.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class HistoryOrder {
    private List<Order> data;

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
}
