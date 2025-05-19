package com.tradplus.demo.nativeads;

import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.widget.RelativeLayout.ALIGN_BOTTOM;
import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import static android.widget.RelativeLayout.ALIGN_PARENT_TOP;
import static android.widget.RelativeLayout.BELOW;
import static android.widget.RelativeLayout.ALIGN_PARENT_END;
import static android.widget.RelativeLayout.CENTER_IN_PARENT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.base.common.TPImageLoader;
import com.tradplus.ads.common.util.DeviceUtils;
import com.tradplus.ads.huawei.HuaweiNativeAd;
import com.tradplus.ads.mgr.nativead.TPCustomNativeAd;
import com.tradplus.ads.mgr.nativead.TPNativeAdRenderImpl;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.ads.open.nativead.TPNativeAdRender;
import com.tradplus.crosspro.ui.util.ViewUtil;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 标准原生广告，这个广告是可以由开发者控制大小，尽可能融入到app的内容中去，从而提升广告的点击和转化
 *
 * native广告分自渲染和模板渲染
 * native 自渲染广告是三方广告平台返回广告素材由开发者来拼接成对于的样式
 * native 模板渲染是三方广告平台返回渲染好的view（很多广告平台可以在对应的后台设置样式），开发者直接添加到一个容器就可以展示出来
 */
public class NativeActivity extends AppCompatActivity implements View.OnClickListener {

    private NativeUtils nativeUtils;
    private TPNative tpNative;
    private ViewGroup adContainer;
    private static final String TAG = "tradpluslog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        adContainer = findViewById(R.id.ad_container);
        nativeUtils = NativeUtils.getInstance();
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.second_page).setOnClickListener(this);
        findViewById(R.id.native_listview).setOnClickListener(this);
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
                Toast.makeText(NativeActivity.this, "广告加载完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: "+ tpAdInfo.adSourceName + "被点击");
                Toast.makeText(NativeActivity.this, "广告被点击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: "+ tpAdInfo.adSourceName + "展示");
                Toast.makeText(NativeActivity.this, "广告展示", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdShowFailed: "+ tpAdInfo.adSourceName + "展示失败");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdLoadFailed: 加载失败 , code : "+ tpAdError.getErrorCode() + ", msg :" + tpAdError.getErrorMsg());
                Toast.makeText(NativeActivity.this, "广告加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+ tpAdInfo.adSourceName + "广告关闭");
            }
        });
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
                // 自渲染的方式show广告 ---- 用于获取三方广告素材
                renderNativeAd();

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

        // 部分渠道不能在TradPlus后台设置宽高，可以在load前传入，尺寸dp
//        tpNative.setAdSize(300, 250);

        // 部分渠道有定制化参数，需要在这里用map的方式传递信息，
        // 例如：admob的native 角标可以自定义位置（key：Admob_Adchoices value：0-4），具体参考文档
        tpNative.setCustomParams(new HashMap<>());

        // 进入广告场景埋点，用来统计广告触达率，一般是进入某个页面开始调用
        tpNative.entryAdScenario("adSceneId");

        tpNative.setCustomParams(setLocalCustomParams());
        tpNative.loadAd();
    }

    private Map<String, Object> setLocalCustomParams() {
        HashMap<String, Object> mLocalExtras = new HashMap<>();
        // 华为dislike关闭 右下角显示
        mLocalExtras.put("huawei_close_position", 2);
        // 华为模板渲染  1 大图（默认） 2 小图 3 三小图
        // 自渲染类型开发者需要高级自定义的方式获取多图tpNativeAdView.getPicObject()，获取返回的图片自行添加到布局中
        mLocalExtras.put("huawei_native_template_type", 1);
        // 华为下载类广告下载控件点击直接安装
        // 自动下载 1 ，默认关闭 0
        // 模板渲染类型展示后直接使用，自渲染类型开发者需要高级自定义的方式实现获取tpNativeAdView.getAppDownloadButton(),判断不为空的时候替换cta按钮
        mLocalExtras.put("huawei_autoinstall", 0);
        return mLocalExtras;

    }

    private void showNativeAd() {
        tpNative.showAd(adContainer, new TPNativeAdRender() {

            @Override
            public ViewGroup createAdLayoutView() {

                LayoutInflater inflater = (LayoutInflater) NativeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup adLayout = (ViewGroup) inflater.inflate(R.layout.tp_native_ad_list_item, null);

                TextView nativeTitleView = adLayout.findViewById(R.id.tp_native_title);
                setTitleView(nativeTitleView, true);

                Button nativeSubTitleView = adLayout.findViewById(R.id.tp_native_text);
                setSubTitleView(nativeSubTitleView, true);

                TextView nativeCTAView = adLayout.findViewById(R.id.tp_native_cta_btn);
                setCallToActionView(nativeCTAView, true);

                ImageView nativeIconImageView = adLayout.findViewById(R.id.tp_native_icon_image);
                setIconView(nativeIconImageView, true);

                ImageView nativeMainImageView = adLayout.findViewById(R.id.tp_mopub_native_main_image);
                setImageView(nativeMainImageView, true);

                FrameLayout adChoiceView = adLayout.findViewById(R.id.tp_ad_choices_container);
                setAdChoicesContainer(adChoiceView, false);

                ImageView nativeAdChoice = adLayout.findViewById(R.id.tp_native_ad_choice);
                setAdChoiceView(nativeAdChoice, true);

                return adLayout;
            }
        },"");
    }

    // 用于获取三方广告素材
    private void renderNativeAd() {
        // 如果三方广告平台是模板渲染，那么TPNativeAdRender中的方法不会被回调，会直接拿到三方平台渲染好的view，add到adContainer中
        tpNative.showAd(adContainer, new TPNativeAdRender() {

            // 获取广告布局文件，用自定义渲染的方式可以自己写布局的id
            @Override
            public ViewGroup createAdLayoutView() {
                LayoutInflater inflater = (LayoutInflater) NativeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                return (ViewGroup) inflater.inflate(R.layout.tp_native_ad_list_item, null);
            }

            @Override
            public ViewGroup renderAdView(TPNativeAdView tpNativeAdView) {
                ViewGroup viewGroup = createAdLayoutView();

                ImageView imageView = viewGroup.findViewById(R.id.tp_mopub_native_main_image);
                if(imageView != null) {
                    if(tpNativeAdView.getMediaView() != null) {
                        Log.i(TAG, "renderAdView getMediaView: ");
                        // 如果三方广告平台有mediaview，需要用三方提供的mediaview来替换原来布局中的imageview
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        ViewParent viewParent = imageView.getParent();
                        if(viewParent != null) {
                            ((ViewGroup)viewParent).removeView(imageView);
                            ((ViewGroup)viewParent).addView(tpNativeAdView.getMediaView(), params);
                            getClickViews().add(tpNativeAdView.getMediaView());
                        }
                    } else if(tpNativeAdView.getMainImage() != null) {
                        // 部分三方平台返回的是drawable，可以直接设置
                        imageView.setImageDrawable(tpNativeAdView.getMainImage());
                    } else if(tpNativeAdView.getMainImageUrl() != null) {
                        // 其他三方平台返回的是图片的url，需要先下载图片再填充到view中
                        TPImageLoader.getInstance().loadImage(imageView, tpNativeAdView.getMainImageUrl());
                    }
                }

                ImageView iconView = viewGroup.findViewById(R.id.tp_native_icon_image);
                if(iconView != null) {
                    if(tpNativeAdView.getIconImage() != null) {
                        iconView.setImageDrawable(tpNativeAdView.getIconImage());
                    } else if(tpNativeAdView.getIconImageUrl() != null){
                        TPImageLoader.getInstance().loadImage(iconView, tpNativeAdView.getIconImageUrl());
                    } else if (tpNativeAdView.getIconView() != null) {
                        ViewGroup.LayoutParams params = iconView.getLayoutParams();
                        ViewParent viewParent = iconView.getParent();
                        iconView = (ImageView)tpNativeAdView.getIconView();
                        if (viewParent != null) {
                            int index = ((ViewGroup)viewParent).indexOfChild(iconView);
                            ((ViewGroup) viewParent).removeView(iconView);
                            ((ViewGroup) viewParent).addView(iconView,index, params);

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

                FrameLayout adChoiceView = viewGroup.findViewById(R.id.tp_ad_choices_container);

                setImageView(imageView, true);
                setIconView(iconView, true);
                setTitleView(titleView, true);
                setSubTitleView(subTitleView, true);
                setCallToActionView(callToActionView, true);
                setAdChoicesContainer(adChoiceView, true);

                return viewGroup;
            }
        }, "adSceneId");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
                if (tpNative != null) {
                    nativeUtils.loadNative(tpNative);
                }
                break;
            case R.id.btn_show:
                if (nativeUtils.isReady()) {
                    nativeUtils.showNative(adContainer);
                }else{
                    Toast.makeText(NativeActivity.this, "无可用广告", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.second_page:
                // 进入下一页
                Intent intent = new Intent(NativeActivity.this, SecondPage.class);
                intent.putExtra("type",TestAdUnitId.TYPE_NATIVE);
                startActivity(intent);
                break;
            case R.id.native_listview:
                // 进入下一页
                Intent intentlist = new Intent(NativeActivity.this, NativeRecycleViewActivity.class);
                startActivity(intentlist);
                break;
        }
    }
}
