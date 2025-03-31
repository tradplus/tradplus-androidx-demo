package com.tradplus.demo.mediavideo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.common.TPVideoAdPlayer;
//import com.tradplus.ads.base.common.TPVideoPlayerListener;
import com.tradplus.ads.base.common.TPVideoPlayerListener;
import com.tradplus.ads.mgr.mediavideo.TPCustomMediaVideoAd;
import com.tradplus.ads.open.mediavideo.MediaVideoAdListener;
import com.tradplus.ads.open.mediavideo.TPMediaVideo;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

public class NewMediaVideo extends AppCompatActivity implements View.OnClickListener {

    private TPMediaVideo tpMediaVideo;
    private TPCustomMediaVideoAd tpCustomMediaVideoAd;
    private FrameLayout mContainer;
    private MediaVideoUtils mediaVideoUtils;
    private static final String TAG = "TradPlusData";

    @Override
    protected void onCreate(Bundle savedmediaVideoUtilsState) {
        super.onCreate(savedmediaVideoUtilsState);
        setContentView(R.layout.activity_mv_firstpage);

        initLayout();
        initTPMediaView();
    }

    private void initTPMediaView() {
        tpMediaVideo = new TPMediaVideo(this,TestAdUnitId.MEDIAVIDEO_ADUNITID);// 插播
        // 设置监听
        tpMediaVideo.setAdListener(new MediaVideoAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: ");
                Toast.makeText(NewMediaVideo.this,"广告加载完成",Toast.LENGTH_SHORT).show();
                // 一次loadAd后，广告加载成功
                // 从缓存中取出加载成功的广告

            }

            @Override
            public void onAdFailed(TPAdError error) {
                // 一次loadAd后，广告加载失败
                Log.i(TAG, "onAdFailed: " + error.getErrorMsg());
                Toast.makeText(NewMediaVideo.this,"广告加载失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                // 广告被点击
                Log.i(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdResume(TPAdInfo tpAdInfo) {
                // 暂停后，广告继续播放
                Log.i(TAG, "onAdResume: ");
            }

            @Override
            public void onAdPause(TPAdInfo tpAdInfo) {
                // 播放后，广告暂停
                Log.i(TAG, "onAdPause: ");
            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {
                // 视频播放开始
                Log.i(TAG, "=== onAdVideoStart adSourcePlacementId:" + tpAdInfo.adSourcePlacementId);
                Toast.makeText(NewMediaVideo.this,"广告展示",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                // 视频播放结束
                Log.i(TAG, "onAdVideoEnd: ");
                if (tpCustomMediaVideoAd != null) {
                    tpCustomMediaVideoAd.onDestroy();
                    mContainer.removeAllViews();
                    Log.i(TAG, "removeAllViews: ");
                }
            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError error) {
                // 视频播放失败
                Log.i(TAG, "onAdVideoError: " + error.getErrorMsg());
            }

            @Override
            public void onAdSkiped(TPAdInfo tpAdInfo) {
                // 可跳过的插播广告，用户点击跳过
                Log.i(TAG, "onAdSkiped: ");
                if (tpCustomMediaVideoAd != null) {
                    tpCustomMediaVideoAd.onDestroy();
                    mContainer.removeAllViews();
                }
            }

            @Override
            public void onAdTapped(TPAdInfo tpAdInfo) {
                // 视频广告中，非点击区域被点击
                Log.i(TAG, "onAdTapped: ");
            }

            @Override
            public void onAdProgress(TPAdInfo tpAdInfo, float v, double v1) {
                // 播放进度
                Log.i(TAG, "onAdProgress v: " + v + ", v1 :" + v1);
            }
        });
    }

    private void initLayout() {
        mediaVideoUtils = MediaVideoUtils.getInstance();
        mContainer = findViewById(R.id.video_container);
        mContainer.setVisibility(View.VISIBLE);

        findViewById(R.id.btn_mute).setOnClickListener(this);
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        findViewById(R.id.btn_destroy).setOnClickListener(this);
    }


    private boolean isVideoMute = true;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_mute:
                isVideoMute = !isVideoMute;
                break;
            case R.id.btn_load:
                TPVideoPlayerListener tpVideoPlayerListener = new TPVideoPlayerListener() {
                    @Override
                    public Object getTPVideoPlayer() {
                        TPVideoAdPlayer videoAdPlayer = mediaVideoUtils.getVideoAdPlayer(isVideoMute, NewMediaVideo.this);
                        return videoAdPlayer;
                    }

                    @Override
                    public Object getContentProgressProvider() {
                        return null;
                    }
                };
                // 请求广告
                tpMediaVideo.loadAd(tpVideoPlayerListener);
                break;
            case R.id.btn_show:
                boolean ready = tpMediaVideo.isReady();
                if (ready) {
                    tpCustomMediaVideoAd = tpMediaVideo.getVideoAd();
                    Object tpAdVideoPlayerObj = tpCustomMediaVideoAd.getTPAdVideoPlayer();
                    TPVideoAdPlayer tpAdVideoPlayer = null;
                    if (tpAdVideoPlayerObj instanceof TPVideoAdPlayer) {
                        tpAdVideoPlayer = (TPVideoAdPlayer)tpAdVideoPlayerObj;
                    }
                    Object adDisplayContainerObj = tpCustomMediaVideoAd.getAdDisplayContainer();
                    if (adDisplayContainerObj instanceof AdDisplayContainer) {
                        AdDisplayContainer adDisplayContainer = (AdDisplayContainer) adDisplayContainerObj;
                        if (adDisplayContainer != null) {
                            ViewGroup adContainer = adDisplayContainer.getAdContainer();
                            if (adContainer != null) {
                                adContainer.setVisibility(View.VISIBLE);
                                Log.i(TAG, "tpAdVideoPlayer: " + tpAdVideoPlayer);
                                mediaVideoUtils.showTpMeidaVide(tpAdVideoPlayer, adContainer, mContainer);
                                tpCustomMediaVideoAd.start("");
                            }
                        }
                    }else if (adDisplayContainerObj instanceof FrameLayout) {
                        FrameLayout adContainer = (FrameLayout) adDisplayContainerObj;
                        if (adContainer != null) {
                            adContainer.setVisibility(View.VISIBLE);
                            Log.i(TAG, "tpAdVideoPlayer: " + tpAdVideoPlayer);
                            mediaVideoUtils.showTpMeidaVide(tpAdVideoPlayer, adContainer, mContainer);
                            tpCustomMediaVideoAd.start("");
                        }
                    }
                }else {
                    Toast.makeText(NewMediaVideo.this,"无可用广告",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_stop:
                // 播放暂停
                if (tpCustomMediaVideoAd != null) {
                    tpCustomMediaVideoAd.pause();
                }
                break;
            case R.id.btn_resume:
                // 暂停后继续播放
                if (tpCustomMediaVideoAd != null) {
                    tpCustomMediaVideoAd.resume();
                }
                break;
            case R.id.btn_destroy:
                // 销毁
                if (tpCustomMediaVideoAd != null) {
                    tpCustomMediaVideoAd.onDestroy();
                    mContainer.removeAllViews();
                    tpCustomMediaVideoAd = null;
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tpCustomMediaVideoAd != null) {
            tpCustomMediaVideoAd.onDestroy();
        }
        mContainer.removeAllViews();
        Log.i(TAG, "onDestroy: ");
    }
}