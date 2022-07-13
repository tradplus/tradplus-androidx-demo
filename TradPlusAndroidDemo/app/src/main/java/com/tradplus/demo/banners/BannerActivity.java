package com.tradplus.demo.banners;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.banner.TPBanner;


import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

/**
 * banner广告
 * banner广告的TPBanner本身是一个view，需要开发者创建后添加到指定位置
 * 广告loaded成功后，TradPlus SDK会自动的把广告内容填充到TPBanner中
 * banner自带有刷新功能，在TradPlus后台配置刷新时间，一次loaded后，间隔固定的时间SDK内部会自动触发下一次load并在loaded成功后替换内容
 */
public class BannerActivity extends AppCompatActivity {

    private TPBanner tpBanner;
    private ViewGroup adContainer;
    private static final String TAG = "tradpluslog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        adContainer = findViewById(R.id.ad_container);

        loadBanner();
    }


    /**
     * --------------------------------------------------------------------------------------------------------------
     * banner的基本用法，如果没有特殊需求，按照如下代码接入即可
     * --------------------------------------------------------------------------------------------------------------
     */

    private void loadBanner() {
        // new TPBanner，也可以把TPBanner写在开发者的xml中，这里改成findViewById
        tpBanner = new TPBanner(this);
        tpBanner.setAdListener(new BannerAdListener() {
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: " + tpAdInfo.adSourceName + "被点击了");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: " + tpAdInfo.adSourceName  + "展示了");
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: " + tpAdInfo.adSourceName + "加载成功");
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                Log.i(TAG, "onAdLoadFailed: 加载失败，code :" + error.getErrorCode() + ", msg : " + error.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: "+tpAdInfo.adSourceName + "广告关闭");
            }
        });
        adContainer.addView(tpBanner);
        tpBanner.loadAd(TestAdUnitId.BANNER_ADUNITID);
    }






    /**
     * ==============================================================================================================
     *                                       以下是高级用法，一般情况下用不到
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
                Log.i(TAG, "onAdImpression: " + tpAdInfo.adSourceName  + "展示了");
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
                Log.i(TAG, "onAdClosed: "+tpAdInfo.adSourceName + "广告关闭");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tpBanner != null){
            tpBanner.onDestroy();
        }
    }

}
