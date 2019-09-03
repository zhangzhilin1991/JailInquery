package com.nyiit.jailinquery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.nyiit.jailinquery.tools.LogUtil;

import butterknife.ButterKnife;

/**
 * @author zhangzhilin
 * created on 2019/1/10
 */
public class SettingActivity extends BaseActivity {
    public static final String TAG = SettingActivity.class.getName();

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("设置");
        }

        //dobindService();

        fragment = new SettingFragement();
        getSupportFragmentManager().beginTransaction().add(R.id.setting_container, fragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //sp.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //sp.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop()");
        if (getActivityCount() == 0) {
            notifityShowFloatWindow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //doUnbindService();
        getSupportFragmentManager().beginTransaction().remove(fragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                SettingActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
