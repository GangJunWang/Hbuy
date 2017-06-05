package au.com.hbuy.aotong.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yangwei on 2016/8/24--17:07.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
@Entity
public class Inform {
    @Id
    private Long id;
    private String uid;
    private String time;
    private String content;
    private String img;
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getImg() {
        return this.img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    @Generated(hash = 1636292729)
    public Inform(Long id, String uid, String time, String content, String img) {
        this.id = id;
        this.uid = uid;
        this.time = time;
        this.content = content;
        this.img = img;
    }
    @Generated(hash = 417978714)
    public Inform() {
    }
}
