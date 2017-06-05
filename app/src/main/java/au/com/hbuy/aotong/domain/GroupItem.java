package au.com.hbuy.aotong.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangwei on 2017/3/29--17:31.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class GroupItem {
    String title;
    List<Order> items = new ArrayList<Order>();

    public GroupItem(List<Order> items, String title) {
        this.items = items;
        this.title = title;
    }

    public List<Order> getItems() {
        return items;
    }

    public void setItems(List<Order> items) {
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
