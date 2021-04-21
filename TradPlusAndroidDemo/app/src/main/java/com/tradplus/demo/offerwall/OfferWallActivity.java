package com.tradplus.demo.offerwall;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tradplus.ads.mobileads.TradPlusErrorCode;
import com.tradplus.ads.mobileads.TradPlusInterstitial;
import com.tradplus.ads.mobileads.TradPlusInterstitialExt;
import com.tradplus.ads.network.CanLoadListener;
import com.tradplus.ads.network.OnAllInterstatitialLoadedStatusListener;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;


public class OfferWallActivity extends AppCompatActivity implements TradPlusInterstitial.InterstitialAdListener, OnAllInterstatitialLoadedStatusListener {

    TradPlusInterstitialExt mTradPlusOfferWall;
    Button show,load;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        tv = findViewById(R.id.tv);
        load = findViewById(R.id.load);
        show = findViewById(R.id.show);

        /*
         * 1、参数2：广告位
         */
        if (mTradPlusOfferWall == null)
        mTradPlusOfferWall = new TradPlusInterstitialExt(this, TestAdUnitId.OFFERWALL_ADUNITID);



        //设置canload监听，确保在广告位初始化成功的时候进行广告的第一次加载
        mTradPlusOfferWall.setCanLoadListener(new CanLoadListener() {
            @Override
            public void canLoad() {
//**********注意事项（1）第一次加载广告
                load.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //广告位是否达到缓存的配置上线
                        if (!mTradPlusOfferWall.isAllReady()) {
                            mTradPlusOfferWall.load();
                            tv.setText("加载广告");
                        }else {
                            tv.setText("广告缓存数已达配置上限");
                        }
                    }
                });


            }
        });
        //初始化广告位
        mTradPlusOfferWall.initUnitId();

        //进入广告位场景的时候调用
        mTradPlusOfferWall.entryAdScenario();

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * 判断是否有广告加载成功：
                 * 可以直接调用obj.show(),当返回true时会自动展示广告；
                 */
                //方式一：
                if (!mTradPlusOfferWall.show()) {
                    Log.d("TradPlus", "No ads is Ready,loading...");
                    tv.setText("没有可用广告，重新请求广告");
                    mTradPlusOfferWall.load();
                }else {
                    tv.setText("有可用广告，展示");
                }
            }
        });

        //设置监听
        mTradPlusOfferWall.setInterstitialAdListener(this);

        mTradPlusOfferWall.setOnAllInterstatitialLoadedStatusListener(this);
    }

    @Override
    public void onInterstitialLoad(TradPlusInterstitial tradPlusInterstitial) {
        //仅仅激励视频和插屏使用，积分墙不使用
    }

    @Override
    public void onInterstitialLoaded(TradPlusInterstitial tradPlusInterstitial) {
        //广告加载成功，获取加载成功的三方广告网络名称和广告位
        //广告位下每一个广告源都会回调
        if(tradPlusInterstitial != null) {
            String channelName = tradPlusInterstitial.getChannelName();
            String adUnitId = tradPlusInterstitial.getAdUnitId();
            Log.d("TradPlus", "OfferWall Loaded");
            tv.setText("广告加载成功");
        }
    }

    @Override
    public void onInterstitialFailed(TradPlusInterstitial tradPlusInterstitial, TradPlusErrorCode tradPlusErrorCode) {
        //广告加载成功，获取加载成功的三方广告网络名称和广告位
        //广告位下每一个广告源都会回调
        if(tradPlusInterstitial != null) {
            String code = tradPlusErrorCode.getCode();
            String errormessage = tradPlusErrorCode.getErrormessage();
            Log.d("TradPlus", "OfferWall Failed , code : "+ code + " , errormessage :" + errormessage);
        }
    }

    @Override
    public void onInterstitialShown(TradPlusInterstitial tradPlusInterstitial) {
        //广告开始展示
        //广告位下每一个广告源都会回调
        if(tradPlusInterstitial != null)
            Toast.makeText(this,"广告源 ： " + tradPlusInterstitial.getChannelName(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInterstitialClicked(TradPlusInterstitial tradPlusInterstitial) {
        //广告被点击
    }

    @Override
    public void onInterstitialDismissed(TradPlusInterstitial tradPlusInterstitial) {
        //广告被关闭
        //广告位下每一个广告源都会回调
//**********注意事项（4）
        //建议：在广告关闭时重新加载广告
//        mTradPlusOfferWall.load();

    }

    @Override
    public void onInterstitialRewarded(TradPlusInterstitial tradPlusInterstitial, String s, int i) {
        //奖励
    }

    @Override
    protected void onResume() {
        mTradPlusOfferWall.onResume();
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        //释放资源
        mTradPlusOfferWall.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLoadStatus(boolean b, String s) {
        //设置可选监听，当缓存广告网络全部加载结束，该方法才会被调用；
        //isLoadedSuccess ture表明单次广告源加载全部完毕，并且有广告源加载成功；
        //false为全部的广告网络加载失败；
        //unitId 是广告位ID


        //需要监听该方法的false返回，当整个广告位加载失败的时候，手动load一次广告
        //即在所有源都失败的情况下，需要重新加载，建议“最多加载3次”即可，且“每次加载之间间隔20秒”
        Log.d("TradPlus","OfferWall onLoadStatus = "+ b);
        if (!b) {
            mTradPlusOfferWall.load();
            tv.setText("所有广告源都加载失败");
        }

    }
}
