package com.tradplus.demo.nativeads;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.ads.open.nativead.TPNativeBanner;
import com.tradplus.demo.R;

public class NativeUtils {

    private static NativeUtils sInstance;
    private TPNative mTpNative;
    private TPNativeBanner mTpNativeBanner;


    public synchronized static NativeUtils getInstance() {
        if (sInstance == null) {
            sInstance = new NativeUtils();
        }
        return sInstance;
    }

    public void loadNative(TPNative tpNative) {
        if (tpNative != null) {
            mTpNative = tpNative;
            tpNative.loadAd();
        }
    }

    public void showNative(ViewGroup adContainer) {
        if (mTpNative != null) {
            mTpNative.showAd(adContainer, R.layout.tp_native_ad_list_item, "");
        }
    }

    public boolean isReady() {
        return mTpNative != null && mTpNative.isReady();
    }

    public void onDestroy() {
        if (mTpNative != null) {
            mTpNative.onDestroy();
        }
    }

    public void loadNativeBanner(TPNativeBanner tpNativeBanner, String adUnitID) {
        if (tpNativeBanner != null && !TextUtils.isEmpty(adUnitID)) {
            mTpNativeBanner = tpNativeBanner;
            mTpNativeBanner.loadAd(adUnitID);
        }
    }

    public boolean isReadyNativeBanner() {
        return mTpNativeBanner != null && mTpNativeBanner.isReady();
    }

    public void showNativeBanner(ViewGroup adContainer) {
        if (mTpNativeBanner != null) {
            if(mTpNativeBanner.getParent() != null) {
                ((ViewGroup) mTpNativeBanner.getParent()).removeView(mTpNativeBanner);
            }
            adContainer.addView(mTpNativeBanner);
            mTpNativeBanner.showAd();
        }
    }
}
