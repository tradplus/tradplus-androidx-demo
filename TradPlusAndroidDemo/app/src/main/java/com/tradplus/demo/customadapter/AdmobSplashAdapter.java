package com.tradplus.demo.customadapter;

import static com.tradplus.ads.base.common.TPError.ADAPTER_ACTIVITY_ERROR;
import static com.tradplus.ads.base.common.TPError.ADAPTER_CONFIGURATION_ERROR;
import static com.tradplus.ads.base.common.TPError.NETWORK_NO_FILL;
import static com.tradplus.ads.base.common.TPError.UNSPECIFIED;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.tradplus.ads.base.GlobalTradPlus;
import com.tradplus.ads.base.adapter.splash.TPSplashAdapter;
import com.tradplus.ads.base.common.TPError;

import java.util.Map;

/*
 * 开屏类型需要继承TPSplashAdapter,并重写以下几个方法
 * loadCustomAd() 用于获取服务器下发和本地配置的参数，实现自定义广告平台的加载逻辑
 * showAd()  实现展示自定义广告平台激励视频的逻辑
 * isReady() 用于展示广告前判断自定义广告是否过期
 * clean() 用于释放资源
 * getNetworkVersion() 自定义三方源的版本号
 * getNetworkName 自定义三方源的名称
 * */
public class AdmobSplashAdapter extends TPSplashAdapter {
    public static final String TAG = "AdmobSplashAdapter";
    private AppOpenAd mAppOpenAd;
    private String placementId;
    private AdRequest request;

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        // tpParams 获取从服务器端下发的字段
        if (tpParams.size() > 0 && tpParams.containsKey("placemntId")) {
            placementId = tpParams.get("placemntId");
        } else {
            /*
             * mLoadAdapterListener在oadCustomAd重写时同步生成
             * 回调方法loadAdapterLoaded ：广告加载成功
             * 回调方法loadAdapterLoadFailed ：广告加载失败
             *          构造TPError方法，ADAPTER_CONFIGURATION_ERROR 服务器下发参数错误
             *          方法setTpErrorCode 设置三方的ErrorCode错误码
             *          方法setErrorMessage 设置三方的ErrorMsg错误信息
             *
             * */
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoadFailed(new TPError(ADAPTER_CONFIGURATION_ERROR));
            }
            return;
        }

        request = new AdRequest.Builder().build();
        MobileAds.disableMediationAdapterInitialization(context);
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.i(TAG, "onInitializationComplete: ");
                AppOpenAd.load(context, placementId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, appOpenAdLoadCallback);
            }
        });
    }

    private final AppOpenAd.AppOpenAdLoadCallback appOpenAdLoadCallback = new AppOpenAd.AppOpenAdLoadCallback() {

        @Override
        public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
            Log.i(TAG, "onAdLoaded: ");
            mAppOpenAd = appOpenAd;
            if (mLoadAdapterListener != null) {
                //使用mLoadAdapterListener，实现广告事件加载成功回调
                mLoadAdapterListener.loadAdapterLoaded(null);
            }
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            Log.i(TAG, "onAdFailedToLoad message: " + loadAdError.getMessage() + ":code:" + loadAdError.getCode());
            TPError tpError = new TPError(NETWORK_NO_FILL);
            tpError.setErrorCode(String.valueOf(loadAdError.getCode()));
            tpError.setErrorMessage(loadAdError.getMessage());
            //使用mLoadAdapterListener，实现广告事件加载失败回调
            if (mLoadAdapterListener != null)
                mLoadAdapterListener.loadAdapterLoadFailed(tpError);
        }

    };

    @Override
    public void showAd() {
        Activity activity = GlobalTradPlus.getInstance().getActivity();
        if (activity == null) {
            if (mShowListener != null) {
                //使用mShowListener，实现广告事件展示失败回调
                mShowListener.onAdVideoError(new TPError(ADAPTER_ACTIVITY_ERROR));
            }
            return;
        }

        if (mAppOpenAd == null) {
            if (mShowListener != null) {
                mShowListener.onAdVideoError(new TPError(UNSPECIFIED));
            }
            return;
        }

        mAppOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
        mAppOpenAd.show(activity);
    }

    final FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
        @Override
        public void onAdDismissedFullScreenContent() {
            Log.i(TAG, "onAdDismissedFullScreenContent: ");
            //使用mShowListener实现广告事件的关闭回调
            if (mShowListener != null) {
                mShowListener.onAdClosed();
            }
        }

        @Override
        public void onAdFailedToShowFullScreenContent(AdError adError) {
            Log.i(TAG, "onAdFailedToShowFullScreenContent msg : " + adError.getMessage() + ":code:" + adError.getCode());
            TPError tpError = new TPError(NETWORK_NO_FILL);
            tpError.setErrorCode(String.valueOf(adError.getCode()));
            tpError.setErrorMessage(adError.getMessage());
            if (mLoadAdapterListener != null)
                mLoadAdapterListener.loadAdapterLoadFailed(tpError);
        }

        @Override
        public void onAdShowedFullScreenContent() {
            Log.i(TAG, "onAdShowedFullScreenContent: ");
            //使用mShowListener实现广告事件的展示回调
            if (mShowListener != null) {
                mShowListener.onAdShown();
            }
        }
    };


    @Override
    public void clean() {
        //释放资源
        if (mAppOpenAd != null) {
            mAppOpenAd.setFullScreenContentCallback(null);
            mAppOpenAd = null;
        }
    }

    @Override
    public boolean isReady() {
        // 用于判断广告是否过期
        return true;
    }

    @Override
    public String getNetworkName() {
        // 自定义三方广告源的名称
        return "Admob";
    }

    @Override
    public String getNetworkVersion() {
        // 自定义三方广告源的版本号
        return String.valueOf(MobileAds.getVersion());
    }
}

