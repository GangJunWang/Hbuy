package au.com.hbuy.aotong.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by yangwei on 2016/12/14--12:57.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
@Entity
public class BuyDbBean implements Serializable{
    @Id
    private Long id;
    private String img;
    private String link;
    private String title;
    private String num;
    private String size;
    private String mk;
    public String getMk() {
        return this.mk;
    }
    public void setMk(String mk) {
        this.mk = mk;
    }
    public String getSize() {
        return this.size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getNum() {
        return this.num;
    }
    public void setNum(String num) {
        this.num = num;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getImg() {
        return this.img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getLink() {
        return this.link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    @Generated(hash = 1506020853)
    public BuyDbBean(Long id, String img, String link, String title, String num,
            String size, String mk) {
        this.id = id;
        this.img = img;
        this.link = link;
        this.title = title;
        this.num = num;
        this.size = size;
        this.mk = mk;
    }
    @Generated(hash = 1881117280)
    public BuyDbBean() {
    }
}
