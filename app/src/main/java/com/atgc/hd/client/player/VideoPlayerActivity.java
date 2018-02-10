/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.client.player;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.atgc.hd.R;
import com.atgc.hd.comm.utils.NetworkUtil;
import com.bumptech.glide.Glide;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

/**
 * Created by GMARUnity on 2017/3/27.
 */
public class VideoPlayerActivity extends Activity {
    private PLVideoTextureView mVideoView;
    private String mVideoPath;
    private int mIsLiveStreaming = 1;
    private MediaController mMediaController;
    private ImageView iv_coverView, iv_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        initView();
    }

    private void initView() {
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_coverView = (ImageView) findViewById(R.id.iv_coverView);
        mVideoView = (PLVideoTextureView) findViewById(R.id.videoView);
        View loadingView = findViewById(R.id.loading);
        mVideoView.setBufferingIndicator(loadingView);
        mVideoView.setScreenOnWhilePlaying(true);
        int codec = getIntent().getIntExtra("mediaCodec", AVOptions.MEDIA_CODEC_SW_DECODE);
        mVideoPath = getIntent().getStringExtra("videoPath");
        mMediaController = new MediaController(this, false, mIsLiveStreaming == 1);
        mMediaController.setMediaPlayer(mVideoView);
        mVideoView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.setMediaController(mMediaController);
        setOptions(codec);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        String imgurl = getIntent().getStringExtra("imgurl");
        if (!TextUtils.isEmpty(imgurl)) {
            iv_coverView.setVisibility(View.VISIBLE);
            Glide.with(this).load(imgurl).into(iv_coverView);
            mVideoView.setCoverView(iv_coverView);
        }
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.setOnVideoSizeChangedListener(new PLMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height, int videoSar, int videoDen) {
                if (width > height) {
                    mVideoView.setDisplayOrientation(270);
                }
            }
        });
        mVideoView.start();
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
//            showToastTips("Play Completed !");
//            finish();
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToast("Invalid URL !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToast("404 resource not found !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToast("Connection refused !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToast("连接超时 !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToast("Empty playlist !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToast("Stream disconnected !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToast("Network IO Error !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToast("Unauthorized Error !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToast("Prepare timeout !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToast("Read frame timeout !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    setOptions(AVOptions.MEDIA_CODEC_SW_DECODE);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToast("unknown error !", false);
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            if (isNeedReconnect) {
                sendReconnectMessage();
            } else {
                finish();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    private void showToast(String str, boolean flag) {
        if (!TextUtils.isEmpty(str)) {
            Toast.makeText(VideoPlayerActivity.this, str, Toast.LENGTH_LONG).show();
        }
    }

    private void setOptions(int codecType) {
        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        //options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        options.setInteger(AVOptions.KEY_PROBESIZE, 500000000);
        // Some optimization with buffering mechanism when be set to 1
        //options.setInteger(AVOptions.KEY_LIVE_STREAMING, mIsLiveStreaming);
        if (mIsLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codecType);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        mVideoView.setAVOptions(options);
    }

    private void sendReconnectMessage() {
        showToast("正在重连...", true);
        //mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(0x01), 500);
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 0x01) {
                return;
            }
            if (NetworkUtil.networkAvailable(VideoPlayerActivity.this)) {
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

}
