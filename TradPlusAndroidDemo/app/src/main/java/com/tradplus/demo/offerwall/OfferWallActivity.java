package com.tradplus.demo.offerwall;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.offerwall.OfferWallAdListener;
import com.tradplus.ads.open.offerwall.TPOfferWall;
import com.tradplus.demo.R;
import com.tradplus.demo.interstititals.InterstitialActivity;
import com.tradplus.utils.TestAdUnitId;


/**
 * 积分墙广告
 * 积分墙广告一般是一个内容列表，展示给用户不同的内容，具体参考三方广告平台的介绍
 * 积分墙一般是在app内提供一个入口，用户点击进入该场景，在退出场景后可能会有一些奖励，具体参考文档
 * 积分墙广告一般需要预加载，在展示机会到来时判断isReady是否准备好，准备好后可以调show
 *
 * 自动加载功能是TradPlus独有的针对部分需要频繁展示广告的场景做的自动补充和过期重新加载的功能，推荐在广告场景触发较多的场景下使用
 * 自动加载功能只需要初始化一次，后续在广告场景到来的时候判断isReady然后show广告即可，不需要额外的调用load
 */
public class OfferWallActivity extends AppCompatActivity {


    Button show,load;
    TextView tv;
    TPOfferWall tpOfferWall;
    private static final String TAG = "OfferWallActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        tv = findViewById(R.id.tv);
        load = findViewById(R.id.load);
        show = findViewById(R.id.show);

        // load
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpOfferWall.loadAd();
            }
        });

        // show
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有可用广告
                if (!tpOfferWall.isReady()) {
                    Log.i(TAG, "isReady: 无可用广告");
                    tv.setText("isReady: 无可用广告");
                }else{
                    //展示
                    tpOfferWall.showAd(OfferWallActivity.this, "");
                    Log.i(TAG, "showAd: 展示");
                }
            }
        });

        // 初始化广告
        initOfferWallAd();
    }

    /**
     * 初始化广告位
     */
    private void initOfferWallAd() {
        tpOfferWall = new TPOfferWall(OfferWallActivity.this, TestAdUnitId.OFFERWALL_ADUNITID,false);
        tpOfferWall.entryAdScenario("scenarioId");
        tpOfferWall.setAdListener(new OfferWallAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: ");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: ");
            }

            @Override
            public void onAdFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdFailed: ");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: ");
            }

            @Override
            public void onAdReward(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdReward: ");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        //释放资源
        super.onDestroy();
        if(tpOfferWall != null){
            tpOfferWall.onDestroy();
        }
    }


}
