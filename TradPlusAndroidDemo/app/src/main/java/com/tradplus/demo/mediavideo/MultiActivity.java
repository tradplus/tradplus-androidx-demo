package com.tradplus.demo.mediavideo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.mediavideo.MediaVideoAdListener;
import com.tradplus.ads.open.mediavideo.TPMediaVideo;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

public class MultiActivity extends AppCompatActivity {

    private static final String TAG = "TradPlusData";
    private ViewGroup mContainer;
    private TPMediaVideo tpMediaVideo;
    private MediaVideoUtils mediaVideoUtils;
    private int isAdDisPlayer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mv_multi);

        initLayout();
        initTPMediaView();
    }

    private void initTPMediaView() {
        tpMediaVideo = new TPMediaVideo(this, TestAdUnitId.MEDIAVIDEO_ADUNITID);
        mediaVideoUtils = MediaVideoUtils.getInstance();
        // 设置监听
        tpMediaVideo.setAdListener(new MediaVideoAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: ");
                Toast.makeText(MultiActivity.this,"广告加载完成，可以等当前广告播放完成后继续展示",Toast.LENGTH_LONG).show();
                // 一次loadAd后，广告加载成功
                // 从缓存中取出加载成功的广告

            }

            @Override
            public void onAdFailed(TPAdError error) {
                // 一次loadAd后，广告加载失败
                Log.i(TAG, "onAdFailed: " + error.getErrorMsg());
                Toast.makeText(MultiActivity.this,"广告加载失败",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MultiActivity.this,"广告展示",Toast.LENGTH_SHORT).show();
                // 广告展示同时, 可以加载下一个广告
                mediaVideoUtils.loadTpMeidaVide(tpMediaVideo,MultiActivity.this);
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                // 视频播放结束
                isAdDisPlayer = 0;
                Log.i(TAG, "onAdVideoEnd: ");
                // 广告播放结束，若播放开始时加载的广告已经loaded，可以直接展示下一个
                boolean meidaVideReady = mediaVideoUtils.isMeidaVideReady(tpMediaVideo);
                if (meidaVideReady) {
                    showMediaVideAd();
                }
            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError error) {
                // 视频播放失败
                Log.i(TAG, "onAdVideoError: " + error.getErrorMsg());
                Toast.makeText(MultiActivity.this,"广告展示失败",Toast.LENGTH_SHORT).show();
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

        if (mediaVideoUtils.isMeidaVideReady(tpMediaVideo)) {
            showMediaVideAd();
        }
    }

    private void initLayout() {
        mContainer = findViewById(R.id.video_container);
    }

    private void showMediaVideAd() {
        if (isAdDisPlayer == 0) {
            ViewGroup adContainer = mediaVideoUtils.getAdContainer();
            if (adContainer.getParent() != null) {
                ((ViewGroup) adContainer.getParent()).removeView(adContainer);
            }
            mContainer.addView(adContainer);
            mContainer.setVisibility(View.INVISIBLE);
            mediaVideoUtils.showTpMeidaVide(tpMediaVideo);
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
