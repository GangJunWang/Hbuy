package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2016/11/28--16:58.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class TakeMsgBean {
    private String title;
    private String img;
    private TakeMsgContent content;

    private String value;
    private String time;

    public TakeMsgContent getContent() {
        return content;
    }

    public void setContent(TakeMsgContent content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
