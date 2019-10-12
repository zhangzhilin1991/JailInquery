package com.nyiit.jailinquery.bean;

import java.util.List;

public class PrisonerInfoBean {

    /**
     * count : 1
     * data : [{"zf_zhye":"0","zf_xq":"无期","zf_xmrq":"2010-09-28","zf_yx":"","zf_rjrq":"2001-10-15","zf_xm":"张步军","zf_zm":"故意伤害罪","zf_bh":"320100001245","zf_nl":"48"}]
     */

    private String count;
    private List<DataBean> data;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * zf_zhye : 0
         * zf_xq : 无期
         * zf_xmrq : 2010-09-28
         * zf_yx :
         * zf_rjrq : 2001-10-15
         * zf_xm : 张步军
         * zf_zm : 故意伤害罪
         * zf_bh : 320100001245
         * zf_nl : 48
         */

        private String zf_zhye;
        private String zf_xq;
        private String zf_xmrq;
        private String zf_yx;
        private String zf_rjrq;
        private String zf_xm;
        private String zf_zm;
        private String zf_bh;
        private String zf_nl;

        public String getZf_zhye() {
            return zf_zhye;
        }

        public void setZf_zhye(String zf_zhye) {
            this.zf_zhye = zf_zhye;
        }

        public String getZf_xq() {
            return zf_xq;
        }

        public void setZf_xq(String zf_xq) {
            this.zf_xq = zf_xq;
        }

        public String getZf_xmrq() {
            return zf_xmrq;
        }

        public void setZf_xmrq(String zf_xmrq) {
            this.zf_xmrq = zf_xmrq;
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

        public String getZf_xm() {
            return zf_xm;
        }

        public void setZf_xm(String zf_xm) {
            this.zf_xm = zf_xm;
        }

        public String getZf_zm() {
            return zf_zm;
        }

        public void setZf_zm(String zf_zm) {
            this.zf_zm = zf_zm;
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
}
