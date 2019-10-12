package com.nyiit.jailinquery.Manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nyiit.jailinquery.bean.PrisonerAwardInfoBean;
import com.nyiit.jailinquery.bean.PrisonerInfoBean;
import com.nyiit.jailinquery.bean.PrisonerPenaltyInfoBean;
import com.nyiit.jailinquery.bean.WifiSetting;
import com.nyiit.jailinquery.tools.LogUtil;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.nyiit.jailinquery.Constant.Constants.METHOD_GET_PRISONERAWARDINFO;
import static com.nyiit.jailinquery.Constant.Constants.METHOD_GET_PRISONERINFO;
import static com.nyiit.jailinquery.Constant.Constants.METHOD_GET_PRISONERPEANLTYINFO;
import static com.nyiit.jailinquery.Constant.Constants.NAMESPACE;
import static com.nyiit.jailinquery.Constant.Constants.WSDL_URI;

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

    private boolean isTest;

    @Inject
    public InqueryManager(WifiConfigManager mWifiConfigManager, ExecutorService executorService) {
        this.mWifiConfigManager = mWifiConfigManager;
        this.executorService = executorService;
    }

    public void setTest(Boolean isTest)
    {
        this.isTest = isTest;
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
                PrisonerInfoBean.DataBean prisonerInfo = inqueryPrisonerInfo(jailCode, searchText);
                String prisonerPenaltyInfo = null;
                String prisonerAwardInfo = null;
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

    private void notifityInquerySuccess(PrisonerInfoBean.DataBean prisonerInfo, String prisonerPenaltyInfo
            , String prisonerAwardInfo, InqueryingCallBack inqueryingCallBack) {
        if (inqueryingCallBack != null) {
            inqueryingCallBack.onSuccess(prisonerInfo, prisonerPenaltyInfo, prisonerAwardInfo);
        }
    }

    /**
     * 获取PrisonerInfo
     * @return
     */
    private PrisonerInfoBean.DataBean inqueryPrisonerInfo(String jailCode, String id) {
        LogUtil.d(TAG, "inqueryPrisonerInfo() jailCode: " + jailCode + ", id: " + id);
        String result;
        if (isTest) {
            result = getTestPeisonerInfoJson();
        } else {
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
            LogUtil.i(TAG, "requestDump" + httpTransportSE.requestDump);
            LogUtil.i(TAG, "responseDump" + httpTransportSE.responseDump);

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            result = object.getProperty(0).toString();
        }
        if (result == null) {
            LogUtil.d(TAG, "inqueryPrisonerInfo() result is null!, return");
            return null;
        }
        LogUtil.d(TAG, "inqueryPrisonerInfo() result: " + result);
        //return result;
        LogUtil.d(TAG, "inqueryPrisonerInfo() parse json:");
        List<PrisonerInfoBean> jbs = new Gson().fromJson(result,
                new TypeToken<List<PrisonerInfoBean>>() {}.getType());
        if (jbs == null || jbs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerInfo() PrisonerInfoBean is invalid");
            return  null;
        }
        LogUtil.d(TAG, "inqueryPrisonerInfo() PrisonerInfoBean； " + jbs.get(0).getData());
        if (jbs.get(0).getCount().equals("0")) {
            LogUtil.d(TAG, "inqueryPrisonerInfo() no data found!, return!");
            return null;
        }
        //List<PrisonerInfo> irs = new Gson().fromJson(jbs.get(0).getData(),
        //        new TypeToken<List<PrisonerInfo>>() {}.getType());
        List<PrisonerInfoBean.DataBean> irs = jbs.get(0).getData();
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
    private String inqueryPrisonerAwardInfo(String jailCode, String prisonerCode) {
        LogUtil.d(TAG, "inqueryPrisonerAwardInfo() jailCode: " + jailCode + ", prisonerCode: " + prisonerCode);
        String result;
        if (isTest) {
            result = getTestPrisonerAwardInfoJson();
        } else {
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
            LogUtil.i(TAG, "requestDump" + httpTransportSE.requestDump);
            LogUtil.i(TAG, "responseDump" + httpTransportSE.responseDump);

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            result = object.getProperty(0).toString();
        }
        if (result == null) {
            LogUtil.d(TAG, "inqueryPrisonerAwardInfo() result is null!, return");
            return null;
        }
        LogUtil.d(TAG, "inqueryPrisonerAwardInfo()  result: " + result);
        //return result;
        LogUtil.d(TAG, "inqueryPrisonerAwardInfo()  parse json:");
        List<PrisonerAwardInfoBean> jbs = new Gson().fromJson(result,
                new TypeToken<List<PrisonerAwardInfoBean>>() {}.getType());
        if (jbs == null || jbs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerAwardInfo() PrisonerAwardInfo is invalid");
            return  null;
        }
        LogUtil.d(TAG, "inqueryPrisonerAwardInfo() PrisonerAwardInfoBean； " + jbs.get(0).getData());
        if (jbs.get(0).getCount().equals("0")) {
            LogUtil.d(TAG, "inqueryPrisonerAwardInfo() no data found!, return!");
            return null;
        }
        //List<PrisonerAwardInfo> irs = new Gson().fromJson(jbs.get(0).getData(),
        //        new TypeToken<List<PrisonerAwardInfo>>() {}.getType());
        List<PrisonerAwardInfoBean.DataBean> irs = jbs.get(0).getData();
        if (irs == null || irs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerAwardInfo() irs is invalid");
        }
        //LogUtil.d(TAG, "PrisonerAwardInfo: " + irs.get(0));
        return jbs.get(0).getPrisonerAwardInfo();
    }

    /**
     * 获取PrisonerPenaltyInfo
     * @return
     */
    private String inqueryPrisonerPenaltyInfo(String jailCode, String prisonerCode) {
        LogUtil.d(TAG, "inqueryPrisonerPenaltyInfo() jailCode: " + jailCode + ", prisonerCode: " + prisonerCode);
        String result;
        if (isTest) {
                result = getTestPeisonerPenaltyInfoJson();
        } else {
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
            LogUtil.i(TAG, "requestDump" + httpTransportSE.requestDump);
            LogUtil.i(TAG, "responseDump" + httpTransportSE.responseDump);

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            result = object.getProperty(0).toString();
        }
        if (result == null) {
            LogUtil.d(TAG, "iinqueryPrisonerPenaltyInfo() result is null!, return");
            return null;
        }
        LogUtil.d(TAG, "iinqueryPrisonerPenaltyInfo() result: " + result);
        //return result;
        LogUtil.d(TAG, "iinqueryPrisonerPenaltyInfo() parse json:");
        List<PrisonerPenaltyInfoBean> jbs = new Gson().fromJson(result,
                new TypeToken<List<PrisonerPenaltyInfoBean>>() {}.getType());
        if (jbs == null || jbs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerPenaltyInfo() PrisonerPenaltyInfoBean is invalid");
            return  null;
        }
        LogUtil.d(TAG, "inqueryPrisonerPenaltyInfo() PrisonerPenaltyInfo： " + jbs.get(0).getData());
        if (jbs.get(0).getCount().equals("0")) {
            LogUtil.d(TAG, "inqueryPrisonerPenaltyInfo() no data found!, return!");
            return null;
        }
        //List<PrisonerPenaltyInfo> irs = new Gson().fromJson(jbs.get(0).getData(),
        //        new TypeToken<List<PrisonerPenaltyInfo>>() {}.getType());
        List<PrisonerPenaltyInfoBean.DataBean> irs = jbs.get(0).getData();
        if (irs == null || irs.size() == 0) {
            LogUtil.d(TAG, "inqueryPrisonerPenaltyInfo() irs is invalid");
            return null;
        }
        //LogUtil.d(TAG, "PrisonerPenaltyInfo: " + irs.get(0).getZf_xm());
        return jbs.get(0).getPrisonerPenaltyInfo();
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
        void onSuccess(PrisonerInfoBean.DataBean prisonerInfo, String prisonerPenaltyInfo
                , String prisonerAwardInfo);

        void onFaild(int resaon);
    }

    /**
     * Peisonerinfo测试数据
     * @return
     */
    private String getTestPeisonerInfoJson()
    {
        return  "[\n" +
                " {\n" +
                "  \"count\" : \"1\",\n" +
                "  \"data\" : [\n" +
                "            {\"zf_zhye\":\"0\",\"zf_xq\":\"无期\",\"zf_xmrq\":\"2010-09-28\",\"zf_yx\":\"\",\"zf_rjrq\":\"2001-10-15\",\"zf_xm\":\"张步军\",\"zf_zm\":\"故意伤害罪\",\"zf_bh\":\"320100001245\",\"zf_nl\":\"48\"}\n" +
                "           ]\n" +
                " }\n" +
                "]";
    }

    /**
     * PeisonerAwardinfo测试数据
     * @return
     */
    private String getTestPrisonerAwardInfoJson()
    {
        return  "[\n" +
                " {\n" +
                "  \"count\": \"2\", \n" +
                "  \"data\": [\n" +
                "           {\"zf_hjrq\": \"2018-07-19\", \"zf_hjlx\": \"监狱表杨\", \"zf_xm\": \"蒋平涛\", \"zf_bh\": \"320500004077\"}, \n" +
                "           {\"zf_hjrq\": \"2019-07-19\", \"zf_hjlx\": \"监狱表杨\", \"zf_xm\": \"蒋平涛\", \"zf_bh\": \"320500004077\"}\n" +
                "          ] \n" +
                " }\n" +
                "]";
    }

    /**
     * tPeisonerPenaltyinfo测试数据
     * @return
     */
    private String getTestPeisonerPenaltyInfoJson()
    {
        return  "[\n" +
                " {\n" +
                "  \"count\": \"2\", \n" +
                "  \"data\": [\n" +
                "           {\"zf_bdrq\": \"2018-07-19\", \"zf_bdlx\": \"减刑\", \"zf_xm\": \"蒋平涛\", \"zf_bh\": \"320500004077\"}, \n" +
                "           {\"zf_bdrq\": \"2019-07-19\", \"zf_bdlx\": \"减刑\", \"zf_xm\": \"蒋平涛\", \"zf_bh\": \"320500004077\"}\n" +
                "          ] \n" +
                " }\n" +
                "]";
    }


}
