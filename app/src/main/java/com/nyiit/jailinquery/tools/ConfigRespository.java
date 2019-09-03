package com.nyiit.jailinquery.tools;

import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;

import com.nyiit.jailinquery.bean.WifiSetting;
import com.nyiit.jailinquery.viewmodel.SharePreferencesLiveData;

import java.util.Map;

import static com.nyiit.jailinquery.Constant.Constants.DEFAULT_JIANYU_BIANHAO;
import static com.nyiit.jailinquery.Constant.Constants.DEFAULT_WIFI_PWD;
import static com.nyiit.jailinquery.Constant.Constants.DEFAULT_WIFI_SSID;
import static com.nyiit.jailinquery.Constant.Constants.JAIL_CODE_KEY;
import static com.nyiit.jailinquery.Constant.Constants.WIFI_PWD_KEY;
import static com.nyiit.jailinquery.Constant.Constants.WIFI_SSID_KEY;

/**
 * 配置工具
 */
public class ConfigRespository {
    private volatile static ConfigRespository sInstance;

    private final SharedPreferences sp;
    private SharePreferencesLiveData mObservableConfigs;

    private ConfigRespository(final SharedPreferences sp) {
        this.sp = sp;
        mObservableConfigs = new SharePreferencesLiveData(sp);

        //LiveData<Map<String, ?>> sourceData = mObservableConfigs.getValue();

        /*mObservableConfigs.addSource(sourceData,
                new Observer<Map<String, ?>>() {
                    @Override
                    public void onChanged(@Nullable Map<String, ?> configs) {
                        mObservableConfigs.postValue(configs);
                    }
                });
                */
    }

    public static ConfigRespository getInstance(final SharedPreferences sp) {
        if (sInstance == null) {
            synchronized (ConfigRespository.class) {
                if (sInstance == null) {
                    sInstance = new ConfigRespository(sp);
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取livedata对象
     *
     * @return
     */
    public LiveData<Map<String, ?>> getConfig() {
        return mObservableConfigs;
    }

    /**
     * 获取当前的wifi SSID
     *
     * @return
     */
    public String getWifiSSID() {
        return sp.getString(WIFI_SSID_KEY, DEFAULT_WIFI_SSID);
    }

    /**
     * 保存wifi SSID
     *
     * @param ssid
     * @return
     */
    public boolean configWifiSSID(String ssid) {
        return sp.edit().putString(WIFI_SSID_KEY, ssid).commit();
    }

    /**
     * 获取当前的wifi pwd
     *
     * @return
     */
    public String getWifiPwd() {
        return sp.getString(WIFI_PWD_KEY, DEFAULT_WIFI_PWD);
    }

    /**
     * 保存wifi pwd
     *
     * @param pwd
     * @return
     */
    public boolean configWifiPwd(String pwd) {
        return sp.edit().putString(WIFI_PWD_KEY, pwd).commit();
    }

    /**
     * 获取当前的wifi pwd
     *
     * @return
     */
    public String getJailCode() {
        return sp.getString(JAIL_CODE_KEY, DEFAULT_JIANYU_BIANHAO);
    }

    /**
     * 保存wifi pwd
     *
     * @param jailCode
     * @return
     */
    public boolean configJailCode(String jailCode) {
        return sp.edit().putString(JAIL_CODE_KEY, jailCode).commit();
    }

    public boolean setConfig(WifiSetting wifiSetting) {
        return sp.edit().putString(WIFI_SSID_KEY, wifiSetting.getSsid())
                .putString(WIFI_PWD_KEY, wifiSetting.getPassword())
                .commit();
    }
}
