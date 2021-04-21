package com.tradplus.demo.rewarded;

import android.app.Activity;
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
import com.tradplus.ads.mobileads.util.SegmentUtils;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.reward.RewardAdListener;
import com.tradplus.ads.open.reward.TPReward;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

import java.util.HashMap;


public class RewardedVideoActivity extends AppCompatActivity {

    Button reward_show,reward_load;
    TextView tv;
    TPReward mTpReward;
    private static final String TAG = "tradpluslog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        tv = findViewById(R.id.tv);
        reward_load = (Button)findViewById(R.id.load);
        reward_show = (Button)findViewById(R.id.show);

        /*
         * 1、参数2：广告位
         *
         * 2、参数3：自动reload模式，true 开启 ，false 关闭
         */
        mTpReward = new TPReward(this,TestAdUnitId.REWRDVIDEO_ADUNITID,true);

        //进入广告场景，广告场景ID后台创建
        mTpReward.entryAdScenario(TestAdUnitId.ENTRY_AD_REWARD);

        HashMap<String, String> customMap = new HashMap<>();
        customMap.put("user_gender",  "male");//男性
        customMap.put("user_level", "10");//游戏等级10
//        SegmentUtils.initCustomMap(customMap);//设置APP维度的规则，对全部placement有效
        SegmentUtils.initPlacementCustomMap(TestAdUnitId.REWRDVIDEO_ADUNITID, customMap);//仅对该广告位有效，会覆盖APP维度设置的规则


        reward_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTpReward.loadAd();
            }
        });

        mTpReward.setAdListener(new RewardAdListener() {
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

            @Override
            public void onAdReward(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdReward: 奖励项目：" + tpAdInfo.currencyName +" ， 奖励数量："+ tpAdInfo.amount);
            }
        });


        mTpReward.setAllAdLoadListener(new LoadAdEveryLayerListener() {
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


        reward_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有可用广告
                if (!mTpReward.isReady()) {
                    Log.i(TAG, "isReady: 无可用广告");
                    tv.setText("isReady: 无可用广告");
                }else{
                    //展示
                    mTpReward.showAd(RewardedVideoActivity.this, TestAdUnitId.ENTRY_AD_REWARD);
                    Log.i(TAG, "showAd: 展示");
                }
            }
        });

    }
}
