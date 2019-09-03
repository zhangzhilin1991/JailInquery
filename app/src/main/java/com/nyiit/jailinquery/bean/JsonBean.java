package com.nyiit.jailinquery.bean;

public class JsonBean {

    /**
     * count : 1
     * data : [{"zf_zhye":"0","zf_xq":"无期","zf_xmrq":"2010-09-28","zf_yx":"","zf_rjrq":"2001-10-15","zf_xm":"张步军","zf_zm":"故意伤害罪","zf_bh":"320100001245","zf_nl":"48"}]
     */

    private String count;
    private String data;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JsonBean{" +
                "count='" + count + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
