package com.nyiit.jailinquery.Constant;

public class Constants {
    public static final String CONFIG = "config";

    public static final int MSG_INQUERYING_TIMEOUT = 0x100;
    public static final int MSG_SHOW_INQUERY_VIEW = 0x101;
    public static final int MSG_SHOW_INQUERYING_VIEW = 0x102;
    public static final int MSG_SHOW_INQUERY_FAILED_VIEW = 0x103;
    public static final int MSG_INQUER_TIMEOUT = 0x104;
    public static final int MSG_SHOW_INQUERY_SUCCESS_VIEW = 0x105;
    public static final int MSG_SHOW_CONNECTING_VIEW = 0x106;
    public static final int MSG_SHOW_CONNECTING_TIMEOUT = 0x107;
    public static final int MSG_SHOW_INQUERY_SUCCESS_FAKE_VIEW = 0x108;
    public static final int MSG_IDCARD_ACQUIRED = 0x109;
    public static final int MSG_IDCARD_CONFIRMED = 0x110;
    public static final int MSG_NETWORK_CHANGED = 0x111;
    public static final int MSG_INQUERY_SUCCESS = 0x112;
    public static final int MSG_INQUERY_FAILED = 0x113;
    public static final int MSG_NETWORK_SWITCH_TIMEOUT = 0x114;
    public static final int MSG_UPDATE_CHAT_MESSAGE = 0x115;

    public static final int INQUERRING_TIMEOUT = 10 * 1000;
    public static final int INQUERY_TIMEOUT = 2 * 60 * 1000;
    public static final int CONNECTING_TIMEOUT = 10 * 1000;
    public static final int NETWORK_SWITCH_TIMEOUT = 30 * 1000;


    //Wifi Config
    public static final String WIFI_SSID_KEY = "WIFI_SSID_KEY";
    public static final String WIFI_PWD_KEY = "WIFI_PWD_KEY";
    public static final String JAIL_CODE_KEY = "JAIL_CODE_KEY";
    public static final String DEFAULT_WIFI_SSID = "TP-LINK_74B91";
    public static final String DEFAULT_WIFI_PWD = "njupt12345";

    //Web service
    public static final String WSDL_URI = "http://26.127.0.16:8082/webserviceServer/services/soap/dsrzdService?wsdl";//wsdl 的uri
    public static final String NAMESPACE = "http://www.cesgroup.com.cn";//namespace ？？？？ TBD
    public static final String METHOD_GET_PRISONERINFO = "getZfxxByJyAndSfzId";//获取罪犯信息
    public static final String METHOD_GET_PRISONERPEANLTYINFO = "getZfJxByJyAndZfBh";//获取罪犯信息加减刑信息
    public static final String METHOD_GET_PRISONERAWARDINFO = "getZfJlByJyAndZfBh"; //获取罪犯奖励信息
    public static final String DEFAULT_JIANYU_BIANHAO= "3205";//监狱编号
    
}
