package au.com.hbuy.aotong.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangwei on 2016/11/28--17:55.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class TakeMsgBeans<T> implements Serializable {
    private List<TakeMsgBean> datas;

    public List<TakeMsgBean> getDatas() {
        return datas;
    }
}
