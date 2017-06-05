package au.com.hbuy.aotong.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yangwei on 2016/12/16--17:09.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
@Entity
public class WaitPayBean implements Serializable {
    @Id
    private Long id;
    private String name;
    private String num;
    public String getNum() {
        return this.num;
    }
    public void setNum(String num) {
        this.num = num;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1484409994)
    public WaitPayBean(Long id, String name, String num) {
        this.id = id;
        this.name = name;
        this.num = num;
    }
    @Generated(hash = 2118862732)
    public WaitPayBean() {
    }
}
