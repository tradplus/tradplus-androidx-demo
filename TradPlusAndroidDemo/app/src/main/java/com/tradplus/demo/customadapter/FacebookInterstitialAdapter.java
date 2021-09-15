package com.tradplus.demo.customadapter;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.BuildConfig;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.tradplus.ads.base.adapter.interstitial.TPInterstitialAdapter;
import com.tradplus.ads.base.common.TPError;

import java.util.Map;

import static com.tradplus.ads.base.common.TPError.ADAPTER_CONFIGURATION_ERROR;
import static com.tradplus.ads.base.common.TPError.NETWORK_NO_FILL;
import static com.tradplus.ads.base.common.TPError.SHOW_FAILED;

/*
 * 插屏类型需要继承TPInterstitialAdapter,并重写以下几个方法
 * loadCustomAd() 用于获取服务器下发和本地配置的参数，实现自定义广告平台的加载逻辑
 * showAd()  实现展示自定义广告平台激励视频的逻辑
 * isReady() 用于展示广告前判断自定义广告是否过期
 * clean() 用于释放资源
 * getNetworkVersion() 自定义三方源的版本号
 * getNetworkName 自定义三方源的名称
 * */
public class FacebookInterstitialAdapter extends TPInterstitialAdapter {

    private InterstitialAd mFacebookInterstitial;
    private String placementId;
    private static final String TAG = "FacebookInterstitial";

    @Override
    public void loadCustomAd(final Context context,
                             final Map<String, Object> userParams,
                             final Map<String, String> tpParams) {
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

        // userParams 获取从本地配置的参数
        // 例如：海外源需要设置CCPA和COPPA，具体接入参考文档，高级功能-隐私规范部分
        FacebookInitializeHelper.setUserParams(userParams,mLoadAdapterListener);

        //初始化SDK
        FacebookInitializeHelper.initialize(context);
        //创建三方广告位对象
        mFacebookInterstitial = new InterstitialAd(context, placementId);
        //设置FB监听，并设置监听回调
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.i(TAG, "onError: code :" + adError.getErrorCode() + " , msg:" + adError.getErrorMessage());
                //NETWORK_NO_FILL，三方NOFILL
                if (mLoadAdapterListener != null) {
                    TPError tpError = new TPError(NETWORK_NO_FILL);
                    tpError.setErrorCode(adError.getErrorCode() + "");
                    tpError.setErrorMessage(adError.getErrorMessage());
                    mLoadAdapterListener.loadAdapterLoadFailed(tpError);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.i(TAG, "onAdLoaded: ");
                if (mFacebookInterstitial == null) {
                    return;
                }

                if (mLoadAdapterListener != null) {
                    //使用mLoadAdapterListener，实现广告事件加载成功回调
                    mLoadAdapterListener.loadAdapterLoaded(null);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.i(TAG, "onAdClicked: ");
                //使用mShowListener实现广告事件的点击回调
                if (mShowListener != null) {
                    mShowListener.onAdVideoClicked();
                }
            }

            @Override
            public void onLoggingImpression(Ad ad)   {
                Log.i(TAG, "onLoggingImpression: ");
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                Log.i(TAG, "onInterstitialDisplayed: ");
                //使用mShowListener实现广告事件的展示回调
                if (mShowListener != null) {
                    mShowListener.onAdVideoStart();
                }

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.i(TAG, "onInterstitialDismissed: ");
                //使用mShowListener实现广告事件的关闭回调
                if (mShowListener != null) {
                    mShowListener.onAdVideoEnd();
                }
            }
        };

        // 请求广告
        InterstitialAd.InterstitialAdLoadConfigBuilder interstitialAdLoadConfigBuilder = mFacebookInterstitial.buildLoadAdConfig().withAdListener(interstitialAdListener);
        mFacebookInterstitial.loadAd(interstitialAdLoadConfigBuilder.build());
    }

    @Override
    public void showAd() {
        /*
         * mShowListener在showAd()重写时生成，用户实现调用show()后的事件回调
         * 回调方法onAdVideoStart ：广告开始展示
         * 回调方法onAdVideoEnd ：广告关闭
         * 回调方法onAdVideoError ：广告展示失败 ，参数一：ErrorCode错误码；参数2：ErrorMsg错误信息
         * 回调方法onAdVideoClicked ：广告被点击
         * */
        if (mFacebookInterstitial != null && mFacebookInterstitial.isAdLoaded()) {
            mFacebookInterstitial.show();
        }else {
            if (mShowListener != null) {
                mShowListener.onAdVideoError(new TPError(SHOW_FAILED));
            }
        }

    }

    @Override
    public void clean() {
        // 释放资源
        if (mFacebookInterstitial != null) {
            mFacebookInterstitial.destroy();
            mFacebookInterstitial = null;
        }

    }

    @Override
    public boolean isReady() {
        // 用于判断广告是否过期
        if (mFacebookInterstitial != null) {
            return !isAdsTimeOut() && !mFacebookInterstitial.isAdInvalidated();
        }
        return false;
    }

    @Override
    public String getNetworkVersion() {
        // 自定义三方广告源的版本号
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public String getNetworkName() {
        // 自定义三方广告源的名称
        return "audience-network";
    }


}
