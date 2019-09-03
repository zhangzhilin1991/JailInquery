package com.nyiit.jailinquery.Manager;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.IntDef;

import com.ainirobot.coreservice.client.ApiListener;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.ainirobot.coreservice.client.module.ModuleCallbackApi;
import com.ainirobot.coreservice.client.speech.SkillApi;
import com.ainirobot.coreservice.client.speech.SkillCallback;
import com.ainirobot.coreservice.client.speech.entity.TTSEntity;
import com.ainirobot.jianyu.speech.manager.TTSManager;
import com.google.gson.Gson;
import com.nyiit.jailinquery.bean.ReqParam;
import com.nyiit.jailinquery.tools.LogUtil;
import com.nyiit.jailinquery.tools.NumericUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.nyiit.jailinquery.tools.NumericUtil.extractNumericFormText;

/**
 * 豹小秘管理类
 * 处理语音回调以及语音识别相关的功能
 */
@Singleton
public class RobotManager {
    private final String TAG = RobotManager.class.getName();

    final SkillApi skillApi = new SkillApi();
    //SkillApi skillApi();

    private boolean isLocalTts = true;

    //@Inject
    TTSManager ttsManager;

    public static final int TYPE_SERVER = 0;
    public static final int TYPE_API = 1;

    @IntDef({TYPE_SERVER, TYPE_API})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE_CONNECT {}

    public interface RobotListener {
        //void onApiDiabaled();
        void onConnected(@TYPE_CONNECT int type);
        void onDisconnected(@TYPE_CONNECT int type);

        //当获取到id时
        void onIdCardNumAcquired(String idCardNum);

        //是否确认
        void onIdCardNumConfirmed(boolean isConfirmed);

        void onExit();

        void onReturn();

        void onChat(String msg);
    }

    private RobotListener mRobotListener;

    public void registerRobotListener(RobotListener listener) {
        this.mRobotListener = listener;
    }

    public void unregisterRobotListener() {
        this.mRobotListener = null;
    }

    public RobotManager(TTSManager ttsManager) {
        this.ttsManager = ttsManager;
    }

    public void connectServer(Context context) {
        RobotApi.getInstance().connectServer(context, new ApiListener() {
            @Override
            public void handleApiDisabled() {
                LogUtil.d(TAG, "connectServer handleApiDisabled()");
            }

            @Override
            public void handleApiConnected() {
                //Server已连接,设置接收请求的回调,包含语音指令、系统事件等
                RobotApi.getInstance().setCallback(new ModuleCallback());
                LogUtil.d(TAG, "connectServer handleApiConnected()");
                //listener.onConnected();
                if (mRobotListener != null) {
                    mRobotListener.onConnected(TYPE_SERVER);
                }
            }

            @Override
            public void handleApiDisconnected() {
                //连接已断开
                //listener.onDisconnected();
                LogUtil.d(TAG, "connectServer handleApiDisconnected()");
                if (mRobotListener != null) {
                    mRobotListener.onDisconnected(TYPE_SERVER);
                }
            }
        });
    }

    public void wakeUp() {
        /*RobotApi.getInstance().wakeUp(reqId, reqParamBean.getSoundAngle(), new ActionListener() {
            public void onResult(int status, String responseString) throws RemoteException {
                LogUtil.d(TAG, TAG, "wakeUp() onResult= " + status);
            }

            public void onError(int errorCode, String errorString) throws RemoteException {
                LogUtil.d(TAG, TAG, "wakeUp() errorCode= " + errorCode);
            }

            public void onStatusUpdate(int status, String data) throws RemoteException {
                LogUtil.d(TAG, TAG, "wakeUp() onStatusUpdate= " + status);
            }
        });
        */
    }

    public void connectApi(Context context) {
        //skillapi
        //skillApi.setRecognizeMode(true);
        skillApi.connectApi(context, new ApiListener() {
            @Override
            public void handleApiDisabled() {
                LogUtil.d(TAG, "connectApi handleApiDisabled()");
            }

            @Override
            public void handleApiConnected() {
                //语音服务连接成功,注册语音回调
                skillApi.registerCallBack(new RobotSkillCallback());
                LogUtil.d(TAG, "connectApi handleApiConnected()");
                skillApi.setRecognizeMode(true);

                if (mRobotListener != null) {
                    mRobotListener.onConnected(TYPE_API);
                }
            }

            @Override
            public void handleApiDisconnected() {
                //语音服务已断开
                LogUtil.d(TAG, "connectApi handleApiDisconnected()");
                if (mRobotListener != null) {
                    mRobotListener.onDisconnected(TYPE_API);
                }
            }
        });
    }

    public void connect(Context context) {
        connectServer(context);
        connectApi(context);

        //tts init
        if (isLocalTts) {
            ttsManager.createSynthesizer(context);
        }
    }

    public void disconnect() {
        skillApi.disconnectApi();
        RobotApi.getInstance().disconnectApi();

        //tts destory
        if (isLocalTts) {
            ttsManager.releaseSynthesizer();
        }
    }

    private void playTTSLocal(String text) {
        //if (isLocalTts) {
            ttsManager.play(text);
        //    return;
        //}
    }

    private void stopTTSLocal()
    {
        ttsManager.stop();
    }

    public void playTTS(String text)
    {
        playTTS(text, false);
    }

    /**
     * TTS播放
     * @param text
     */
    public void playTTS(String text, boolean isLocalTts) {
        if (isLocalTts) {
            //ttsManager.play(text);
            //return;
            playTTSLocal(text);
            return;
        }
        /*skillApi.playText(text, new TextListener() {
            @Override
            public void onStart() {
//播放开始
                LogUtil.d(TAG, "playTTS onStart()");
            }
            @Override
            public void onStop() {
//播放停止
                LogUtil.d(TAG, "playTTS onStop()");
            }
            @Override
            public void onError() {
//播放错误
                LogUtil.d(TAG, "playTTS onError()");
            }
            @Override
            public void onComplete() {
//播放完成
                LogUtil.d(TAG, "playTTS onComplete()");
            }
        });
        */
        skillApi.playText(new TTSEntity(text), new TextListener() {
            @Override
            public void onStart() {
//播放开始
                LogUtil.d(TAG, "playTTS onStart()");
            }
            @Override
            public void onStop() {
//播放停止
                LogUtil.d(TAG, "playTTS onStop()");
            }
            @Override
            public void onError() {
//播放错误
                LogUtil.d(TAG, "playTTS onError()");
            }
            @Override
            public void onComplete() {
//播放完成
                LogUtil.d(TAG, "playTTS onComplete()");
            }
        });
    }

    /**
     * 停止TTS播放
     */
    public void stopTTS() {
        //if (isLocalTts) {
        //    ttsManager.stop();
        //    return;
        //}
        //
        stopTTSLocal();

        skillApi.stopTTS();
    }

    public class RobotSkillCallback extends SkillCallback
    {
        @Override
        public void onSpeechParResult(String s) throws RemoteException {
            LogUtil.d(TAG, "onSpeechParResult() " + s);
        }

        @Override
        public void onStart() throws RemoteException {
            LogUtil.d(TAG, "onStart()");
        }

        @Override
        public void onStop() throws RemoteException {
            LogUtil.d(TAG, "onStop()");
        }

        @Override
        public void onVolumeChange(int i) throws RemoteException {
            LogUtil.d(TAG, "onVolumeChange()" + i);

        }

        @Override
        public void onQueryEnded(int i) throws RemoteException {
            LogUtil.d(TAG, "onQueryEnded() " + i);
        }

        @Override
        public void onQueryAsrResult(String asrResult) throws RemoteException {
            super.onQueryAsrResult(asrResult);
            LogUtil.d(TAG, "onQueryAsrResult() asrResult " + asrResult);
            //if (asrResult.con) {
            //}
            //正则表达式，用于匹配非数字串，+号用于匹配出多个非数字串
            /*String regEx="[^0-9]+";
            Pattern pattern = Pattern.compile(regEx);
            //用定义好的正则表达式拆分字符串，，把字符串中的数字留出来
            String[] cs = pattern.split(asrResult);
            LogUtil.d(TAG, TAG, "cs: " + cs);
            //System.out.println(Arrays.toString(cs));
            String id = cs.toString();
            LogUtil.d(TAG, TAG, "id: " + id);
            if (id.length() == 0) {
                return;
            }
            if (id.length() == 17) {
                //如果是17位，尝试加X
                id = id + "X";
            }
            LogUtil.d(TAG, TAG, "id: " + id);
            */
            if (mRobotListener != null ) {
                mRobotListener.onChat(asrResult);
                //playTTS("请确认");
            }

            //无赖哎
            String idCardNum = NumericUtil.extractNumeric(asrResult);
            if (idCardNum == null || idCardNum.length() == 0) {
                //playTTS("识别错误 请重新输入");
                //return;
                idCardNum = extractNumericFormText(asrResult);
            }
            if (idCardNum == null || idCardNum.length() == 0) {
                return;
            }
            LogUtil.d(TAG, "idCardNum: " + idCardNum);
            if (mRobotListener != null ) {
                mRobotListener.onIdCardNumAcquired(idCardNum);
                //playTTS("请确认");
            }

        }
    }

    public class ModuleCallback extends ModuleCallbackApi {
        @Override
        public boolean onSendRequest(int reqId, String reqType, String reqText, String reqParam)
                throws RemoteException {
            //接收语音指令,
            // reqType : 语音指令类型
            // reqText : 语音识别内容
            // reqParam : 语音指令参数


            try {
                LogUtil.d(TAG, "onSendRequest(): reqType = " + reqType + ", reqText = " + reqText
                        + ", reqParam = " + reqParam);

                if (reqType.equals("req_speech_wakeup")) {
                    skillApi.setRecognizeMode(true);
                    return true;
                }
                ReqParam reqParamBean = new Gson().fromJson(reqParam, ReqParam.class);

                RobotApi.getInstance().wakeUp(reqId, reqParamBean.getSoundAngle(), new ActionListener() {
                public void onResult(int status, String responseString) throws RemoteException {
                    LogUtil.d(TAG, "wakeUp() onResult= " + status);
                }

                public void onError(int errorCode, String errorString) throws RemoteException {
                    LogUtil.d(TAG, "wakeUp() errorCode= " + errorCode);
                }

                public void onStatusUpdate(int status, String data) throws RemoteException {
                    LogUtil.d(TAG, "wakeUp() onStatusUpdate= " + status);
                }
            });

            //if (reqType.equals("req_speech_wakeup")) {
            //    skillApi.setRecognizeMode(true);
            if (reqType.equals("chat&chat") && reqText.equals("我要查询")) {
                stopTTS();
                playTTS(reqParamBean.getAnswerText());
            } else if (reqType.equals("verification_code&inform")) {
                //需要通知界面
                /*stopTTS();
                String text = reqText;
                LogUtil.d(TAG, TAG, "text = " + text);
                //判断是否含有数字？？？
                String num = NumericUtil.extractNumeric(text);
                LogUtil.d(TAG, TAG, "num = " + num);
                if (num == null && num.length() == 0) {
                    playTTS("识别错误 请重新输入");
                    return true;
                }
                if (mRobotListener != null) {
                    mRobotListener.onIdCardNumAcquired(num);
                    playTTS("请确认");
                }
                */
            } else if (reqType.equals("general_command&confirm")) {
                //if (reqText.contains("是") || reqText.contains("确认") || reqText.equals("确定") || reqText.contains("正确")) {
                    //开始查询，
                    stopTTS();
                    if (mRobotListener != null) {
                        mRobotListener.onIdCardNumConfirmed(true);
                    }
                //} else {

                //}
            } else if (reqType.equals("general_command&cancel")) {
                if (mRobotListener != null) {
                    mRobotListener.onIdCardNumConfirmed(false);
                }
            } else if (reqType.equals("general_command&stop")) {
                //退出
                stopTTS();
                if (mRobotListener != null) {
                    mRobotListener.onExit();
                }

            } else if (reqType.equals("general_command&pause")) {
                //停止tts
                stopTTS();
            } else if (reqType.equals("general_command&return")) {
                //退出
                stopTTS();
                if (mRobotListener != null) {
                    mRobotListener.onReturn();
                }
            }

            } catch (Exception e) {
                LogUtil.d(TAG, "onSendRequest error: " + e.getMessage());
            }

            return true;
        }

        @Override
        public void onRecovery() throws RemoteException {
            //控制权恢复,收到该事件后,重新恢复对机器人的控制
            LogUtil.d(TAG, "onRecovery()");
        }

        @Override
        public void onSuspend() throws RemoteException {
            //控制权被系统剥夺,收到该事件后,所有Api调用无效
            LogUtil.d(TAG, "onSuspend()");
        }
    }




}
