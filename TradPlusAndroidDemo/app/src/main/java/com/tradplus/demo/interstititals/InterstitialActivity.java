package com.tradplus.demo.interstititals;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tradplus.ads.mobileads.TradPlusErrorCode;
import com.tradplus.ads.mobileads.TradPlusInterstitial;
import com.tradplus.ads.mobileads.TradPlusInterstitialExt;
import com.tradplus.ads.mobileads.util.SegmentUtils;
import com.tradplus.ads.network.CanLoadListener;
import com.tradplus.ads.network.OnAllInterstatitialLoadedStatusListener;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

import java.util.HashMap;


public class InterstitialActivity extends AppCompatActivity implements TradPlusInterstitial.InterstitialAdListener, OnAllInterstatitialLoadedStatusListener {

    private TradPlusInterstitialExt mTradPlusInterstitial;
    Button interstitial_show,  interstitial_load;
    TextView tv;
    boolean autoload;


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
         * 2、参数3：自动reload模式，设置true，TradPlus SDK会在5个地方帮您自动加载广告，无需手动调用load
         *           （1）初始化广告位成功的时候，setCanLoadListener的canLoad()回调中；
         *           （2）调用obj.show()返回false时 此时说明该广告位下没有广告加载成功；
         *           （3）当广告关闭，onInterstitialDismissed回调中
         *           （4）当广告位下所有广告源加载失败的时候（需要调用setOnAllInterstatitialLoadedStatusListener监听）
         *           （5）obj.isReady()返回false，会自动加载广告
         *          不传默认false，则您需要在上述五个地方手动调用obj.load()方法以保证有广告的填充。
         */
        autoload = getIntent().getBooleanExtra("autoload",false);


        //流量分组
        HashMap<String, String> customMap = new HashMap<>();
        customMap.put("user_age",  "18");//18岁
        customMap.put("user_gender", "male");//男性
//        SegmentUtils.initCustomMap(customMap);//设置APP维度的规则，对全部placement有效
        SegmentUtils.initPlacementCustomMap(TestAdUnitId.INTERSTITIAL_ADUNITID, customMap);//仅对该广告位有效，会覆盖APP维度设置的规则


        if (mTradPlusInterstitial == null)
        mTradPlusInterstitial = new TradPlusInterstitialExt(this, TestAdUnitId.INTERSTITIAL_ADUNITID,autoload);


        /*
         * AutoReload设为false，或者不使用；
         * 您需要设置canload监听，并在canLoad回调中进行第一次广告请求；
         * 否则不需要设置canload监听
         */
        if (autoload) {
            //使用自动reload
            tv.setText("自动reload模式: 广告加载成功后，只需调用show");
        }

        mTradPlusInterstitial.setCanLoadListener( new CanLoadListener() {
            @Override
            public void canLoad() {
                interstitial_load.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (autoload) {
                            //使用自动reload
                            tv.setText("自动reload模式: 广告加载成功后，只需调用show");

                            //广告位是否达到缓存的配置上线
                            if (mTradPlusInterstitial.isAllReady())
                                tv.setText("广告缓存数已达配置上限");
                        }

                        if (!mTradPlusInterstitial.isAllReady()) {
                            mTradPlusInterstitial.load();
                            tv.setText("加载广告");
                        }else{
                            tv.setText("广告缓存数已达配置上限");
                        }
                    }
                });


            }
        });

        //初始化广告位
        mTradPlusInterstitial.initUnitId();

        //进入广告位场景时调用
        mTradPlusInterstitial.entryAdScenario();

        interstitial_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * 判断是否有广告加载成功：
                 * 可以直接调用obj.show(),当返回true时会自动展示广告；
                 * 也可以通过obj.isReady()判断是否有广告填充；
                 *
                 * AutoReload设为false，或者不使用；
                 * 您需要在obj.isReady()或者obj.show()返回为false的情况下调用load()
                 * 否则不需要调用
                 */

                if (autoload) {
                    //方式一：自动reload设置为true（推荐使用）
                    mTradPlusInterstitial.show();
                    tv.setText("自动reload模式: 广告加载成功直接展示");
                }else {
                    //方式二：：自动reload不设置或设置为false
                    if (!mTradPlusInterstitial.show()) {
                        Log.d("TradPlus", "No ads is Ready,loading...");
                        tv.setText("没有可用广告，重新请求广告");
                        mTradPlusInterstitial.load();
                    }else {
                        tv.setText("有可用广告，展示");
                    }

                }

            }
        });

        //设置监听
        //必须调用，针对每一个您添加的广告网络回调（多缓存）
        mTradPlusInterstitial.setInterstitialAdListener(this);

        //可选,该监听会针对整个广告位加载的结果做一个状态的返回（多缓存）
        //不设置自动reload模式 ：需要监听该方法的false返回，当整个广告位加载失败的时候，手动load一次广告
        mTradPlusInterstitial.setOnAllInterstatitialLoadedStatusListener(this);
    }

    @Override
    public void onInterstitialLoad(TradPlusInterstitial tradPlusInterstitial) {
        //可选：已获得HeaderBidding广告源信息后发起加载的回调
    }

    @Override
    public void onInterstitialLoaded(TradPlusInterstitial tradPlusInterstitial) {
        //广告加载成功，获取加载成功的三方广告网络名称和广告位
        //广告位下每一个广告源都会回调
        if(tradPlusInterstitial != null) {
            String channelName = tradPlusInterstitial.getChannelName();
            String adUnitId = tradPlusInterstitial.getAdUnitId();
        }
        Log.d("TradPlus","Interstitial Loaded");
        tv.setText("广告加载成功");
    }

    @Override
    public void onInterstitialFailed(TradPlusInterstitial tradPlusInterstitial, TradPlusErrorCode tradPlusErrorCode) {
        //广告加载失败
        //广告位下每一个广告源都会回调
        if (tradPlusInterstitial != null) {
            String code = tradPlusErrorCode.getCode();
            String errormessage = tradPlusErrorCode.getErrormessage();
            Log.d("TradPlus", "Interstitial Failed ,code : " + code + ", errormessage :" + errormessage);
        }
        tv.setText("广告加载失败");

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
        //广告位下每一个广告源都会回调
        /* 广告关闭：只要关闭广告就会回调
         * AutoReload设为false，或者不使用；
         * 则您需要在关闭广告的情况下调用load()，重新加载广告以保证填充率。
         * AutoReload设为true，则不需要调用
         */
        if (autoload){
            tv.setText("广告关闭，自动reload");
        }else{
            tv.setText("广告关闭，重新load广告");
            if(mTradPlusInterstitial != null)
                mTradPlusInterstitial.load();
        }
    }

    @Override
    public void onInterstitialRewarded(TradPlusInterstitial tradPlusInterstitial, String s, int i) {
        //集成插屏广告中不回调该方法
    }

    @Override
    public void onLoadStatus(final boolean isLoadedSuccess, String unitId) {
        /*
         *     设置可选监听，当缓存广告网络全部加载结束，该方法才会被调用；
         *     isLoadedSuccess 为false，表明该广告位下所有的广告源加载失败
         *     isLoadedSuccess 为true，表明该广告位下有可用广告
         *
         *     自动加载模式下，会在所有广告源都加载失败的情况下最多6次帮你加载广告，间隔时间为15秒、30秒、60秒等
         *     不使用自动加载模式，需要你在所有广告源加载失败时，手动请求广告，并且控制次数和间隔时间
         */
        Log.d("TradPlus","RewardedVideo onLoadStatus = "+ isLoadedSuccess);
        if (!autoload && !isLoadedSuccess) {
            mTradPlusInterstitial.load();
            tv.setText("所有广告源都加载失败");
        }
    }

    @Override
    protected void onResume() {
        mTradPlusInterstitial.onResume();
        super.onResume();
    }
    @Override
    protected void onPause() {

        super.onPause();
    }
    @Override
    protected void onDestroy() {
        //释放资源
        mTradPlusInterstitial.onDestroy();
        super.onDestroy();
    }
}
