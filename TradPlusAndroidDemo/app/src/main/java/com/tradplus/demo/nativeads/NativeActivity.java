package com.tradplus.demo.nativeads;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.adapter.nativead.TPNativeAdView;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.base.common.TPImageLoader;
import com.tradplus.ads.mgr.nativead.TPCustomNativeAd;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.ads.open.nativead.TPNativeAdRender;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

import java.util.HashMap;

/**
 * 标准原生广告，这个广告是可以由开发者控制大小，尽可能融入到app的内容中去，从而提升广告的点击和转化
 *
 * native广告分自渲染和模板渲染
 * native 自渲染广告是三方广告平台返回广告素材由开发者来拼接成对于的样式
 * native 模板渲染是三方广告平台返回渲染好的view（很多广告平台可以在对应的后台设置样式），开发者直接添加到一个容器就可以展示出来
 */
public class NativeActivity extends AppCompatActivity {

    private TPNative tpNative;
    private ViewGroup adContainer;
    private static final String TAG = "tradpluslog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        adContainer = findViewById(R.id.ad_container);
        loadNormalNative();
    }


    @Override
    protected void onDestroy() {
        //释放资源
        tpNative.onDestroy();
        super.onDestroy();
    }


    /**
     * --------------------------------------------------------------------------------------------------------------
     * native的基本用法，如果没有特殊需求，按照如下代码接入即可
     * --------------------------------------------------------------------------------------------------------------
     */
    private void loadNormalNative() {
        tpNative = new TPNative(NativeActivity.this,TestAdUnitId.NATIVE_ADUNITID);
        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");
                tpNative.getNativeAd().showAd(adContainer, R.layout.native_ad_list_item,"");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: "+ tpAdInfo.adSourceName + "被点击");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: "+ tpAdInfo.adSourceName + "展示");
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdShowFailed: "+ tpAdInfo.adSourceName + "展示失败");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdLoadFailed: 加载失败 , code : "+ tpAdError.getErrorCode() + ", msg :" + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+ tpAdInfo.adSourceName + "广告关闭");
            }
        });

        tpNative.loadAd();
    }





    /**
     * ==============================================================================================================
     *                                       以下是高级用法，一般情况下用不到
     * ==============================================================================================================
     */



    private void loadNativeExpress() {
        tpNative = new TPNative(NativeActivity.this,TestAdUnitId.NATIVE_ADUNITID);
        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");
                // 自渲染的方式show广告
                renderNativeAd();

                // 获取广告源详细信息后再show广告（不能先用自渲染show出来然后再获取信息）
//                getNativeAdInfoAndShowAd();

                // 获取模板渲染的信息，返回三方广告平台渲染好的view，这种方式获取后show的广告tradplus会统计不到部分事件信息。
//                View view = tpBaseAd.getRenderView();
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: "+ tpAdInfo.adSourceName + "被点击");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: "+ tpAdInfo.adSourceName + "展示");
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdShowFailed: "+ tpAdInfo.adSourceName + "展示失败");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdLoadFailed: 加载失败 , code : "+ tpAdError.getErrorCode() + ", msg :" + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+ tpAdInfo.adSourceName + "广告关闭");
            }
        });

        tpNative.setAllAdLoadListener(new LoadAdEveryLayerListener() {
            @Override
            public void onAdAllLoaded(boolean b) {
                // 所有广告层级都加载完成，b == true 加载到有可用的广告，b == false 没有加载到可用的广告
            }

            @Override
            public void oneLayerLoadFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                // 每一层广告加载失败都会回调这个方法
            }

            @Override
            public void oneLayerLoaded(TPAdInfo tpAdInfo) {
                // 每一层广告加载成功都会回调这个方法
            }

            @Override
            public void onLoadAdStart(TPAdInfo tpAdInfo) {

            }

            @Override
            public void onBiddingStart(TPAdInfo tpAdInfo) {

            }

            @Override
            public void onBiddingEnd(TPAdInfo tpAdInfo) {

            }
        });

        // 部分渠道不能在TradPlus后台设置宽高，可以在load前传入，尺寸dp
        tpNative.setAdSize(300, 250);

        // 部分渠道有定制化参数，需要在这里用map的方式传递信息，
        // 例如：admob的native 角标可以自定义位置（key：Admob_Adchoices value：0-4），具体参考文档
        tpNative.setCustomParams(new HashMap<>());

        // 进入广告场景埋点，用来统计广告触达率，一般是进入某个页面开始调用
        tpNative.entryAdScenario("adSceneId");

        tpNative.loadAd();
    }

    private void renderNativeAd() {
        // 如果三方广告平台是模板渲染，那么TPNativeAdRender中的方法不会被回调，会直接拿到三方平台渲染好的view，add到adContainer中
        tpNative.showAd(adContainer, new TPNativeAdRender() {

            // 获取广告布局文件，用自定义渲染的方式可以自己写布局的id
            @Override
            public ViewGroup createAdLayoutView() {
                LayoutInflater inflater = (LayoutInflater) NativeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                return (ViewGroup) inflater.inflate(R.layout.native_ad_list_item, null);
            }

            @Override
            public ViewGroup renderAdView(TPNativeAdView tpNativeAdView) {
                ViewGroup viewGroup = createAdLayoutView();

                ImageView imageView = viewGroup.findViewById(R.id.mopub_native_main_image);
                if(imageView != null) {
                    if(tpNativeAdView.getMediaView() != null) {
                        // 如果三方广告平台有mediaview，需要用三方提供的mediaview来替换原来布局中的imageview
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        ViewParent viewParent = imageView.getParent();
                        if(viewParent != null) {
                            ((ViewGroup)viewParent).removeView(imageView);
                            ((ViewGroup)viewParent).addView(tpNativeAdView.getMediaView(), params);
                        }
                    } else if(tpNativeAdView.getMainImage() != null) {
                        // 部分三方平台返回的是drawable，可以直接设置
                        imageView.setImageDrawable(tpNativeAdView.getMainImage());
                    } else if(tpNativeAdView.getMainImageUrl() != null) {
                        // 其他三方平台返回的是图片的url，需要先下载图片再填充到view中
                        TPImageLoader.getInstance().loadImage(imageView, tpNativeAdView.getMainImageUrl());
                    }
                }

                ImageView iconView = viewGroup.findViewById(R.id.native_icon_image);
                if(iconView != null) {
                    if(tpNativeAdView.getIconImage() != null) {
                        iconView.setImageDrawable(tpNativeAdView.getIconImage());
                    } else if(tpNativeAdView.getIconImageUrl() != null){
                        TPImageLoader.getInstance().loadImage(iconView, tpNativeAdView.getIconImageUrl());
                    }
                }

                TextView titleView = viewGroup.findViewById(R.id.native_title);
                if(titleView != null && tpNativeAdView.getTitle() != null) {
                    titleView.setText(tpNativeAdView.getTitle());
                }

                TextView subTitleView = viewGroup.findViewById(R.id.native_text);
                if(subTitleView != null && tpNativeAdView.getSubTitle() != null) {
                    subTitleView.setText(tpNativeAdView.getSubTitle());
                }

                Button callToActionView = viewGroup.findViewById(R.id.native_cta_btn);
                if(callToActionView != null && tpNativeAdView.getCallToAction() != null) {
                    callToActionView.setText(tpNativeAdView.getCallToAction());
                }

                // facebook会需要一个adchoice的容器来填充adchoice
                FrameLayout adChoiceView = viewGroup.findViewById(R.id.ad_choices_container);

                // 把主要的元素设置给三方广告平台，第二个参数是是否可以点击
                setImageView(imageView, true);
                setIconView(iconView, true);
                setTitleView(titleView, true);
                setSubTitleView(subTitleView, true);
                setCallToActionView(callToActionView, true);
                setAdChoicesContainer(adChoiceView, false);

                return viewGroup;
            }
        }, "adSceneId");
    }

    private void getNativeAdInfoAndShowAd() {
        TPCustomNativeAd customNativeAd = tpNative.getNativeAd();

        String id = customNativeAd.getCustomNetworkId();  // 广告源的id
        String name = customNativeAd.getCustomNetworkName(); // 广告源的名称

        Object obj = customNativeAd.getCustomNetworkObj(); // 广告源对应的native对象，通过强转可以获取到三方广告的信息
        if(obj instanceof com.mopub.nativeads.StaticNativeAd){

        }else if(obj instanceof com.google.android.gms.ads.formats.UnifiedNativeAd){

        }else if(obj instanceof com.facebook.ads.NativeAd){

        }else if(obj instanceof com.facebook.ads.NativeBannerAd){

        }else if(obj instanceof com.kwad.sdk.api.KsDrawAd){
        }else if(obj instanceof com.bytedance.sdk.openadsdk.TTNativeExpressAd){
        }else if(obj instanceof com.qq.e.comm.pi.AdData){
        }else{
        }

        // show 广告
        customNativeAd.showAd(adContainer, R.layout.native_ad_list_item, "adSceneId");
    }
}
