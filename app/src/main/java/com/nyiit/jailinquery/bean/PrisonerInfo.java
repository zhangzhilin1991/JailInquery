package com.nyiit.jailinquery.bean;


import java.io.Serializable;

/**
 * 罪犯信息
 */
public class PrisonerInfo implements Serializable {


    //罪犯姓名
    private String zf_xm;
    //罪犯编号
    private String zf_bh;
    //罪犯年龄
    private String zf_nl;
    //罪犯账户余额
    private String zf_zhye;
    //罪犯罪名
    private String zf_zm;
    //罪犯刑期
    private String zf_xq;
    //罪犯罪行余期
    private String zf_yx;
    //罪犯入刑日期
    private String zf_rjrq;
    //罪犯刑慢日期
    private String zf_xmrq;

    public PrisonerInfo(){}

    public PrisonerInfo(String zf_xm, String zf_bh, String zf_nl, String zf_zhye, String zf_zm, String zf_xq, String zf_yx, String zf_rjrq, String zf_xmrq) {
        this.zf_xm = zf_xm;
        this.zf_bh = zf_bh;
        this.zf_nl = zf_nl;
        this.zf_zhye = zf_zhye;
        this.zf_zm = zf_zm;
        this.zf_xq = zf_xq;
        this.zf_yx = zf_yx;
        this.zf_rjrq = zf_rjrq;
        this.zf_xmrq = zf_xmrq;
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

    public String getZf_nl() {
        return zf_nl;
    }

    public void setZf_nl(String zf_nl) {
        this.zf_nl = zf_nl;
    }

    public String getZf_zhye() {
        return zf_zhye;
    }

    public void setZf_zhye(String zf_zhye) {
        this.zf_zhye = zf_zhye;
    }

    public String getZf_zm() {
        return zf_zm;
    }

    public void setZf_zm(String zf_zm) {
        this.zf_zm = zf_zm;
    }

    public String getZf_xq() {
        return zf_xq;
    }

    public void setZf_xq(String zf_xq) {
        this.zf_xq = zf_xq;
    }

    public String getZf_yx() {
        return zf_yx;
    }

    public void setZf_yx(String zf_yx) {
        this.zf_yx = zf_yx;
    }

    public String getZf_rjrq() {
        return zf_rjrq;
    }

    public void setZf_rjrq(String zf_rjrq) {
        this.zf_rjrq = zf_rjrq;
    }

    public String getZf_xmrq() {
        return zf_xmrq;
    }

    public void setZf_xmrq(String zf_xmrq) {
        this.zf_xmrq = zf_xmrq;
    }

    @Override
    public String toString() {
        return "姓名 " + zf_xm + " " +
                "编号 " + zf_bh + " " +
                "年龄 " + zf_nl + " " +
                "账户余额 " + zf_zhye + " " +
                "罪名 " + zf_zm + " " +
                "刑期 " + zf_xq + " " +
                "余刑 " + zf_yx + " " +
                "入监日期 " + zf_rjrq + " " +
                "刑满日期 " + zf_xmrq;
    }
}
