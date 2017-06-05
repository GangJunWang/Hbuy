package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2016/12/26--13:56.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class BuyCommit {
    private String img;
    private String link;
    private String num;
    private String title;
    private String prop;
    private String note;

    public BuyCommit(String img, String link, String note, String num, String prop, String title) {
        this.img = img;
        this.link = link;
        this.note = note;
        this.num = num;
        this.prop = prop;
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
