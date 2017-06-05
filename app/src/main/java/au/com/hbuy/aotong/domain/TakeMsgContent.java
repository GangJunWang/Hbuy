package au.com.hbuy.aotong.domain;

import java.util.List;

/**
 * Created by yangwei on 2016/11/28--16:59.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class TakeMsgContent {
    private String type;
    private String data;
    private List<ContentData> listdata;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<ContentData> getListdata() {
        return listdata;
    }

    public void setListdata(List<ContentData> listdata) {
        this.listdata = listdata;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
