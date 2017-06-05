package au.com.hbuy.aotong.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangwei on 2016/9/30--14:17.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class OtherBean implements Serializable{
    private String page;
    private List<WorkOrderBean> data;

    public String getPage() {
        return page;
    }

    public List<WorkOrderBean> getData() {
        return data;
    }
}
