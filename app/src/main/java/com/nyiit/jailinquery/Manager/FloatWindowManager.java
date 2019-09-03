package com.nyiit.jailinquery.Manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import com.nyiit.jailinquery.tools.LogUtil;
import com.nyiit.jailinquery.view.FloatWindowView;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 悬浮窗管理类
 *
 * @author zhangzhilin1991@sina.com
 * created on 2019/02/14
 */
@Singleton
public class FloatWindowManager {
    private static final String TAG = FloatWindowManager.class.getName();
    /**
     * 悬浮窗
     */
    private FloatWindowView mFloatWindowView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private volatile boolean mHasShown;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    @Inject
    public void createFloatWindow(Context context, WindowManager windowManager) {
        LogUtil.d(TAG, "createFloatWindow");
        wmParams = new WindowManager.LayoutParams();
        //WindowManager windowManager = getWindowManager(context);
        this.mWindowManager = windowManager;
        mFloatWindowView = new FloatWindowView(context);
        if (Build.VERSION.SDK_INT >= 24) { /*android7.0不能用TYPE_TOAST*/
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else { /*以下代码块使得android6.0之后的用户不必再去手动开启悬浮窗权限*/
            String packname = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packname));
            if (permission) {
                wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为右侧置顶
        wmParams.gravity = Gravity.START | Gravity.BOTTOM;
        //wmParams.verticalMargin = 200;
        //wmParams.horizontalMargin = 200;

        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        //窗口高度
        int screenHeight = dm.heightPixels;
        //以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = screenWidth;
        wmParams.y = screenHeight / 2 + screenHeight/4;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatWindowView.setParams(wmParams);
        //windowManager.addView(mFloatWindowView, wmParams);
        //mHasShown = true;
    }

    /**
     * 移除悬浮窗
     */
    public void removeFloatWindow() {
        LogUtil.d(TAG, "removeFloatWindow");
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = mFloatWindowView.isAttachedToWindow();
        }
        LogUtil.d(TAG, "removeFloatWindow() isAttach: " + isAttach);
        if (mHasShown && isAttach && mWindowManager != null)
            mWindowManager.removeViewImmediate(mFloatWindowView);
    }

    /**
     * 返回当前已创建的WindowManager。
     */
    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 隐藏悬浮窗口
     */
    public void hide() {
        if (mHasShown)
            mWindowManager.removeViewImmediate(mFloatWindowView);
        mHasShown = false;
    }

    /**
     * 显示悬浮窗口
     */
    public void show() {
        if (!mHasShown)
            mWindowManager.addView(mFloatWindowView, wmParams);
        mHasShown = true;
    }
}
