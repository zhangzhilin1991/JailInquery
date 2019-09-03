package com.nyiit.jailinquery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nyiit.jailinquery.tools.LogUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.app.ApplicationErrorReport.TYPE_NONE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * NetWorkChangedReceiver。
 *
 * @author zhangzhilin
 * created on 2018/10/17
 */
public class NetWorkChangedReceiver extends BroadcastReceiver {
    public static final String TAG = NetWorkChangedReceiver.class.getName();

    //FaceRecoService service;
    private OnNetWorkChangedListener listener;

    private int type = -0x100;

    public NetWorkChangedReceiver() {
    }

    public void registerListener(OnNetWorkChangedListener listener) {
        this.listener = listener;
    }

    //public void setService(FaceRecoService service) {
    //    this.service = service;
    //}

    public void unregisterListener() {
        this.listener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            LogUtil.d(TAG, "getActiveNetworkInfo failed!");
            return;
        }
        if ( networkInfo.isAvailable()) {
            switch (networkInfo.getType()) {
                case TYPE_MOBILE:
                    //Toast.makeText(context, "正在使用2G/3G/4G网络", Toast.LENGTH_SHORT).show();
                    if (type == TYPE_MOBILE) {
                        return;
                    }
                    type = TYPE_MOBILE;
                    LogUtil.d(TAG, "正在使用2G/3G/4G网络");

                    if (listener != null) {
                        listener.OnNetWorkChanged(TYPE_MOBILE);
                    }
                    break;
                case TYPE_WIFI:
                    //Toast.makeText(context, "正在使用wifi上网", Toast.LENGTH_SHORT).show();
                    if (type == TYPE_WIFI) {
                        return;
                    }
                    type = TYPE_WIFI;
                    LogUtil.d(TAG, "正在使用wifi上网");
                    if (listener != null) {
                        listener.OnNetWorkChanged(TYPE_WIFI);
                    }
                    break;
                default:
                    break;
            }
        } else {
            //Toast.makeText(context, "当前无网络连接", Toast.LENGTH_SHORT).show();
            //if (type == TYPE_MOBILE) {
            //    return;
            //}
            type = networkInfo.getType();
            LogUtil.d(TAG, "当前无网络连接");
            if (listener != null) {
                listener.OnNetWorkChanged(TYPE_NONE);
            }
        }
        if (networkInfo != null) {
            LogUtil.d(TAG, "TYPE: " + networkInfo.getType() + ", isAvailable: " + networkInfo.isAvailable());
        }
    }

/**
 * 判断当前的网络是否是wifi，以及状态
 */
public interface OnNetWorkChangedListener {
        void OnNetWorkChanged(int type);
    }
}
