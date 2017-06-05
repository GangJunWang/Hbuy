package au.com.hbuy.aotong.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangwei on 2016/12/9--14:38.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class OrderDetails implements Serializable {
    private String id;
    private List<PkgBean> pkgBeanList; //转运订单
    private List<Good> BuyList;  //代购订单
    public ExtraBean getExtraBean() {
        return extraBean;
    }

    public List<Good> getBuyList() {
        return BuyList;
    }

    public void setBuyList(List<Good> buyList) {
        BuyList = buyList;
    }

    public void setExtraBean(ExtraBean extraBean) {
        this.extraBean = extraBean;
    }

    private ExtraBean extraBean;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PkgBean> getPkgBeanList() {
        return pkgBeanList;
    }

    public void setPkgBeanList(List<PkgBean> pkgBeanList) {
        this.pkgBeanList = pkgBeanList;
    }


}
