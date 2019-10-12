package com.nyiit.jailinquery.tools;

import android.util.Log;

import com.nyiit.jailinquery.BuildConfig;

/**
 * Created by sin on 2015/11/12.
 * <p/>
 * 日志工具类
 */
public class LogUtil {
    private static final String TAG = "LogUtil";

    /**
     * 是否需要打印日志，最好在application的onCreate方法中初始化
     * true：打印日志
     * false：不打印日志
     * <p/>
     * 注意：测试环境为true，线上环境为false
     */
    public static boolean isDebug = BuildConfig.DEBUG;

    public LogUtil() {
        throw new UnsupportedOperationException("can not be instantiated");
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (isDebug) {
            Log.v(TAG, msg);
        }
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }
}
