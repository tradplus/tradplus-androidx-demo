package com.tradplus.demo.banners;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.banner.TPBanner;


import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

public class BannerActivity extends AppCompatActivity {

    private TPBanner tpBanner;
    private ViewGroup adContainer;
    private static final String TAG = "tradpluslog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        adContainer = findViewById(R.id.ad_container);
        tpBanner = new TPBanner(this);
        tpBanner.setAdListener(new BannerAdListener() {
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: " + tpAdInfo.adSourceName + "被点击了");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: " + tpAdInfo.adSourceName  + "展示了");
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                Log.i(TAG, "onAdLoadFailed: 加载失败，code :" + error.getErrorCode() + ", msg : " + error.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+tpAdInfo.adSourceName + "广告关闭");
            }
        });
        adContainer.addView(tpBanner);
        tpBanner.loadAd(TestAdUnitId.BANNER_ADUNITID);
    }


}
