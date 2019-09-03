package com.nyiit.jailinquery.viewmodel;

import android.arch.lifecycle.MediatorLiveData;
import android.content.SharedPreferences;

import com.nyiit.jailinquery.tools.LogUtil;

import java.util.HashMap;
import java.util.Map;

import static com.nyiit.jailinquery.Constant.Constants.DEFAULT_JIANYU_BIANHAO;
import static com.nyiit.jailinquery.Constant.Constants.DEFAULT_WIFI_PWD;
import static com.nyiit.jailinquery.Constant.Constants.DEFAULT_WIFI_SSID;
import static com.nyiit.jailinquery.Constant.Constants.JAIL_CODE_KEY;
import static com.nyiit.jailinquery.Constant.Constants.WIFI_PWD_KEY;
import static com.nyiit.jailinquery.Constant.Constants.WIFI_SSID_KEY;

public class SharePreferencesLiveData extends MediatorLiveData<Map<String, ?>> {

    public static final String TAG = SharePreferencesLiveData.class.getName();

    private SharedPreferences sharedPreferences;

    //private List<String> keyList = Collections.synchronizedList(new ArrayList<String>());

    //private volatile boolean isChanged = false;

    //private String value;
    //private String key;
    //private String defaultValue;

    //private Map<String, ?> valueMaps;
    //private String key;

    //private Config config;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            LogUtil.d(TAG, "onSharedPreferenceChanged() key: " + key);
            //if (SharePreferencesLiveData.this.key.equals(key)) {
            //setValue(getValueFromPreferences(key, defaultValue));
            //
            //valueMaps.entrySet();
            //}
            //setValue();
            //isChanged = true;
            //valueMaps = sharedPreferences.getAll();
            //if (valueMaps == null) {
            //    LogUtil.d(TAG, "onSharedPreferenceChanged() valuesmaps is null");
            //    return;
            //}
            //LogUtil.d(TAG, "onSharedPreferenceChanged() valueMaps:" + valueMaps.size());
            //for (Map.Entry<String, ?> entry : valueMaps.entrySet()) {
            //    LogUtil.d(TAG, "onSharedPreferenceChanged() key: " + entry.getKey() + ", value: " + entry.getValue());
            //}
            //Config config = getConfig(key, sharedPreferences);
            Map<String, ?> valueMaps = getConfig(key, sharedPreferences);
            if (valueMaps == null || valueMaps.size() == 0) {
                return;
            }
            postValue(valueMaps);
        }
    };

    public SharePreferencesLiveData(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        //this.key = key;
        //this.defaultValue = defaultValue;
        //valueMaps = sharedPreferences.getAll();
    }

    @Override
    protected void onActive() {
        super.onActive();
        LogUtil.d(TAG, "onActive()");
        //value = getValueFromPreferences(key, defaultValue);
        //if (isChanged) {
        //Config config = getConfig();
        Map<String, ?> valueMaps = sharedPreferences.getAll();
        if (valueMaps != null) {
            postValue(valueMaps);
        }
        //    isChanged = false;
        //}
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        LogUtil.d(TAG, "onInactive()");
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    private String getValueFromPreferences(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    /*private Config getConfig() {
        Map<String, ?> maps = sharedPreferences.getAll();
        if (maps == null) {
            LogUtil.d(TAG, "getConfig() maps is null");
            return null;
        }
        Config config = new Config();
        for (Map.Entry<String, ?> entry : maps.entrySet()) {
        LogUtil.d(TAG, "getConfig() key: " + entry.getKey() + ", value: " + entry.getValue());
                switch (entry.getKey()) {
                    case FACERECO_THRESHOLD_KEY:
                        config.setFaceRecoThreshold(((Integer) entry.getValue()).intValue());
                        break;
                    case FACERECO_INTERVAL_TIME_KEY:
                        config.setFaceRecoIntervalTime(((Integer) entry.getValue()).intValue());
                        break;
                    case FACERECO_GATE_OPEN_TIME_KEY:
                        config.setGateOpenDuration(((Integer) entry.getValue()).intValue());
                        break;
                    case TTS_GREETING_KEY:
                        config.setTtsGreeting((String) entry.getValue());
                        break;
                    case TTS_SPEAKER_KEY:
                        config.setTtsSpeaker((String) entry.getValue());
                        break;
                    case TTS_PITCH_KEY:
                        config.setTtsPitch(((Integer) entry.getValue()).intValue());
                        break;
                    case TTS_SPEED_KEY:
                        config.setTtsSpeed(((Integer) entry.getValue()).intValue());
                        break;
                    case TTS_VOLUME_KEY:
                        config.setTtsVolume(((Integer) entry.getValue()).intValue());
                        break;
                        default:
                            LogUtil.d(TAG, "invalid key:" + entry.getKey());
                }
            }

        return config;
    }
    */

    /**
     * 获取已经改变的Config
     *
     * @param key
     * @return
     */
    private Map<String, ?> getConfig(String key, SharedPreferences sp) {
        if (key == null || sp == null) {
            LogUtil.d(TAG, "getConfig() invalid params");
            return null;
        }

        switch (key) {
            case WIFI_SSID_KEY:
                Map<String, String> valueMaps1 = new HashMap<>();
                valueMaps1.put(key, sp.getString(WIFI_SSID_KEY, DEFAULT_WIFI_SSID));
                return valueMaps1;
            case WIFI_PWD_KEY:
                //config.setFaceRecoIntervalTime(sp.getInt(FACERECO_INTERVAL_TIME_KEY, FACE_DETECT_INTERVEL_TIME));
                //break;
                Map<String, String> valueMaps2 = new HashMap<>();
                valueMaps2.put(key, sp.getString(WIFI_PWD_KEY, DEFAULT_WIFI_PWD));
                return valueMaps2;
            case JAIL_CODE_KEY:
                Map<String, String> valueMaps3 = new HashMap<>();
                valueMaps3.put(key, sp.getString(JAIL_CODE_KEY, DEFAULT_JIANYU_BIANHAO));
                return valueMaps3;
            default:
                LogUtil.d(TAG, "invalid key:" + key);
        }
        return null;
    }
}
