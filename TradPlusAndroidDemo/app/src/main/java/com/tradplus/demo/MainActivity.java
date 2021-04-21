package com.tradplus.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.mobileads.gdpr.Const;
import com.tradplus.ads.mobileads.util.TestDeviceUtil;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.demo.banners.BannerActivity;
import com.tradplus.demo.interstititals.InterstitialActivity;
import com.tradplus.demo.nativeads.DrawNativeExpressVideoActivity;
import com.tradplus.demo.nativeads.NativeActivity;
import com.tradplus.demo.nativeads.NativeBannerViewActivity;
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
        gpdrChild = (CheckBox) findViewById(R.id.is_gdpr_child);

        custom_gdpr_sdk_init = (Button) findViewById(R.id.btn_custom_gdpr_sdk_init);
        custom_gdpr_sdk_init.setOnClickListener(this);

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
                TradPlusSdk.setGDPRChild(MainActivity.this,isChecked);
            }
        });

        CheckBox isChild = (CheckBox) findViewById(R.id.is_child);
        Log.i("privacy", "onCreate coppa: "+TradPlusSdk.isCOPPAChild(MainActivity.this)+":ccpa:"+(TradPlusSdk.getCCPADataCollection(this) == TradPlusSdk.PRIVACY_ACCEPT_KEY));
        isChild.setChecked(TradPlusSdk.isCOPPAChild(MainActivity.this) == TradPlusSdk.PRIVACY_ACCEPT_KEY);
        isChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("isChild", "onCheckedChanged: " + b);
                TradPlusSdk.setCOPPAChild(MainActivity.this, b);
            }
        });

        cbCCPA = (CheckBox) findViewById(R.id.do_not_sell);
        cbCCPA.setChecked(TradPlusSdk.getCCPADataCollection(MainActivity.this) == TradPlusSdk.PRIVACY_ACCEPT_KEY);
        cbCCPA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("cbCCPA", "onCheckedChanged: " + b);
                TradPlusSdk.setCCPADataCollection(MainActivity.this, b);
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
                if (!TradPlusSdk.getIsInit()) {
                    initSdk();
                }
                break;
            case R.id.btn_yeu:
                TradPlusSdk.setEUTraffic(MainActivity.this,true);
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
                TradPlusSdk.setEUTraffic(MainActivity.this,false);
                if (unknownCountry != null) {
                    unknownCountry.setVisibility(View.GONE);
                }
                if (gdpr_container!=null && gdpr_container.getVisibility()== View.VISIBLE) {
                    gdpr_container.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_gdpr_neu:
                TradPlusSdk.setGDPRUploadDataLevel(MainActivity.this,TradPlusSdk.NONPERSONALIZED);
                if (gdpr_container != null) {
                    gdpr_container.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_gdpr_yeu:
                TradPlusSdk.setGDPRUploadDataLevel(MainActivity.this,TradPlusSdk.PERSONALIZED);
                if (gdpr_container != null) {
                    gdpr_container.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_custom_gdpr_sdk_init:
                if (!TradPlusSdk.getIsInit()) {
                    initSdk1();
                }
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
        }
    }

    private void hasShowGDPR() {
        if (!TradPlusSdk.isFirstShowGDPR(MainActivity.this)) {
            showGDPR();
        } else {
            Toast.makeText(MainActivity.this, "您已经选择过，想修改请点击修改按钮！", Toast.LENGTH_LONG).show();
        }
    }

    private void showGDPR() {
        TradPlusSdk.showUploadDataNotifyDialog(this, new TradPlusSdk.TPGDPRAuthListener() {
            @Override
            public void onAuthResult(int level) {
                Log.i("level", "onAuthResult: "+level);
                TradPlusSdk.setIsFirstShowGDPR(MainActivity.this,true);
            }
        }, Const.URL.GDPR_URL);
    }

    private void initSdk() {
        isAccept = true;
        TradPlusSdk.setGDPRListener(new TradPlusSdk.TPGDPRListener() {
            @Override
            public void success(String msg) {

                if (TradPlusSdk.isEUTraffic(MainActivity.this)) {
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
                Log.i("TradPlusSdk", "failed: "+errorMsg);
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
        TradPlusSdk.initSdk(this, TestAdUnitId.APPID);
//        TradPlusSdk.setIsCNLanguageLog(true);//Log中文模式
        //设置测试模式，正式上线前注释
        TestDeviceUtil.getInstance().setNeedTestDevice(true);

    }

    private void initSdk1() {
        isAccept = false;
        TradPlusSdk.setGDPRListener(new TradPlusSdk.TPGDPRListener() {
            @Override
            public void success(String msg) {

                if (TradPlusSdk.isEUTraffic(MainActivity.this)) {
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
                Log.i("TradPlusSdk", "failed: "+errorMsg);
                //未知是否为欧盟
                if (gdprCutomStatus != null) {
                    gdprCutomStatus.setText("未知国家");
                }
                if (unknownCountry != null) {
                    unknownCountry.setVisibility(View.VISIBLE);
                }
            }
        });

        TradPlusSdk.setPrivacyListener(new TradPlusSdk.TPPrivacyListener() {
            @Override
            public void success(String msg) {
                Log.i("ccpa", "success: ");
                if (TradPlusSdk.isCalifornia(MainActivity.this)) {
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
        TradPlusSdk.initSdk(this, TestAdUnitId.APPID);
//        TradPlusSdk.setIsCNLanguageLog(true);//Log中文模式

        //设置测试模式，正式上线前注释
        TestDeviceUtil.getInstance().setNeedTestDevice(true);
    }
}
