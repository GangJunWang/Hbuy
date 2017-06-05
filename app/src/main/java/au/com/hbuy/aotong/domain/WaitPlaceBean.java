package au.com.hbuy.aotong.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangwei on 2017/1/14--10:14.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class WaitPlaceBean implements Serializable {
    private String destination_country_id;
    private List<Order> list;

    public String getDestination_country_id() {
        return destination_country_id;
    }

    public void setDestination_country_id(String destination_country_id) {
        this.destination_country_id = destination_country_id;
    }

    public List<Order> getList() {
        return list;
    }

    public void setList(List<Order> list) {
        this.list = list;
    }
}
