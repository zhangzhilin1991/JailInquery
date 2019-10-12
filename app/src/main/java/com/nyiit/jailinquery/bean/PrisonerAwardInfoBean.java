package com.nyiit.jailinquery.bean;

import java.util.List;

public class PrisonerAwardInfoBean {

    /**
     * count : 2
     * data : [{"zf_hjrq":"2019-07-19","zf_hjlx":"监狱表杨","zf_xm":"蒋平涛","zf_bh":"320500004077"},{"zf_hjrq":"2019-07-19","zf_hjlx":"监狱表杨","zf_xm":"蒋平涛","zf_bh":"320500004077"}]
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

    public String getPrisonerAwardInfo() {
        StringBuilder prisonerAwardInfoSb = new StringBuilder();
        //prisonerAwardInfoSb.append("犯人奖励信息");
        for (int i = 0; i < data.size(); i++) {
            prisonerAwardInfoSb.append(data.get(i).toString());
        }

        return prisonerAwardInfoSb.toString();
    }

    public static class DataBean {
        /**
         * zf_hjrq : 2019-07-19
         * zf_hjlx : 监狱表杨
         * zf_xm : 蒋平涛
         * zf_bh : 320500004077
         */

        private String zf_hjrq;
        private String zf_hjlx;
        private String zf_xm;
        private String zf_bh;

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

        @Override
        public String toString() {
            return zf_hjrq + " " +
                    "奖励类型: " + zf_hjlx + "\n";
        }
    }
}
