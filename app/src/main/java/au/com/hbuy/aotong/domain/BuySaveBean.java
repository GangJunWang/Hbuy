package au.com.hbuy.aotong.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangwei on 2017/1/13--15:15.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class BuySaveBean implements Serializable {
    private List<BuyWaitPayBean> data;
    private String page_all;
    private String page;

    public String getPage() {
        return page;
    }

    public String getPage_all() {
        return page_all;
    }

    public List<BuyWaitPayBean> getData() {
        return data;
    }
}
