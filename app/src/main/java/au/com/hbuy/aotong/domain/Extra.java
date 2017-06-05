package au.com.hbuy.aotong.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangwei on 2016/12/9--14:15.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class Extra implements Serializable {
    private List<PkgBean> pkgs;
    private ExtraBean extra;
    private List<Good> goods;  //代购订单
    public ExtraBean getExtra() {
        return extra;
    }

    public List<Good> getGoods() {
        return goods;
    }

    public void setGoods(List<Good> goods) {
        this.goods = goods;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public void setPkgs(List<PkgBean> pkgs) {
        this.pkgs = pkgs;
    }

    public List<PkgBean> getPkgs() {
        return pkgs;
    }



}
