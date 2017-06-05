package au.com.hbuy.aotong.domain;

import java.util.List;

/**
 * Created by yangwei on 2016/12/6--16:14.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class ContentDatas {
    private String data;
    private List<ContentData> listData;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<ContentData> getListData() {
        return listData;
    }

    public void setListData(List<ContentData> listData) {
        this.listData = listData;
    }
}
