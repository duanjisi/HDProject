/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.comm.utils.ImageLoaderUtils;
import com.atgc.hd.comm.utils.SoundUtil;
import com.atgc.hd.comm.utils.TimeUtil;
import com.atgc.hd.comm.utils.UIUtils;
import com.atgc.hd.entity.MessageItem;
import com.atgc.hd.widget.xlistview.GifTextView;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>描述：通讯消息适配器
 * <p>作者：duanjisi 2018年 01月 25日
 */
@SuppressLint("NewApi")
public class MessageAdapter extends BaseAdapter {
    private static final String TAG = MessageAdapter.class.getSimpleName();
    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
    public static final int MESSAGE_TYPE_INVALID = -1;

    public static final int MESSAGE_TYPE_MINE_EMOTION = 0x00;
    public static final int MESSAGE_TYPE_MINE_IMAGE = 0x01;
    public static final int MESSAGE_TYPE_MINE_VIDEO = 0x02;
    public static final int MESSAGE_TYPE_MINE_TXT = 0x06;
    public static final int MESSAGE_TYPE_MINE_AUDIO = 0x08;

    public static final int MESSAGE_TYPE_OTHER_EMOTION = 0x03;
    public static final int MESSAGE_TYPE_OTHER_IMAGE = 0x04;
    public static final int MESSAGE_TYPE_OTHER_VIDEO = 0x05;
    public static final int MESSAGE_TYPE_OTHER_TXT = 0x07;
    public static final int MESSAGE_TYPE_OTHER_AUDIO = 0x09;

    public static final int MESSAGE_TYPE_TIME_TITLE = 0x07;
    public static final int MESSAGE_TYPE_HISTORY_DIVIDER = 0x08;
    private static final int VIEW_TYPE_COUNT = 2;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MessageItem> mMsgList;
    private long mPreDate;
    private Resources resources;
    private ImageLoader imageLoader;
    private int widthScreen;
    private int widthMin;
    private SoundUtil mSoundUtil = SoundUtil.getInstance();
    private Handler mHandler = new Handler();
    private int mImageHeight;
    private String toUid;

    public MessageAdapter(Context context, List<MessageItem> msgList) {
        this.mContext = context;
        widthScreen = UIUtils.getScreenWidth(context) / 2;
        widthMin = UIUtils.getScreenWidth(context) / 3;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 3;
        resources = context.getResources();
        mMsgList = msgList;
        mInflater = LayoutInflater.from(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
//        mSpUtil = PushApplication.getInstance().getSpUtil();
//        mSoundUtil = SoundUtil.getInstance();
    }

    public MessageAdapter(Context context, List<MessageItem> msgList, String toUid) {
        this.mContext = context;
        this.toUid = toUid;
        widthScreen = UIUtils.getScreenWidth(context) / 2;
        widthMin = UIUtils.getScreenWidth(context) / 4;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 3;
        resources = context.getResources();
        mMsgList = msgList;
        mInflater = LayoutInflater.from(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
//        mSpUtil = PushApplication.getInstance().getSpUtil();
//        mSoundUtil = SoundUtil.getInstance();
    }

    public void removeHeadMsg() {
        if (mMsgList.size() - 10 > 10) {
            for (int i = 0; i < 10; i++) {
                mMsgList.remove(i);
            }
            notifyDataSetChanged();
        }
    }

    public void removeItem(MessageItem item) {
        Iterator iterator = mMsgList.iterator();
        while (iterator.hasNext()) {
            MessageItem mode = (MessageItem) iterator.next();
            if (mode.getMsgId().equals(item.getMsgId())) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }


    public void setmMsgList(List<MessageItem> msgList) {
        mMsgList = msgList;
        notifyDataSetChanged();
    }

    public void upDateMsg(MessageItem msg) {
        mMsgList.add(msg);
        notifyDataSetChanged();
    }

    public void upDateMsgByList(List<MessageItem> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                mMsgList.add(list.get(i));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMsgList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMsgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        MessageHolderBase holder = null;
        if (null == convertView && null != mInflater) {
            holder = new MessageHolderBase();
            switch (type) {
//                case MESSAGE_TYPE_MINE_EMOTION: {
//                    convertView = mInflater.inflate(
//                            R.layout.zf_chat_mine_emotion_message_item, parent, false);
//                    holder = new EmotionMessageHolder();
//                    convertView.setTag(holder);
//                    fillEmotionMessageHolder((EmotionMessageHolder) holder,
//                            convertView);
//                    break;
//                }
//                case MESSAGE_TYPE_MINE_IMAGE: {
//                    convertView = mInflater.inflate(
//                            R.layout.zf_chat_mine_image_message_item, parent, false);
//                    holder = new ImageMessageHolder();
//                    convertView.setTag(holder);
//                    // fillTextMessageHolder(holder, convertView);
//                    fillImageMessageHolder((ImageMessageHolder) holder,
//                            convertView);
//                    break;
//                }
//                case MESSAGE_TYPE_MINE_VIDEO: {
//                    convertView = mInflater.inflate(
//                            R.layout.zf_chat_mine_video_message_item, parent, false);
//                    holder = new VideoMessageHolder();
//                    convertView.setTag(holder);
//                    fillVideoMessageHolder((VideoMessageHolder) holder,
//                            convertView);
//                    break;
//                }
//                case MESSAGE_TYPE_MINE_TXT: {
//                    convertView = mInflater.inflate(
//                            R.layout.zf_chat_mine_text_message_item, parent, false);
//                    holder = new TextMessageHolder();
//                    convertView.setTag(holder);
//                    fillTextMessageHolder((TextMessageHolder) holder,
//                            convertView);
//                    break;
//                }
                case MESSAGE_TYPE_MINE_AUDIO: {//声音
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_mine_audio_message_item, parent, false);
                    holder = new AudioMessageHolder();
                    convertView.setTag(holder);
                    fillAudioMessageHolder((AudioMessageHolder) holder,
                            convertView);
                    break;
                }
//                case MESSAGE_TYPE_OTHER_EMOTION: {
//                    convertView = mInflater.inflate(
//                            R.layout.zf_chat_other_emotion_message_item, parent, false);
//                    holder = new EmotionMessageHolder();
//                    convertView.setTag(holder);
//                    fillEmotionMessageHolder((EmotionMessageHolder) holder,
//                            convertView);
//                    break;
//                }
//                case MESSAGE_TYPE_OTHER_IMAGE: {
//                    convertView = mInflater
//                            .inflate(R.layout.zf_chat_other_image_message_item,
//                                    parent, false);
//                    holder = new ImageMessageHolder();
//                    convertView.setTag(holder);
//                    fillImageMessageHolder((ImageMessageHolder) holder,
//                            convertView);
//                    break;
//                }
//                case MESSAGE_TYPE_OTHER_VIDEO: {
//                    convertView = mInflater
//                            .inflate(R.layout.zf_chat_other_video_message_item,
//                                    parent, false);
//                    holder = new VideoMessageHolder();
//                    convertView.setTag(holder);
//                    fillVideoMessageHolder((VideoMessageHolder) holder,
//                            convertView);
//                    break;
//                }
//                case MESSAGE_TYPE_OTHER_TXT: {
//                    convertView = mInflater.inflate(
//                            R.layout.zf_chat_other_text_message_item, parent, false);
//                    holder = new TextMessageHolder();
//                    convertView.setTag(holder);
//                    fillTextMessageHolder((TextMessageHolder) holder,
//                            convertView);
//                    break;
//                }
                case MESSAGE_TYPE_OTHER_AUDIO: {
                    convertView = mInflater.inflate(
                            R.layout.zf_chat_other_audio_message_item, parent, false);
                    holder = new AudioMessageHolder();
                    convertView.setTag(holder);
                    fillAudioMessageHolder((AudioMessageHolder) holder,
                            convertView);
                    break;
                }
                default:
                    break;
            }
        } else {
            holder = (MessageHolderBase) convertView.getTag();
        }
        final MessageItem mItem = mMsgList.get(position);
        if (mItem != null) {
            int msgType = mItem.getMsgType();
            if (msgType == MessageItem.MESSAGE_TYPE_AUDIO) {
                handleAudioMessage((AudioMessageHolder) holder, mItem, parent);
            }
        }
        return convertView;
    }


    private void scalLoadImage(ImageMessageHolder holder, String url) {
        String[] size = getSize(url);
        int width = Integer.parseInt(size[0]);
        int height = Integer.parseInt(size[1]);
        scaleSize(holder.ivphoto, width, height);
        imageLoader.displayImage(url, holder.ivphoto, ImageLoaderUtils.getDisplayImageOptions());
    }

    private void scaleSize(ImageView iv, int w, int h) {
        int width = w;
        int height = h;
        if (w > widthScreen) {
            while (width > widthScreen) {
                width = width / 2;
                height = height / 2;
            }
        } else if (w < widthMin) {
            while (width < widthMin) {
                width = width * 2;
                height = height * 2;
            }
        }
        iv.getLayoutParams().width = width;
        iv.getLayoutParams().height = height;
    }

    private String[] getSize(String url) {
        Log.i("liwya", url);
        String str = url.substring(url.indexOf("-"), url.length());
        String size = str.substring(str.indexOf("-") + 1, str.indexOf("."));
        return size.split("x");
    }

    private void scalLoadImageVideo(VideoMessageHolder holder, String url) {
        String[] size = getSize(url);
        int width = Integer.parseInt(size[0]);
        int height = Integer.parseInt(size[1]);
//        if (width > widthScreen / 2) {
//            Log.i("info", "=====width:" + width + "\n" + "height:" + height);
//            holder.iv_cover.getLayoutParams().width = width / 2;
//            holder.iv_cover.getLayoutParams().height = height / 2;
//        }
        scaleSize(holder.iv_cover, width, height);
        imageLoader.displayImage(url, holder.iv_cover, ImageLoaderUtils.getDisplayImageOptions());
    }

    //    /**
//     * @param holder
//     * @param info
//     * @param isMine
//     * @param parent
//     * @param position
//     * @Description 处理语音消息
//     */
    private void handleAudioMessage(final AudioMessageHolder holder,
                                    final MessageItem mItem, final View parent) {
        handleBaseMessage(holder, mItem);
        // 语音
        holder.voiceTime.setText(TimeUtil.getVoiceRecorderTime(mItem
                .getVoiceTime()));
        holder.msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItem.getMsgType() == MessageItem.MESSAGE_TYPE_AUDIO) {
                    if (mItem.isComMeg()) {
                        startAnimation(holder.msg, R.drawable.voice_other_animlist);
                    } else {
                        startAnimation(holder.msg, R.drawable.voice_my_animlist);
                    }
                    // 播放语音
                    mSoundUtil.playRecorder(mContext, mItem.getMessage(), new SoundUtil.CompletionListener() {
                        @Override
                        public void onCompletion() {
                            if (mItem.isComMeg()) {
                                stopAnimation(holder.msg, R.drawable.voice_other_animlist);
                                holder.msg.setImageResource(R.drawable.chatto_se_playing);
                            } else {
                                stopAnimation(holder.msg, R.drawable.voice_my_animlist);
                                holder.msg.setImageResource(R.drawable.chatto_voice_playing);
                            }
                        }
                    });
                }
            }
        });
    }


    private void startAnimation(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();
    }

    private void stopAnimation(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.stop();
    }

    private void handleBaseMessage(MessageHolderBase holder,
                                   final MessageItem mItem) {
        holder.time.setText(TimeUtil.getChatTime(Long.parseLong(mItem.getTemp())));
        holder.time.setVisibility(View.VISIBLE);

//        if (mItem.isComMeg() && !toUid.equals("")) {
//            boolean showNick = PreferenceUtils.getBoolean(mContext, PrefKey.SHOW_NICK_NAME + toUid, true);
//            if (showNick) {
//                UIUtils.showView(holder.nick);
//            } else {
//                UIUtils.hindView(holder.nick);
//            }
//        }
        holder.nick.setText(mItem.getNick());
        holder.progressBar.setVisibility(View.GONE);
        holder.progressBar.setProgress(50);
        imageLoader.displayImage(mItem.getAvatar(), holder.head, ImageLoaderUtils.getDisplayImageOptions());
        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void fillBaseMessageholder(MessageHolderBase holder,
                                       View convertView) {
        holder.head = (ImageView) convertView.findViewById(R.id.icon);
        holder.nick = (TextView) convertView.findViewById(R.id.tv_nick_name);
        holder.time = (TextView) convertView.findViewById(R.id.datetime);
        // holder.msg = (GifTextView) convertView.findViewById(R.id.textView2);
        holder.rlMessage = (RelativeLayout) convertView
                .findViewById(R.id.relativeLayout1);
//         holder.ivphoto = (ImageView) convertView
//         .findViewById(R.id.iv_chart_item_photo);
        holder.progressBar = (ProgressBar) convertView
                .findViewById(R.id.progressBar1);
        // holder.voiceTime = (TextView) convertView
        // .findViewById(R.id.tv_voice_time);
        holder.flPickLayout = (FrameLayout) convertView
                .findViewById(R.id.message_layout);
    }

    private void fillAudioMessageHolder(AudioMessageHolder holder,
                                        View convertView) {
        fillBaseMessageholder(holder, convertView);
        holder.voiceTime = (TextView) convertView
                .findViewById(R.id.tv_voice_time);
        holder.ivphoto = (ImageView) convertView
                .findViewById(R.id.iv_chart_item_photo);
        holder.msg = (ImageView) convertView.findViewById(R.id.textView2);
//        holder.msg = (GifView) convertView.findViewById(R.id.textView2);
    }


    private static class MessageHolderBase {
        ImageView head;
        TextView time;
        TextView nick;
        ImageView imageView;
        ProgressBar progressBar;
        RelativeLayout rlMessage;
        FrameLayout flPickLayout;
    }

    private static class TextMessageHolder extends MessageHolderBase {
        /**
         * 文字消息体
         */
        TextView tvMsg;
    }

    private static class EmotionMessageHolder extends MessageHolderBase {
        /**
         * 表情消息体
         */
//        GifView gifView;
//        GifImageView gifView;
        ImageView gifView;
        ImageView ivEmo;
    }

    private static class ImageMessageHolder extends MessageHolderBase {
        /**
         * 图片消息体
         */
        ImageView ivphoto;

    }

    private static class AudioMessageHolder extends MessageHolderBase {
        ImageView ivphoto;
        /**
         * 语音秒数
         */
        TextView voiceTime;
        //        GifView msg;
        ImageView msg;
    }

    private static class VideoMessageHolder extends MessageHolderBase {
        ImageView iv_cover;
        TextView duration;
        ImageView iv_player;
    }


    /**
     * 取名字f010
     *
     * @param faceName
     */
    private CharSequence options(String faceName) {
        int start = faceName.lastIndexOf("/");
        CharSequence c = faceName.subSequence(start + 1, faceName.length() - 4);
        return c;
    }

    static class ViewHolder {

        ImageView head;
        TextView time;
        GifTextView msg;
        ImageView imageView;
        ProgressBar progressBar;
        TextView voiceTime;
        ImageView ivphoto;
        RelativeLayout rlMessage;
        FrameLayout flPickLayout;
    }

    @Override
    public int getItemViewType(int position) {
        // logger.d("chat#getItemViewType -> position:%d", position);
        try {
            if (position >= mMsgList.size()) {
                return MESSAGE_TYPE_INVALID;
            }
            MessageItem item = mMsgList.get(position);
            if (item != null) {
                boolean comMeg = item.isComMeg();
                int type = item.getMsgType();
                if (comMeg) {
                    // 接受的消息
                    switch (type) {
                        case MessageItem.MESSAGE_TYPE_AUDIO: {
                            return MESSAGE_TYPE_OTHER_AUDIO;
                        }
                        default:
                            break;
                    }
                } else {
                    // 发送的消息
                    switch (type) {
                        case MessageItem.MESSAGE_TYPE_AUDIO: {
                            return MESSAGE_TYPE_MINE_AUDIO;
                        }
                        default:
                            break;
                    }
                }
            }
            return MESSAGE_TYPE_INVALID;
        } catch (Exception e) {
            Log.e("fff", e.getMessage());
            return MESSAGE_TYPE_INVALID;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

}