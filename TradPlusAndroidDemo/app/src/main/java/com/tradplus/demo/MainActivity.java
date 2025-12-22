package com.tradplus.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.sigmob.windad.WindCustomController;
import com.tradplus.ads.base.common.TPPrivacyManager;
import com.tradplus.ads.base.network.TPSettingManager;
import com.tradplus.ads.kwad_ads.KuaishouInitManager;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.ads.sigmob.SigmobInitManager;
import com.tradplus.ads.taptap.TapTapInitManager;
import com.tradplus.ads.toutiao.ToutiaoInitManager;
import com.tradplus.demo.banners.BannerActivity;
import com.tradplus.demo.interstititals.InterstitialActivity;
import com.tradplus.demo.mediavideo.NewMediaVideo;
import com.tradplus.demo.mediavideo.vmap.VWAPActivity;
import com.tradplus.demo.nativeads.DrawNativeExpressVideoActivity;
import com.tradplus.demo.nativeads.HuaweiDownLoad;
import com.tradplus.demo.nativeads.NativeActivity;
import com.tradplus.demo.nativeads.NativeBannerViewActivity;
import com.tradplus.demo.offerwall.OfferWallActivity;
import com.tradplus.demo.rewarded.RewardedVideoActivity;
import com.tradplus.privacy.CSJCustomUserData;
import com.tradplus.privacy.KSUserDataObtainController;
import com.tradplus.privacy.SigmobCustomController;
import com.tradplus.privacy.TapTapUserDataCustomController;
import com.tradplus.utils.TestAdUnitId;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CheckBox btn_box;
    private Button native_ad_cache, splash_ads, draw_video_view, native_advanced_btn, rewarded_video_btn,
            offerwall_ad, interstitial_ad, banner_btn, nativebanner_ad, interactive_ad, meidavideo_ads;
    private CheckBox gpdrChild;
    private CheckBox cbCCPA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 海外隐私政策
        setPrivacyConsent();
        initView();
    }

    private void setPrivacyConsent() {
        // Google UMP
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                // 指示用户是否低于同意年龄; true 低于同意年龄
                // 未满同意年龄的用户不会收到 GDPR 消息表单
                .setTagForUnderAgeOfConsent(false)
                .build();

        ConsentInformation consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this, params, (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(this,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.

                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    Log.i("TradPlusLog", "授权完成,初始化SDK: ");
                                    // 授权完成,初始化SDK
                                    initTPSDK();
                                }
                            });
                }, (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.


                });


        // 用户已经进行过UMP选择
        if (consentInformation.canRequestAds()) {
            // 授权完成,初始化SDK
            initTPSDK();
        }

        // 集成Google UMP后; 如果美国加州没有投放APP，无需调用
//        checkAreaSetCCPA();

    }

    private void checkAreaSetCCPA() {
        // 判断用户是否已经选择过，返回true表示已经进行过选择，就不需要再次进行GDPR弹窗
        boolean firstShowGDPR = TradPlusSdk.isFirstShowGDPR(this);
        // 查询地区
        TradPlusSdk.checkCurrentArea(this, new TPPrivacyManager.OnPrivacyRegionListener() {
            @Override
            public void onSuccess(boolean isEu, boolean isCn, boolean isCalifornia,boolean isBr) {
                // 获取到相关地域配置后，设置相关隐私API

                // 集成Google UMP后无需处理欧洲地区
                // 表明是欧洲地区，设置GDPR弹窗
//                if (isEu) {
//                    if (!firstShowGDPR) {
//                        TradPlusSdk.showUploadDataNotifyDialog(application, new TradPlusSdk.TPGDPRAuthListener() {
//                            @Override
//                            public void onAuthResult(int level) {
//                                // 获取设置结果并做记录，true 表明用户 进行过选择
//                                TradPlusSdk.setIsFirstShowGDPR(application, true);
//                            }
//                        }, Const.URL.GDPR_URL); // Const.URL.GDPR_URL 为TradPlus 定义的授权页面
//                    }
//                }

                // 表明是美国加州地区，设置CCPA
                if (isCalifornia) {
                    // false 加州用户均不上报数据 ；true 接受上报数据
                    // 默认不上报，如果上报数据，需要让用户选择
                    TradPlusSdk.setCCPADoNotSell(MainActivity.this, false);
                }


                if (!isEu) {
                    initTPSDK();
                }

                if (isBr) {
                    // 巴西地区 设置LGPD
                    // 0 设备数据允许上报 ；1 设备数据不允许上报
                    TradPlusSdk.setLGPDConsent(MainActivity.this, 0);
                }

            }

            @Override
            public void onFailed() {
                // 一般为网络问题导致查询失败，开发者需要自己判断地区，然后进行隐私设置
                // 然后在初始化SDK
                initTPSDK();
            }
        });
    }


    private void initTPSDK() {
        if (!TradPlusSdk.getIsInit()) {
            // 初始化是否成功 （可选）
            TradPlusSdk.setTradPlusInitListener(new TradPlusSdk.TradPlusInitListener() {
                @Override
                public void onInitSuccess() {
                    Log.i("TradPlusLog", "onInitSuccess: ");
                }
            });
            // 初始化SDK
            TradPlusSdk.initSdk(this, TestAdUnitId.APPID);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.is_personad:
                //个性化推荐广告开关；默认是开启状态 true
                TradPlusSdk.setOpenPersonalizedAd(isChecked);
                break;
            case R.id.is_privacyUser:
                // 隐私信息控制开关；默认是开启状态 true
                TradPlusSdk.setPrivacyUserAgree(isChecked);
                // 自定义设置隐私信息控制开关
                // CSJ
                ToutiaoInitManager.getInstance().setTTCustomController(new CSJCustomUserData());
                // TapTap
                TapTapInitManager.getInstance().setTTCustomController(new TapTapUserDataCustomController());
                // kuaishou
                KuaishouInitManager.getInstance().setKsCustomController(new KSUserDataObtainController());
                // sigmob
                SigmobInitManager.getInstance().setTTCustomController(new SigmobCustomController());
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.splash_ads:
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                break;
            case R.id.draw_video_view:
                startActivity(new Intent(MainActivity.this, DrawNativeExpressVideoActivity.class));
                break;
            case R.id.native_ad_advanced:
                startActivity(new Intent(MainActivity.this, NativeActivity.class));
                break;
            case R.id.rewarded_video_ad:
                Intent intent = new Intent(MainActivity.this, RewardedVideoActivity.class);
                startActivity(intent);
                break;
            case R.id.interstitial_ad:
                Intent interstitialIntent = new Intent(MainActivity.this, InterstitialActivity.class);
                startActivity(interstitialIntent);
                break;
            case R.id.banner_ad:
                startActivity(new Intent(MainActivity.this, BannerActivity.class));
                break;
            case R.id.nativebanner_ad:
                startActivity(new Intent(MainActivity.this, NativeBannerViewActivity.class));
                break;
            case R.id.offerwall_ad:
                startActivity(new Intent(MainActivity.this, OfferWallActivity.class));
                break;
            case R.id.interactive_ads:
                startActivity(new Intent(MainActivity.this, InterActiveActivity.class));
                break;
            case R.id.meidavideo_ads:
                startActivity(new Intent(MainActivity.this, NewMediaVideo.class));
                break;
            case R.id.meidavideo_vmap_ads:
                startActivity(new Intent(MainActivity.this, VWAPActivity.class));
                break;
        }
    }

    private void initView() {
        // 开屏
        splash_ads = findViewById(R.id.splash_ads);
        splash_ads.setOnClickListener(this);

        // Draw信息流
        draw_video_view = (Button) findViewById(R.id.draw_video_view);
        draw_video_view.setOnClickListener(this);

        // 标准原生
        native_advanced_btn = (Button) findViewById(R.id.native_ad_advanced);
        native_advanced_btn.setOnClickListener(this);

        // 激励
        rewarded_video_btn = (Button) findViewById(R.id.rewarded_video_ad);
        rewarded_video_btn.setOnClickListener(this);

        // 积分墙
        offerwall_ad = (Button) findViewById(R.id.offerwall_ad);
        offerwall_ad.setOnClickListener(this);

        // 插屏
        interstitial_ad = (Button) findViewById(R.id.interstitial_ad);
        interstitial_ad.setOnClickListener(this);

        // 横幅
        banner_btn = (Button) findViewById(R.id.banner_ad);
        banner_btn.setOnClickListener(this);

        // 原生横幅
        nativebanner_ad = (Button) findViewById(R.id.nativebanner_ad);
        nativebanner_ad.setOnClickListener(this);

        // 互动
        interactive_ad = (Button) findViewById(R.id.interactive_ads);
        interactive_ad.setOnClickListener(this);

        // 插播广告
        meidavideo_ads = (Button) findViewById(R.id.meidavideo_ads);
        meidavideo_ads.setOnClickListener(this);
        findViewById(R.id.meidavideo_vmap_ads).setOnClickListener(this);

        ((CheckBox) findViewById(R.id.is_personad)).setOnCheckedChangeListener(this);
        ((CheckBox) findViewById(R.id.is_privacyUser)).setOnCheckedChangeListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
