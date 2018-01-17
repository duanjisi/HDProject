
package com.atgc.hd.entity;
public class MessageItem extends BaseData {

    // emotion
    public static final int MESSAGE_TYPE_EMOTION = 1;
    // image
    public static final int MESSAGE_TYPE_IMG = 2;
    // Txt
    public static final int MESSAGE_TYPE_TXT = 3;
    // Record
    public static final int MESSAGE_TYPE_VIDEO = 4;
    // audio
    public static final int MESSAGE_TYPE_AUDIO = 5;
    // file
    public static final int MESSAGE_TYPE_FILE = 6;

    private int msgType;// 消息类型
    private String name;// 消息来自
    private long time;// 消息日期
    private String message;// 消息内容
    private int headImg;// 头像
    private boolean isComMeg = true;// 是否为收到的消息

    private int isNew;
    private int voiceTime = 0;
    private String temp;//发送时间戳
    private String nick;
    private String userid;
    private String avatar;
    private String msgId = "";

    public MessageItem() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgType   消息类型 MessageItem
     * @param name      用户名
     * @param date      时间
     * @param message   消息
     * @param headImg   头像
     * @param isComMeg  接收的消息 false不是true是
     * @param isNew     是否是新消息
     * @param voiceTime 录音的时间 如果没有为0
     */
    public MessageItem(int msgType, String name, long date, String message,
                       int headImg, boolean isComMeg, int isNew, int voiceTime, String temp) {
        super();
        this.msgType = msgType;
        this.name = name;
        this.time = date;
        this.message = message;
        this.headImg = headImg;
        this.isComMeg = isComMeg;
        this.isNew = isNew;
        this.voiceTime = voiceTime;
        this.temp = temp;
    }

    public MessageItem(int msgType, String name, long date, String message,
                       int headImg, boolean isComMeg, int isNew, int voiceTime, String temp,
                       String nick, String userid, String avatar) {
        super();
        this.msgType = msgType;
        this.name = name;
        this.time = date;
        this.message = message;
        this.headImg = headImg;
        this.isComMeg = isComMeg;
        this.isNew = isNew;
        this.voiceTime = voiceTime;
        this.temp = temp;
        this.nick = nick;
        this.userid = userid;
        this.avatar = avatar;
    }

    public int getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(int voiceTime) {
        this.voiceTime = voiceTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return time;
    }

    public void setDate(long date) {
        this.time = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHeadImg() {
        return headImg;
    }

    public void setHeadImg(int headImg) {
        this.headImg = headImg;
    }

    public boolean isComMeg() {
        return isComMeg;
    }

    public void setComMeg(boolean isComMeg) {
        this.isComMeg = isComMeg;
    }

    public static int getMessageTypeText() {
        return MESSAGE_TYPE_EMOTION;
    }

    public static int getMessageTypeImg() {
        return MESSAGE_TYPE_IMG;
    }

    public static int getMessageTypeFile() {
        return MESSAGE_TYPE_FILE;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    //注意这里重写了equals方法
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            if (this.getClass() == obj.getClass()) {
                MessageItem u = (MessageItem) obj;
                if (this.getAvatar().equals(u.getAvatar()) &&
                        this.getMessage().equals(u.getMessage()) &&
                        this.getName().equals(u.getName()) &&
                        this.getUserid().equals(u.getUserid()) &&
                        this.getNick().equals(u.getNick()) &&
                        this.getUserid().equals(u.getUserid()) &&
                        this.getMsgType() == u.getMsgType() &&
                        this.getDate() == u.getDate() &&
                        this.getMsgType() == u.getMsgType()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public String toString() {
        return "MessageItem{" +
                "msgType=" + msgType +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", message='" + message + '\'' +
                ", headImg=" + headImg +
                ", isComMeg=" + isComMeg +
                ", isNew=" + isNew +
                ", voiceTime=" + voiceTime +
                ", temp='" + temp + '\'' +
                ", nick='" + nick + '\'' +
                ", userid='" + userid + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
