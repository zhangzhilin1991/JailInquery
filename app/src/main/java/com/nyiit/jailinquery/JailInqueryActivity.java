package com.nyiit.jailinquery;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nyiit.jailinquery.Manager.InqueryManager;
import com.nyiit.jailinquery.Manager.RobotManager;
import com.nyiit.jailinquery.Manager.WifiConfigManager;
import com.nyiit.jailinquery.bean.PrisonerInfoBean;
import com.nyiit.jailinquery.bean.WifiSetting;
import com.nyiit.jailinquery.receiver.NetWorkChangedReceiver;
import com.nyiit.jailinquery.tools.ConfigRespository;
import com.nyiit.jailinquery.tools.LogUtil;
import com.nyiit.jailinquery.view.CustomKeyboardEditText;
import com.nyiit.jailinquery.viewmodel.FaceRecoViewModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static com.nyiit.jailinquery.Constant.Constants.CONNECTING_TIMEOUT;
import static com.nyiit.jailinquery.Constant.Constants.DEFAULT_JIANYU_BIANHAO;
import static com.nyiit.jailinquery.Constant.Constants.DEFAULT_WIFI_PWD;
import static com.nyiit.jailinquery.Constant.Constants.DEFAULT_WIFI_SSID;
import static com.nyiit.jailinquery.Constant.Constants.INQUERRING_TIMEOUT;
import static com.nyiit.jailinquery.Constant.Constants.INQUERY_TIMEOUT;
import static com.nyiit.jailinquery.Constant.Constants.JAIL_CODE_KEY;
import static com.nyiit.jailinquery.Constant.Constants.MSG_IDCARD_ACQUIRED;
import static com.nyiit.jailinquery.Constant.Constants.MSG_IDCARD_CONFIRMED;
import static com.nyiit.jailinquery.Constant.Constants.MSG_INQUERYING_TIMEOUT;
import static com.nyiit.jailinquery.Constant.Constants.MSG_INQUERY_FAILED;
import static com.nyiit.jailinquery.Constant.Constants.MSG_INQUERY_SUCCESS;
import static com.nyiit.jailinquery.Constant.Constants.MSG_INQUER_TIMEOUT;
import static com.nyiit.jailinquery.Constant.Constants.MSG_NETWORK_CHANGED;
import static com.nyiit.jailinquery.Constant.Constants.MSG_NETWORK_SWITCH_TIMEOUT;
import static com.nyiit.jailinquery.Constant.Constants.MSG_SHOW_CONNECTING_TIMEOUT;
import static com.nyiit.jailinquery.Constant.Constants.MSG_SHOW_CONNECTING_VIEW;
import static com.nyiit.jailinquery.Constant.Constants.MSG_SHOW_INQUERYING_VIEW;
import static com.nyiit.jailinquery.Constant.Constants.MSG_SHOW_INQUERY_SUCCESS_FAKE_VIEW;
import static com.nyiit.jailinquery.Constant.Constants.MSG_SHOW_INQUERY_SUCCESS_VIEW;
import static com.nyiit.jailinquery.Constant.Constants.MSG_SHOW_INQUERY_VIEW;
import static com.nyiit.jailinquery.Constant.Constants.MSG_UPDATE_CHAT_MESSAGE;
import static com.nyiit.jailinquery.Constant.Constants.NETWORK_SWITCH_TIMEOUT;
import static com.nyiit.jailinquery.Constant.Constants.WIFI_PWD_KEY;
import static com.nyiit.jailinquery.Constant.Constants.WIFI_SSID_KEY;
import static com.nyiit.jailinquery.Manager.InqueryManager.ERR_CONNECT_WIFI_FAILED;
import static com.nyiit.jailinquery.Manager.InqueryManager.ERR_INQUERYING_FAILED;

/**
 * 监狱人员信息查询界面。
 *
 * @author zhangzhilin1991@sina.com
 * created on 2019/02/14
 */
public class JailInqueryActivity extends BaseActivity implements NetWorkChangedReceiver.OnNetWorkChangedListener {

    public static final String TAG = JailInqueryActivity.class.getName();
    private static final int STATE_IDLE = -1;
    private static final int STATE_CONNECTING_FAILED = 0;
    private static final int STATE_INQUERY_FAILED = 1;
    private static final int STATE_INQUERY_SUCCESS = 2;

    private static final int INQUERY_FAILED_NETWORK_ERR = 0;
    private static final int INQUERY_FAILED_NO_DATA = 1;
    private static final int INQUERY_FAILED_TIMEOUT = 2;

    private int inquery_failed_code = INQUERY_FAILED_NETWORK_ERR;

    private static final boolean TEST = BuildConfig.DEBUG;
    //@Inject
    //TTSManager ttsManager;
    private final Handler mhandler = new UIHandler(this);
    @Inject
    WifiConfigManager wifiConfigManager;
    @Inject
    InqueryManager inqueryManager;
    @Inject
    ConfigRespository configRespository;
    @Inject
    RobotManager robotManager;
    NetWorkChangedReceiver netWorkChangedReceiver;
    ConnectivityManager connectionManager;


    @BindView(R.id.jail_inquery_et)
    CustomKeyboardEditText jailInqueryEt;
    @BindView(R.id.jail_inquery_tv)
    Button jailInqueryTv;
    @BindView(R.id.jail_inquery_rl)
    RelativeLayout jailInqueryLl;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.jail_inquerying_ll)
    LinearLayout jailInqueryingLl;
    @BindView(R.id.jail_inquery_failed_retry)
    Button jailInqueryFailedRetry;
    @BindView(R.id.jail_inquery_failed_ll)
    LinearLayout jailInqueryFailedLl;
    @BindView(R.id.jail_inquery_result_name_tag_tv)
    TextView jailInqueryResultNameTagTv;
    @BindView(R.id.jail_inquery_result_name_tv)
    TextView jailInqueryResultNameTv;
    @BindView(R.id.jail_inquery_result_id_tag_tv)
    TextView jailInqueryResultIdTagTv;
    @BindView(R.id.jail_inquery_result_id_tv)
    TextView jailInqueryResultIdTv;
    @BindView(R.id.jail_inquery_result_age_tv)
    TextView jailInqueryResultAgeTv;
    @BindView(R.id.jail_inquery_result_back_btn)
    Button jailInqueryResultBackBtn;
    @BindView(R.id.jail_inquery_result_ll)
    LinearLayout jailInqueryResultLl;
    @BindView(R.id.jail_inquery_failed_cancel)
    Button jailInqueryFailedCancel;
    @BindView(R.id.jail_inquery_result_basic_info_tv)
    TextView jailInqueryResultBasicInfoTv;
    @BindView(R.id.jail_inquery_result_age_tag_tv)
    TextView jailInqueryResultAgeTagTv;
    @BindView(R.id.jail_inquery_result_account_tag_tv)
    TextView jailInqueryResultAccountTagTv;
    @BindView(R.id.jail_inquery_result_account_tv)
    TextView jailInqueryResultAccountTv;
    @BindView(R.id.jail_inquery_result_crime_tag_tv)
    TextView jailInqueryResultCrimeTagTv;
    @BindView(R.id.jail_inquery_result_crime_tv)
    TextView jailInqueryResultCrimeTv;
    @BindView(R.id.jail_inquery_result_priosn_term_tag_tv)
    TextView jailInqueryResultPriosnTermTagTv;
    @BindView(R.id.jail_inquery_result_priosn_term_tv)
    TextView jailInqueryResultPriosnTermTv;
    @BindView(R.id.jail_inquery_result_remain_punishment_tag_tv)
    TextView jailInqueryResultRemainPunishmentTagTv;
    @BindView(R.id.jail_inquery_result_remain_punishment_tv)
    TextView jailInqueryResultRemainPunishmentTv;
    @BindView(R.id.jail_inquery_result_enter_date_tag_tv)
    TextView jailInqueryResultEnterDateTagTv;
    @BindView(R.id.jail_inquery_result_enter_date_tv)
    TextView jailInqueryResultEnterDateTv;
    @BindView(R.id.jail_inquery_result_release_date_tag_tv)
    TextView jailInqueryResultReleaseDateTagTv;
    @BindView(R.id.jail_inquery_result_release_date_tv)
    TextView jailInqueryResultReleaseDateTv;
    @BindView(R.id.jail_inquery_result_basic_info_cl)
    ConstraintLayout jailInqueryResultBasicInfoCl;
    @BindView(R.id.jail_inquery_result_penalty_info_tv)
    TextView jailInqueryResultPenaltyInfoTv;
    //@BindView(R.id.jail_inquery_result_penalty_tag_tv)
    //TextView jailInqueryResultPenaltyTagTv;
    @BindView(R.id.jail_inquery_result_penalty_tv)
    TextView jailInqueryResultPenaltyTv;
    //@BindView(R.id.jail_inquery_result_penalty_type_tag_tv)
    //TextView jailInqueryResultPenaltyTypeTagTv;
    //@BindView(R.id.jail_inquery_result_penalty_type_tv)
    //TextView jailInqueryResultPenaltyTypeTv;
    @BindView(R.id.jail_inquery_result_penaty_info_cl)
    ConstraintLayout jailInqueryResultPenatyInfoCl;
    @BindView(R.id.jail_inquery_result_award_info_tv)
    //TextView jailInqueryResultAwardInfoTv;
    //@BindView(R.id.jail_inquery_result_award_date_tag_tv)
    TextView jailInqueryResultAwardDateTagTv;
    @BindView(R.id.jail_inquery_result_award_date_tv)
    TextView jailInqueryResultAwardDateTv;
    //@BindView(R.id.jail_inquery_result_award_type_tag_tv)
    //TextView jailInqueryResultAwardTypeTagTv;
    //@BindView(R.id.jail_inquery_result_award_type_tv)
    //TextView jailInqueryResultAwardTypeTv;
    @BindView(R.id.jail_inquery_result_award_info_cl)
    ConstraintLayout jailInqueryResultAwardInfoCl;
    @BindView(R.id.jail_inquery_connecting_wifi_ll)
    LinearLayout jailInqueryConnectingWifiLl;
    @BindView(R.id.jail_inquery_result_basic_info_center)
    TextView jailInqueryResultBasicInfoCenter;
    @BindView(R.id.jail_inquery_connect_failed_failed_retry)
    Button jailInqueryConnectFailedFailedRetry;
    @BindView(R.id.jail_inquery_connect_wifi_failed_ll)
    LinearLayout jailInqueryConnectWifiFailedLl;
    @BindView(R.id.jail_chat)
    TextView jailChat;
    @BindView(R.id.baseline)
    View baseline;
    @BindView(R.id.jail_listening_anim_iv)
    ImageView jailListeningAnimIv;
    private volatile boolean isInquery = false;
    private StringBuilder inqueryResultStr;
    private StringBuilder inqueryIdNumStr = new StringBuilder();
    private int inqstate = STATE_IDLE;
    //private String searchTextStr = null;
    private FaceRecoViewModel model;
    private volatile String WIFI_SSID = DEFAULT_WIFI_SSID;
    private volatile String WIFI_PWD = DEFAULT_WIFI_PWD;
    private volatile String JAIL_CODE = DEFAULT_JIANYU_BIANHAO;
    private Observer<Map<String, ?>> configObserver = new Observer<Map<String, ?>>() {
        @Override
        public void onChanged(@Nullable Map<String, ?> valueMaps) {
            LogUtil.d(TAG, "onConfigChanged");
            //if (config.getFaceRecoIntervalTime() != -1) {
            //    fd_interval_time = config.getFaceRecoIntervalTime();
            //}
            if (valueMaps == null) {
                LogUtil.d(TAG, "onConfigChanged() invalid params!");
                return;
            }
            for (Map.Entry<String, ?> entry : valueMaps.entrySet()) {
                LogUtil.d(TAG, "onConfigChanged() key: " + entry.getKey() + ", value: " + entry.getValue());
                switch (entry.getKey()) {
                    case WIFI_SSID_KEY:
                        //config.setFaceRecoThreshold(((Integer) entry.getValue()).intValue());
                        WIFI_SSID = (String) (entry.getValue());
                        break;
                    case WIFI_PWD_KEY:
                        //config.setFaceRecoIntervalTime(((Integer) entry.getValue()).intValue());
                        WIFI_PWD = (String) (entry.getValue());
                        break;
                    case JAIL_CODE_KEY:
                        JAIL_CODE = (String) (entry.getValue());
                        break;
                    default:
                        LogUtil.d(TAG, "invalid key:" + entry.getKey());
                }
            }
        }
    };
    private RobotManager.RobotListener mRobotListener = new RobotManager.RobotListener() {
        @Override
        public void onConnected(int type) {
            LogUtil.d(TAG, "onConnected() type = " + type);
        }

        @Override
        public void onDisconnected(int type) {
            LogUtil.d(TAG, "onDisconnected() type = " + type);
        }

        @Override
        public void onIdCardNumAcquired(String idCardNum) {
            LogUtil.d(TAG, "onIdCardNumAcquired() idCardNum = " + idCardNum);
            if (mhandler != null) {
                mhandler.obtainMessage(MSG_IDCARD_ACQUIRED, idCardNum).sendToTarget();
                //mhandler.sendMessage();
            }
        }

        @Override
        public void onIdCardNumConfirmed(boolean isConfirmed) {
            LogUtil.d(TAG, "onIdCardNumConfirmed() isConfirmed = " + isConfirmed);
            if (mhandler != null) {
                //if (isConfirmed) {
                mhandler.obtainMessage(MSG_IDCARD_CONFIRMED, isConfirmed).sendToTarget();
                //} else {
                //清理界面
                //}
            }
        }

        @Override
        public void onExit() {
            JailInqueryActivity.this.finish();
        }

        @Override
        public void onReturn() {
            //showInqueryView();
            if (mhandler != null) {
                //robotManager.stopTTS();
                mhandler.obtainMessage(MSG_SHOW_INQUERY_VIEW).sendToTarget();
            }
            //jailInqueryEt.setText("");
        }

        @Override
        public void onChat(String msg) {
            if (mhandler != null) {
                mhandler.obtainMessage(MSG_UPDATE_CHAT_MESSAGE, msg).sendToTarget();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jail_inquery);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar_head);
        //toolbar
        setSupportActionBar(toolbar);
        //setToolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("服刑人员查询");
        }

        jailInqueryEt.setKeyboardDropAnchor(jailInqueryTv);

        requestPermissions();

        //dobindService();
        //showInqueryView();
        //initSerach();
        //if (BuildConfig.DEBUG) {
            /*mhandler.getLooper().setMessageLogging(new Printer() {
                @Override
                public void println(String x) {
                    LogUtil.d(TAG, "UiHandler" + x);
                }
            });
            */
        //}

        connectionManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //startInquryTimeout();
        netWorkChangedReceiver = new NetWorkChangedReceiver();
        netWorkChangedReceiver.registerListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netWorkChangedReceiver, intentFilter);

        //连服务器
        conncetRobotServer();

        init();
    }

    @Override
    protected void onStart() {
        //LogUtil.d(TAG, "onStart()");
        super.onStart();
        //FloatWindowManager.hide();
        //ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        //NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        //if (networkInfo == null || networkInfo.getType() == TYPE_MOBILE) {
        //if (TEST) {
        //        mhandler.obtainMessage(MSG_SHOW_INQUERY_VIEW).sendToTarget();
        //} else {
        //mhandler.obtainMessage(MSG_SHOW_CONNECTING_VIEW).sendToTarget();
        //}
        //wifiConfigManager.set4GNetworkState(false);
        // wifiConfigManager.enableWifi("TP-hjl");
        //set4GNetWorkEnabled(false);
        //} else {
        mhandler.obtainMessage(MSG_SHOW_INQUERY_VIEW).sendToTarget();
        //}
        //notifityHideFloatWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //CrashReport.testJavaCrash();
        //mhandler.obtainMessage(MSG_SHOW_INQUERY_VIEW).sendToTarget();
        startListeningAnim();
        robotManager.registerRobotListener(mRobotListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopListeningAnim();
        robotManager.unregisterRobotListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //FloatWindowManager.show();
        //wifiConfigManager.set4GNetworkState(true);
        //set4GNetWorkEnabled(true);
        //wifiConfigManager.disableWifi();
        // unregisterReceiver(netWorkChangedReceiver);
        //LogUtil.d(TAG, "onStop()");
        //if (getActivityCount() == 0) {
        //notifityShowFloatWindow();
        //}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (mhandler != null) {
        //    mhandler.getLooper().quit();
        //}
        netWorkChangedReceiver.unregisterListener();
        unregisterReceiver(netWorkChangedReceiver);
        //doUnbindService();
        disconncetRobotServer();

        unInit();
    }

    @OnClick({R.id.jail_inquery_tv, R.id.jail_inquery_failed_retry, R.id.jail_inquery_failed_cancel,
            R.id.jail_inquery_result_back_btn, R.id.jail_inquery_connect_failed_failed_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.jail_inquery_tv:
            case R.id.jail_inquery_failed_retry:
                //第一次点击显示输入界面
                //if (clickCount == 0) {
                //模拟点点击
                //jailInqueryEt.performClick();
                //    clickCount = 1;
                //} else if (clickCount == 1) {
                //第二次点击开始查询
                String searchTextStr = jailInqueryEt.getEditableText().toString();
                //设置为查询字符串。
                inqueryIdNumStr.delete(0, 18);
                inqueryIdNumStr.append(searchTextStr);
                if (TextUtils.isEmpty(inqueryIdNumStr) || inqueryIdNumStr.length() != 18) {
                    //ToastUtil.showShort(JailInqueryActivity.this, "请先输入身份证件号码！");
                    robotManager.playTTS("请先检查身份证件号码");
                    return;
                }
                LogUtil.d(TAG, "inqueryIdNumStr: " + inqueryIdNumStr);
                //showInqueryingView();
                //set4GNetWorkEnabled(false);
                //超时定时器
                //mhandler.obtainMessage(MSG_SHOW_INQUERYING_VIEW).sendToTarget();
                NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
                if (networkInfo == null || networkInfo.getType() == TYPE_MOBILE) {
                    mhandler.obtainMessage(MSG_SHOW_CONNECTING_VIEW).sendToTarget();
                } else if (networkInfo.getType() == TYPE_WIFI) {
                    mhandler.obtainMessage(MSG_SHOW_INQUERYING_VIEW).sendToTarget();
                }
                break;
            case R.id.jail_inquery_result_back_btn:
                //showInqueryView();
                robotManager.stopTTS();
                mhandler.obtainMessage(MSG_SHOW_INQUERY_VIEW).sendToTarget();
                break;
            case R.id.jail_inquery_failed_cancel:
                //showInqueryView();
                mhandler.obtainMessage(MSG_SHOW_INQUERY_VIEW).sendToTarget();
                break;
            case R.id.jail_inquery_connect_failed_failed_retry:
                //showConnectingView();
                //set4GNetWorkEnabled(false);
                mhandler.obtainMessage(MSG_SHOW_CONNECTING_VIEW).sendToTarget();
                //case R.id.jail_inquery_et:
                //jailInqueryEt.setCursorVisible(true);
                //    break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG, "dispatchTouchEvent");
        //检测到触摸事件就重置定时器。
        resetInqueryTimeout();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                JailInqueryActivity.this.finish();
                break;
            case R.id.setting_menu:
                Intent intent = new Intent(JailInqueryActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示连接wifi的界面
     */
    private void showConnectingView() {
        jailInqueryLl.setVisibility(View.GONE);
        jailInqueryFailedLl.setVisibility(View.GONE);
        jailInqueryingLl.setVisibility(View.GONE);
        jailInqueryResultLl.setVisibility(View.GONE);
        jailInqueryConnectingWifiLl.setVisibility(View.VISIBLE);
        jailInqueryConnectWifiFailedLl.setVisibility(View.GONE);
    }

    /**
     * 显示连接wifi的界面
     */
    private void showConnectWifiFailedView() {
        jailInqueryLl.setVisibility(View.GONE);
        jailInqueryFailedLl.setVisibility(View.GONE);
        jailInqueryingLl.setVisibility(View.GONE);
        jailInqueryResultLl.setVisibility(View.GONE);
        jailInqueryConnectingWifiLl.setVisibility(View.GONE);
        jailInqueryConnectWifiFailedLl.setVisibility(View.VISIBLE);
    }

    /**
     * 显示查询中界面
     */
    private void showInqueryingView() {
        jailInqueryLl.setVisibility(View.GONE);
        jailInqueryFailedLl.setVisibility(View.GONE);
        jailInqueryingLl.setVisibility(View.VISIBLE);
        jailInqueryResultLl.setVisibility(View.GONE);
        jailInqueryConnectingWifiLl.setVisibility(View.GONE);
        jailInqueryConnectWifiFailedLl.setVisibility(View.GONE);
    }

    /**
     * 显示查询界面
     */
    private void showInqueryView() {
        jailInqueryLl.setVisibility(View.VISIBLE);
        jailInqueryFailedLl.setVisibility(View.GONE);
        jailInqueryingLl.setVisibility(View.GONE);
        jailInqueryResultLl.setVisibility(View.GONE);
        jailInqueryConnectingWifiLl.setVisibility(View.GONE);
        jailInqueryConnectWifiFailedLl.setVisibility(View.GONE);
    }

    /**
     * 查询失败界面
     */
    private void showInqueryFailedView() {
        jailInqueryLl.setVisibility(View.GONE);
        jailInqueryFailedLl.setVisibility(View.VISIBLE);
        jailInqueryingLl.setVisibility(View.GONE);
        jailInqueryResultLl.setVisibility(View.GONE);
        jailInqueryConnectingWifiLl.setVisibility(View.GONE);
        jailInqueryConnectWifiFailedLl.setVisibility(View.GONE);
    }

    /**
     * 查询成功界面
     */
    private void showInquerySuccessView() {
        jailInqueryLl.setVisibility(View.GONE);
        jailInqueryFailedLl.setVisibility(View.GONE);
        jailInqueryingLl.setVisibility(View.GONE);
        jailInqueryResultLl.setVisibility(View.VISIBLE);
        jailInqueryConnectingWifiLl.setVisibility(View.GONE);
        jailInqueryConnectWifiFailedLl.setVisibility(View.GONE);
    }

    private void startInqueryingTimer() {
        if (mhandler.hasMessages(MSG_INQUERYING_TIMEOUT)) {
            mhandler.removeMessages(MSG_INQUERYING_TIMEOUT);
        }
        Message message = mhandler.obtainMessage(MSG_INQUERYING_TIMEOUT);
        mhandler.sendMessageDelayed(message, INQUERRING_TIMEOUT);
    }

    private void stopInqueryTimer() {
        if (mhandler.hasMessages(MSG_INQUERYING_TIMEOUT)) {
            mhandler.removeMessages(MSG_INQUERYING_TIMEOUT);
        }
    }

    private void startConnectingTimer() {
        if (mhandler.hasMessages(MSG_SHOW_CONNECTING_TIMEOUT)) {
            mhandler.removeMessages(MSG_SHOW_CONNECTING_TIMEOUT);
        }
        Message message = mhandler.obtainMessage(MSG_SHOW_CONNECTING_TIMEOUT);
        mhandler.sendMessageDelayed(message, CONNECTING_TIMEOUT);
    }

    private void stopConnectingTimer() {
        if (mhandler.hasMessages(MSG_SHOW_CONNECTING_TIMEOUT)) {
            mhandler.removeMessages(MSG_SHOW_CONNECTING_TIMEOUT);
        }
    }

    /**
     * 启动刑期查询定时器。
     */
    private void startInquryTimeout() {
        if (mhandler.hasMessages(MSG_INQUER_TIMEOUT)) {
            mhandler.removeMessages(MSG_INQUER_TIMEOUT);
        }
        Message message = mhandler.obtainMessage(MSG_INQUER_TIMEOUT);
        mhandler.sendMessageDelayed(message, INQUERY_TIMEOUT);
    }

    /**
     * 重置刑期查询定时器。
     */
    private void resetInqueryTimeout() {
        startInquryTimeout();
    }

    private void initSerach() {
        //im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        /*jailInqueryEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogUtil.d(TAG, "beforeTextChanged:" + s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.d(TAG, "onTextChanged:" + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.d(TAG, "afterTextChanged:" + s.toString());
                //searchTextStr = s.toString();
            }
        });
        */
    }

    private void startListeningAnim() {
        AnimationDrawable animationDrawable = (AnimationDrawable) jailListeningAnimIv.getDrawable();
        animationDrawable.start();
    }

    private void stopListeningAnim() {
        AnimationDrawable animationDrawable = (AnimationDrawable) jailListeningAnimIv.getDrawable();
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    private void startInquery(String jailCode, String searchText, WifiSetting wifiSetting) {
        LogUtil.d(TAG, "startInquery() jailCode: " + jailCode + ", searchText: " + searchText);

        //inquery
        inqueryManager.startInquery(jailCode, searchText, wifiSetting, new InqueryManager.InqueryingCallBack() {
            @Override
            public void onSuccess(final PrisonerInfoBean.DataBean prisonerInfo,final String prisonerPenaltyInfo
                    ,final String prisonerAwardInfo) {
                LogUtil.d(TAG, "startInquery() onSuccess: " + prisonerInfo);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updatePrisonerInfo(prisonerInfo, prisonerPenaltyInfo, prisonerAwardInfo);
                    }
                });

                if (mhandler.hasMessages(MSG_INQUERYING_TIMEOUT)) {
                    mhandler.removeMessages(MSG_INQUERYING_TIMEOUT);
                }
                resetInqueryTimeout();
                //Message message = mhandler.obtainMessage();
                //Bundle bundle = new Bundle();
                // bundle.p
                //mhandler.obtainMessage(MSG_SHOW_INQUERY_SUCCESS_VIEW).sendToTarget();
                mhandler.obtainMessage(MSG_INQUERY_SUCCESS).sendToTarget();
            }

            @Override
            public void onFaild(int resaon) {
                LogUtil.d(TAG, "startInquery() onFaild: " + resaon);
                if (mhandler.hasMessages(MSG_INQUERYING_TIMEOUT)) {
                    mhandler.removeMessages(MSG_INQUERYING_TIMEOUT);
                }
                resetInqueryTimeout();
                //mhandler.obtainMessage(MSG_SHOW_INQUERY_FAILED_VIEW).sendToTarget();
                Message message = mhandler.obtainMessage(MSG_INQUERY_FAILED);
                message.arg1 = resaon;
                mhandler.sendMessage(message);
            }
        });
    }

    private void updatePrisonerInfo(PrisonerInfoBean.DataBean prisonerInfo, String prisonerPenaltyInfo
            , String prisonerAwardInfo) {
        LogUtil.d(TAG, "updatePrisonerInfo()");
        inqueryResultStr = new StringBuilder();
        if (prisonerInfo != null) {
            jailInqueryResultNameTv.setText(prisonerInfo.getZf_xm());
            jailInqueryResultAgeTv.setText(prisonerInfo.getZf_nl());
            jailInqueryResultIdTv.setText(prisonerInfo.getZf_bh());
            jailInqueryResultAccountTv.setText(prisonerInfo.getZf_zhye());
            jailInqueryResultCrimeTv.setText(prisonerInfo.getZf_zm());
            jailInqueryResultPriosnTermTv.setText(prisonerInfo.getZf_xq());
            jailInqueryResultRemainPunishmentTv.setText(prisonerInfo.getZf_yx());
            jailInqueryResultEnterDateTv.setText(prisonerInfo.getZf_rjrq());
            jailInqueryResultReleaseDateTv.setText(prisonerInfo.getZf_xmrq());

            inqueryResultStr.append("犯人基本信息" + prisonerInfo.toString());
        }
        if (prisonerPenaltyInfo != null) { //list
            //jailInqueryResultPenaltyTypeTv.setText(prisonerPenaltyInfo.getZf_bdlx());
            //jailInqueryResultPenaltyTv.setText(prisonerPenaltyInfo.getZf_bdrq());
            jailInqueryResultPenaltyTv.setText(prisonerPenaltyInfo);
            inqueryResultStr.append("犯人加减刑信息" + prisonerPenaltyInfo.toString());
        }
        if (prisonerAwardInfo != null) { //list
            //JailInquery
            //jailInqueryResultAwardDateTv.setText(prisonerAwardInfo.getZf_hjrq());
            //jailInqueryResultAwardTypeTv.setText(prisonerAwardInfo.getZf_hjlx());
            jailInqueryResultAwardDateTv.setText(prisonerAwardInfo);
            inqueryResultStr.append("犯人奖励信息" + prisonerAwardInfo.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtil.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    private void init() {
        FaceRecoViewModel.Factory factory = new FaceRecoViewModel.Factory(getApplication(), configRespository);
        //ttsManager.initialTts(handler);
        model = ViewModelProviders.of(this, factory)
                .get(FaceRecoViewModel.class);
        subscribeToModel();

        //ttsManager.createSynthesizer(this);
        addEditTextListener();

        if (TEST) {
            inqueryManager.setTest(true);
        }
    }

    private void unInit() {
        unSubscribeToModel();
        //ttsManager.releaseSynthesizer();
    }

    private void subscribeToModel() {
        if (model != null) {
            //model.getmObservableConfigs().getValue();
            model.getmObservableConfigs().observe(this, configObserver);
        }
    }

    private void unSubscribeToModel() {
        if (model != null) {
            model.getmObservableConfigs().removeObserver(configObserver);
            model = null;
        }
    }

    @Override
    public void OnNetWorkChanged(int type) {
        //
        //if (type == TYPE_WIFI) {
        LogUtil.d(TAG, "OnNetWorkChanged : " + type);
        //if (isInquery) {
        //mhandler.obtainMessage(MSG_SHOW_INQUERY_VIEW).sendToTarget();
        Message message = mhandler.obtainMessage(MSG_NETWORK_CHANGED);
        message.arg1 = type;
        mhandler.sendMessage(message);
        //执行查询程序
        //jailInqueryTv.performClick();
        //开始查询
            /*searchTextStr = jailInqueryEt.getEditableText().toString();
            if (TextUtils.isEmpty(searchTextStr) || searchTextStr.length() != 18) {
                //ToastUtil.showShort(JailInqueryActivity.this, "请先输入身份证件号码！");
                robotManager.playTTS("请先检查身份证件号码");
                return;
            }
            */
        //    }
        //} else if (type == TYPE_MOBILE) {
        //4g网络已经启用
        //下面根据不同的状态执行不同的操作哎
        //
        //    mhandler.obtainMessage();
        //}
    }

    private void set4GNetWorkEnabled(boolean enabled) {
        LogUtil.d(TAG, "set4GNetWorkEnabled: " + enabled);
        //如果当前就是4G 那就没有直接切换到view界面
        //

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.ainirobot.mobiledata", "com.ainirobot.mobiledata.MobileDataService");
        intent.setComponent(componentName);
        if (enabled) {
            intent.setAction("com.ainirobot.mobiledata.action.ENABLE_MOBILE_DATA");
        } else {
            intent.setAction("com.ainirobot.mobiledata.action.DISABLE_MOBILE_DATA");
        }
        startService(intent);

        //网络切换超时定时器
        if (mhandler != null) {
            mhandler.removeMessages(MSG_NETWORK_SWITCH_TIMEOUT);
            Message message = mhandler.obtainMessage(MSG_NETWORK_SWITCH_TIMEOUT);
            mhandler.sendMessageDelayed(message, NETWORK_SWITCH_TIMEOUT);
        }
    }

    private void conncetRobotServer() {
        //robotManager.registerRobotListener(mRobotListener);
        robotManager.connect(this.getApplicationContext());
    }

    private void disconncetRobotServer() {
        //robotManager.registerRobotListener(mRobotListener);
        robotManager.disconnect();
    }

    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS}, 0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //假数据
    /*private void setFakeData() {
        LogUtil.d(TAG, "setFakeData()");
        PrisonerInfo prisonerInfo = new PrisonerInfo(
                "张小峰",
                "320500004210",
                "47",
                "",
                "故意杀人罪",
                "无期",
                "",
                "2004年9月28日",
                "2020年9月18日"
        );
        PrisonerAwardInfo prisonerAwardInfo = new PrisonerAwardInfo(
                "",
                "",
                "减刑",
                "3个表扬。"
        );
        PrisonerPenaltyInfo prisonerPenaltyInfo = new PrisonerPenaltyInfo(
                "",
                "",
                "2006年12月19日\n" +
                        "2010年3月31\n" +
                        "2012年6月18日\n" +
                        "2013年12月26\n" +
                        "2015年9月24日\n" +
                        "2017年12月27日",
                "2006年12月19日经江苏省无锡市中级人民法院裁定，减刑19年9个月，刑期至2026年9月18日，剥夺政治权利9年。\n" +
                        "2010年3月31日经江苏省无锡市中级人民法院裁定，减刑2年，刑期至2024年9月18日，剥夺政治权利9年。\n" +
                        "2012年6月18日经江苏省无锡市中级人民法院裁定，减刑1年3个月，刑期至2023年6月18日，剥夺政治权利9年。\n" +
                        "2013年12月26日经江苏省无锡市中级人民法院裁定，减刑1年，刑期至2022年6月18日，剥夺政治权利9年。\n" +
                        "2015年9月24日经江苏省无锡市中级人民法院裁定，减刑1年1个月，刑期至2021年5月18日，剥夺政治权利9年。\n" +
                        "2017年12月27日经江苏省无锡市中级人民法院裁定，减刑8个月，刑期至2020年9月18日，剥夺政治权利4年。"
        );

        updatePrisonerInfo(prisonerInfo, prisonerPenaltyInfo, prisonerAwardInfo);
    }
    */

    private void addEditTextListener() {
        jailInqueryEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                jailInqueryEt.setSelection(jailInqueryEt.getText().length());
            }
        });
    }

    @IntDef({STATE_CONNECTING_FAILED, STATE_INQUERY_FAILED, STATE_INQUERY_SUCCESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface INQSTATE {
    }

    private static class UIHandler extends Handler {

        private final WeakReference<JailInqueryActivity> mActivity;

        public UIHandler(JailInqueryActivity activity) {
            mActivity = new WeakReference<JailInqueryActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JailInqueryActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case MSG_UPDATE_CHAT_MESSAGE:
                    if (msg.obj != null) {
                        //activity.startListeningAnim();
                        activity.jailChat.setText((String) msg.obj);
                    }
                    break;
                //case MSG_INQUERYING_TIMEOUT:
                //case MSG_SHOW_INQUERY_FAILED_VIEW:
                //activity.isInquery = false;
                //activity.set4GNetWorkEnabled(true);
                //activity.showInqueryFailedView();
                //    break;
                case MSG_SHOW_CONNECTING_TIMEOUT:
                    //activity.showConnectWifiFailedView();
                    //activity.set4GNetWorkEnabled(true);
                    //activity.isInquery = false;
                    activity.isInquery = false;
                    activity.inqstate = STATE_CONNECTING_FAILED;
                    //如果网络不发生改变如何做，网络切换超时？？？。

                    activity.set4GNetWorkEnabled(true);
                    break;
                case MSG_SHOW_INQUERY_VIEW:
                    activity.inqstate = STATE_IDLE;
                    activity.isInquery = false;
                    //activity.robotManager.stopTTS();
                    activity.inqueryIdNumStr.delete(0, activity.inqueryIdNumStr.length());
                    activity.jailInqueryEt.setText("");
                    activity.showInqueryView();
                    activity.stopConnectingTimer();
                    //playTTS
                    //activity.robotManager.playTTS("请输入身份证号");
                    break;
                case MSG_SHOW_INQUERYING_VIEW:
                    //activity.isInquery = false;
                    activity.showInqueryingView();
                    activity.startInqueryingTimer();

                    LogUtil.d(TAG, "inqueryIdNumStr: " + activity.inqueryIdNumStr);
                    //showInqueryingView();
                    //set4GNetWorkEnabled(false);
                    //超时定时器
                    //activity.mhandler.obtainMessage(MSG_SHOW_INQUERYING_VIEW).sendToTarget();
                    //mhandler.obtainMessage(MSG_SHOW_CONNECTING_VIEW).sendToTarget();
                    //clickCount = 0;
                    //
                    //if (TEST) {
                        //
                        //Message message = activity.mhandler.obtainMessage(MSG_SHOW_INQUERY_SUCCESS_FAKE_VIEW);
                        //activity.mhandler.sendMessageDelayed(message, 3 * 1000);
                        //return;
                    //}
                    activity.startInquery(activity.JAIL_CODE, activity.inqueryIdNumStr.toString(), new WifiSetting(activity.WIFI_SSID, activity.WIFI_PWD));

                    break;
                case MSG_INQUER_TIMEOUT:
                    //关闭Activity.
                    //activity.isInquery = false;
                    //activity.finish();
                    break;
                //case MSG_SHOW_INQUERY_SUCCESS_FAKE_VIEW:
                //    activity.mhandler.removeMessages(MSG_INQUERYING_TIMEOUT);
                    //activity.setFakeData();
                //    activity.mhandler.obtainMessage(MSG_INQUERY_SUCCESS).sendToTarget();
                //    break;
                case MSG_SHOW_INQUERY_SUCCESS_VIEW:
                    activity.showInquerySuccessView();
                    //activity.set4GNetWorkEnabled(true);

                    //播放
                    activity.robotManager.stopTTS();
                    activity.robotManager.playTTS(activity.inqueryResultStr.toString(), true);

                    break;
                case MSG_SHOW_CONNECTING_VIEW:
                    //查询设置为真
                    activity.isInquery = true;
                    activity.set4GNetWorkEnabled(false);
                    //NetworkInfo networkInfo = activity.connectionManager.getActiveNetworkInfo();
                    //if (networkInfo == null || networkInfo.getType() == TYPE_MOBILE) {
                    //activity.set4GNetWorkEnabled(false);
                    activity.showConnectingView();
                    activity.startConnectingTimer();
                    //} else {
                    //主动调用
                    //activity.OnNetWorkChanged(TYPE_WIFI);
                    //}
                    break;
                case MSG_IDCARD_CONFIRMED:
                    if (msg.obj != null) {
                        boolean isConfirmed = (boolean) msg.obj;
                        if (isConfirmed) {
                            activity.jailInqueryTv.performClick();
                        } else {
                            activity.showInqueryView();
                            activity.inqueryIdNumStr.delete(0, 18);
                            activity.jailInqueryEt.setText(activity.inqueryIdNumStr);
                        }
                    }
                    break;
                case MSG_IDCARD_ACQUIRED:
                    if (activity.isInquery) {
                        return;
                    }
                    if (msg.obj != null) {
                        String newText = (String) msg.obj;
                        activity.inqueryIdNumStr.delete(0, activity.inqueryIdNumStr.length());
                        activity.inqueryIdNumStr.append(activity.jailInqueryEt.getText());
                        if (activity.inqueryIdNumStr.length() >= 18) {
                            activity.robotManager.playTTS("请先删除号码后，再输入");
                            return;
                        }

                        activity.inqueryIdNumStr.append(newText);
                        LogUtil.d(TAG, "MSG_IDCARD_ACQUIRED before inqueryIdNumStr : " + activity.inqueryIdNumStr);
                        if (activity.inqueryIdNumStr.length() > 18) {
                            activity.inqueryIdNumStr.delete(18, activity.inqueryIdNumStr.length());
                        }
                        LogUtil.d(TAG, "MSG_IDCARD_ACQUIRED after inqueryIdNumStr : " + activity.inqueryIdNumStr);

                        activity.jailInqueryEt.setText(activity.inqueryIdNumStr);
                        if (activity.inqueryIdNumStr.length() == 18) {
                            activity.robotManager.playTTS("请确认");
                        }
                    }
                    break;
                case MSG_NETWORK_SWITCH_TIMEOUT:
                    //msg.arg1 = TYPE_WIFI;
                    //NetworkInfo networkInfo = activity.connectionManager.getActiveNetworkInfo();
                    //if (networkInfo == null || networkInfo.getType() == TYPE_WIFI) {
                    //    activity.set4GNetWorkEnabled(true);
                    //} else if (networkInfo.getType() == TYPE_MOBILE) {
                    //重试
                    //activity.isInquery = false;
                    activity.robotManager.playTTS("网络异常, 请检查网络设置");
                    activity.mhandler.obtainMessage(MSG_SHOW_INQUERY_VIEW).sendToTarget();
                    //}
                    break;
                case MSG_NETWORK_CHANGED:
                    int type = msg.arg1;
                    LogUtil.d(TAG, "MSG_NETWORK_CHANGED: " + type);
                    if (type == TYPE_WIFI) {
                        //wifi网络
                        LogUtil.d(TAG, "MSG_NETWORK_CHANGED isInquery: " + activity.isInquery);
                        if (activity.isInquery) {
                            activity.mhandler.obtainMessage(MSG_SHOW_INQUERYING_VIEW).sendToTarget();
                        }
                        activity.stopConnectingTimer();
                    } else if (type == TYPE_MOBILE) {
                        //重置网络
                        //activity.conncetRobotServer();

                        //4g网络
                        if (activity.inqstate == STATE_CONNECTING_FAILED) {
                            //连接失败
                            activity.robotManager.playTTS("网络连接失败 请重试");
                            activity.mhandler.obtainMessage(MSG_SHOW_INQUERY_VIEW).sendToTarget();
                        } else if (activity.inqstate == STATE_INQUERY_FAILED) {
                            //查询失败
                            activity.isInquery = false;
                            switch (activity.inquery_failed_code) {
                                case INQUERY_FAILED_NETWORK_ERR:
                                    activity.robotManager.playTTS("查询失败 网络连接错误 请重试");
                                    break;
                                case INQUERY_FAILED_NO_DATA:
                                    activity.robotManager.playTTS("查询失败 未查到数据 请重试");
                                    break;
                                case INQUERY_FAILED_TIMEOUT:
                                    activity.robotManager.playTTS("查询失败 超时 请重试");
                                    break;
                                    default:
                                        activity.robotManager.playTTS("查询失败 请重试 错误代码"
                                                + activity.inquery_failed_code);
                            }
                            activity.mhandler.obtainMessage(MSG_SHOW_INQUERY_VIEW).sendToTarget();
                        } else if (activity.inqstate == STATE_INQUERY_SUCCESS) {
                            //显示犯人信息界面
                            activity.isInquery = false;
                            //activity.robotManager.playTTS("网络链接失败 请重试");
                            activity.mhandler.obtainMessage(MSG_SHOW_INQUERY_SUCCESS_VIEW).sendToTarget();
                        }
                    }

                    if (activity.mhandler.hasMessages(MSG_NETWORK_SWITCH_TIMEOUT)) {
                        activity.mhandler.removeMessages(MSG_NETWORK_SWITCH_TIMEOUT);
                    }
                    break;
                case MSG_INQUERY_SUCCESS:
                    activity.inqstate = STATE_INQUERY_SUCCESS;
                    activity.set4GNetWorkEnabled(true);
                    break;
                case MSG_INQUERY_FAILED:
                    if (msg.arg1 == ERR_CONNECT_WIFI_FAILED) {
                        //网络连接失败
                        activity.inquery_failed_code = INQUERY_FAILED_NETWORK_ERR;
                    } else if (msg.arg1 == ERR_INQUERYING_FAILED){
                        //查询失败
                        activity.inquery_failed_code = INQUERY_FAILED_NO_DATA;
                    }
                    activity.inqstate = STATE_INQUERY_FAILED;
                    activity.set4GNetWorkEnabled(true);
                    break;
                case MSG_INQUERYING_TIMEOUT:
                    activity.inqstate = STATE_INQUERY_FAILED;
                    activity.inquery_failed_code = INQUERY_FAILED_TIMEOUT;
                    activity.set4GNetWorkEnabled(true);
                    break;
            }
        }
    }
}