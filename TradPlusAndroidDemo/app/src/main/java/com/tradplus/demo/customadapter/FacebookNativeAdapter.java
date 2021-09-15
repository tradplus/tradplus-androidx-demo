package com.tradplus.demo.customadapter;

import static android.view.Gravity.CENTER;
import static com.tradplus.ads.base.common.TPError.ADAPTER_ACTIVITY_ERROR;
import static com.tradplus.ads.base.common.TPError.ADAPTER_CONFIGURATION_ERROR;
import static com.tradplus.ads.base.common.TPError.NETWORK_NO_FILL;
import static com.tradplus.ads.base.common.TPError.SHOW_FAILED;
import static com.tradplus.ads.base.common.TPError.UNSPECIFIED;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.BuildConfig;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeBannerAd;
import com.tradplus.ads.base.adapter.nativead.TPNativeAdapter;
import com.tradplus.ads.common.DataKeys;
import com.tradplus.ads.common.util.DeviceUtils;
import com.tradplus.ads.base.common.TPError;
import com.tradplus.ads.mobileads.util.AppKeyManager;

import java.lang.ref.WeakReference;
import java.util.Map;

/*
 * Native类型需要继承TPNativeAdapter,并重写以下几个方法
 * loadCustomAd() 用于获取服务器下发和本地配置的参数，实现自定义广告平台的加载逻辑
 * clean() 当Banner广告从屏幕remove后，释放资源
 * getNetworkVersion() 自定义三方源的版本号
 * getNetworkName 自定义三方源的名称
 * */
public class FacebookNativeAdapter extends TPNativeAdapter {

    private NativeAd mFacebookNative;
    private FacebookNativeAd mFacebookNativeAd;
    private NativeBannerAd mNativeBannerAd;
    private String placementId;
    private Context mContext;
    private static final String TAG = "FacebookNative";

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        mContext = context;
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
                mLoadAdapterListener.loadAdapterLoadFailed(new TPError(ADAPTER_ACTIVITY_ERROR));
            }
            return;
        }

        // userParams 获取从本地配置的参数
        // 例如：海外源需要设置CCPA和COPPA，具体接入参考文档，高级功能-隐私规范部分
        FacebookInitializeHelper.setUserParams(userParams, mLoadAdapterListener);

        //初始化SDK
        FacebookInitializeHelper.initialize(context);

        // 创建三方广告位对象
        mFacebookNative = new NativeAd(context, placementId);
        // 请求Native模版
        mFacebookNative.loadAd(
                mFacebookNative.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                        .build());

    }

    //设置FB监听，并设置监听回调
    NativeAdListener nativeAdListener = new NativeAdListener() {
        @Override
        public void onError(Ad ad, AdError adError) {
            Log.i(TAG, "onError: code :" + adError.getErrorCode() + " , msg:" + adError.getErrorMessage());

            if (mLoadAdapterListener != null) {
                TPError tpError = new TPError(NETWORK_NO_FILL);
                tpError.setErrorCode(adError.getErrorCode() + "");
                tpError.setErrorMessage(adError.getErrorMessage());
                mLoadAdapterListener.loadAdapterLoadFailed(tpError);
            }
        }

        @Override
        public void onAdLoaded(Ad ad) {
            // 模版Native
            if (mFacebookNative != null) {
                Log.i(TAG, "TemplateNativeAdLoaded: ");
                View adView = NativeAdView.render(mContext, mFacebookNative);

                // Add the Native Ad View to your ad container.
                // The recommended dimensions for the ad container are:
                // Width: 280dp - 500dp
                // Height: 250dp - 500dp
                RelativeLayout relativeLayout = new RelativeLayout(mContext);
                relativeLayout.addView(adView, new RelativeLayout.LayoutParams(DeviceUtils.dip2px(mContext, 320)
                        , DeviceUtils.dip2px(mContext, 340)));
                relativeLayout.setGravity(CENTER);

                //Native类型通过FacebookNativeAd继承TPBaseAd之后，可通过mFacebookNativeAd里面的方法执行广告事件的回调
                //参数2：设置模版类型 1
                mFacebookNativeAd = new FacebookNativeAd(relativeLayout, AppKeyManager.TEMPLATE_RENDERING_YES);
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoaded(mFacebookNativeAd);
                }

            } else {
                /*
                 * 使用mLoadAdapterListener实现广告事件的加载失败回调
                 * */
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(new TPError(UNSPECIFIED));
                }
            }

        }

        @Override
        public void onAdClicked(Ad ad) {
            Log.i(TAG, "onAdClicked: ");
            //通过mFacebookNativeAd里面的方法执行广告事件的点击回调
            if (mFacebookNativeAd != null)
                mFacebookNativeAd.onAdViewClicked();
        }

        @Override
        public void onLoggingImpression(Ad ad) {
            Log.i(TAG, "onLoggingImpression: ");
            //通过mFacebookNativeAd里面的方法执行广告事件的展示回调
            if (mFacebookNativeAd != null)
                mFacebookNativeAd.onAdViewExpanded();
        }

        @Override
        public void onMediaDownloaded(Ad ad) {

        }
    };


    @Override
    public void clean() {
        // 释放资源
        AdSettings.clearTestDevices();
        if (mFacebookNativeAd != null)
            mFacebookNativeAd.clean();
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
