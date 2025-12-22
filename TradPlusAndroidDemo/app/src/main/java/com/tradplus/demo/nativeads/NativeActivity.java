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
        tpNative.setCustomParams(setLocalCustomParams());
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
