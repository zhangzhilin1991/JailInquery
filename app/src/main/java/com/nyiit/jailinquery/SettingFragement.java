package com.nyiit.jailinquery;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import static com.nyiit.jailinquery.Constant.Constants.CONFIG;

/**
 * @author zhangzhilin
 * created on 2019/1/10
 */
public class SettingFragement extends PreferenceFragmentCompat {
    public static final String TAG = SettingFragement.class.getName();

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName(CONFIG);
        addPreferencesFromResource(R.xml.config);
    }

    /*@Override
    public boolean onPreferenceTreeClick(Preference preference) {
        //switch (preference.getKey()) {
            //case getKeyFromStringRes(R.string.facereco_gate_open_time_key) :
            //    break;
            //    case
        String key = preference.getKey();
        LogUtil.d(TAG, "key: " + key);
            if (key.equals(getKeyFromStringRes(R.string.facereco_gate_open_time_key))) {
                EditTextPreference editTextPreference = (EditTextPreference)findPreference(key);
                LogUtil.d(TAG, "key: " + key + ", value: " + editTextPreference.getText());
            }
        //}
        return super.onPreferenceTreeClick(preference);
    }
    */

    private String getKeyFromStringRes(int id) {
        return getResources().getString(id);
    }

}
