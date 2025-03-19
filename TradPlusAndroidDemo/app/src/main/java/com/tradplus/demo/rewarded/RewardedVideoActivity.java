package com.tradplus.demo.rewarded;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.AdSettings;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.util.SegmentUtils;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.reward.RewardAdListener;
import com.tradplus.ads.open.reward.TPReward;
import com.tradplus.demo.R;
import com.tradplus.demo.banners.SecondPageActivity;
import com.tradplus.utils.TestAdUnitId;

import java.util.HashMap;
import java.util.Map;


/**
 * 激励视频广告
 * 激励视频广告一般是全屏的15-30s的视频，调用时机是在给用户奖励或者获得某些特定物品时，如果用户顺利的看完广告，就把奖励发放给用户，具体参考文档
 * 激励视频广告是三方广告平台提供的activity，一般不支持做定制或者修改
 * 激励视频广告一般需要预加载，在展示机会到来时判断isReady是否准备好，准备好后可以调show
 *
 * 自动加载功能是TradPlus独有的针对部分需要频繁展示广告的场景做的自动补充和过期重新加载的功能，推荐在广告场景触发较多的场景下使用
 * 自动加载功能只需要初始化一次，后续在广告场景到来的时候判断isReady然后show广告即可，不需要额外的调用load
 */
public class RewardedVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private TPReward mTpReward;
    private VideoUtils videoUtils;
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

    private void initRewardAd() {
        if (mTpReward == null) {
            mTpReward = new TPReward(this, TestAdUnitId.REWRDVIDEO_ADUNITID);

            //进入广告场景，广告场景ID后台创建
            // 广告场景是用来统计进入广告场景的次数和进入场景后展示广告的次数，所以请在准确的位置调用
            mTpReward.entryAdScenario(TestAdUnitId.ENTRY_AD_REWARD);

            // 流量分组的时候用到，可以自定义一些app相关的属性，在TradPlus后台根据这些属性来对用户做分组
            // 设置流量分组有两个维度，一个是全局的，一个是单个广告位的，单个广告位的相同属性会覆盖全局的
            HashMap<String, String> customMap = new HashMap<>();
            customMap.put("user_gender", "male");//男性
            customMap.put("user_level", "10");//游戏等级10
//        SegmentUtils.initCustomMap(customMap);//设置APP维度的规则，对全部placement有效
            SegmentUtils.initPlacementCustomMap(TestAdUnitId.REWRDVIDEO_ADUNITID, customMap);//仅对该广告位有效，会覆盖APP维度设置的规则

            // 监听广告的不同状态
            mTpReward.setAdListener(new RewardAdListener() {
                @Override
                public void onAdLoaded(TPAdInfo tpAdInfo) {
                    Log.i(TAG, "onAdLoaded: ");
                    Toast.makeText(RewardedVideoActivity.this, "广告加载成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdClicked(TPAdInfo tpAdInfo) {
                    Log.i(TAG, "onAdClicked: 广告" + tpAdInfo.adSourceName + "被点击");
                    Toast.makeText(RewardedVideoActivity.this, "广告被点击", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdImpression(TPAdInfo tpAdInfo) {
                    Log.i(TAG, "onAdImpression: 广告" + tpAdInfo.adSourceName + "展示");
                    Toast.makeText(RewardedVideoActivity.this, "广告展示", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onAdFailed(TPAdError tpAdError) {
                    Log.i(TAG, "onAdFailed: " + tpAdError.getErrorCode() + ",MSG :" + tpAdError.getErrorMsg());
                    Toast.makeText(RewardedVideoActivity.this, "广告加载失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdClosed(TPAdInfo tpAdInfo) {
                    Log.i(TAG, "onAdClosed: 广告" + tpAdInfo.adSourceName + "被关闭");
                    Toast.makeText(RewardedVideoActivity.this, "广告关闭", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onAdReward(TPAdInfo tpAdInfo) {
                    Log.i(TAG, "onAdReward: 奖励项目：" + tpAdInfo.currencyName + " ， 奖励数量：" + tpAdInfo.amount);
                    Toast.makeText(RewardedVideoActivity.this, "广告奖励", Toast.LENGTH_SHORT).show();
                    // 给用户发放奖励
                    // 在V6.4.5以后的版本，可以根据三方广告平台的文档来增加奖励验证功能（奖励验证需要开发者后台配合，TradPlus不提供后台服务奖励验证的功能）
                }

                @Override
                public void onAdVideoStart(TPAdInfo tpAdInfo) {
                    // V8.1.0.1 播放开始
                }

                @Override
                public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                    // V8.1.0.1 播放结束
                }

                @Override
                public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {

                }

            });

            // 监听每一层广告的加载情况，非特殊需求可以不实现
            mTpReward.setAllAdLoadListener(new LoadAdEveryLayerListener() {
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
        videoUtils.loadReward(mTpReward);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mTpReward != null){
            mTpReward.onDestroy();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
                initRewardAd();
                break;
            case R.id.btn_show:
                if (videoUtils.isReady()) {
                    videoUtils.showReward(RewardedVideoActivity.this);
                }else {
                    Toast.makeText(RewardedVideoActivity.this, "无可用广告 or 已经展示", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.second_page:
                // 进入下一页
                Intent intent = new Intent(RewardedVideoActivity.this, SecondPageActivity.class);
                intent.putExtra("type",TestAdUnitId.TYPE_REWARDED);
                startActivity(intent);
                break;
        }
    }
}

