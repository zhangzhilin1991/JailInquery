package com.nyiit.jailinquery.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.provider.Settings;
import android.view.WindowManager;

import com.nyiit.jailinquery.JailinqueryApplication;
import com.nyiit.jailinquery.Manager.FloatWindowManager;
import com.nyiit.jailinquery.tools.LogUtil;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * JailInqueryService
 *
 * @author zhangzhilin1991@sina.com
 * created on 2019/02/14
 */
public class JailInqueryService extends Service {
    public static final int MSG_SHOW_FLOAT_BUTTON = 0x0;
    public static final int MSG_HIDE_FLOAT_BUTTON = 0x1;
    private static final String TAG = JailInqueryService.class.getName();
    final Handler mIncomingHandler = new IncomingHandler(this);
    final Messenger mMessenger = new Messenger(mIncomingHandler);
    @Inject
    WindowManager windowManager;
    @Inject
    FloatWindowManager floatWindowManager;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        addFloatWindow();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent newIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(newIntent);
            } else {
                //执行6.0以上绘制代码
                //addFloatWindow();
                //mMessenger.send(Message.obtain(null, MSG_SHOW_FLOAT_BUTTON));
                sendShowFloatButtonMessage();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //sendShowFloatButtonMessage();
        if (((JailinqueryApplication) getApplication()).getActivityCount() == 0) {
            sendShowFloatButtonMessage();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFloatWindow();
    }

    private void sendShowFloatButtonMessage() {
        if (mIncomingHandler != null) {
            mIncomingHandler.obtainMessage(MSG_SHOW_FLOAT_BUTTON).sendToTarget();
        }
    }

    private void addFloatWindow() {
        floatWindowManager.createFloatWindow(this, windowManager);
    }

    private void removeFloatWindow() {
        floatWindowManager.removeFloatWindow();
    }

    private void hideFloatWindow() {
        floatWindowManager.hide();
    }

    private void showFloatWindow() {
        floatWindowManager.show();
    }

    private static class IncomingHandler extends Handler {

        private final WeakReference<JailInqueryService> mService;

        public IncomingHandler(JailInqueryService service) {
            mService = new WeakReference<JailInqueryService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JailInqueryService service = mService.get();
            if (service == null) {
                return;
            }
            switch (msg.what) {
                case MSG_SHOW_FLOAT_BUTTON:
                    LogUtil.d(TAG, "MSG_SHOW_FLOAT_BUTTON");
                    service.showFloatWindow();
                    break;
                case MSG_HIDE_FLOAT_BUTTON:
                    LogUtil.d(TAG, "MSG_HIDE_FLOAT_BUTTON");
                    service.hideFloatWindow();
                    break;
            }
        }
    }

}
