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


public class InterstitialActivity extends AppCompatActivity {

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
        /*
         * 1、参数2：广告位
         *
         * 2、参数3：自动reload模式，true 开启 ，false 关闭
         */
        mTPInterstitial = new TPInterstitial(this,TestAdUnitId.INTERSTITIAL_ADUNITID,true);

        //进入广告场景，广告场景ID后台创建
        mTPInterstitial.entryAdScenario(TestAdUnitId.ENTRY_AD_INTERSTITIAL);

        HashMap<String, String> customMap = new HashMap<>();
        customMap.put("user_gender",  "male");//男性
        customMap.put("user_level", "10");//游戏等级10
//        SegmentUtils.initCustomMap(customMap);//设置APP维度的规则，对全部placement有效
        SegmentUtils.initPlacementCustomMap(TestAdUnitId.ENTRY_AD_INTERSTITIAL, customMap);//仅对该广告位有效，会覆盖APP维度设置的规则


        interstitial_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTPInterstitial.loadAd();
            }
        });

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
        });


        interstitial_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有可用广告
                if (!mTPInterstitial.isReady()) {
                    Log.i(TAG, "isReady: 无可用广告");
                    tv.setText("isReady: 无可用广告");
                }else{
                    //展示
                    mTPInterstitial.showAd(InterstitialActivity.this, TestAdUnitId.ENTRY_AD_INTERSTITIAL);
                    Log.i(TAG, "showAd: 展示");
                }
            }
        });

    }
}
