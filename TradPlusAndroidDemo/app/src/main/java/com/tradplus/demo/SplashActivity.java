package com.tradplus.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.tradplus.ads.base.GlobalTradPlus;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.splash.SplashAdListener;
import com.tradplus.ads.open.splash.TPSplash;
import com.tradplus.demo.mediavideo.MultiActivity;
import com.tradplus.utils.TestAdUnitId;


/**
 * 开屏广告
 * 开屏广告是打开app的时候展示一个3-5s的全屏的广告
 * 开屏广告分冷启动和热启动，冷启动时要尽可能提前开始加载广告，这样才能确保在进入app之前加载到并展示广告
 * 热启动是app切换到后台，并没有真正的退出，这种情况下要能检测到并提前加载广告
 * <p>
 * 开屏广告一般要配合app的启动页来使用，在加载的时间先给用户看启动页，等广告加载成功后展示广告，广告结束进入app内部
 */
public class SplashActivity extends AppCompatActivity {

    private TPSplash tpSplash;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViewById(R.id.splash_start_loadad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 加载广告
                loadSplashAd();
            }
        });


        findViewById(R.id.splash_start_showad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 广告加载成功后调用show方法 展示广告
                // 传入一个容器（容器一般要求全屏或者至少占屏幕75%以上，其余部分可以展示app的logo信息）
                if (tpSplash != null) {
                    tpSplash.showAd(findViewById(R.id.splash_container));

                }
            }
        });


        // 开屏广告一般要配合app的启动页来使用，在加载的时间先给用户看启动页，等广告加载成功后展示广告，广告结束进入app内部
        // 启动超时定时器
        startTimeoutTimer();

    }

    private void startTimeoutTimer() {
        // 这里要做一个超时判断，如果超过xx秒以后没有广告返回，那么需要自动跳转到app内部，不影响app的使用
    }


    //开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode || KeyEvent.KEYCODE_HOME == keyCode) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * --------------------------------------------------------------------------------------------------------------
     * 开屏的基本用法，如果没有特殊需求，按照如下代码接入即可
     * --------------------------------------------------------------------------------------------------------------
     */

    private void loadSplashAd() {
        // 初始化广告位,注意快手的sdk需要传入的activity是FragmentActivity，否则无法展示快手开屏
        tpSplash = new TPSplash(SplashActivity.this, TestAdUnitId.SPLASH_ADUNITID);
        // 设置监听
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
                // 广告关闭后，要把开屏页面关闭，如果是跟内容在同一个activity，这里把开屏的容器remove掉
                SplashActivity.this.finish();
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.i(TAG, "onAdLoaded: ");
                // 加载成功后展示广告
                //======================================================================================================
                // 这里一定要注意，需要判断一下是否已经进入app内部，如果加载时间过长，已经进入到app内部，这次load结果就不展示了
                Toast.makeText(SplashActivity.this, "广告加载成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdInfo) {
                Log.i(TAG, "onAdLoadFailed: ");
                // 广告加载失败
                //======================================================================================================
                // 这里一定要注意，需要判断一下是否已经进入app内部，如果加载时间过长，已经进入到app内部，这次load结果就不展示了
                Toast.makeText(SplashActivity.this, "广告加载失败", Toast.LENGTH_SHORT).show();
            }

        });

        // 开始加载开屏
        tpSplash.loadAd(null);
    }


    /**
     * ==============================================================================================================
     * 以下是高级用法，一般情况下用不到，包含了缓存，预加载，内置一份默认配置等功能
     * ==============================================================================================================
     */


    private void loadCustomSplashAd() {
        // 初始化广告位,注意快手的sdk需要传入的activity是FragmentActivity，否则无法展示快手开屏
        TPSplash tpSplash = new TPSplash(SplashActivity.this, TestAdUnitId.SPLASH_ADUNITID);
        // 设置监听
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
                // 广告关闭后，要把开屏页面关闭，如果是跟内容在同一个activity，这里把开屏的容器remove掉
                SplashActivity.this.finish();
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.i(TAG, "onAdLoaded: ");
                // 加载成功后展示广告
                //======================================================================================================
                // 这里一定要注意，需要判断一下是否已经进入app内部，如果加载时间过长，已经进入到app内部，这次load结果就不展示了
                //======================================================================================================

                // 如果是预加载的广告，在loaded成功后把tpSplash保存下来，展示前调用isReady
                if (tpSplash.isReady()) {
                    tpSplash.showAd(findViewById(R.id.splash_container));
                }
            }
        });

        // 开始加载开屏
        // 如果开屏需要预加载（提前为热启动做准备），那么在load的时候传入null，show的时候传入容器
        tpSplash.loadAd(null);

        // 如果要提升冷启动的加载速度，可以提前内置一套默认的tradplus配置，这样在首次安装并打开app后第一时间会根据这份配置请求三方广告平台
        // 这里的内容需要在接入后，TradPlus后台配置正式的广告信息，然后在一台设备上成功展示一次开屏广告，在AndroidStudio的log中过滤“TPSplash”的TAG，把内容复制出来，调用setDefaultConfig来设置
        // 这个接口只会在首次安装后第一次打开app生效，后续有正式配置下载下来，就不会再使用这份配置（清除app本地缓存后也会使用这次的配置）
        tpSplash.setDefaultConfig("xxxxxxxxxxxxxxxxxxxxxxxxx");

        // 开屏如果要预加载，那么每次进入要show的场景的时候，需要刷新context，不然快手出不来
        GlobalTradPlus.getInstance().refreshContext(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
