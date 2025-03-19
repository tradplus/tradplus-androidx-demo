package com.tradplus.demo.customadapter.c2s;

import static com.tradplus.ads.base.common.TPError.ADAPTER_CONFIGURATION_ERROR;
import static com.tradplus.ads.base.common.TPError.SHOW_FAILED;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mobads.sdk.api.AdSettings;
import com.baidu.mobads.sdk.api.BDAdConfig;
import com.baidu.mobads.sdk.api.BDDialogParams;
import com.baidu.mobads.sdk.api.BiddingListener;
import com.baidu.mobads.sdk.api.ExpressInterstitialAd;
import com.baidu.mobads.sdk.api.ExpressResponse;
import com.baidu.mobads.sdk.api.FullScreenVideoAd;
import com.baidu.mobads.sdk.api.NativeResponse;
import com.baidu.mobads.sdk.api.RewardVideoAd;
import com.baidu.mobads.sdk.api.SplashAd;
import com.tradplus.ads.baidu.BaiduBiddingNotice;
import com.tradplus.ads.baidu.BaiduConstant;
import com.tradplus.ads.base.adapter.reward.TPRewardAdapter;
import com.tradplus.ads.base.common.TPError;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaiduIntersititalVideoC2SAdapter extends TPRewardAdapter {

    private String mPlacementId, mAppId,ecpmLevel;
    private RewardVideoAd mRewardVideoAd;
    private OnC2STokenListener onC2STokenListener;
    private boolean isBiddingLoaded;
    private static final String TAG = "BaiduReward";
    public static final String AD_PLACEMENT_ID = "placementId";
    public static final String APP_ID = "appId";

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {

        // tpParams 获取从服务器端下发的字段
        if (tpParams.size() > 0 && tpParams.containsKey(AD_PLACEMENT_ID) && tpParams.containsKey(APP_ID)) {
            mAppId = tpParams.get(APP_ID);
            mPlacementId = tpParams.get(AD_PLACEMENT_ID);
        } else {
            if (onC2STokenListener != null) {
                onC2STokenListener.onC2SBiddingFailed("", ADAPTER_CONFIGURATION_ERROR);
            }
            return;
        }

        // 初始化 BaiduSDK
        BDAdConfig bdAdConfig = new BDAdConfig.Builder()
                .setAppsid(mAppId)
                .setDialogParams(new BDDialogParams.Builder()
                        .build())
                .build(context);

        bdAdConfig.init();

        // 请求广告
        requestAd(context);
    }

    private void requestAd(Context context) {
        if (onC2STokenListener != null && isBiddingLoaded) {
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoaded(null);
            }
            return;
        }

        if (mRewardVideoAd == null) {
            mRewardVideoAd = new RewardVideoAd(context, mPlacementId, mRewardVideoAdListener, false);
        }

        mRewardVideoAd.load();
    }

    RewardVideoAd.RewardVideoAdListener mRewardVideoAdListener = new RewardVideoAd.RewardVideoAdListener() {
        @Override
        public void onAdShow() {
            Log.i(TAG, "onAdShow: ");
            if (mShowListener != null) {
                mShowListener.onAdShown();
            }
        }

        @Override
        public void onAdClick() {
            Log.i(TAG, "onAdClick: ");
            if (mShowListener != null) {
                mShowListener.onAdVideoClicked();
            }
        }

        @Override
        public void onAdClose(float v) {
            Log.i(TAG, "onAdClose: ");
            if (mShowListener != null) {
                mShowListener.onAdClosed();
            }

        }

        @Override
        public void onAdFailed(String s) {
            Log.i(TAG, "onAdFailed: " + s);
            if (onC2STokenListener != null) {
                onC2STokenListener.onC2SBiddingFailed("", s);
            }
        }

        @Override
        public void onVideoDownloadSuccess() {
            Log.i(TAG, "onVideoDownloadSuccess: 视频缓存成功");
            if (onC2STokenListener != null) {
                ecpmLevel = mRewardVideoAd.getECPMLevel();
                Log.i(TAG, "激励视频 bid price: " + ecpmLevel);
                if (TextUtils.isEmpty(ecpmLevel)) {
                    onC2STokenListener.onC2SBiddingFailed("", "ecpmLevel is Empty");
                    return;
                }
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put(BaiduConstant.ECPM, Double.parseDouble(ecpmLevel));
                onC2STokenListener.onC2SBiddingResult(hashMap);
            }
            isBiddingLoaded = true;
        }

        @Override
        public void onVideoDownloadFailed() {
            Log.i(TAG, "onVideoDownloadFailed: 视频缓存失败");
            if (onC2STokenListener != null) {
                onC2STokenListener.onC2SBiddingFailed("", "视频缓存失败");
            }
        }

        @Override
        public void playCompletion() {
            Log.i(TAG, "playCompletion: 播放完成");
            if (mShowListener != null) {
                mShowListener.onAdVideoEnd();
            }

        }

        @Override
        public void onAdSkip(float v) {
            Log.i(TAG, "onAdSkip: 用户点击跳过");
            if (mShowListener != null) {
                mShowListener.onRewardSkip();
            }
        }

        @Override
        public void onRewardVerify(boolean rewardVerify) {
            Log.i(TAG, "onRewardVerify: 发放奖励 " + rewardVerify);
            if (mShowListener != null) {
                mShowListener.onReward();
            }
        }

        @Override
        public void onAdLoaded() {
            Log.i(TAG, "onAdLoaded: ");
        }
    };

    @Override
    public boolean isReady() {
        return mRewardVideoAd != null && mRewardVideoAd.isReady();
    }

    @Override
    public void showAd() {
        if (mRewardVideoAd != null && mRewardVideoAd.isReady()) {
            if (!TextUtils.isEmpty(ecpmLevel)) {
                onBidWinResult(ecpmLevel);
            }
            mRewardVideoAd.show();
        } else {
            if (mShowListener != null) {
                mShowListener.onAdVideoError(new TPError(SHOW_FAILED));
            }
        }
    }

    @Override
    public String getNetworkName() {
        return "Baidu";
    }

    @Override
    public String getNetworkVersion() {
        return AdSettings.getSDKVersion();
    }

    @Override
    public void getC2SBidding(final Context context, final Map<String, Object> localParams, final Map<String, String> tpParams, final OnC2STokenListener onC2STokenListener) {
        this.onC2STokenListener = onC2STokenListener;
        loadCustomAd(context, localParams, tpParams);
    }

    public void onBidWinResult(String ecpmLevel) {
        try {
            LinkedHashMap<String, Object> secondInfo = new LinkedHashMap<>();
            // 单位：分
            secondInfo.put("ecpm", (int) Math.round(Double.parseDouble(ecpmLevel)));

            secondInfo.put("adn", 10);

            secondInfo.put("ad_t", 7);
            // 竞价时间，秒级时间戳
            secondInfo.put("ad_time", (System.currentTimeMillis() / 1000L));

            secondInfo.put("bid_t", 4);

            BiddingListener winBiddingListener = new BiddingListener() {
                @Override
                public void onBiddingResult(boolean result, String message, HashMap<String, Object> ext) {
                    Log.i(TAG, "onBiddingResult-win: " + result + ", msg信息：" + message);
                }
            };

            Log.i(TAG, "biddingSuccess: " + ecpmLevel);
            mRewardVideoAd.biddingSuccess(secondInfo, winBiddingListener);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    @Override
    public void setLossNotifications(String auctionPrice, String auctionPriceCny, String lossReason) {
        try {
            LinkedHashMap<String, Object> secondInfo = new LinkedHashMap<>();
            // 单位：分
            secondInfo.put("ecpm", (int) Math.round(Double.parseDouble(auctionPriceCny)));
            secondInfo.put("adn", 10);
            secondInfo.put("ad_t", 7);
            secondInfo.put("ad_time", (System.currentTimeMillis() / 1000L));
            secondInfo.put("bid_t", 4);

            BiddingListener winBiddingListener = new BiddingListener() {
                @Override
                public void onBiddingResult(boolean result, String message, HashMap<String, Object> ext) {
                    Log.i(TAG, "onBiddingResult-win: " + result + ", msg信息：" + message);
                }
            };
            mRewardVideoAd.biddingFail(secondInfo,winBiddingListener);
        } catch (Throwable throwable) {

        }
    }
}
