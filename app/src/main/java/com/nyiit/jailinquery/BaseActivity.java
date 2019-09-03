package com.nyiit.jailinquery;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nyiit.jailinquery.service.JailInqueryService;
import com.nyiit.jailinquery.tools.LogUtil;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();

    protected Toolbar toolbar;

    protected Messenger mService = null;
    protected ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "onServiceConnected");
            mService = new Messenger(service);
            notifityHideFloatWindow();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "onServiceDisconnected");
            mService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected int getActivityCount() {
        return ((JailinqueryApplication) getApplication()).getActivityCount();
    }

    /*protected void dobindService() {
        if (mConnection != null) {
            bindService(new Intent(this,
                    JailInqueryService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    protected void doUnbindService() {
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }
     */

    protected void notifityShowFloatWindow() {
        Message message = Message.obtain(null, JailInqueryService.MSG_SHOW_FLOAT_BUTTON);
        if (mService != null) {
            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    protected void notifityHideFloatWindow() {
        LogUtil.d(TAG, "notifityHideFloatWindow");
        if (mService != null) {
            try {
                Message message = Message.obtain(null, JailInqueryService.MSG_HIDE_FLOAT_BUTTON);
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
