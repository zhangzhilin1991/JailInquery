package com.nyiit.jailinquery;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import com.ainirobot.jianyu.speech.manager.TTSManager;
import com.nyiit.jailinquery.di.component.DaggerAppComponent;
import com.nyiit.jailinquery.service.JailInqueryService;
import com.nyiit.jailinquery.tools.LogUtil;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;

/**
 * 应用Application.
 *
 * @author zhangzhilin1991@sina.com
 * created on 2019/02/14
 */
public class JailinqueryApplication extends Application implements HasActivityInjector, HasServiceInjector {
    private static final String TAG = JailinqueryApplication.class.getName();
    @Inject
    DispatchingAndroidInjector<Service> dispatchingAndroidServiceInjector;
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidActivityInjector;
    private volatile int activityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this);

        //enableStrictMode();
        registerActivityLifeCycleCallback();
        init();
        //startService(new Intent(this, JailInqueryService.class));
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidActivityInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingAndroidServiceInjector;
    }

    private void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
    }

    private void init() {
        //crash reprotinit
        //CrashReport.initCrashReport(getApplicationContext());
        //CrashReport.initCrashReport(getApplicationContext(), "8103483e68", true);
        Bugly.init(getApplicationContext(), "8103483e68", BuildConfig.DEBUG);
        //leakCanery init
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        TTSManager.createUtility(this, getString(com.ainirobot.jianyu.R.string.app_id));
    }

    private void registerActivityLifeCycleCallback() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
                LogUtil.d(TAG, "onActivityStarted: " + activityCount);
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                LogUtil.d(TAG, "onActivityStopped: " + activityCount);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * 获取当前有几个Activity启动
     *
     * @return
     */
    public int getActivityCount() {
        return activityCount;
    }
}
