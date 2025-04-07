package com.tradplus.demo.banners;

import static android.view.Gravity.CENTER;

import static com.tradplus.utils.TestAdUnitId.HUAWEI_BANNER_DOWNLOAD;

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
import com.tradplus.ads.common.util.DeviceUtils;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.banner.TPBanner;


import com.tradplus.ads.open.nativead.TPNativeAdRender;
import com.tradplus.demo.R;
import com.tradplus.demo.nativeads.HuaweiDownLoad;
import com.tradplus.utils.TestAdUnitId;

import java.util.HashMap;

/**
 * banner广告
 * banner广告的TPBanner本身是一个view，需要开发者创建后添加到指定位置
 * 广告loaded成功后，TradPlus SDK会自动的把广告内容填充到TPBanner中
 * banner自带有刷新功能，在TradPlus后台配置刷新时间，一次loaded后，间隔固定的时间SDK内部会自动触发下一次load并在loaded成功后替换内容
 */
public class BannerActivity extends AppCompatActivity implements View.OnClickListener {

    private BannerUtils bannerUtils;
    private TPBanner tpBanner;
    private ViewGroup adContainer;
    private static final String TAG = "tradpluslog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        adContainer = findViewById(R.id.ad_container);
        bannerUtils = BannerUtils.getInstance();
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.second_page).setOnClickListener(this);
        loadBanner();
//        loadHuaweiDownNativeBanner();
    }


    /**
     * --------------------------------------------------------------------------------------------------------------
     * banner的基本用法，如果没有特殊需求，按照如下代码接入即可
     * --------------------------------------------------------------------------------------------------------------
     */

    private void loadBanner() {
        tpBanner = new TPBanner(this);
        tpBanner.closeAutoShow();
        tpBanner.setAdListener(new BannerAdListener() {
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Toast.makeText(BannerActivity.this, "广告被点击了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Toast.makeText(BannerActivity.this, "广告展示", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Toast.makeText(BannerActivity.this, "广告加载完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                Toast.makeText(BannerActivity.this, "广告加载失败", Toast.LENGTH_SHORT).show();
                findViewById(R.id.btn_show).setClickable(false);
                findViewById(R.id.second_page).setClickable(false);
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: " + tpAdInfo.adSourceName + "广告关闭");
            }
        });

    }


    /**
     * ==============================================================================================================
     * 以下是高级用法，一般情况下用不到
     * ==============================================================================================================
     */


    private void loadCustomBanner() {
        tpBanner = new TPBanner(this);
        tpBanner.setAdListener(new BannerAdListener() {
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: " + tpAdInfo.adSourceName + "被点击了");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: " + tpAdInfo.adSourceName + "展示了");
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");

                // 获取baseAd，可以拿到三方广告平台原始的view，开发者自己做展示的逻辑处理,
                // 这种方式TradPlus会检测不到后续展示的事件，非特殊需求不要这样做
//                TPBaseAd tpBaseAd = tpBanner.getBannerAd();
//                View view = tpBaseAd.getRenderView();
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                Log.i(TAG, "onAdLoadFailed: 加载失败，code :" + error.getErrorCode() + ", msg : " + error.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: " + tpAdInfo.adSourceName + "广告关闭");
            }
        });

        // v6.4.5以后才有
//        tpBanner.setAllAdLoadListener(new LoadAdEveryLayerListener() {
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

        // 部分三方广告源有特殊自定义，需要用这个接口来传一些信息给三方广告平台，具体参考文档
//        HashMap<String, Object> custom = new HashMap<>();
//        tpBanner.setCustomParams(custom);

        adContainer.addView(tpBanner);
        tpBanner.loadAd(TestAdUnitId.BANNER_ADUNITID);

        // V6.4.5才有，设置不自动释放，默认是true，如果特殊场景需要保存TPBanner继续使用，可以设置false
        // 在TPBanner被remove后会释放广告资源（TPBanner的onDetachedFromWindow中会是否资源）
//        tpBanner.setAutoDestroy(false);

        // 手动释放广告，跟上面的setAutoDestroy配合使用
//        tpBanner.onDestroy();

        // 设置广告的展示和隐藏，这个会决定广告的刷新（当TPBanner监听到onWindowVisibilityChanged事件，会选择暂停或者继续自动刷新的逻辑）
//        tpBanner.setVisibility(View.GONE);
    }
    
    
    private void loadHuaweiDownNativeBanner() {
        TPBanner tpBanner = new TPBanner(this);
        HashMap<String, Object> mLocalExtras = new HashMap<>();
        // 华为下载类广告下载控件点击直接安装
        // 自动下载 1 ，默认关闭 0
        mLocalExtras.put("huawei_autoinstall", 1);
        tpBanner.setCustomParams(mLocalExtras);
        tpBanner.setNativeAdRender(new TPNativeAdRender() {
            @Override
            public ViewGroup createAdLayoutView() {
                LayoutInflater inflater = (LayoutInflater) BannerActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // 该布局为 原生拼成开屏
                // 如果需要其他样式 根据需求自己调整UI
                return (ViewGroup) inflater.inflate(R.layout.tp_native_banner_ad_unit, null);
            }

            @Override
            public ViewGroup renderAdView(TPNativeAdView tpNativeAdView) {
                ViewGroup viewGroup = createAdLayoutView();

                // 大图
                ImageView imageView = viewGroup.findViewById(com.tradplus.demo.R.id.tp_mopub_native_main_image);
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
                ImageView iconView = viewGroup.findViewById(com.tradplus.demo.R.id.tp_native_icon_image);
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
                            ((ViewGroup) viewParent).addView(iconView, index, params);

                        }
                    }
                }

                // 主标题
                TextView titleView = viewGroup.findViewById(com.tradplus.demo.R.id.tp_native_title);
                if (titleView != null && tpNativeAdView.getTitle() != null) {
                    titleView.setText(tpNativeAdView.getTitle());
                }

                // 副标题
                TextView subTitleView = viewGroup.findViewById(com.tradplus.demo.R.id.tp_native_text);
                if (subTitleView != null && tpNativeAdView.getSubTitle() != null) {
                    subTitleView.setText(tpNativeAdView.getSubTitle());
                }

                // 点击按钮
                Button callToActionView = viewGroup.findViewById(R.id.tp_native_cta_btn);
                if (callToActionView != null && tpNativeAdView.getCallToAction() != null) {
                    callToActionView.setText(tpNativeAdView.getCallToAction());
                }

                // 华为下载按钮
                AppDownloadButton appDownloadButton = (AppDownloadButton) tpNativeAdView.getAppDownloadButton();
                if (appDownloadButton != null) {
                    // 设置字体大小
                    appDownloadButton.setTextSize(DeviceUtils.dip2px(BannerActivity.this, 16));
                    // 设置按钮大小
                    appDownloadButton.setPadding(DeviceUtils.dip2px(BannerActivity.this, 50), DeviceUtils.dip2px(BannerActivity.this, 15), DeviceUtils.dip2px(BannerActivity.this, 50), DeviceUtils.dip2px(BannerActivity.this, 15));
                    // 设置应用下载按钮样式
                    appDownloadButton.setAppDownloadButtonStyle(new MyAppDownloadStyle(BannerActivity.this));
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


                    // 设置下载按钮位置
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = CENTER;
                    layoutParams.bottomMargin = DeviceUtils.dip2px(BannerActivity.this, 1);
                    ViewGroup lastView = (ViewGroup) viewGroup.getChildAt(viewGroup.getChildCount() - 1);
                    lastView.addView(appDownloadButton, layoutParams);

                    // 隐藏点击按钮
                    callToActionView.setVisibility(View.INVISIBLE);
                } else {
                    setCallToActionView(callToActionView, true);
                }
                Log.i(TAG, "===== renderAdView appDownloadButton : " + appDownloadButton);

                FrameLayout adChoiceView = viewGroup.findViewById(com.tradplus.demo.R.id.tp_ad_choices_container);

                setImageView(imageView, true);
                setIconView(iconView, true);
                setTitleView(titleView, true);
                setSubTitleView(subTitleView, true);

                setAdChoicesContainer(adChoiceView, false);

                return viewGroup;
            }
        });
        adContainer.addView(tpBanner);
        tpBanner.loadAd(HUAWEI_BANNER_DOWNLOAD);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
                if (tpBanner != null) {
                    bannerUtils.loadBanner(tpBanner, TestAdUnitId.BANNER_ADUNITID);
                }
                break;
            case R.id.btn_show:
                if (bannerUtils.isReady()) {
                    bannerUtils.showBanner(adContainer);
                }else {
                    Toast.makeText(BannerActivity.this, "无可用广告 or 已经展示", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.second_page:
                // 进入下一页
                Intent intent = new Intent(BannerActivity.this, SecondPageActivity.class);
                intent.putExtra("type",TestAdUnitId.TYPE_BANNER);
                startActivity(intent);
                break;
        }
    }
}
