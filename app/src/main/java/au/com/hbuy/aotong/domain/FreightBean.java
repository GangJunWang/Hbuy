package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2016/8/2--14:56.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class FreightBean implements Serializable {
    /**
     外币转换
     人民币转外币：RMB/汇出价*100=外币
     外币转人民币：外币*汇入价/100=RMB
     */
    private int type;
    private double result = 0.00;
    private double fbp;   //现汇买入价
    private double sp;    //汇/钞卖出价
    private double mp;    //中间价

    public double getMp() {
        return mp;
    }

    public void setMp(double mp) {
        this.mp = mp;
    }

    public FreightBean() {
    }

    public FreightBean(double result, int type) {
        this.result = result;
        this.type = type;
    }

    public double getFbp() {
        return fbp;
    }

    public void setFbp(double fbp) {
        this.fbp = fbp;
    }

    public double getSp() {
        return sp;
    }

    public void setSp(double sp) {
        this.sp = sp;
    }

    public FreightBean(int type, double fbp, double sp, double mp) {
        this.type = type;
        this.sp = sp;
        this.mp = mp;
        this.fbp = fbp;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
