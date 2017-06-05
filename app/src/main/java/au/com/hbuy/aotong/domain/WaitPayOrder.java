package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2016/12/8--18:05.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */
public class WaitPayOrder {
    private String no;
    private String time;
    private String money;
    private String list;

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
