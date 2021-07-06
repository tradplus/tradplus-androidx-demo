package com.tradplus.demo.interstititals;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.mobileads.TradPlusErrorCode;
import com.tradplus.ads.mobileads.TradPlusInterstitial;
import com.tradplus.ads.mobileads.TradPlusInterstitialExt;
import com.tradplus.ads.mobileads.util.SegmentUtils;
import com.tradplus.ads.network.CanLoadListener;
import com.tradplus.ads.network.OnAllInterstatitialLoadedStatusListener;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;
import com.tradplus.ads.open.reward.RewardAdListener;
import com.tradplus.ads.open.reward.TPReward;
import com.tradplus.demo.R;
import com.tradplus.demo.rewarded.RewardedVideoActivity;
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
public class InterstitialActivity extends AppCompatActivity  {

    Button interstitial_show,  interstitial_load;
    TextView tv;
    TPInterstitial mTPInterstitial;
    private static final String TAG = "tradpluslog";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        tv = findViewById(R.id.tv);
        interstitial_load = (Button)findViewById(R.id.load);
        interstitial_show = (Button)findViewById(R.id.show);

        // load按钮
        interstitial_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTPInterstitial.loadAd();
            }
        });

        // show按钮
        interstitial_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有可用广告
                if (!mTPInterstitial.isReady()) {
                    // 无可用的广告，如果开启自动加载，可以什么都不做，如果没开启自动加载，可以在这里调用一次load（注意不要频繁触发）
                    Log.i(TAG, "isReady: 无可用广告");
                    tv.setText("isReady: 无可用广告");
                }else{
                    //展示
                    mTPInterstitial.showAd(InterstitialActivity.this, TestAdUnitId.ENTRY_AD_INTERSTITIAL);
                    Log.i(TAG, "showAd: 展示");
                }
            }
        });

        // 初始化广告
        initInterstitialAd();
    }

    /**
     * 初始化广告位
     * 如果要开启自动加载，初始化广告位的时机要尽可能提前，这样才能保住在进入广告场景后有可用的广告
     * 如果不开启自动加载，那么初始化广告位后，在合适的时机来调用load
     */
    private void initInterstitialAd() {
        /*
         * 1、参数2：广告位
         *
         * 2、参数3：自动reload模式，true 开启 ，false 关闭（详细请参考接入文档或者类和方法的注释）
         */
        mTPInterstitial = new TPInterstitial(this,TestAdUnitId.INTERSTITIAL_ADUNITID,true);

        //进入广告场景，广告场景ID后台创建
        // 广告场景是用来统计进入广告场景的次数和进入场景后展示广告的次数，所以请在准确的位置调用
        mTPInterstitial.entryAdScenario(TestAdUnitId.ENTRY_AD_INTERSTITIAL);

        // 流量分组的时候用到，可以自定义一些app相关的属性，在TradPlus后台根据这些属性来对用户做分组
        // 设置流量分组有两个维度，一个是全局的，一个是单个广告位的，单个广告位的相同属性会覆盖全局的
        HashMap<String, String> customMap = new HashMap<>();
        customMap.put("user_gender",  "male");//男性
        customMap.put("user_level", "10");//游戏等级10
//        SegmentUtils.initCustomMap(customMap);//设置APP维度的规则，对全部placement有效
        SegmentUtils.initPlacementCustomMap(TestAdUnitId.ENTRY_AD_INTERSTITIAL, customMap);//仅对该广告位有效，会覆盖APP维度设置的规则

        // 监听广告的不同状态
        mTPInterstitial.setAdListener(new InterstitialAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: ");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: 广告"+ tpAdInfo.adSourceName +"被点击");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: 广告"+ tpAdInfo.adSourceName +"展示");

            }

            @Override
            public void onAdFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdFailed: ");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: 广告"+ tpAdInfo.adSourceName +"被关闭");

            }
        });

        // 监听每一层广告的加载情况，非特殊需求可以不实现
        mTPInterstitial.setAllAdLoadListener(new LoadAdEveryLayerListener() {
            @Override
            public void onAdAllLoaded(boolean b) {
                Log.i(TAG, "onAdAllLoaded: 该广告位下所有广告加载结束，是否有广告加载成功 ：" + b);
                tv.setText("onAdAllLoaded:广告加载结束");
            }

            @Override
            public void oneLayerLoadFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.i(TAG, "oneLayerLoadFailed:  广告"+ tpAdInfo.adSourceName +" 加载失败，code :: "+
                        tpAdError.getErrorCode() +" , Msg :: "+ tpAdError.getErrorMsg());
            }

            @Override
            public void oneLayerLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "oneLayerLoaded:  广告"+ tpAdInfo.adSourceName +" 加载成功");
                tv.setText("oneLayerLoaded:  广告"+ tpAdInfo.adSourceName +" 加载成功");
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
    }
}
