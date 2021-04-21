package com.tradplus.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tradplus.ads.mobileads.TradPlus;
import com.tradplus.ads.mobileads.gdpr.ATGDPRAuthCallback;
import com.tradplus.ads.mobileads.gdpr.Const;
import com.tradplus.ads.mobileads.util.TestDeviceUtil;
import com.tradplus.demo.banners.BannerActivity;
import com.tradplus.demo.interstititals.InterstitialActivity;
import com.tradplus.demo.nativeads.DrawNativeExpressVideoActivity;
import com.tradplus.demo.nativeads.NativeActivity;
import com.tradplus.demo.nativeads.NativeBannerViewActivity;
import com.tradplus.demo.nativeads.NativeCacheActivity;
import com.tradplus.demo.nativeads.NativeSlotActivity;
import com.tradplus.demo.offerwall.OfferWallActivity;
import com.tradplus.demo.rewarded.RewardedVideoActivity;
import com.tradplus.utils.TestAdUnitId;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button gdpr_view;
    private Button initSDK;
    private TextView gdprStatus,gdprCutomStatus;
    private RelativeLayout unknownCountry;
    private Button yeu,neu;
    private boolean isAccept = false;
    private TextView tx_tip;
    private RelativeLayout gdpr_container;
    private Button gdpr_neu,gdpr_yeu;
    private Button custom_gdpr_sdk_init;
    private CheckBox btn_box;
    private Button native_ad_cache,splash_ads,draw_video_view,nativelist_ad,native_advanced_btn,rewarded_video_btn,offerwall_ad,interstitial_ad,banner_btn,nativebanner_ad;
    private CheckBox gpdrChild;
    private CheckBox cbCCPA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSDK = (Button) findViewById(R.id.btn_gdpr_sdk_init);
        initSDK.setOnClickListener(this);
        gdpr_view = (Button) findViewById(R.id.gdpr_view);
        gdpr_view.setOnClickListener(this);
        gdprStatus = (TextView) findViewById(R.id.gdpr_status);
        gdprCutomStatus = (TextView) findViewById(R.id.gdpr_custom_status);
        unknownCountry = (RelativeLayout) findViewById(R.id.unknown_country);
        tx_tip = (TextView) findViewById(R.id.tip);
        yeu = (Button) findViewById(R.id.btn_yeu);
        yeu.setOnClickListener(this);
        neu = (Button) findViewById(R.id.btn_neu);
        neu.setOnClickListener(this);
        gdpr_container = (RelativeLayout) findViewById(R.id.gdpr_container);
        gdpr_neu = (Button) findViewById(R.id.btn_gdpr_neu);
        gdpr_neu.setOnClickListener(this);
        gdpr_yeu = (Button) findViewById(R.id.btn_gdpr_yeu);
        gdpr_yeu.setOnClickListener(this);
        btn_box = (CheckBox) findViewById(R.id.btn_box);
        gpdrChild = (CheckBox) findViewById(R.id.is_gdpr_child);

        custom_gdpr_sdk_init = (Button) findViewById(R.id.btn_custom_gdpr_sdk_init);
        custom_gdpr_sdk_init.setOnClickListener(this);

        native_ad_cache = findViewById(R.id.native_ad_cache);
        native_ad_cache.setOnClickListener(this);
        splash_ads = findViewById(R.id.splash_ads);
        splash_ads.setOnClickListener(this);
        draw_video_view = (Button)findViewById(R.id.draw_video_view);
        draw_video_view.setOnClickListener(this);
        nativelist_ad = (Button)findViewById(R.id.nativelist_ad);
        nativelist_ad.setOnClickListener(this);
        native_advanced_btn = (Button)findViewById(R.id.native_ad_advanced);
        native_advanced_btn.setOnClickListener(this);
        rewarded_video_btn = (Button)findViewById(R.id.rewarded_video_ad);
        rewarded_video_btn.setOnClickListener(this);
        offerwall_ad = (Button)findViewById(R.id.offerwall_ad);
        offerwall_ad.setOnClickListener(this);
        interstitial_ad = (Button)findViewById(R.id.interstitial_ad);
        interstitial_ad.setOnClickListener(this);
        banner_btn = (Button)findViewById(R.id.banner_ad);
        banner_btn.setOnClickListener(this);
        nativebanner_ad = (Button)findViewById(R.id.nativebanner_ad);
        nativebanner_ad.setOnClickListener(this);

        //欧盟GDPR等协议，发海外需要添加
        gpdrChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TradPlus.setGDPRChild(MainActivity.this,isChecked);
            }
        });

        CheckBox isChild = (CheckBox) findViewById(R.id.is_child);
        Log.i("privacy", "onCreate coppa: "+TradPlus.IsCOPPAChild(MainActivity.this)+":ccpa:"+(TradPlus.getCCPADataCollection(this) == TradPlus.PRIVACY_ACCEPT_KEY));
        isChild.setChecked(TradPlus.IsCOPPAChild(MainActivity.this) == TradPlus.PRIVACY_ACCEPT_KEY);
        isChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("isChild", "onCheckedChanged: " + b);
                TradPlus.setCOPPAChild(MainActivity.this, b);
            }
        });

        cbCCPA = (CheckBox) findViewById(R.id.do_not_sell);
        cbCCPA.setChecked(TradPlus.getCCPADataCollection(MainActivity.this) == TradPlus.PRIVACY_ACCEPT_KEY);
        cbCCPA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("cbCCPA", "onCheckedChanged: " + b);
                TradPlus.setCCPADataCollection(MainActivity.this, b);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gdpr_view:
                showGDPR();
                break;
            case R.id.btn_gdpr_sdk_init:
                if (!TradPlus.isInit) {
                    initSdk();
                }
                break;
            case R.id.btn_yeu:
                TradPlus.setEUTraffic(MainActivity.this,true);
                if(isAccept){
                    hasShowGDPR();
                }else {
                    if (gdpr_container != null) {
                        gdpr_container.setVisibility(View.VISIBLE);
                    }
                    if (unknownCountry != null) {
                        unknownCountry.setVisibility(View.GONE);

                    }
                }
                break;
            case R.id.btn_neu:
                TradPlus.setEUTraffic(MainActivity.this,false);
                if (unknownCountry != null) {
                    unknownCountry.setVisibility(View.GONE);
                }
                if (gdpr_container!=null && gdpr_container.getVisibility()== View.VISIBLE) {
                    gdpr_container.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_gdpr_neu:
                TradPlus.setGDPRUploadDataLevel(MainActivity.this,TradPlus.NONPERSONALIZED);
                if (gdpr_container != null) {
                    gdpr_container.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_gdpr_yeu:
                TradPlus.setGDPRUploadDataLevel(MainActivity.this,TradPlus.PERSONALIZED);
                if (gdpr_container != null) {
                    gdpr_container.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_custom_gdpr_sdk_init:
                if (!TradPlus.isInit) {
                    initSdk1();
                }
                break;
            case R.id.native_ad_cache:
                startActivity(new Intent(MainActivity.this, NativeCacheActivity.class));
                break;
            case R.id.splash_ads:
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                break;
            case R.id.draw_video_view:
                startActivity(new Intent(MainActivity.this, DrawNativeExpressVideoActivity.class));
                break;
            case R.id.nativelist_ad:
                startActivity(new Intent(MainActivity.this, NativeSlotActivity.class));
                break;
            case R.id.native_ad_advanced:
                startActivity(new Intent(MainActivity.this, NativeActivity.class));
                break;
            case R.id.rewarded_video_ad:
                Intent intent = new Intent(MainActivity.this, RewardedVideoActivity.class);
                intent.putExtra("autoload",btn_box.isChecked());
                startActivity(intent);
                break;
            case R.id.offerwall_ad:
                startActivity(new Intent(MainActivity.this, OfferWallActivity.class));
                break;
            case R.id.interstitial_ad:
                Intent interstitialIntent = new Intent(MainActivity.this, InterstitialActivity.class);
                interstitialIntent.putExtra("autoload",btn_box.isChecked());
                startActivity(interstitialIntent);
                break;
            case R.id.banner_ad:
                startActivity(new Intent(MainActivity.this, BannerActivity.class));
                break;
            case R.id.nativebanner_ad:
                startActivity(new Intent(MainActivity.this, NativeBannerViewActivity.class));
                break;
        }
    }

    private void hasShowGDPR() {
        if (!TradPlus.isFirstShowGDPR(MainActivity.this)) {
            showGDPR();
        } else {
            Toast.makeText(MainActivity.this, "您已经选择过，想修改请点击修改按钮！", Toast.LENGTH_LONG).show();
        }
    }

    private void showGDPR() {
        TradPlus.showUploadDataNotifyDialog(this, new ATGDPRAuthCallback() {
            @Override
            public void onAuthResult(int level) {
                Log.i("level", "onAuthResult: "+level);
                TradPlus.setIsFirstShowGDPR(MainActivity.this,true);
            }
        }, Const.URL.GDPR_URL);
    }

    private void initSdk() {
        isAccept = true;
        TradPlus.invoker().setmGDPRListener(new TradPlus.IGDPRListener() {
            @Override
            public void success(String msg) {

                if (TradPlus.isEUTraffic(MainActivity.this)) {
                    if (gdprStatus != null) {
                        gdprStatus.setText("欧盟用户！");
                    }
                    if (gdpr_view != null) {
                        gdpr_view.setVisibility(View.VISIBLE);
                    }
                    hasShowGDPR();

                }else {
                    if (gdprStatus != null) {
                        gdprStatus.setText("非欧盟用户！");
                    }
                }

            }

            @Override
            public void failed(String errorMsg) {
                Log.i("tradplus", "failed: "+errorMsg);
                //未知是否为欧盟
                if (gdprStatus != null) {
                    gdprStatus.setText("未知国家");
                }
                if (unknownCountry != null) {
                    unknownCountry.setVisibility(View.VISIBLE);
                }
            }
        });
        //初始化SDK
        TradPlus.invoker().initSDK(this, TestAdUnitId.APPID);
//        TradPlus.setIsCNLanguageLog(true);//Log中文模式
        //设置测试模式，正式上线前注释
        TestDeviceUtil.getInstance().setNeedTestDevice(true);

    }

    private void initSdk1() {
        isAccept = false;
        TradPlus.invoker().setmGDPRListener(new TradPlus.IGDPRListener() {
            @Override
            public void success(String msg) {

                if (TradPlus.isEUTraffic(MainActivity.this)) {
                    if (gdprCutomStatus != null) {
                        gdprCutomStatus.setText("欧盟用户！");
                    }
                    if (gdpr_container != null) {
                        gdpr_container.setVisibility(View.VISIBLE);
                    }
                }else {
                    if (gdprCutomStatus != null) {
                        gdprCutomStatus.setText("非欧盟用户！");
                    }
                }

            }

            @Override
            public void failed(String errorMsg) {
                Log.i("tradplus", "failed: "+errorMsg);
                //未知是否为欧盟
                if (gdprCutomStatus != null) {
                    gdprCutomStatus.setText("未知国家");
                }
                if (unknownCountry != null) {
                    unknownCountry.setVisibility(View.VISIBLE);
                }
            }
        });

        TradPlus.invoker().setPrivacyListener(new TradPlus.IPrivacyListener() {
            @Override
            public void success(String msg) {
                Log.i("ccpa", "success: ");
                if (TradPlus.isCalifornia(MainActivity.this)) {
                    cbCCPA.setVisibility(View.VISIBLE);
                }else {
                    if (cbCCPA.getVisibility()== View.VISIBLE) {
                        cbCCPA.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void failed(String errormsg) {
                Log.i("ccpa", "failed: ");
            }
        });
        //初始化SDK
        TradPlus.invoker().initSDK(this, TestAdUnitId.APPID);
//        TradPlus.setIsCNLanguageLog(true);//Log中文模式

        //设置测试模式，正式上线前注释
        TestDeviceUtil.getInstance().setNeedTestDevice(true);
    }
}
