package com.nyiit.jailinquery.bean;

import java.util.List;

public class PrisonerPenaltyInfoBean {

    /**
     * count : 2
     * data : [{"zf_bdrq":"2019-07-19","zf_bdlx":"减刑","zf_xm":"蒋平涛","zf_bh":"320500004077"},{"zf_bdrq":"2019-07-19","zf_bdlx":"减刑","zf_xm":"蒋平涛","zf_bh":"320500004077"}]
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

    public String getPrisonerPenaltyInfo() {
        //String prisonerPenaltyInfo = data.toString();
        StringBuilder prisonerPenaltyInfoSb = new StringBuilder();
        //prisonerPenaltyInfoSb.append("犯人加减刑信息");
        for (int i = 0; i < data.size(); i++) {
            prisonerPenaltyInfoSb.append(data.get(i).toString());
        }

        return prisonerPenaltyInfoSb.toString();
    }

    public static class DataBean {
        /**
         * zf_bdrq : 2019-07-19
         * zf_bdlx : 减刑
         * zf_xm : 蒋平涛
         * zf_bh : 320500004077
         */

        private String zf_bdrq;
        private String zf_bdlx;
        private String zf_xm;
        private String zf_bh;

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
            return zf_bdrq + " " +
                    "加减刑类型: " + zf_bdlx + "\n";
        }
    }
}
