package com.tradplus.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.mobileads.TradPlusView;
import com.tradplus.ads.open.splash.SplashAdListener;
import com.tradplus.ads.open.splash.TPSplash;
import com.tradplus.utils.TestAdUnitId;


public class SplashActivity extends AppCompatActivity {

    private TradPlusView tradPlusView;
    private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TPSplash tpSplash = new TPSplash(SplashActivity.this, TestAdUnitId.SPLASH_ADUNITID);
        tpSplash.setAdListener(new SplashAdListener() {
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: ");
                SplashActivity.this.finish();
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdImpression: ");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: ");
                SplashActivity.this.finish();
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.i(TAG, "onAdLoaded: ");
                //展示广告
                tpSplash.showAd();
            }
        });

        tpSplash.loadAd(findViewById(R.id.splash_container));


    }


    //开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK==keyCode|| KeyEvent.KEYCODE_HOME==keyCode){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
