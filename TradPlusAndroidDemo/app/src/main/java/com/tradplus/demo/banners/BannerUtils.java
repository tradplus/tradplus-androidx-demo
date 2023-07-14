package com.tradplus.demo.banners;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.tradplus.ads.open.banner.TPBanner;

public class BannerUtils {

    private static BannerUtils sInstance;
    private TPBanner mTpbanner;


    public synchronized static BannerUtils getInstance() {
        if (sInstance == null) {
            sInstance = new BannerUtils();
        }
        return sInstance;
    }

    public void loadBanner(TPBanner tpbanner, String adUnitId) {
       if (tpbanner != null && !TextUtils.isEmpty(adUnitId)) {
           mTpbanner = tpbanner;
           tpbanner.loadAd(adUnitId);
       }
    }

    public void showBanner(ViewGroup adContainer) {
        if (mTpbanner != null) {
            if(mTpbanner.getParent() != null) {
                ((ViewGroup) mTpbanner.getParent()).removeView(mTpbanner);
            }
            adContainer.addView(mTpbanner);
            mTpbanner.showAd();
        }

    }

    public boolean isReady() {
        return mTpbanner != null && mTpbanner.isReady();
    }

    public void onDestroy() {
        if (mTpbanner != null) {
            mTpbanner.onDestroy();
        }

    }
}
