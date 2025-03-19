package com.tradplus.demo.interstititals;

import static android.view.Gravity.CENTER;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.ads.AppDownloadButton;
import com.huawei.hms.ads.AppDownloadButtonStyle;
import com.huawei.hms.ads.AppDownloadStatus;
import com.tradplus.ads.base.adapter.nativead.TPNativeAdView;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.common.TPImageLoader;
import com.tradplus.ads.base.util.SegmentUtils;
import com.tradplus.ads.common.util.DeviceUtils;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;
import com.tradplus.ads.open.nativead.TPNativeAdRender;
import com.tradplus.demo.R;
import com.tradplus.demo.banners.SecondPageActivity;
import com.tradplus.demo.rewarded.RewardedVideoActivity;
import com.tradplus.demo.rewarded.VideoUtils;
import com.tradplus.utils.TestAdUnitId;

import java.util.HashMap;


/**
 * 插屏广告
 * 插屏广告一般是全屏的，调用时机是在页面切换时，一般有图片和视频两种，部分渠道会有定制化的插屏，具体参考文档
 * 插屏广告是三方广告平台提供的activity，一般不支持做定制或者修改
 * 插屏广告一般需要预加载，在展示机会到来时判断isReady是否准备好，准备好后可以调show
 *
 * 自动加载功能是TradPlus独有的针对部分需要频繁展示广告的场景做的自动补充和过期重新加载的功能，推荐在广告场景触发较多的场景下使用
 * 自动加载功能只需要初始化一次，后续在广告场景到来的时候判断isReady然后show广告即可，不需要额外的调用load
 */
public class InterstitialActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoUtils videoUtils;
    TPInterstitial mTPInterstitial;
    private static final String TAG = "tradpluslog";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        videoUtils = VideoUtils.getInstance();
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.second_page).setOnClickListener(this);
    }


    private void initInterstitialAd() {
        if (mTPInterstitial == null) {
            mTPInterstitial = new TPInterstitial(this, "788E1FCB278B0D7E97282231154458B7");

            //进入广告场景，广告场景ID后台创建
            // 广告场景是用来统计进入广告场景的次数和进入场景后展示广告的次数，所以请在准确的位置调用
            mTPInterstitial.entryAdScenario(TestAdUnitId.ENTRY_AD_INTERSTITIAL);

            // 流量分组的时候用到，可以自定义一些app相关的属性，在TradPlus后台根据这些属性来对用户做分组
            // 设置流量分组有两个维度，一个是全局的，一个是单个广告位的，单个广告位的相同属性会覆盖全局的
            HashMap<String, String> customMap = new HashMap<>();
            customMap.put("user_gender", "male");//男性
            customMap.put("user_level", "10");//游戏等级10
//        SegmentUtils.initCustomMap(customMap);//设置APP维度的规则，对全部placement有效
            SegmentUtils.initPlacementCustomMap(TestAdUnitId.ENTRY_AD_INTERSTITIAL, customMap);//仅对该广告位有效，会覆盖APP维度设置的规则

            // 监听广告的不同状态
            mTPInterstitial.setAdListener(new InterstitialAdListener() {
                @Override
                public void onAdLoaded(TPAdInfo tpAdInfo) {
                    Log.i(TAG, "onAdLoaded: ");
                    Toast.makeText(InterstitialActivity.this, "广告加载成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdClicked(TPAdInfo tpAdInfo) {
                    Log.i(TAG, "onAdClicked: 广告" + tpAdInfo.adSourceName + "被点击");
                    Toast.makeText(InterstitialActivity.this, "广告被点击", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdImpression(TPAdInfo tpAdInfo) {
                    Log.i(TAG, "onAdImpression: 广告" + tpAdInfo.adSourceName + "展示");
                    Toast.makeText(InterstitialActivity.this, "广告被展示", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onAdFailed(TPAdError tpAdError) {
                    Log.i(TAG, "onAdFailed: ");
                    Toast.makeText(InterstitialActivity.this, "广告加载失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdClosed(TPAdInfo tpAdInfo) {
                    Log.i(TAG, "onAdClosed: 广告" + tpAdInfo.adSourceName + "被关闭");
                    Toast.makeText(InterstitialActivity.this, "广告加载关闭", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                    Log.i(TAG, "onAdClosed: 广告" + tpAdInfo.adSourceName + "展示失败");
                }

                @Override
                public void onAdVideoStart(TPAdInfo tpAdInfo) {
                    // V8.1.0.1 播放开始
                }

                @Override
                public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                    // V8.1.0.1 播放结束
                }
            });

            // 监听每一层广告的加载情况，非特殊需求可以不实现
            mTPInterstitial.setAllAdLoadListener(new LoadAdEveryLayerListener() {
                @Override
                public void onAdAllLoaded(boolean b) {
                    Log.i(TAG, "onAdAllLoaded: 该广告位下所有广告加载结束，是否有广告加载成功 ：" + b);

                }

                @Override
                public void oneLayerLoadFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                    Log.i(TAG, "oneLayerLoadFailed:  广告" + tpAdInfo.adSourceName + " 加载失败，code :: " +
                            tpAdError.getErrorCode() + " , Msg :: " + tpAdError.getErrorMsg());
                }

                @Override
                public void oneLayerLoaded(TPAdInfo tpAdInfo) {
                    Log.i(TAG, "oneLayerLoaded:  广告" + tpAdInfo.adSourceName + " 加载成功");
                }

                @Override
                public void onAdStartLoad(String s) {
                    // 每次调用load方法时返回的回调，包含自动加载等触发时机。V7.9.0 新增。
                }

                @Override
                public void oneLayerLoadStart(TPAdInfo tpAdInfo) {
                    // 每层waterfall 向三方广告源发起请求前，触发的回调。V7.9.0 新增。
                }

                @Override
                public void onBiddingStart(TPAdInfo tpAdInfo) {

                }

                @Override
                public void onBiddingEnd(TPAdInfo tpAdInfo, TPAdError tpAdError) {

                }

                @Override
                public void onAdIsLoading(String s) {
                    // 调用load之后如果收到此回调，说明广告位仍处于加载状态，无法触发新的一轮广告加载。V 9.0.0.1新增
                }

            });
        }

        // 华为下载类按钮
        HashMap<String, Object> mLocalExtras = new HashMap<>();
        mLocalExtras.put("huawei_autoinstall", 1);
        mTPInterstitial.setCustomParams(mLocalExtras);

        videoUtils.loadInterstitial(mTPInterstitial);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
                // 初始化广告
                initInterstitialAd();
                break;
            case R.id.btn_show:
//                if (videoUtils.isReadyInterstitial()) {
//                    videoUtils.showInterstitial(InterstitialActivity.this);
//                }else {
//                    Toast.makeText(InterstitialActivity.this, "无可用广告 or 已经展示", Toast.LENGTH_SHORT).show();
//                }

                customNativeAdRender();
                break;
            case R.id.second_page:
                // 进入下一页
                Intent intent = new Intent(InterstitialActivity.this, SecondPageActivity.class);
                intent.putExtra("type",TestAdUnitId.TYPE_INTERSTITIAL);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mTPInterstitial != null){
            mTPInterstitial.onDestroy();
        }
    }

    // 原生拼插屏，自定义布局样式 不设置setCustomNativeAdRender 使用TP内置布局
    // 使用前建议根据屏幕方向适配两套布局，
    public void customNativeAdRender() {
        if (mTPInterstitial != null) {
            mTPInterstitial.setCustomNativeAdRender(new TPNativeAdRender() {
                @Override
                public ViewGroup createAdLayoutView() {
                    LayoutInflater inflater = (LayoutInflater) InterstitialActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    // tp_native_inter_ad 为开发者自定义布局样式
                    return (ViewGroup) inflater.inflate(R.layout.tp_native_inter_ad, null);
                }

                @Override
                public ViewGroup renderAdView(TPNativeAdView tpNativeAdView) {
                    ViewGroup viewGroup = createAdLayoutView();
                    // 大图
                    ImageView imageView = viewGroup.findViewById(R.id.tp_mopub_native_main_image);
                    if (imageView != null) {
                        if (tpNativeAdView.getMediaView() != null) {
                            ViewGroup.LayoutParams params = imageView.getLayoutParams();
                            ViewParent viewParent = imageView.getParent();
                            if (viewParent != null) {
                                ((ViewGroup) viewParent).removeView(imageView);
                                ((ViewGroup) viewParent).addView(tpNativeAdView.getMediaView(), params);
                                getClickViews().add(tpNativeAdView.getMediaView());
                            }
                        } else if (tpNativeAdView.getMainImage() != null) {
                            imageView.setImageDrawable(tpNativeAdView.getMainImage());
                        } else if (tpNativeAdView.getMainImageUrl() != null) {
                            TPImageLoader.getInstance().loadImage(imageView, tpNativeAdView.getMainImageUrl());
                        }
                    }

                    // icon
                    ImageView iconView = viewGroup.findViewById(R.id.tp_native_icon_image);
                    if (iconView != null) {
                        if (tpNativeAdView.getIconImage() != null) {
                            iconView.setImageDrawable(tpNativeAdView.getIconImage());
                        } else if (tpNativeAdView.getIconImageUrl() != null) {
                            TPImageLoader.getInstance().loadImage(iconView, tpNativeAdView.getIconImageUrl());
                        } else if (tpNativeAdView.getIconView() != null) {
                            ViewGroup.LayoutParams params = iconView.getLayoutParams();
                            ViewParent viewParent = iconView.getParent();
                            iconView = (ImageView) tpNativeAdView.getIconView();
                            if (viewParent != null) {
                                int index = ((ViewGroup) viewParent).indexOfChild(iconView);
                                ((ViewGroup) viewParent).removeView(iconView);
                                iconView.setId(com.tradplus.demo.R.id.tp_native_icon_image);
                                ((ViewGroup) viewParent).addView(iconView, index, params);

                            }
                        }
                    }

                    // 主标题
                    TextView titleView = viewGroup.findViewById(R.id.tp_native_title);
                    if (titleView != null && tpNativeAdView.getTitle() != null) {
                        Log.i(TAG, "renderAdView Title: " + tpNativeAdView.getTitle());
                        titleView.setText(tpNativeAdView.getTitle());
                    }

                    // 副标题
                    TextView subTitleView = viewGroup.findViewById(R.id.tp_native_text);
                    if (subTitleView != null && tpNativeAdView.getSubTitle() != null) {
                        Log.i(TAG, "renderAdView SubTitle: " + tpNativeAdView.getSubTitle());
                        subTitleView.setText(tpNativeAdView.getSubTitle());
                    }

                    // 点击按钮
                    TextView callToActionView = viewGroup.findViewById(R.id.tp_native_cta_btn);
                    if (callToActionView != null && tpNativeAdView.getCallToAction() != null) {
                        callToActionView.setText(tpNativeAdView.getCallToAction());
                    }

                    // 华为下载按钮
                    AppDownloadButton appDownloadButton = (AppDownloadButton) tpNativeAdView.getAppDownloadButton();
                    if (appDownloadButton != null) {
                        // 设置字体大小
                        appDownloadButton.setTextSize(DeviceUtils.dip2px(InterstitialActivity.this, 16));
                        // 设置按钮大小
                        appDownloadButton.setPadding(DeviceUtils.dip2px(InterstitialActivity.this, 100), DeviceUtils.dip2px(InterstitialActivity.this, 15), DeviceUtils.dip2px(InterstitialActivity.this, 100), DeviceUtils.dip2px(InterstitialActivity.this, 15));
                        // 设置应用下载按钮样式
                        appDownloadButton.setAppDownloadButtonStyle(new MyAppDownloadStyle(InterstitialActivity.this));
                        // 设置应用下载安装状态变更监听器
                        appDownloadButton.setOnDownloadStatusChangedListener(new AppDownloadButton.OnDownloadStatusChangedListener() {
                            @Override
                            public void onStatusChanged(AppDownloadStatus appDownloadStatus) {
                                // 下载安装状态变更
                                Log.i(TAG, "====onStatusChanged: " + appDownloadStatus.name());
                            }

                            @Override
                            public void onUserCancel(String packageName, String uniqueId) {
                                // 用户主动取消下载回调
                                Log.i(TAG, "====onUserCancel: ");
                            }
                        });
                        // 隐藏点击按钮
                        callToActionView.setVisibility(View.INVISIBLE);

                        // 设置下载按钮位置 可以自己调整
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DeviceUtils.dip2px(InterstitialActivity.this, 320), DeviceUtils.dip2px(InterstitialActivity.this, 50));
                        layoutParams.gravity = CENTER;
                        layoutParams.bottomMargin = DeviceUtils.dip2px(InterstitialActivity.this, 15);
                        ViewGroup lastView = (ViewGroup) viewGroup.getChildAt(viewGroup.getChildCount() - 1);
                        lastView.addView(appDownloadButton, layoutParams);
                    } else {
                        setCallToActionView(callToActionView, true);
                    }


                    // adchoice
                    FrameLayout adChoiceView = viewGroup.findViewById(R.id.tp_ad_choices_container);

                    // 把主要的元素设置给三方广告平台，第二个参数是是否可以点击
                    setImageView(imageView, true);
                    setIconView(iconView, true);
                    setTitleView(titleView, true);
                    setSubTitleView(subTitleView, true);
                    setImageView(imageView, true);
                    setAdChoicesContainer(adChoiceView, true);

                    return viewGroup;
                }
            });
            mTPInterstitial.showAd(InterstitialActivity.this, "");
        }
    }

    public class MyAppDownloadStyle extends AppDownloadButtonStyle {

        public MyAppDownloadStyle(Context context) {
            super(context);
            installingStyle.setTextSize(DeviceUtils.dip2px(context, 12));
            normalStyle.setTextSize(DeviceUtils.dip2px(context, 12));
            processingStyle.setTextSize(DeviceUtils.dip2px(context, 12));
            normalStyle.setTextColor(context.getResources().getColor(com.tradplus.ads.huawei.R.color.white));
            normalStyle.setBackground(context.getResources().getDrawable(com.tradplus.ads.huawei.R.drawable.tp_native_button_rounded_corners_shape));
            processingStyle.setTextColor(context.getResources().getColor(com.tradplus.ads.huawei.R.color.black));

        }
    }
}
