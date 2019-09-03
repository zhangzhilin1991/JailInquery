package com.nyiit.jailinquery.Manager;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nyiit.jailinquery.BuildConfig;
import com.nyiit.jailinquery.bean.InqueryResult;
import com.nyiit.jailinquery.bean.JsonBean;
import com.nyiit.jailinquery.bean.PrisonerAwardInfo;
import com.nyiit.jailinquery.bean.PrisonerInfo;
import com.nyiit.jailinquery.bean.PrisonerPenaltyInfo;
import com.nyiit.jailinquery.bean.WifiSetting;
import com.nyiit.jailinquery.tools.LogUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.nyiit.jailinquery.Constant.Constants.DEFAULT_JIANYU_BIANHAO;
import static com.nyiit.jailinquery.Constant.Constants.METHOD_GET_PRISONERAWARDINFO;
import static com.nyiit.jailinquery.Constant.Constants.METHOD_GET_PRISONERINFO;
import static com.nyiit.jailinquery.Constant.Constants.METHOD_GET_PRISONERPEANLTYINFO;
import static com.nyiit.jailinquery.Constant.Constants.NAMESPACE;
import static com.nyiit.jailinquery.Constant.Constants.WSDL_URI;
import static com.nyiit.jailinquery.Manager.WifiConfigManager.WIFI_WPA;

/**
 * 查询犯人信息管理类
 */
@Singleton
public class InqueryManager {
    private static final String TAG = InqueryManager.class.getName();

    public static final int ERR_CONNECT_WIFI_FAILED = 1;
    public static final int ERR_INQUERYING_FAILED = 2;
    WifiConfigManager mWifiConfigManager;
    ExecutorService executorService;
    private Future<String> future;

    @Inject
    public InqueryManager(WifiConfigManager mWifiConfigManager, ExecutorService executorService) {
        this.mWifiConfigManager = mWifiConfigManager;
        this.executorService = executorService;
    }

    /**
     * 异步查询接口
     */
    public synchronized void startInquery(final String jailCode, final String searchText, final WifiSetting wifiSetting, final InqueryingCallBack inqueryingCallBack) {
        future = executorService.submit(new Callable<String>() {
            @Override
            public String call() {
                if (mWifiConfigManager == null) {
                    notifityInqueryFaild(ERR_CONNECT_WIFI_FAILED, inqueryingCallBack);
                    return null;
                }
                //testSoap();
                //查询时连接
                //boolean isConnected = mWifiConfigManager.syncConnect(wifiSetting, WIFI_WPA);
                //boolean isConnected = mWifiConfigManager.enableWifi("TP-hjl");
                /*if (isConnected == false) {
                    LogUtil.d(TAG, "WIFI CONNECT FAILED");
                    //mWifiConfigManager.disconnectWifi();
                    mWifiConfigManager.disableWifi();
                    notifityInqueryFaild(ERR_CONNECT_WIFI_FAILED, inqueryingCallBack);
                    return null;
                }
                */
                //setMobileData()

                //mWifiConfigManager.setMobileData(false);
                //mWifiConfigManager.setMobileDataState(false);
                //mWifiConfigManager.set4GNetworkState(false);
                //LogUtil.d(TAG, "onWifiConnectStatusChanged: CONNECTED");
                //if (BuildConfig.DEBUG) {
                //    notifityInquerySuccess(null, null, null, inqueryingCallBack);
                //    return null;
               // }
                //try {
            //PrisonerInfo prisonerInfo = inqueryPrisonerInfo(jailCode, searchText);
                PrisonerInfo prisonerInfo = inqueryPrisonerInfo(jailCode, searchText);
                PrisonerPenaltyInfo prisonerPenaltyInfo = null;
                PrisonerAwardInfo prisonerAwardInfo = null;
                if (prisonerInfo != null) {
                    prisonerPenaltyInfo = inqueryPrisonerPenaltyInfo(jailCode, prisonerInfo.getZf_bh());
                    prisonerAwardInfo = inqueryPrisonerAwardInfo(jailCode, prisonerInfo.getZf_bh());
                }
                //prisonerPenaltyInfo = inqueryPrisonerPenaltyInfo(jailCode, "320500003873");
                //prisonerAwardInfo = inqueryPrisonerAwardInfo(jailCode, prisonerInfo.getZf_bh());

                //} catch ()
                if ((prisonerInfo == null) && (prisonerPenaltyInfo == null) && (prisonerAwardInfo == null)) {
                    notifityInqueryFaild(ERR_INQUERYING_FAILED, inqueryingCallBack);
                } else {
                    notifityInquerySuccess(prisonerInfo, prisonerPenaltyInfo, prisonerAwardInfo, inqueryingCallBack);
                }
                //查询后断开
                //mWifiConfigManager.disableWifi();
                //mWifiConfigManager.setMobileDataState(true);
                return null;
            }
        });
    }

    public synchronized void stopInquery() {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }

    private void notifityInqueryFaild(int resaon, InqueryingCallBack inqueryingCallBack) {
        if (inqueryingCallBack != null) {
            inqueryingCallBack.onFaild(resaon);
        }
    }

    private void notifityInquerySuccess(PrisonerInfo prisonerInfo, PrisonerPenaltyInfo prisonerPenaltyInfo
            , PrisonerAwardInfo prisonerAwardInfo, InqueryingCallBack inqueryingCallBack) {
        if (inqueryingCallBack != null) {
            inqueryingCallBack.onSuccess(prisonerInfo, prisonerPenaltyInfo, prisonerAwardInfo);
        }
    }

    /**
     * 获取PrisonerInfo
     * @return
     */
    private PrisonerInfo inqueryPrisonerInfo(String jailCode, String id) {
        LogUtil.d(TAG, "inqueryPrisonerInfo() jailCode: " + jailCode + ", id: " + id);
        SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_PRISONERINFO);
        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        request.addProperty("arg0", jailCode);
        request.addProperty("arg1", id);

        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = "utf-8";
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;//由于是.net开发的webservice，所以这里要设置为true

        HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
        httpTransportSE.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

        httpTransportSE.debug = true;
        //LogUtil.d(TAG,"requestDump : " + httpTransportSE.requestDump);
        try {
            LogUtil.d(TAG, "call()");
            httpTransportSE.call(null, envelope);//调用
        } catch (Exception e) {
            LogUtil.d(TAG, "httpTransportSE.call exception: " + e.getMessage());
            return null;
        }
        LogUtil.i(TAG,"requestDump" + httpTransportSE.requestDump);
        LogUtil.i(TAG,"responseDump" + httpTransportSE.responseDump);

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        String result = object.getProperty(0).toString();
        if (result == null) {
            LogUtil.d(TAG, "inqueryPrisonerInfo() result is null!, return");
            return null;
        }
        LogUtil.d(TAG, "inqueryPrisonerInfo() result: " + result);
        //return result;
        LogUtil.d(TAG, "inqueryPrisonerInfo() parse json:");
        List<JsonBean> jbs = new Gson().fromJson(result,
                new TypeToken<List<JsonBean>>() {}.getType());
        if (jbs == null || jbs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerInfo() JsonBean is invalid");
            return  null;
        }
        LogUtil.d(TAG, "inqueryPrisonerInfo() JsonBean； " + jbs.get(0));
        if (jbs.get(0).getCount().equals("0")) {
            LogUtil.d(TAG, "inqueryPrisonerInfo() no data found!, return!");
            return null;
        }
        List<PrisonerInfo> irs = new Gson().fromJson(jbs.get(0).getData(),
                new TypeToken<List<PrisonerInfo>>() {}.getType());
        if (irs == null || irs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerInfo() irs is invalid");
        }
        LogUtil.d(TAG, "PrisonerInfo: " + irs.get(0));
        return irs.get(0);
    }

    /**
     * 获取PrisonerAwardInfo
     * @return
     */
    private PrisonerAwardInfo inqueryPrisonerAwardInfo(String jailCode, String prisonerCode) {
        LogUtil.d(TAG, "inqueryPrisonerAwardInfo() jailCode: " + jailCode + ", prisonerCode: " + prisonerCode);
        SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_PRISONERAWARDINFO);
        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        request.addProperty("arg0", jailCode);
        request.addProperty("arg1", prisonerCode);

        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = "utf-8";
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;//由于是.net开发的webservice，所以这里要设置为true

        HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
        httpTransportSE.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

        httpTransportSE.debug = true;
        //LogUtil.d(TAG,"requestDump : " + httpTransportSE.requestDump);
        try {
            LogUtil.d(TAG, "call()");
            httpTransportSE.call(null, envelope);//调用
        } catch (Exception e) {
            LogUtil.d(TAG, "httpTransportSE.call exception: " + e.getMessage());
            return null;
        }
        LogUtil.i(TAG,"requestDump" + httpTransportSE.requestDump);
        LogUtil.i(TAG,"responseDump" + httpTransportSE.responseDump);

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        String result = object.getProperty(0).toString();
        if (result == null) {
            LogUtil.d(TAG, "inqueryPrisonerAwardInfo() result is null!, return");
            return null;
        }
        LogUtil.d(TAG, "inqueryPrisonerAwardInfo()  result: " + result);
        //return result;
        LogUtil.d(TAG, "inqueryPrisonerAwardInfo()  parse json:");
        List<JsonBean> jbs = new Gson().fromJson(result,
                new TypeToken<List<JsonBean>>() {}.getType());
        if (jbs == null || jbs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerAwardInfo()  JsonBean is invalid");
            return  null;
        }
        LogUtil.d(TAG, "inqueryPrisonerAwardInfo()  JsonBean； " + jbs.get(0));
        if (jbs.get(0).getCount().equals("0")) {
            LogUtil.d(TAG, "inqueryPrisonerAwardInfo() no data found!, return!");
            return null;
        }
        List<PrisonerAwardInfo> irs = new Gson().fromJson(jbs.get(0).getData(),
                new TypeToken<List<PrisonerAwardInfo>>() {}.getType());
        if (irs == null || irs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerAwardInfo() irs is invalid");
        }
        LogUtil.d(TAG, "PrisonerAwardInfo: " + irs.get(0));
        return irs.get(0);
    }

    /**
     * 获取PrisonerPenaltyInfo
     * @return
     */
    private PrisonerPenaltyInfo inqueryPrisonerPenaltyInfo(String jailCode, String prisonerCode) {
        LogUtil.d(TAG, "inqueryPrisonerPenaltyInfo() jailCode: " + jailCode + ", prisonerCode: " + prisonerCode);
        SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_PRISONERPEANLTYINFO);
        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        request.addProperty("arg0", jailCode);
        request.addProperty("arg1", prisonerCode);

        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = "utf-8";
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;//由于是.net开发的webservice，所以这里要设置为true

        HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
        httpTransportSE.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

        httpTransportSE.debug = true;
        //LogUtil.d(TAG,"requestDump : " + httpTransportSE.requestDump);
        try {
            LogUtil.d(TAG, "call()");
            httpTransportSE.call(null, envelope);//调用
        } catch (Exception e) {
            LogUtil.d(TAG, "httpTransportSE.call exception: " + e.getMessage());
            return null;
        }
        LogUtil.i(TAG,"requestDump" + httpTransportSE.requestDump);
        LogUtil.i(TAG,"responseDump" + httpTransportSE.responseDump);

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        String result = object.getProperty(0).toString();
        if (result == null) {
            LogUtil.d(TAG, "iinqueryPrisonerPenaltyInfo() result is null!, return");
            return null;
        }
        LogUtil.d(TAG, "iinqueryPrisonerPenaltyInfo() result: " + result);
        //return result;
        LogUtil.d(TAG, "iinqueryPrisonerPenaltyInfo() parse json:");
        List<JsonBean> jbs = new Gson().fromJson(result,
                new TypeToken<List<JsonBean>>() {}.getType());
        if (jbs == null || jbs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerPenaltyInfo() JsonBean is invalid");
            return  null;
        }
        LogUtil.d(TAG, "inqueryPrisonerPenaltyInfo() JsonBean； " + jbs.get(0));
        if (jbs.get(0).getCount().equals("0")) {
            LogUtil.d(TAG, "inqueryPrisonerPenaltyInfo() no data found!, return!");
            return null;
        }
        List<PrisonerPenaltyInfo> irs = new Gson().fromJson(jbs.get(0).getData(),
                new TypeToken<List<PrisonerPenaltyInfo>>() {}.getType());
        if (irs == null || irs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerPenaltyInfo() irs is invalid");
        }
        LogUtil.d(TAG, "PrisonerPenaltyInfo: " + irs.get(0));
        return irs.get(0);
    }

    private void testSoap() {
        String NAMESPACE = "http://WebXml.com.cn/";

        String URL = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx?WSDL";

        String METHOD_NAME = "getRegionCountry";

       String SOAP_ACTION = "http://WebXml.com.cn/getRegionCountry";
        LogUtil.d("getWeather", "getWeather start...");
        SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
        //rpc.addProperty("theCityName", "南京");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(rpc);
        int MSG_TIMEOUT = 15000;
        HttpTransportSE ht = new HttpTransportSE(URL,MSG_TIMEOUT);
        ht.debug = true;
        try {
            ht.call(SOAP_ACTION, envelope);
            SoapObject result=(SoapObject)envelope.getResponse();
            //updateUI(result);//这个是我自己的方法
            LogUtil.d(TAG, "testSoap() result: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public interface InqueryingCallBack {
        void onSuccess(PrisonerInfo prisonerInfo, PrisonerPenaltyInfo prisonerPenaltyInfo
                , PrisonerAwardInfo prisonerAwardInfo);

        void onFaild(int resaon);
    }
}
