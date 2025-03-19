package com.tradplus.demo.nativeads;

import static android.view.Gravity.CENTER;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.ads.AppDownloadButton;
import com.huawei.hms.ads.AppDownloadButtonStyle;
import com.huawei.hms.ads.AppDownloadStatus;
import com.tradplus.ads.base.adapter.nativead.TPNativeAdView;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.base.common.TPImageLoader;
import com.tradplus.ads.common.util.DeviceUtils;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.ads.open.nativead.TPNativeAdRender;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

import java.util.HashMap;

/**
 * AppDownloadButton API Doc ：
 * https://developer.huawei.com/consumer/cn/doc/HMSCore-References/appdownloadbutton-0000001139541279
 */
public class HuaweiDownLoad extends AppCompatActivity {

    private TPNative tpNative;
    private final static String TAG = "HuaweiDownLoad";
    private FrameLayout adContainer;
    private Context mContext = HuaweiDownLoad.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huaweidownload_native);

        adContainer = findViewById(R.id.ad_container);

        tpNative = new TPNative(HuaweiDownLoad.this, TestAdUnitId.NATIVE_ADUNITID);
        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");

                // 华为 TradPlus后台配置为 开发者自渲染
                loadHuaweiNormal();

                // 华为 TradPlus后台配置为 模板渲染
//                loadHuaweiExpress();

            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: " + tpAdInfo.adSourceName + "被点击");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: " + tpAdInfo.adSourceName + "展示");
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdShowFailed: " + tpAdInfo.adSourceName + "展示失败");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdLoadFailed: 加载失败 , code : " + tpAdError.getErrorCode() + ", msg :" + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: " + tpAdInfo.adSourceName + "广告关闭");
            }
        });

        HashMap<String, Object> mLocalExtras = new HashMap<>();
        // 华为dislike关闭 右边上角显示
        mLocalExtras.put("huawei_close_position", 1);

        // TradPlus后台配置为 模板渲染
        // 华为模板渲染  1 大图（默认） 2 小图 3 三小图
        // 自渲染类型开发者需要高级自定义的方式获取多图tpNativeAdView.getPicObject()，获取返回的图片自行添加到布局中
//        mLocalExtras.put("huawei_native_template_type", 1);


        // 华为下载类广告下载控件点击直接安装
        // 自动下载 1 ，默认关闭 0
        mLocalExtras.put("huawei_autoinstall", 1);
        tpNative.setCustomParams(mLocalExtras);

        tpNative.loadAd();

    }

    private void loadHuaweiNormal() {
        tpNative.showAd(adContainer, new TPNativeAdRender() {
            @Override
            public ViewGroup createAdLayoutView() {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // 该布局为 原生拼成开屏
                // 如果需要其他样式 根据需求自己调整UI
                return (ViewGroup) inflater.inflate(R.layout.tp_native_splash_ad, null);
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
                TextView callToActionView = viewGroup.findViewById(R.id.tp_native_cta_btn);
                if (callToActionView != null && tpNativeAdView.getCallToAction() != null) {
                    callToActionView.setText(tpNativeAdView.getCallToAction());
                }

                // 华为下载按钮
                AppDownloadButton appDownloadButton = (AppDownloadButton) tpNativeAdView.getAppDownloadButton();
                if (appDownloadButton != null) {
                    // 设置字体大小
                    appDownloadButton.setTextSize(DeviceUtils.dip2px(mContext, 16));
                    // 设置按钮大小
                    appDownloadButton.setPadding(DeviceUtils.dip2px(mContext, 100), DeviceUtils.dip2px(mContext, 15), DeviceUtils.dip2px(mContext, 100), DeviceUtils.dip2px(mContext, 15));
                    // 设置应用下载按钮样式
                    appDownloadButton.setAppDownloadButtonStyle(new MyAppDownloadStyle(mContext));
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
                    layoutParams.bottomMargin = DeviceUtils.dip2px(mContext, 15);
                    ViewGroup lastView = (ViewGroup) viewGroup.getChildAt(viewGroup.getChildCount() - 1);
                    lastView.addView(appDownloadButton, layoutParams);

                    // 隐藏点击按钮
                    callToActionView.setVisibility(View.INVISIBLE);
                } else {
                    setCallToActionView(callToActionView, true);
                }
                Log.i(TAG, "===== renderAdView appDownloadButton : " + appDownloadButton);

                // facebook会需要一个adchoice的容器来填充adchoice
                FrameLayout adChoiceView = viewGroup.findViewById(com.tradplus.demo.R.id.tp_ad_choices_container);

                // 把主要的元素设置给三方广告平台，第二个参数是是否可以点击
                setImageView(imageView, true);
                setIconView(iconView, true);
                setTitleView(titleView, true);
                setSubTitleView(subTitleView, true);

                setAdChoicesContainer(adChoiceView, false);

                return viewGroup;
            }
        }, "adSceneId");
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

    private void loadHuaweiExpress() {
        tpNative.showAd(adContainer, R.layout.tp_native_ad_list_item, "adSceneId");
    }

}
