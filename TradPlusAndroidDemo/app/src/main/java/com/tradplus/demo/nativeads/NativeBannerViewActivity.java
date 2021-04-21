package com.tradplus.demo.nativeads;


import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.nativead.TPNativeBanner;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

public class NativeBannerViewActivity extends AppCompatActivity {


    private TPNativeBanner tpNativeBanner;
    private ViewGroup adContainer;
    private static final String TAG = "tradpluslog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nativebanner);

        adContainer = findViewById(R.id.ad_container);
        tpNativeBanner = new TPNativeBanner(NativeBannerViewActivity.this);

        tpNativeBanner.setAdListener(new BannerAdListener(){
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: " + tpAdInfo.adSourceName + "被点击了");
            }

            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");
            }

            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: "+ tpAdInfo.adSourceName + "展示");
            }

            public void onAdLoadFailed(TPAdError tpAdInfo) {
                Log.i(TAG, "onAdLoadFailed:加载失败: code : "+ tpAdInfo.getErrorCode() + ", msg :" + tpAdInfo.getErrorMsg());
            }

            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+ tpAdInfo.adSourceName + "被关闭");
            }

        });
        adContainer.addView(tpNativeBanner);
        tpNativeBanner.loadAd(TestAdUnitId.NATIVEBANNER_ADUNITID);

    }

}
