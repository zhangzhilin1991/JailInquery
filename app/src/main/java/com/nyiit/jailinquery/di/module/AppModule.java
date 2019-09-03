package com.nyiit.jailinquery.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.view.WindowManager;

import com.ainirobot.jianyu.speech.manager.TTSManager;
import com.nyiit.jailinquery.Manager.FloatWindowManager;
import com.nyiit.jailinquery.Manager.RobotManager;
import com.nyiit.jailinquery.tools.ConfigRespository;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.nyiit.jailinquery.Constant.Constants.CONFIG;

/**
 * Dagger2 Module
 */

@Module
public class AppModule {
    @Provides
    @Singleton
    public Context providesContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    public ExecutorService provideExecutorService() {
        // 1. Create a default TrackSelector
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        int KEEP_ALIVE_TIME = 1;
        TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES * 2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, taskQueue);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    @Provides
    @Singleton
    public WifiManager provideWifiManager(Application application) {
        return (WifiManager) (application.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
    }

    @Provides
    @Singleton
    public WindowManager provideWindowManager(Application application) {
        return (WindowManager) (application.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
    }

    @Provides
    @Singleton
    public ConfigRespository provideConfigRespository(SharedPreferences sharedPreferences) {
        return ConfigRespository.getInstance(sharedPreferences);
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(Application application) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    @Provides
    @Singleton
    public FloatWindowManager provideFloatWindowManager() {
        return new FloatWindowManager();
    }

    @Provides
    @Singleton
    public RobotManager provideRobotManager(TTSManager ttsManager) {
        return new RobotManager(ttsManager);
    }

    @Provides
    @Singleton
    public TTSManager provideTTSManager() {
        return new TTSManager();
    }

}
