package au.com.hbuy.aotong.greenDao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yangwei on 2016/8/11--10:16.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
@Entity
public class Message {
    @Id
    private Long id;
    private String uid;
    private String name;
    private String time;
    private String content;
    private int status;
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
    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    @Generated(hash = 545844002)
    public Message(Long id, String uid, String name, String time, String content,
            int status) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.time = time;
        this.content = content;
        this.status = status;
    }
    @Generated(hash = 637306882)
    public Message() {
    }
}
