package com.tradplus.demo.customadapter.c2s;


import static com.tradplus.ads.base.common.TPError.ADAPTER_CONFIGURATION_ERROR;

import static com.tradplus.ads.base.common.TPError.NETWORK_NO_FILL;
import static com.tradplus.ads.base.common.TPError.SHOW_FAILED;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.tradplus.ads.base.adapter.banner.TPBannerAdImpl;
import com.tradplus.ads.base.adapter.banner.TPBannerAdapter;
import com.tradplus.ads.base.annotation.NonNull;
import com.tradplus.ads.base.common.TPError;

import java.util.HashMap;
import java.util.Map;

import sg.bigo.ads.BigoAdSdk;
import sg.bigo.ads.api.AdBid;
import sg.bigo.ads.api.AdConfig;
import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdInteractionListener;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.AdSize;
import sg.bigo.ads.api.BannerAd;
import sg.bigo.ads.api.BannerAdLoader;
import sg.bigo.ads.api.BannerAdRequest;

public class BigoBannerC2SAdapter extends TPBannerAdapter {

    private static final String TAG = "BigoBanner";
    private String mPlacementId,mAppId;
    private TPBannerAdImpl mTpBannerAd;
    private BannerAd mBannerAd;
    private boolean isC2SBidding;
    private boolean isBiddingLoaded;
    private OnC2STokenListener onC2STokenListener;

    private View view;

    public static final String AD_PLACEMENT_ID = "placementId";
    public static final String APP_ID = "appId";
    public static final String ECPM = "ecpm";

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        if (mLoadAdapterListener == null && !isC2SBidding) {
            return;
        }

        if (tpParams != null && tpParams.size() > 0) {
            mPlacementId = tpParams.get(AD_PLACEMENT_ID);
            mAppId = tpParams.get(APP_ID);
        }

        if (TextUtils.isEmpty(mPlacementId) || TextUtils.isEmpty(mAppId)) {
            loadFailed(new TPError(ADAPTER_CONFIGURATION_ERROR),"",ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (BigoAdSdk.isInitialized()) {
            requestBanner();
        }else {
            AdConfig build = new AdConfig.Builder().setAppId(mAppId).build();
            BigoAdSdk.initialize(context, build, new BigoAdSdk.InitListener() {
                @Override
                public void onInitialized() {
                    requestBanner();
                }
            });
        }

    }

    private void requestBanner() {
        if (isC2SBidding && isBiddingLoaded) {
            mTpBannerAd = new TPBannerAdImpl(null, view);
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoaded(mTpBannerAd);
            }
            return;
        }

        BannerAdRequest.Builder builder = new BannerAdRequest.Builder().withSlotId(mPlacementId).withAdSizes(AdSize.BANNER);

        BannerAdLoader bannerAdLoader = new BannerAdLoader.Builder().withAdLoadListener(new AdLoadListener<BannerAd>() {
            @Override
            public void onError(@NonNull AdError error) {
                if (error != null) {
                    int code = error.getCode();
                    String message = error.getMessage();
                    loadFailed(new TPError(NETWORK_NO_FILL),code +"",message);
                    Log.i(TAG, "code :" + code + ", message :" + message);
                }else {
                    loadFailed(new TPError(NETWORK_NO_FILL),"","");
                }

            }

            @Override
            public void onAdLoaded(@NonNull BannerAd ad) {
                view = ad.adView();
                if (view == null) {
                    loadFailed(new TPError(NETWORK_NO_FILL),"","view == null");
                    return;
                }

                mBannerAd = ad;
                mBannerAd.setAdInteractionListener(adInteractionListener);

                // C2S Bidding 获取价格 并通过sdk内部价格
                if (isC2SBidding) {
                    if (onC2STokenListener != null) {
                        AdBid bid = ad.getBid();
                        if (bid == null) {
                            onC2STokenListener.onC2SBiddingFailed("","bid == 0");
                            return;
                        }

                        double price = bid.getPrice();
                        if (price <= 0.0) {
                            onC2STokenListener.onC2SBiddingFailed("","price return 0");
                            return;

                        }
                        isBiddingLoaded = true;
                        Log.i(TAG, "bid price: " + price);
                        Map<String, Object> hashMap = new HashMap<>();
                        //value必须使用double，内部有类型判断
                        hashMap.put(ECPM, price);
                        onC2STokenListener.onC2SBiddingResult(hashMap);
                    }
                    return;
                }

                // Waterfall loaded
                if (mLoadAdapterListener != null) {
                    Log.i(TAG, "onAdLoaded: ");
                    if (mTpBannerAd == null) {
                        mTpBannerAd = new TPBannerAdImpl(null, view);
                    }
                    mLoadAdapterListener.loadAdapterLoaded(mTpBannerAd);
                }
            }
        }).build();


        bannerAdLoader.loadAd(builder.build());

    }

    private void loadFailed(TPError tpError, String errorCode, String errorMsg) {
        Log.i(TAG, "loadFailed: errorCode :" + errorCode + ", errorMsg :" + errorMsg);
        if (isC2SBidding) {
            if (onC2STokenListener != null) {
                onC2STokenListener.onC2SBiddingFailed(errorCode, errorMsg);
            }
            return;
        }

        if (tpError != null && mLoadAdapterListener != null) {
            tpError.setErrorCode(errorCode);
            tpError.setErrorMessage(errorMsg);
            mLoadAdapterListener.loadAdapterLoadFailed(tpError);
        }
    }

    private final AdInteractionListener adInteractionListener = new AdInteractionListener() {
        @Override
        public void onAdError(@NonNull AdError error) {
            TPError tpError = new TPError(SHOW_FAILED);
            if (error != null) {
                int code = error.getCode();
                String message = error.getMessage();
                tpError.setErrorMessage(message);
                tpError.setErrorCode(code + "");
                Log.i(TAG, "code :" + code + ", message :" + message);
            }

            if (mTpBannerAd != null) {
                mTpBannerAd.onAdShowFailed(tpError);
            }
        }

        @Override
        public void onAdImpression() {
            Log.i(TAG, "onAdImpression: ");
            if (mTpBannerAd != null) {
                mTpBannerAd.adShown();
            }
        }

        @Override
        public void onAdClicked() {
            Log.i(TAG, "onAdClicked: ");
            if (mTpBannerAd != null) {
                mTpBannerAd.adClicked();
            }
        }

        @Override
        public void onAdOpened() {

        }

        @Override
        public void onAdClosed() {
            Log.i(TAG, "onAdClosed: ");
            if (mTpBannerAd != null) {
                mTpBannerAd.adClosed();
            }
        }
    };

    @Override
    public void clean() {
        if (mBannerAd != null) {
            mBannerAd.setAdInteractionListener(null);
            mBannerAd.destroy();
            mBannerAd = null;
        }
    }

    @Override
    public String getNetworkName() {
        return "Bigo";
    }

    @Override
    public String getNetworkVersion() {
        return BigoAdSdk.getSDKVersionName();
    }

    // Bidding Start时调用广告并获取价格
    @Override
    public void getC2SBidding(final Context context, final Map<String, Object> localParams, final Map<String, String> tpParams, final OnC2STokenListener onC2STokenListener) {
        this.onC2STokenListener = onC2STokenListener;
        isC2SBidding = true;
        loadCustomAd(context, localParams, tpParams);
    }


}
