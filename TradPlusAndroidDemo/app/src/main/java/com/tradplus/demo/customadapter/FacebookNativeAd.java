package com.tradplus.demo.customadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeBannerAd;
import com.tradplus.ads.base.adapter.nativead.TPNativeAdView;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.base.common.TPImageLoader;
import com.tradplus.ads.mobileads.util.AppKeyManager;

import java.util.ArrayList;
import java.util.List;


public class FacebookNativeAd extends TPBaseAd {

    private static final String TAG = "FacebookNative";
    private NativeAd mFacebookNative;
    private AdOptionsView adOptionsView;
    private View mFacebookView;
    private String mAdSize;
    private Context mContext;
    private int isRender;
    private TPNativeAdView mNativeAdView; // 自渲染
    private NativeAdLayout mContainer;

    // 模版类型获取三方返回的View和广告类型
    public FacebookNativeAd(View facebookView ,int templete) {
        mFacebookView = facebookView;
        isRender = templete;
    }

    // 自渲染类型通过TPNativeAdView包装过的三方对象
//    private void initNativeAd(Context context) {
//        mNativeAdView = new TPNativeAdView();
//        mContainer = new NativeAdLayout(context);
//        MediaView nativeAdMedia = new MediaView(context);
//        adOptionsView = new AdOptionsView(context, mFacebookNative, mContainer);
//
//        mNativeAdView.setCallToAction(mFacebookNative.getAdCallToAction());
//        mNativeAdView.setTitle(mFacebookNative.getAdHeadline());
//        mNativeAdView.setSubTitle(mFacebookNative.getAdBodyText());
//        mNativeAdView.setAdSource(mFacebookNative.getSponsoredTranslation());
//        mNativeAdView.setIconImageUrl(mFacebookNative.getAdIcon().getUrl());
//        mNativeAdView.setMediaView(nativeAdMedia);
//    }

    public void onAdViewClicked() {
        if (mShowListener != null) {
            mShowListener.onAdClicked();
        }
    }

    public void onAdViewExpanded() {
        if (mShowListener != null) {
            mShowListener.onAdShown();
        }
    }

    /**
     * 返回三方的对象，如果有开发者需要自己拿素材，可以直接获取这个对象
     * @return
     */
    @Override
    public Object getNetworkObj() {
        return mFacebookNative == null ? mFacebookNative : null;
    }

    /**
     * 自渲染模式中，开发者在渲染结束后，会把可点击的view传过来(addview前调用)
     * @param viewGroup 开发者自己的布局，主要的几个view用tag标记过了
     * @param clickViews 可点击的广告view
     */
    @Override
    public void registerClickView(ViewGroup viewGroup, ArrayList<View> clickViews) {
//        if (mFacebookNative != null) {
//            View iconView = viewGroup.findViewWithTag(TPBaseAd.NATIVE_AD_TAG_ICON);
//            if (mNativeAdView != null && mNativeAdView.getMediaView() instanceof MediaView && iconView instanceof ImageView) {
//                mFacebookNative.registerViewForInteraction(
//                        viewGroup,
//                        (MediaView) mNativeAdView.getMediaView(),
//                        (ImageView) iconView,
//                        clickViews);
//            }
//
//            FrameLayout adChoicesView = viewGroup.findViewWithTag(TPBaseAd.NATIVE_AD_TAG_ADCHOICES);
//            if (adChoicesView != null && adOptionsView != null) {
//                adChoicesView.removeAllViews();
//                adChoicesView.addView(adOptionsView, 0);
//            }
//        }
    }

    /**
     * 获取tradplus包装过的对象，里面可以获取自渲染的所有素材
     * 对应广告类型AD_TYPE_NORMAL_NATIVE
     * @return
     */
    @Override
    public TPNativeAdView getTPNativeView() {
        return null;
//        return mNativeAdView;
    }

    /**
     * 获取广告类型，
     * AD_TYPE_NORMAL_NATIVE：自渲染，
     * AD_TYPE_NATIVE_EXPRESS：模板渲染，
     * AD_TYPE_NATIVE_LIST：多个模板view
     * @return
     */
    @Override
    public int getNativeAdType() {
        //返回广告类型，模板类型，自渲染类型
        if (isRender == AppKeyManager.TEMPLATE_RENDERING_YES) {
            return AD_TYPE_NATIVE_EXPRESS; //模版
        } else {
            return AD_TYPE_NORMAL_NATIVE;//自渲染
        }
    }

    /**
     * 获取模板渲染的view，对应广告类型AD_TYPE_NATIVE_EXPRESS
     * @return
     */
    @Override
    public View getRenderView() {
        if (mFacebookView != null) {
            return mFacebookView;
        } else {
            return null;
        }
    }

    /**
     * 获取多模板渲染view，对应AD_TYPE_NATIVE_LIST
     * @return
     */
    @Override
    public List<View> getMediaViews() {
        return null;
    }

    /**
     * 获取三方平台的容器（部分三方源需要用他们提供的容器）
     * @return
     */
    @Override
    public ViewGroup getCustomAdContainer() {
        return null;
    }

    // 释放资源
    @Override
    public void clean() {
        if (mFacebookNative != null) {
            mFacebookNative.unregisterView();
            mFacebookNative.destroy();
            mFacebookNative = null;
        }
    }
}
