package com.nyiit.jailinquery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nyiit.jailinquery.service.JailInqueryService;

import static android.content.Intent.ACTION_BOOT_COMPLETED;


/**
 * NetWorkChangedReceiver。
 *
 * @author zhangzhilin1991@sina.com
 * created on 2018/10/17
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    public static final String TAG = BootCompletedReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG, "recevie boot completed ... ");
        //Intent actiityIntent = new Intent(context, WelcomeActivity.class);
        //actiityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(actiityIntent);
        if (intent.getAction().equals(ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, JailInqueryService.class));
        }
    }
}
