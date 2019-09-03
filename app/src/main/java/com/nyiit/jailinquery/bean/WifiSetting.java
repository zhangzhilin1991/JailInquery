package com.nyiit.jailinquery.bean;

/**
 * wifi ssid和密码。
 */
public class WifiSetting {
    private String ssid;
    private String password;

    public WifiSetting(String ssid, String password) {
        this.ssid = ssid;
        this.password = password;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "WifiSetting{" +
                "ssid='" + ssid + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
