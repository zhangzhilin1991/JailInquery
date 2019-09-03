package com.nyiit.jailinquery.bean;

/**
 * 罪犯加减刑信息
 */
public class PrisonerPenaltyInfo {
    //罪犯姓名
    private String zf_xm;
    //罪犯编号
    private String zf_bh;
    //罪犯加减刑日期
    private String zf_bdrq;
    //罪犯加减刑类型
    private String zf_bdlx;

    public PrisonerPenaltyInfo() {}

    public PrisonerPenaltyInfo(String zf_xm, String zf_bh, String zf_bdrq, String zf_bdlx) {
        this.zf_xm = zf_xm;
        this.zf_bh = zf_bh;
        this.zf_bdrq = zf_bdrq;
        this.zf_bdlx = zf_bdlx;
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

    public String getZf_bdrq() {
        return zf_bdrq;
    }

    public void setZf_bdrq(String zf_bdrq) {
        this.zf_bdrq = zf_bdrq;
    }

    public String getZf_bdlx() {
        return zf_bdlx;
    }

    public void setZf_bdlx(String zf_bdlx) {
        this.zf_bdlx = zf_bdlx;
    }

    @Override
    public String toString() {
        return "加减刑日期 " + zf_bdrq + " " +
                "加减刑情况 " + zf_bdlx;
    }
}
