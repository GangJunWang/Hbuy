package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2016/11/29--14:38.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class InformMesBean {
    private String title;
    private String img;
    private String content;
    private String value;
    private String time;
    private String type;

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getImg() {
        return img;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public static class ContentBean {
        private String data;

        public String getData() {
            return data;
        }
    }
}
