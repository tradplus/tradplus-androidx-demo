package com.tradplus.demo.mediavideo;

import android.content.Intent;
import android.os.Bundle;
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

public class FirstPageActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaVideoUtils mediaVideoUtils;
    private TPMediaVideo tpMediaVideo;

    @Override
    protected void onCreate(Bundle savedmediaVideoUtilsState) {
        super.onCreate(savedmediaVideoUtilsState);
        setContentView(R.layout.activity_mv_firstpage);

        initLayout();

        // 创建广告位对象
        tpMediaVideo = new TPMediaVideo(this, TestAdUnitId.MEDIAVIDEO_ADUNITID);
        // 设置监听
        tpMediaVideo.setAdListener(new MediaVideoAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Toast.makeText(FirstPageActivity.this,"广告加载完成",Toast.LENGTH_SHORT).show();
                // 一次loadAd后，广告加载成功
                // 从缓存中取出加载成功的广告

            }

            @Override
            public void onAdFailed(TPAdError error) {
                // 一次loadAd后，广告加载失败
                Toast.makeText(FirstPageActivity.this,"广告加载失败",Toast.LENGTH_SHORT).show();
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

    private void initLayout() {
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.second_page).setOnClickListener(this);
        findViewById(R.id.multiply_page).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
                // 请求广告
                mediaVideoUtils = MediaVideoUtils.getInstance();
                mediaVideoUtils.loadTpMeidaVide(tpMediaVideo,this);
                break;
            case R.id.second_page:
                // 进入下一页
                Intent intent = new Intent(FirstPageActivity.this, SecondPageActivity.class);
                startActivity(intent);
                break;
            case R.id.multiply_page:
                // 进入下一页
                Intent intentmultiply = new Intent(FirstPageActivity.this, MultiActivity.class);
                startActivity(intentmultiply);
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

        if (mediaVideoUtils != null) {
            mediaVideoUtils.onAdDestroy();
        }

        if (tpMediaVideo != null) {
            tpMediaVideo.onDestroy();
        }
    }
}