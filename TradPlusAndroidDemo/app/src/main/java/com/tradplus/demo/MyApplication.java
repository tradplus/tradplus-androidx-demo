package com.tradplus.demo;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.tradplus.ads.base.common.TPPrivacyManager;
import com.tradplus.ads.mobileads.gdpr.Const;
import com.tradplus.ads.mobileads.util.TestDeviceUtil;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.utils.TestAdUnitId;


public class MyApplication extends MultiDexApplication {

    public static Application application;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        // 设置测试模式
        // 正式上线前注释
        TestDeviceUtil.getInstance().setNeedTestDevice(true);

        // 判断用户是否已经选择过，返回true表示已经进行过选择，就不需要再次弹窗
        boolean firstShowGDPR = TradPlusSdk.isFirstShowGDPR(this);

        // 查询地区
        TradPlusSdk.checkCurrentArea(this, new TPPrivacyManager.OnPrivacyRegionListener() {
            @Override
            public void onSuccess(boolean isEu, boolean isCn, boolean isCalifornia) {
                // 获取到相关地域配置后，设置相关隐私API

                // 表明是欧洲地区，设置GDPR
                if (isEu) {
                    if (!firstShowGDPR) {
                        TradPlusSdk.showUploadDataNotifyDialog(application, new TradPlusSdk.TPGDPRAuthListener() {
                            @Override
                            public void onAuthResult(int level) {
                                // 获取设置结果并做记录，true 表明用户 进行过选择
                                TradPlusSdk.setIsFirstShowGDPR(application, true);
                            }
                        }, Const.URL.GDPR_URL); // Const.URL.GDPR_URL 为TradPlus 定义的授权页面
                    }
                }

                // 表明是美国加州地区，设置CCPA
                if (isCalifornia) {
                    // false 加州用户均不上报数据 ；true 接受上报数据
                    // 默认不上报，如果上报数据，需要让用户选择
                    TradPlusSdk.setCCPADoNotSell(application, false);
                }

                // 初始化SDK
                TradPlusSdk.initSdk(application, TestAdUnitId.APPID);
            }

            @Override
            public void onFailed() {
                // 一般为网络问题导致查询失败，开发者需要自己判断地区，然后进行隐私设置
                // 然后在初始化SDK
            }
        });


    }
}
