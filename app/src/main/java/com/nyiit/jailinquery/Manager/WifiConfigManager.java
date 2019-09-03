package com.nyiit.jailinquery.Manager;

import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.nyiit.jailinquery.bean.WifiSetting;
import com.nyiit.jailinquery.tools.LogUtil;
import com.nyiit.jailinquery.tools.ShellCommandExecutor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * wifi配置管理类
 *
 * @author zhangzhilin1991@sina.com
 * created on 2019/1/10
 */
@Singleton
public class WifiConfigManager {

    public static final String TAG = WifiConfigManager.class.getName();
    public static final int STATUS_CONNECT_FAILED = 0;
    public static final int STATUS_CONNECT_SUCCESS = 1;
    //WIFICIPHER_WEP是WEP ，WIFICIPHER_WPA是WPA，WIFICIPHER_NOPASS没有密码
    //public enum WifiCipherType {
    public static final int WIFI_WEP = 0;
    public static final int WIFI_WPA = 1;
    public static final int WIFI_NOPASS = 2;
    public static final int WIFI_INVALID = 3;
    //Handler mHandler;
    //@Inject
    WifiManager wifiManager;
    //@Inject
    ExecutorService executor;
    Context mContext;

    private volatile int netID = -1;

    @Inject
    public WifiConfigManager(Context mContext, WifiManager wifiManager, ExecutorService executor) {
        this.mContext = mContext;
        this.wifiManager = wifiManager;
        this.executor = executor;
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    private static void setIpAssignment(String assign, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    private static void setIpAddress(InetAddress addr, int prefixLength,
                                     WifiConfiguration wifiConf) throws SecurityException,
            IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException,
            InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class<?> laClass = Class.forName("android.net.LinkAddress");
        Constructor<?> laConstructor = laClass.getConstructor(new Class[]{

                InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);
        ArrayList<Object> mLinkAddresses = (ArrayList<Object>) getDeclaredField(
                linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    private static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    private static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    private static void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    private static void setGateway(InetAddress gateway, WifiConfiguration wifiConf)
            throws SecurityException,
            IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException, ClassNotFoundException,
            NoSuchMethodException, InstantiationException,
            InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        if (Build.VERSION.SDK_INT >= 14) { // android4.x版本
            Class<?> routeInfoClass = Class.forName("android.net.RouteInfo");
            Constructor<?> routeInfoConstructor = routeInfoClass
                    .getConstructor(new Class[]{InetAddress.class});
            Object routeInfo = routeInfoConstructor.newInstance(gateway);
            ArrayList<Object> mRoutes = (ArrayList<Object>) getDeclaredField(

                    linkProperties, "mRoutes");
            mRoutes.clear();
            mRoutes.add(routeInfo);
        } else { // android3.x版本
            ArrayList<InetAddress> mGateways = (ArrayList<InetAddress>) getDeclaredField(

                    linkProperties, "mGateways");
            //    mGateways.clear();
            mGateways.add(gateway);
        }
    }

    private static void setDNS(InetAddress dns, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>)

                getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); // 清除原有DNS设置（如果只想增加，不想清除，词句可省略）
        mDnses.add(dns);
        //增加新的DNS
    }

    // 提供一个外部接口，传入要连接的无线网

    /**
     * 异步连接
     *
     * @param wifiSetting
     * @param type
     * @param listener
     */
    public void connect(WifiSetting wifiSetting, int type, OnWifiConnectStatusChangedListener listener) {
        executor.execute(new ConnectRunnable(wifiSetting, type, listener));
    }

    /**
     * 同步连接
     *
     * @param wifiSetting
     * @param type
     */
    public synchronized boolean syncConnect(WifiSetting wifiSetting, int type) {
        //new ConnectRunnable(wifiSetting, type, listener).run();
        openWifi();
        //sendMsg("opened");
        LogUtil.d(TAG, "opening Wifi");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒检测……
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                break;
            }
        }

        //LogUtil.d(TAG, "open Wifi successful");

        LogUtil.d(TAG, "create wifi config");
        WifiConfiguration wifiConfig = createWifiInfo(wifiSetting.getSsid(), wifiSetting.getPassword(),
                type);
        //
        if (wifiConfig == null) {
            //sendMsg("wifiConfig is null!");
            LogUtil.d(TAG, "wifiConfig is null!");
            return false;
        }

        //LogUtil.d(TAG, "check wifi config");

        //WifiConfiguration tempConfig = isExsits(wifiSetting.getSsid());

        //int netID = -1;
        //if (tempConfig != null) {
        //    LogUtil.d(TAG, "removeNetwork: " + tempConfig.networkId);
        //    wifiManager.removeNetwork(tempConfig.networkId);
            //LogUtil.d(TAG, "updateNetwork");
            //wifiManager.updateNetwork(wifiConfig);
        //}
        //if (netID != -1) {
        //    LogUtil.d(TAG, "removeNetwork: " + netID);
         //   wifiManager.removeNetwork(netID);
         //   netID = -1;
        //}

        //LogUtil.d(TAG, "addNetwork!");
        //netID = wifiManager.addNetwork(wifiConfig);

        //LogUtil.d(TAG, "netID: " + netID);

        //if (netID == -1) {
        //    LogUtil.d(TAG, "addNetwork failed!");
        //    //return false;
        //}
        //int netID = wifiManager.addNetwork(wifiConfig);
        //LogUtil.d(TAG, "enbaleNetwork!");
        //boolean enabled = wifiManager.enableNetwork(netID, true);
        //sendMsg("enableNetwork status enable=" + enabled);
        //LogUtil.d(TAG, "enableNetwork status enable=" + enabled);
        boolean connected = wifiManager.reconnect();
        //sendMsg("enableNetwork connected=" + connected);
        LogUtil.d(TAG, "enableNetwork connected=" + connected);
        //sendMsg("连接成功!");
        //while ()

        return connected;
    }

    /**
     *
     * @return
     */
    public synchronized boolean enableWifi(String ssid) {
        //new ConnectRunnable(wifiSetting, type, listener).run();
        openWifi();
        //sendMsg("opened");
        LogUtil.d(TAG, "opening Wifi");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒检测……
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                break;
            }
        }

        LogUtil.d(TAG, "check wifi config");

        //WifiConfiguration tempConfig = isExsits(ssid);

        //int netID = -1;
        //if (tempConfig != null) {
        //    LogUtil.d(TAG, "removeNetwork: " + tempConfig.networkId);
            //wifiManager.removeNetwork(tempConfig.networkId);
        //LogUtil.d(TAG, "updateNetwork");
        //wifiManager.updateNetwork(wifiConfig);
        //}
        //if (netID != -1) {
        //    LogUtil.d(TAG, "removeNetwork: " + netID);
        //   wifiManager.removeNetwork(netID);
        //   netID = -1;
        //}

        //LogUtil.d(TAG, "addNetwork!");
        //netID = wifiManager.addNetwork(wifiConfig);

        //LogUtil.d(TAG, "netID: " + netID);

        //if (netID == -1) {
        //    LogUtil.d(TAG, "addNetwork failed!");
        //    //return false;
        //}
        //int netID = wifiManager.addNetwork(wifiConfig);
        //LogUtil.d(TAG, "enbaleNetwork!");
        //boolean enabled = wifiManager.enableNetwork(netID, true);

        boolean connected = wifiManager.reconnect();
        //sendMsg("enableNetwork connected=" + connected);
        LogUtil.d(TAG, "enableNetwork connected=" + connected);

        //sendMsg("连接成功!");
        return connected;
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID) {
        LogUtil.d(TAG, "isExsits() SSID： " + SSID);
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password,
                                             int Type) {
        LogUtil.d(TAG , "createWifiInfo()");
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // nopass
        if (Type == WIFI_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        // wep
        if (Type == WIFI_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wpa
        if (Type == WIFI_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    // 打开wifi功能
    private boolean openWifi() {
        LogUtil.d(TAG, "openWifi()");
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    private boolean closeWifi() {
        LogUtil.d(TAG, "closeWifi()");
        boolean bRet = true;
        if (wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(false);
        }
        return bRet;
    }

    /**
     * 关闭Wifi连接
     *
     * @return
     */
    public synchronized boolean disableWifi() {
        //if (netID != -1) {
        //    wifiManager.removeNetwork(netID);
        //    netID = -1;
        //}
        return closeWifi();
    }

    /**
     * 网关 。
     *
     * @param ip
     * @return
     */
    public String long2ip(long ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }

    public String intToIp(int ipAddress) {
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    public interface OnWifiConnectStatusChangedListener {
        void onWifiConnectStatusChanged(int status);
    }

    class ConnectRunnable implements Runnable {
        private int type;

        private WifiSetting wifiSetting;

        private OnWifiConnectStatusChangedListener listener;

        public ConnectRunnable(WifiSetting wifiSetting, int type, OnWifiConnectStatusChangedListener listener) {
            this.wifiSetting = wifiSetting;
            this.type = type;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                // 打开wifi
                openWifi();
                //sendMsg("opened");
                LogUtil.d(TAG, "opening Wifi");
                Thread.sleep(200);
                // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
                // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
                while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    try {
                        // 为了避免程序一直while循环，让它睡个100毫秒检测……
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                    }
                }

                LogUtil.d(TAG, "open Wifi successful");

                WifiConfiguration wifiConfig = createWifiInfo(wifiSetting.getSsid(), wifiSetting.getPassword(),
                        type);
                //
                if (wifiConfig == null) {
                    //sendMsg("wifiConfig is null!");
                    listener.onWifiConnectStatusChanged(STATUS_CONNECT_FAILED);
                    return;
                }

                WifiConfiguration tempConfig = isExsits(wifiSetting.getSsid());

                if (tempConfig != null) {
                    wifiManager.removeNetwork(tempConfig.networkId);
                }

                int netID = wifiManager.addNetwork(wifiConfig);
                boolean enabled = wifiManager.enableNetwork(netID, true);
                //sendMsg("enableNetwork status enable=" + enabled);
                LogUtil.d(TAG, "enableNetwork status enable=" + enabled);
                boolean connected = wifiManager.reconnect();
                //sendMsg("enableNetwork connected=" + connected);
                LogUtil.d(TAG, "enableNetwork connected=" + connected);
                //sendMsg("连接成功!");
                if (listener != null) {
                    listener.onWifiConnectStatusChanged(STATUS_CONNECT_SUCCESS);
                }
            } catch (Exception e) {
                // TODO: handle exception
                //sendMsg(e.getMessage());
                if (listener != null) {
                    listener.onWifiConnectStatusChanged(STATUS_CONNECT_FAILED);
                }
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置手机的移动数据
     */
    public void setMobileData(boolean pBoolean) {

        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;

            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);

            method.invoke(mConnectivityManager, pBoolean);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("移动数据设置错误: " + e.toString());
        }
    }

    /**
     * 返回手机移动数据的状态
     *
     * @param pContext
     * @param arg
     *            默认填null
     * @return true 连接 false 未连接
     */
    public static boolean getMobileDataState(Context pContext, Object[] arg) {

        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }

            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);

            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

            return isOpen;

        } catch (Exception e) {
            // TODO: handle exception

            System.out.println("得到移动数据状态出错");
            return false;
        }
    }


    public void setMobileDataState(boolean enabled) {
        TelephonyManager telephonyService = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method setDataEnabled = telephonyService.getClass().getDeclaredMethod("setDataEnabled",boolean.class);
            if (null != setDataEnabled) {
                setDataEnabled.invoke(telephonyService, enabled);
            }
        } catch (Exception e) {
            LogUtil.d(TAG, "setMobileDataState err: " + e.getMessage());
        }
    }

    /**
     * 设置4G网络状态
     * @param enabled
     */
    public void set4GNetworkState(boolean  enabled){
        LogUtil.d(TAG, "set4GNetworkState: " +enabled);
            if (enabled) {
               /* try {
                    Runtime.getRuntime().exec("su 0 svc data enable\n");
                } catch (IOException e) {
                    LogUtil.d(TAG, "打开4G失败: " + e.getMessage());
                }
                */
                //Runtime.getRuntime().exec("ls -al");
                new ShellCommandExecutor().addCommand("su 0 svc data enable").execute();
            } else {
                /*try {
                    Runtime.getRuntime().exec("su 0 svc data disable\n");
                } catch (IOException e) {
                    LogUtil.d(TAG, "关闭4G失败: " + e.getMessage());
                    e.printStackTrace();
                }
                */
                new ShellCommandExecutor().addCommand("su 0 svc data disable").execute();
            }
    }

    public boolean getMobileDataState(Context context) {
        TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method getDataEnabled = telephonyService.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getDataEnabled) {
                return (Boolean) getDataEnabled.invoke(telephonyService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    }

