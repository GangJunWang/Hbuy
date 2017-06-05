package au.com.hbuy.aotong.domain;

import java.io.Serializable;

/**
 * Created by yangwei on 2017/3/23--12:56.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class ParityBean implements Serializable {
    private Parity aud;
    private Parity usd;
    private Parity nzd;
    private Parity jpy;
    private Parity krw;
    private Parity thb;
    private Parity php;
    private Parity mop;
    private Parity eur;
    private Parity cad;
    private Parity nok;
    private Parity dkk;
    private Parity sek;
    private Parity sgd;
    private Parity chf;
    private Parity hkd;
    private Parity gbp;

    public Parity getAud() {
        return aud;
    }

    public void setAud(Parity aud) {
        this.aud = aud;
    }

    public Parity getCad() {
        return cad;
    }

    public void setCad(Parity cad) {
        this.cad = cad;
    }

    public Parity getChf() {
        return chf;
    }

    public void setChf(Parity chf) {
        this.chf = chf;
    }

    public Parity getDkk() {
        return dkk;
    }

    public void setDkk(Parity dkk) {
        this.dkk = dkk;
    }

    public Parity getEur() {
        return eur;
    }

    public void setEur(Parity eur) {
        this.eur = eur;
    }

    public Parity getGbp() {
        return gbp;
    }

    public void setGbp(Parity gbp) {
        this.gbp = gbp;
    }

    public Parity getHkd() {
        return hkd;
    }

    public void setHkd(Parity hkd) {
        this.hkd = hkd;
    }

    public Parity getJpy() {
        return jpy;
    }

    public void setJpy(Parity jpy) {
        this.jpy = jpy;
    }

    public Parity getKrw() {
        return krw;
    }

    public void setKrw(Parity krw) {
        this.krw = krw;
    }

    public Parity getMop() {
        return mop;
    }

    public void setMop(Parity mop) {
        this.mop = mop;
    }

    public Parity getNok() {
        return nok;
    }

    public void setNok(Parity nok) {
        this.nok = nok;
    }

    public Parity getNzd() {
        return nzd;
    }

    public void setNzd(Parity nzd) {
        this.nzd = nzd;
    }

    public Parity getPhp() {
        return php;
    }

    public void setPhp(Parity php) {
        this.php = php;
    }

    public Parity getSek() {
        return sek;
    }

    public void setSek(Parity sek) {
        this.sek = sek;
    }

    public Parity getSgd() {
        return sgd;
    }

    public void setSgd(Parity sgd) {
        this.sgd = sgd;
    }

    public Parity getThb() {
        return thb;
    }

    public void setThb(Parity thb) {
        this.thb = thb;
    }

    public Parity getUsd() {
        return usd;
    }

    public void setUsd(Parity usd) {
        this.usd = usd;
    }

    public class Parity {
        private String fbp;  //现汇买入价
        private String mbp;   //现钞买入价
        private String sp;   //汇/钞卖出价
        private String mp;  //中间价

        public String getFbp() {
            return fbp;
        }

        public void setFbp(String fbp) {
            this.fbp = fbp;
        }

        public String getMbp() {
            return mbp;
        }

        public void setMbp(String mbp) {
            this.mbp = mbp;
        }

        public String getMp() {
            return mp;
        }

        public void setMp(String mp) {
            this.mp = mp;
        }

        public String getSp() {
            return sp;
        }

        public void setSp(String sp) {
            this.sp = sp;
        }
    }
}
