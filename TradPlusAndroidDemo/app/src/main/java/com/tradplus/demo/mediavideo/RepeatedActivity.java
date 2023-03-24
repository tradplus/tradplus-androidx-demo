package com.tradplus.demo.mediavideo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.mgr.mediavideo.TPCustomMediaVideoAd;
import com.tradplus.ads.open.mediavideo.MediaVideoAdListener;
import com.tradplus.ads.open.mediavideo.TPMediaVideo;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

import java.util.ArrayList;

public class RepeatedActivity extends AppCompatActivity implements View.OnClickListener {

    private TPMediaVideo tpMediaVideo;
    private ViewGroup mContainer;
    private ArrayList<TPCustomMediaVideoAd> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeated);

        initLayout();
        initTPMediaView();
    }

    private void initLayout() {
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_show).setEnabled(false);
        mContainer = findViewById(R.id.video_container);
    }

    private void initTPMediaView() {
        // 创建广告位对象
        if (tpMediaVideo == null) {
            tpMediaVideo = new TPMediaVideo(this, TestAdUnitId.MEDIAVIDEO_ADUNITID);
        }

        // 设置监听
        tpMediaVideo.setAdListener(new MediaVideoAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Toast.makeText(RepeatedActivity.this,"广告加载完成",Toast.LENGTH_SHORT).show();
                // 一次loadAd后，广告加载成功
                // 从缓存中取出加载成功的广告
                findViewById(R.id.btn_show).setEnabled(true);
                TPCustomMediaVideoAd videoAd = tpMediaVideo.getVideoAd();
                arrayList.add(videoAd);


            }

            @Override
            public void onAdFailed(TPAdError error) {
                // 一次loadAd后，广告加载失败
                Toast.makeText(RepeatedActivity.this,"广告加载失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                // 广告被点击
            }

            @Override
            public void onAdResume(TPAdInfo tpAdInfo) {
                // 暂停后，广告继续播放
            }

            @Override
            public void onAdPause(TPAdInfo tpAdInfo) {
                // 播放后，广告暂停
            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {
                // 视频播放开始
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                // 视频播放结束
            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError error) {
                // 视频播放失败
            }

            @Override
            public void onAdSkiped(TPAdInfo tpAdInfo) {
                // 可跳过的插播广告，用户点击跳过
            }

            @Override
            public void onAdTapped(TPAdInfo tpAdInfo) {
                // 视频广告中，非点击区域被点击
            }

            @Override
            public void onAdProgress(TPAdInfo tpAdInfo, float v, double v1) {

            }
        });
    }

    int showCount = 0;
    int loadFirstTime = 0;
    int showFirstTime = 0;
    RelativeLayout adContainer;
    RelativeLayout adContainerSecond;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
                if (tpMediaVideo != null) {
                    // 加载广告
                    AudioManager audioManager = (AudioManager) RepeatedActivity.this.getSystemService(Context.AUDIO_SERVICE);
                    VideoView videoVidew = new VideoView(RepeatedActivity.this);
                    VideoAdPlayerAdapter videoAdPlayerAdpter = new VideoAdPlayerAdapter(videoVidew, audioManager, true);

                    if (loadFirstTime == 0) {
                        adContainer = new RelativeLayout(RepeatedActivity.this);
                        adContainer.addView(videoVidew);
                        tpMediaVideo.loadAd(adContainer, videoAdPlayerAdpter);
                        loadFirstTime = 1;
                    }else {
                        adContainerSecond = new RelativeLayout(RepeatedActivity.this);
                        adContainerSecond.addView(videoVidew);
                        tpMediaVideo.loadAd(adContainerSecond, videoAdPlayerAdpter);
                        loadFirstTime = 0;
                        findViewById(R.id.btn_load).setEnabled(false);
                    }
                }
                break;
            case R.id.btn_show:
                int size = arrayList.size();
                if (size <= 0) {
                    return;
                }

                if (showFirstTime == 0) {
                    if (adContainer.getParent() != null) {
                        ((ViewGroup) adContainer.getParent()).removeView(adContainer);
                    }
                    mContainer.addView(adContainer);
                    showFirstTime = 1;
                }else {
                    if (adContainerSecond.getParent() != null) {
                        ((ViewGroup) adContainerSecond.getParent()).removeView(adContainerSecond);
                    }
                    mContainer.addView(adContainerSecond);
                    showFirstTime = 0;
                    findViewById(R.id.btn_load).setEnabled(true);
                    findViewById(R.id.btn_show).setEnabled(false);
                }

                if (showCount < size) {
                    TPCustomMediaVideoAd tpCustomMediaVideoAd = arrayList.get(showCount);
                    showCount ++;
                    if (tpCustomMediaVideoAd != null) {
                        tpCustomMediaVideoAd.start("广告场景ID");
                    }
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

        if (tpMediaVideo != null) {
            tpMediaVideo.onDestroy();
        }
    }
}