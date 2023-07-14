package com.tradplus.demo.rewarded;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tradplus.ads.base.adapter.reward.TPRewardAdapter;
import com.tradplus.ads.open.banner.TPBanner;
import com.tradplus.ads.open.interstitial.TPInterstitial;
import com.tradplus.ads.open.reward.TPReward;

public class VideoUtils {

    private static VideoUtils sInstance;
    private TPReward mTPReward;
    private TPInterstitial mTPInterstitial;


    public synchronized static VideoUtils getInstance() {
        if (sInstance == null) {
            sInstance = new VideoUtils();
        }
        return sInstance;
    }

    public void loadReward(TPReward tpReward) {
       if (tpReward != null) {
           mTPReward = tpReward;
           tpReward.loadAd();
       }
    }

    public void showReward(Activity activity) {
        if (mTPReward != null) {
            mTPReward.showAd(activity,"sceneId");
        }

    }

    public boolean isReady() {
        return mTPReward != null && mTPReward.isReady();
    }

    public void loadInterstitial(TPInterstitial tpInterstitial) {
        if (tpInterstitial != null) {
            mTPInterstitial = tpInterstitial;
            mTPInterstitial.loadAd();
        }
    }

    public void showInterstitial(Activity activity) {
        if (mTPInterstitial != null) {
            mTPInterstitial.showAd(activity,"sceneId");
        }

    }

    public boolean isReadyInterstitial() {
        return mTPInterstitial != null && mTPInterstitial.isReady();
    }

    public void onDestroy() {
        if (mTPReward != null) {
            mTPReward.onDestroy();
        }

        if (mTPInterstitial != null) {
            mTPInterstitial.onDestroy();
        }

    }
}
