package com.nyiit.jailinquery.bean;

/**
 * 罪犯获奖信息
 */
public class PrisonerAwardInfo {
    //罪犯姓名
    private String zf_xm;
    //罪犯编号
    private String zf_bh;
    //罪犯获奖日期
    private String zf_hjrq;
    //罪犯获奖类型
    private String zf_hjlx;

    public PrisonerAwardInfo() {}

    public PrisonerAwardInfo(String zf_xm, String zf_bh, String zf_hjrq, String zf_hjlx) {
        this.zf_xm = zf_xm;
        this.zf_bh = zf_bh;
        this.zf_hjrq = zf_hjrq;
        this.zf_hjlx = zf_hjlx;
    }

    public String getZf_xm() {
        return zf_xm;
    }

    public void setZf_xm(String zf_xm) {
        this.zf_xm = zf_xm;
    }

    public String getZf_bh() {
        return zf_bh;
    }

    public void setZf_bh(String zf_bh) {
        this.zf_bh = zf_bh;
    }

    public String getZf_hjrq() {
        return zf_hjrq;
    }

    public void setZf_hjrq(String zf_hjrq) {
        this.zf_hjrq = zf_hjrq;
    }

    public String getZf_hjlx() {
        return zf_hjlx;
    }

    public void setZf_hjlx(String zf_hjlx) {
        this.zf_hjlx = zf_hjlx;
    }

    @Override
    public String toString() {
        return "奖励日期 " + zf_hjrq + " " +
                "奖励类型 " + zf_hjlx;
    }
}
