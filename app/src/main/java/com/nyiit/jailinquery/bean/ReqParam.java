package com.nyiit.jailinquery.bean;

public class ReqParam {

    /**
     * answerText : 请说出你的身份证号
     * answerTextPlay : true
     * card : {"recommend":{"cards":[{"text":"城市大脑那能有什么用呢","type":"text","uuid":"b4c069ac9a19260a4ae9118759432800"},{"text":"那智能零售用到了什么技术啊","type":"text","uuid":"964526fd050abd62e02bcb9c70204c59"},{"text":"那什么是物品识别技术","type":"text","uuid":"31206f02105758de3f60448665670ea7"},{"text":"交通大数据","type":"text","uuid":"47e357b2efcf7985bc98fa433529e5c7"},{"text":"南邮介绍","type":"text","uuid":"df27837563d238e8fb78c6f48324c306"},{"text":"城市大脑","type":"text","uuid":"fc972de10ab3cd8d47170f01ece0b5a0"},{"text":"豹咖啡应用场景","type":"text","uuid":"60b16c0cfd8c945a5087259d17ba701f"},{"text":"智能家居","type":"text","uuid":"e9791be3970751aca102150ed06168b3"},{"text":"互联网+","type":"text","uuid":"5c2f5d26bbd1dbeb1cc094cafd7bc5ac"},{"text":"南京系壳智能科技有限公司","type":"text","uuid":"b9d98d29221fb2843d2f536db1dc165d"}],"guides":[{"from":"order:0","outSpeech":{"text":"你好呀，生活的烦恼和妈妈说说","textPlay":true,"type":"text"},"uuid":"b681f752a39240a299bd7cf55e7230db"}]},"text":"请说出你的身份证号"}
     * intent : chat&chat
     * queryType : 1
     * sid : 5bdaa10f-7961-4a3e-be2f-077c5f3de19d
     * skillData : {"answer":{"data":"请说出你的身份证号","type":"text"},"recommend":{"cards":[{"text":"城市大脑那能有什么用呢","type":"text","uuid":"b4c069ac9a19260a4ae9118759432800"},{"text":"那智能零售用到了什么技术啊","type":"text","uuid":"964526fd050abd62e02bcb9c70204c59"},{"text":"那什么是物品识别技术","type":"text","uuid":"31206f02105758de3f60448665670ea7"},{"text":"交通大数据","type":"text","uuid":"47e357b2efcf7985bc98fa433529e5c7"},{"text":"南邮介绍","type":"text","uuid":"df27837563d238e8fb78c6f48324c306"},{"text":"城市大脑","type":"text","uuid":"fc972de10ab3cd8d47170f01ece0b5a0"},{"text":"豹咖啡应用场景","type":"text","uuid":"60b16c0cfd8c945a5087259d17ba701f"},{"text":"智能家居","type":"text","uuid":"e9791be3970751aca102150ed06168b3"},{"text":"互联网+","type":"text","uuid":"5c2f5d26bbd1dbeb1cc094cafd7bc5ac"},{"text":"南京系壳智能科技有限公司","type":"text","uuid":"b9d98d29221fb2843d2f536db1dc165d"}],"guides":[{"from":"order:0","outSpeech":{"text":"你好呀，生活的烦恼和妈妈说说","textPlay":true,"type":"text"},"uuid":"b681f752a39240a299bd7cf55e7230db"}]}}
     * slots : {}
     * soundAngle : -1
     * userText : 我要查询
     */

    private String answerText;
    private boolean answerTextPlay;
    private String card;
    private String intent;
    private int queryType;
    private String sid;
    private String skillData;
    private String slots;
    private int soundAngle;
    private String userText;

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isAnswerTextPlay() {
        return answerTextPlay;
    }

    public void setAnswerTextPlay(boolean answerTextPlay) {
        this.answerTextPlay = answerTextPlay;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSkillData() {
        return skillData;
    }

    public void setSkillData(String skillData) {
        this.skillData = skillData;
    }

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }

    public int getSoundAngle() {
        return soundAngle;
    }

    public void setSoundAngle(int soundAngle) {
        this.soundAngle = soundAngle;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }
}
