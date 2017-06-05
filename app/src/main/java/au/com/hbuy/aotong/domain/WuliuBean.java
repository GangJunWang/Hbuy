package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2016/8/2--14:56.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class WuliuBean implements Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
