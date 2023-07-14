package com.tradplus.demo.nativeads;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.adapter.nativead.TPNativeAdView;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.base.common.TPImageLoader;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.nativead.TPNativeAdRender;
import com.tradplus.ads.open.nativead.TPNativeBanner;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

/**
 * nativebanner是用native来拼接的banner，跟native相比是少了大图，在特定的场景可以代替banner广告（填充率和点击率，素材内容和质量上有差异）
 * nativebanner是按照banner的逻辑来实现，所以load成功以后会自动show出来，同时nativebanner也会有自动刷新等功能
 */
public class NativeBannerViewActivity extends AppCompatActivity implements View.OnClickListener {

    private NativeUtils nativeUtils;
    private TPNativeBanner tpNativeBanner;
    private ViewGroup adContainer;
    private static final String TAG = "tradpluslog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nativebanner);

        adContainer = findViewById(R.id.ad_container);
        nativeUtils = NativeUtils.getInstance();
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.second_page).setOnClickListener(this);
        findViewById(R.id.native_listview).setOnClickListener(this);
        loadNativeBanner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // v6.4.5以后才有
        tpNativeBanner.onDestroy();
    }

    /**
     * --------------------------------------------------------------------------------------------------------------
     * nativeBanner的基本用法，如果没有特殊需求，按照如下代码接入即可
     * --------------------------------------------------------------------------------------------------------------
     */


    private void loadNativeBanner() {
        // 也可以把TPNativeBanner写在xml中，findViewById的方式来初始化，这样就省略了addView操作
        tpNativeBanner = new TPNativeBanner(NativeBannerViewActivity.this);
        tpNativeBanner.closeAutoShow();
        tpNativeBanner.setAdListener(new BannerAdListener(){
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: " + tpAdInfo.adSourceName + "被点击了");
                Toast.makeText(NativeBannerViewActivity.this, "广告被点击了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");
                Toast.makeText(NativeBannerViewActivity.this, "广告加载完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: "+ tpAdInfo.adSourceName + "展示");
                Toast.makeText(NativeBannerViewActivity.this, "广告展示", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdInfo) {
                Log.i(TAG, "onAdLoadFailed:加载失败: code : "+ tpAdInfo.getErrorCode() + ", msg :" + tpAdInfo.getErrorMsg());
                Toast.makeText(NativeBannerViewActivity.this, "广告加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+ tpAdInfo.adSourceName + "被关闭");
            }

        });
    }



    /**
     * ==============================================================================================================
     *                                       以下是高级用法，一般情况下用不到
     * ==============================================================================================================
     */



    private void loadCustomNativeBanner() {
        tpNativeBanner = new TPNativeBanner(NativeBannerViewActivity.this);
        tpNativeBanner.setAdListener(new BannerAdListener(){
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: " + tpAdInfo.adSourceName + "被点击了");
            }

            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");
                // 如果在load前调用了closeAutoShow关闭了自动展示，那么在loaded后进行自渲染操作
//                renderAndShowNativeBanner();

                // v6.4.5以后才有
                // 或者在load前调用closeAutoShow关闭了自动展示，那么loaded后在需要的时候调用showAd
//                tpNativeBanner.showAd();
            }

            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: "+ tpAdInfo.adSourceName + "展示");
            }

            public void onAdLoadFailed(TPAdError tpAdInfo) {
                Log.i(TAG, "onAdLoadFailed:加载失败: code : "+ tpAdInfo.getErrorCode() + ", msg :" + tpAdInfo.getErrorMsg());
            }

            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+ tpAdInfo.adSourceName + "被关闭");
            }

        });

        // v6.4.5以后才有
//        tpNativeBanner.setAllAdLoadListener(new LoadAdEveryLayerListener() {
//            @Override
//            public void onAdAllLoaded(boolean b) {
//                // 所有广告层级都加载完成，b == true 加载到有可用的广告，b == false 没有加载到可用的广告
//            }
//
//            @Override
//            public void oneLayerLoadFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
//                // 每一层广告加载失败都会回调这个方法
//            }
//
//            @Override
//            public void oneLayerLoaded(TPAdInfo tpAdInfo) {
//                // 每一层广告加载成功都会回调这个方法
//            }
//        });

        adContainer.addView(tpNativeBanner);

        // 关闭自动展示，正常情况下加载完成会自动展示，可以调用closeAutoShow来关闭自动展示，关闭后需要开发者自行触发
//        tpNativeBanner.closeAutoShow();

        // 可以自定义渲染方法，如果不调用，TradPlusSdk内部会有一套默认的模板来渲染
        tpNativeBanner.setNativeAdRender(customAdRender);

        // v6.4.5以后才有
        // nativeBanner在收到系统的onDetachedFromWindow事件后，会自动进行资源释放操作，如果不想广告被回收，需要设置false，同时在需要的时候手动调用onDestroy
//        tpNativeBanner.setAutoDestroy(false);

        tpNativeBanner.loadAd(TestAdUnitId.NATIVEBANNER_ADUNITID);
    }

    private void renderAndShowNativeBanner() {

        // 获取三方的广告信息，也可以获取到模板渲染时候的view（这样获取出来以后  TradPlus就无法统计到广告loaded以后的所有事件，所以非特殊情况不要这样用）
        TPBaseAd tpBaseAd = tpNativeBanner.getBannerAd();
        Object obj = tpBaseAd.getNetworkObj(); // 获取三方的广告对象，可以强转成对应广告平台的对象(具体参考native中的示例)
        int type = tpBaseAd.getNativeAdType(); // 获取广告类型，模板类型，自渲染类型，list类型等，可以分别获取对应的信息
        if(type == TPBaseAd.AD_TYPE_NATIVE_EXPRESS) {
            View view = tpBaseAd.getRenderView();// 获取模板渲染时候三方广告平台渲染好的view
            tpNativeBanner.addView(view);
        } else if (type == TPBaseAd.AD_TYPE_NORMAL_NATIVE) {
            TPNativeAdView tpNativeAdView = tpBaseAd.getTPNativeView();// 获取自渲染时详细的素材信息，
            ViewGroup tempAdView = customAdRender.renderAdView(tpNativeAdView); // 自渲染出需要的view
            ViewGroup adCustomContainer = tpBaseAd.getCustomAdContainer(); // 获取三方广告平台的广告容器（部分三方平台会要求广告先add到平台的容器中，不这样做广告是不计费的）
            if(adCustomContainer != null) {
                // 如果三方广告平台有容器，需要把渲染好的view添加到三方广告平台的容器里，然后再加到tpNativeBanner中
                adCustomContainer.addView(tempAdView);
                tpNativeBanner.addView(adCustomContainer);
            } else {
                // 三方没有容器，可以直接把渲染好的view添加到tpNativeBanner中
                tpNativeBanner.addView(tempAdView);
            }
        }
    }

    private TPNativeAdRender customAdRender = new TPNativeAdRender() {
        // 获取广告布局文件，用自定义渲染的方式可以自己写布局的id
        @Override
        public ViewGroup createAdLayoutView() {
            LayoutInflater inflater = (LayoutInflater) NativeBannerViewActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return (ViewGroup) inflater.inflate(R.layout.tp_native_banner_ad_unit, null);
        }

        @Override
        public ViewGroup renderAdView(TPNativeAdView tpNativeAdView) {
            ViewGroup viewGroup = createAdLayoutView();

            ImageView iconView = viewGroup.findViewById(R.id.tp_native_icon_image);
            if(iconView != null) {
                if(tpNativeAdView.getIconImage() != null) {
                    iconView.setImageDrawable(tpNativeAdView.getIconImage());
                } else if(tpNativeAdView.getIconImageUrl() != null){
                    TPImageLoader.getInstance().loadImage(iconView, tpNativeAdView.getIconImageUrl());
                } else if (tpNativeAdView.getIconView() != null) {
                    ViewGroup.LayoutParams params = iconView.getLayoutParams();
                    ViewParent viewParent = iconView.getParent();
                    if (viewParent != null) {
                        int index = ((ViewGroup)viewParent).indexOfChild(iconView);
                        ((ViewGroup) viewParent).removeView(iconView);
                        tpNativeAdView.getIconView().setId(R.id.tp_native_icon_image);
                        ((ViewGroup) viewParent).addView(tpNativeAdView.getIconView(),index, params);

                    }
                }
            }

            TextView titleView = viewGroup.findViewById(R.id.tp_native_title);
            if(titleView != null && tpNativeAdView.getTitle() != null) {
                titleView.setText(tpNativeAdView.getTitle());
            }

            TextView subTitleView = viewGroup.findViewById(R.id.tp_native_text);
            if(subTitleView != null && tpNativeAdView.getSubTitle() != null) {
                subTitleView.setText(tpNativeAdView.getSubTitle());
            }

            Button callToActionView = viewGroup.findViewById(R.id.tp_native_cta_btn);
            if(callToActionView != null && tpNativeAdView.getCallToAction() != null) {
                callToActionView.setText(tpNativeAdView.getCallToAction());
            }

            // facebook会需要一个adchoice的容器来填充adchoice
            FrameLayout adChoiceView = viewGroup.findViewById(R.id.tp_ad_choices_container);

            // 把主要的元素设置给三方广告平台，第二个参数是是否可以点击
            setIconView(iconView, true);
            setTitleView(titleView, true);
            setSubTitleView(subTitleView, true);
            setCallToActionView(callToActionView, true);
            setAdChoicesContainer(adChoiceView, false);

            return viewGroup;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
                if (tpNativeBanner != null) {
                    nativeUtils.loadNativeBanner(tpNativeBanner,TestAdUnitId.NATIVEBANNER_ADUNITID);
                }
                break;
            case R.id.btn_show:
                if (nativeUtils.isReadyNativeBanner()) {
                    nativeUtils.showNativeBanner(adContainer);
                }else{
                    Toast.makeText(NativeBannerViewActivity.this, "无可用广告", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.second_page:
                // 进入下一页
                Intent intent = new Intent(NativeBannerViewActivity.this, SecondPage.class);
                intent.putExtra("type",TestAdUnitId.TYPE_NATIVEBANNER);
                startActivity(intent);
                break;
            case R.id.native_listview:
                // 进入下一页
                Intent intentlist = new Intent(NativeBannerViewActivity.this, NativeBannerRecycleViewActivity.class);
                startActivity(intentlist);
                break;
        }
    }
}
