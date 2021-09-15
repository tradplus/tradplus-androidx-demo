package com.tradplus.demo.customadapter;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.BuildConfig;
import com.tradplus.ads.base.adapter.banner.TPBannerAdImpl;
import com.tradplus.ads.base.adapter.banner.TPBannerAdapter;
import com.tradplus.ads.common.util.Views;
import com.tradplus.ads.base.common.TPError;

import java.util.Map;

import static com.tradplus.ads.base.common.TPError.ADAPTER_CONFIGURATION_ERROR;
import static com.tradplus.ads.base.common.TPError.SHOW_FAILED;
/*
* Banner类型需要继承TPBannerAdapter,并重写以下几个方法
* loadCustomAd() 用于获取服务器下发和本地配置的参数，实现自定义广告平台的加载逻辑
* clean() 当Banner广告从屏幕remove后，释放资源
* getNetworkVersion() 自定义三方源的版本号
* getNetworkName 自定义三方源的名称
* */
public class FacebookBannerAdapter extends TPBannerAdapter {

    private AdView mFacebookBanner;
    private String placementId;
    private TPBannerAdImpl mTpBannerAd;
    private static final String TAG = "FacebookBanner";

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

        // userParams 获取从本地配置的参数
        // 例如：海外源需要设置CCPA和COPPA，具体接入参考文档，高级功能-隐私规范部分
        FacebookInitializeHelper.setUserParams(userParams,mLoadAdapterListener);

        //初始化SDK
        FacebookInitializeHelper.initialize(context);

        //创建FB广告位对象，并按照配置下发的尺寸进行请求
        mFacebookBanner = new AdView(context, placementId, AdSize.BANNER_HEIGHT_50);
        //设置FB监听，并设置监听回调
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.i(TAG, "onError: code :" + adError.getErrorCode() + " , msg:" + adError.getErrorMessage());

                if (mLoadAdapterListener != null) {
                    TPError tpError = new TPError(SHOW_FAILED);
                    tpError.setErrorCode(adError.getErrorCode() + "");
                    tpError.setErrorMessage(adError.getErrorMessage());
                    mLoadAdapterListener.loadAdapterLoadFailed(tpError);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.i(TAG, "onAdLoaded: ");
                if (mFacebookBanner == null) {
                    return;
                }

                if (mLoadAdapterListener != null) {
                    //Banner类型需要创建TPBannerAdImpl，传入已经加载成功的View的对象
                    mTpBannerAd = new TPBannerAdImpl(null, mFacebookBanner);
                    //将TPBannerAdImpl对象传入loadAdapterLoaded，实现广告事件加载成功回调
                    mLoadAdapterListener.loadAdapterLoaded(mTpBannerAd);
                }

            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.i(TAG, "onAdClicked: ");
                //使用mTpBannerAd实现广告事件的点击回调
                if (mTpBannerAd != null) {
                    mTpBannerAd.adClicked();
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.i(TAG, "onLoggingImpression: ");
                //使用mTpBannerAd实现广告事件的展示回调
                if (mTpBannerAd != null)
                    mTpBannerAd.adShown();
            }
        };

        //请求广告
        mFacebookBanner.loadAd(mFacebookBanner.buildLoadAdConfig().withAdListener(adListener).build());
    }

    @Override
    public void clean() {
        Log.i(TAG, "clean: ");
        if (mFacebookBanner != null) {
            Views.removeFromParent(mFacebookBanner);
            mFacebookBanner.destroy();
            mFacebookBanner = null;
        }

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
