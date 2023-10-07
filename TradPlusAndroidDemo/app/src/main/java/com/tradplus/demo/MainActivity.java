package com.tradplus.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.kwad_ads.KuaishouInitManager;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.ads.sigmob.SigmobInitManager;
import com.tradplus.ads.taptap.TapTapInitManager;
import com.tradplus.ads.toutiao.ToutiaoInitManager;
import com.tradplus.demo.banners.BannerActivity;
import com.tradplus.demo.interstititals.InterstitialActivity;
import com.tradplus.demo.mediavideo.FirstPageActivity;
import com.tradplus.demo.mediavideo.vmap.VWAPActivity;
import com.tradplus.demo.nativeads.DrawNativeExpressVideoActivity;
import com.tradplus.demo.nativeads.NativeActivity;
import com.tradplus.demo.nativeads.NativeBannerRecycleViewActivity;
import com.tradplus.demo.nativeads.NativeBannerViewActivity;
import com.tradplus.demo.nativeads.NativeRecycleViewActivity;
import com.tradplus.demo.offerwall.OfferWallActivity;
import com.tradplus.demo.rewarded.RewardedVideoActivity;
import com.tradplus.meditaiton.utils.ImportSDKUtil;
import com.tradplus.privacy.CSJCustomUserData;
import com.tradplus.privacy.KSUserDataObtainController;
import com.tradplus.privacy.SigmobCustomController;
import com.tradplus.privacy.TapTapUserDataCustomController;
import com.tradplus.utils.TestAdUnitId;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CheckBox btn_box;
    private Button native_ad_cache, splash_ads, draw_video_view, native_advanced_btn, rewarded_video_btn,
            offerwall_ad, interstitial_ad, banner_btn, nativebanner_ad, interactive_ad,meidavideo_ads;
    private CheckBox gpdrChild;
    private CheckBox cbCCPA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        // 测试工具
        Button tools = (Button) findViewById(R.id.tools);
        tools.setOnClickListener(this);

        ((CheckBox) findViewById(R.id.is_personad)).setOnCheckedChangeListener(this);
        ((CheckBox) findViewById(R.id.is_privacyUser)).setOnCheckedChangeListener(this);
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
                startActivity(new Intent(MainActivity.this, FirstPageActivity.class));
                break;
            case R.id.meidavideo_vmap_ads:
                startActivity(new Intent(MainActivity.this, VWAPActivity.class));
                break;
            case R.id.tools:
                ImportSDKUtil.getInstance().showTestTools(MainActivity.this, TestAdUnitId.APPID);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
