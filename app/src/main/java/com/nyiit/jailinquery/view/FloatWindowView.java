package com.nyiit.jailinquery.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nyiit.jailinquery.JailInqueryActivity;
import com.nyiit.jailinquery.R;
import com.nyiit.jailinquery.tools.LogUtil;

/**
 * 悬浮窗View.
 *
 * @author zhangzhilin1991@sina.com
 * created on 2019/02/14
 */
public class FloatWindowView extends LinearLayout {

    public static final String TAG = FloatWindowView.class.getName();

    private final WindowManager mWindowManager;
    private final TextView mFloatView;
    private long startTime;
    private float mTouchStartX;
    private float mTouchStartY;
    private boolean isclick;
    private WindowManager.LayoutParams mWmParams;
    private Context mContext;
    private long endTime;

    public FloatWindowView(Context context) {
        this(context, null);
        mContext = context;
    }

    public FloatWindowView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.view_float_window, this);
        //浮动窗口按钮
        mFloatView = (TextView) findViewById(R.id.float_window_tv);
        mFloatView.setClickable(true);
        mFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                LogUtil.d(TAG, "onClick");
                Intent intent = new Intent(context, JailInqueryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.d(TAG, "dispatchTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.d(TAG, "dispatchTouchEvent ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.d(TAG, "dispatchTouchEvent ACTION_UP");
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        //下面的这些事件，跟图标的移动无关，为了区分开拖动和点击事件
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                LogUtil.d(TAG, "onTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.d(TAG, "onTouchEvent ACTION_MOVE");
                //图标移动的逻辑在这里
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    // 更新浮动窗口位置参数
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);
                    //暂时不做移动
                    LogUtil.d(TAG, "onTouchEvent updateViewLayout");
                    mWindowManager.updateViewLayout(this, mWmParams);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.d(TAG, "onTouchEvent ACTION_UP");
                /*
                endTime = System.currentTimeMillis();
                //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
                if ((endTime - startTime) > 0.1 * 1000L) {
                    isclick = false;
                } else {
                    isclick = true;
                }
                */
                break;
        }
        //响应点击事件
        //if (isclick) {
        //Toast.makeText(mContext, "我是大傻叼", Toast.LENGTH_SHORT).show();
        //}
        return true;
    }


    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mWmParams = params;
    }

    private void updateViewLayout() {
        LogUtil.d(TAG, "updateViewLayout");
        mWindowManager.updateViewLayout(this, mWmParams);
    }
}
