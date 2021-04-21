package com.tradplus.demo.nativeads;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

public class NativeActivity extends AppCompatActivity {

    private TPNative tpNative;
    private ViewGroup adContainer;
    private static final String TAG = "tradpluslog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        adContainer = findViewById(R.id.ad_container);
        tpNative = new TPNative(NativeActivity.this,TestAdUnitId.NATIVE_ADUNITID);
        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");
                tpNative.getNativeAd().showAd(adContainer, R.layout.native_ad_list_item,"");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: "+ tpAdInfo.adSourceName + "被点击");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: "+ tpAdInfo.adSourceName + "展示");
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdShowFailed: "+ tpAdInfo.adSourceName + "展示失败");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdLoadFailed: 加载失败 , code : "+ tpAdError.getErrorCode() + ", msg :" + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+ tpAdInfo.adSourceName + "广告关闭");
            }
        });

        tpNative.loadAd();

    }


    @Override
    protected void onDestroy() {
        //释放资源
        tpNative.onDestroy();
        super.onDestroy();
    }

}
