package au.com.hbuy.aotong.domain;

/**
 * Created by yangwei on 2016/12/25--17:30.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class WaitPayCommit {
    private String title;
    private String money;

    public WaitPayCommit(String title, String money) {
        this.money = money;
        this.title = title;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
