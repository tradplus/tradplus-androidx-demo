package com.tradplus.demo.mediavideo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.mediavideo.MediaVideoAdListener;
import com.tradplus.ads.open.mediavideo.TPMediaVideo;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

public class SecondPageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TradPlusData";
    private ViewGroup mContainer;
    private TPMediaVideo tpMediaVideo;
    private MediaVideoUtils mediaVideoUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mv_secondpage);

        initLayout();
        initTPMediaView();
    }

    private int isAdDisPlayer = 0;
    private void initTPMediaView() {
        tpMediaVideo = new TPMediaVideo(this, TestAdUnitId.MEDIAVIDEO_ADUNITID);
        mediaVideoUtils = MediaVideoUtils.getInstance();
        // 设置监听
        tpMediaVideo.setAdListener(new MediaVideoAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: ");
                Toast.makeText(SecondPageActivity.this,"广告加载完成",Toast.LENGTH_SHORT).show();
                // 一次loadAd后，广告加载成功
                // 从缓存中取出加载成功的广告

            }

            @Override
            public void onAdFailed(TPAdError error) {
                // 一次loadAd后，广告加载失败
                Log.i(TAG, "onAdFailed: " + error.getErrorMsg());
                Toast.makeText(SecondPageActivity.this,"广告加载失败",Toast.LENGTH_SHORT).show();
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
                Log.i(TAG, "onAdVideoStart:");
                isAdDisPlayer = 1;
                mContainer.setVisibility(View.VISIBLE);
                Toast.makeText(SecondPageActivity.this,"广告展示",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                // 视频播放结束
                isAdDisPlayer = 0;
                Log.i(TAG, "onAdVideoEnd: ");
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
            }

            @Override
            public void onAdTapped(TPAdInfo tpAdInfo) {
                // 视频广告中，非点击区域被点击
                Log.i(TAG, "onAdTapped: ");
            }

            @Override
            public void onAdProgress(TPAdInfo tpAdInfo, float v, double v1) {
                // 播放进度
            }
        });

    }

    private void initLayout() {
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        mContainer = findViewById(R.id.video_container);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
                if (tpMediaVideo != null) {
                    // 加载广告
                    mediaVideoUtils.loadTpMeidaVide(tpMediaVideo,this);
                }
                break;
            case R.id.btn_show:
                boolean meidaVideReady = mediaVideoUtils.isMeidaVideReady(tpMediaVideo);
                if (!meidaVideReady) {
                    Toast.makeText(SecondPageActivity.this,"无可用广告",Toast.LENGTH_SHORT).show();
                }else {
                    showMediaVideAd();
                }
                break;
            case R.id.btn_stop:
                // 播放暂停
                if (mediaVideoUtils != null) {
                    mediaVideoUtils.onAdPause();
                }
                break;
            case R.id.btn_resume:
                // 暂停后继续播放
                if (mediaVideoUtils != null) {
                    mediaVideoUtils.onAdResume();
                }
                break;
        }
    }

    private void showMediaVideAd() {
        if (isAdDisPlayer == 0) {
            RelativeLayout adContainer = mediaVideoUtils.getAdContainer();
            if (adContainer.getParent() != null) {
                ((ViewGroup) adContainer.getParent()).removeView(adContainer);
            }
            mContainer.addView(adContainer);
            mContainer.setVisibility(View.INVISIBLE);
            mediaVideoUtils.showTpMeidaVide(tpMediaVideo);
        }else {
            Toast.makeText(SecondPageActivity.this,"广告展示中...",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaVideoUtils != null) {
            mediaVideoUtils.onAdPause();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaVideoUtils != null) {
            mediaVideoUtils.onAdResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        if (mediaVideoUtils != null) {
            mediaVideoUtils.onAdDestroy();
        }

        if (tpMediaVideo != null) {
            tpMediaVideo.onDestroy();
        }
    }
}