package com.nyiit.jailinquery.bean;

import java.io.Serializable;

public class InqueryResult implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -369558847578246550L;

    int count;
    String data;

    public InqueryResult(int count, String data) {
        this.count = count;
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
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
        return "InqueryResult{" +
                "count=" + count +
                ", data=" + data +
                '}';
    }
}
