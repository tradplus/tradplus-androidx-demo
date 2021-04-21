package com.tradplus.demo.banners;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tradplus.ads.mobileads.TradPlusErrorCode;
import com.tradplus.ads.mobileads.TradPlusView;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;


public class BannerActivity extends AppCompatActivity implements TradPlusView.FSAdViewListener {

    private TradPlusView mTradPlusView;
    private static final String TAG = "TradPlus";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        mTradPlusView = findViewById(R.id.BannerView);
        //创建广告位
        mTradPlusView.setAdUnitId(TestAdUnitId.BANNER_ADUNITID);
        //进入广告位所在界面时调用
        mTradPlusView.entryAdScenario();
        //设置监听
        mTradPlusView.setAdViewListener(this);
        //加载广告
        mTradPlusView.loadAd();
    }

    @Override
    public void onAdViewLoaded(TradPlusView tradPlusView) {
        //广告加载成功
        Log.d(TAG, "onAdViewLoaded: ");
        if(tradPlusView != null)
            Toast.makeText(this,"广告源 ： " + tradPlusView.getChannelName(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAdViewFailed(TradPlusView tradPlusView, TradPlusErrorCode tradPlusErrorCode) {
        //广告加载失败
        String code = tradPlusErrorCode.getCode();
        String errormessage = tradPlusErrorCode.getErrormessage();
        Log.d(TAG, "onAdViewFailed: code:" + code + " , errormessage : "+ errormessage);

    }

    @Override
    public void onAdViewClicked(TradPlusView tradPlusView) {
        //广告被点击
        Log.d(TAG, "onAdViewClicked: ");
    }

    @Override
    public void onAdViewExpanded(TradPlusView tradPlusView) {
        //广告展示
        Log.d(TAG, "onAdViewExpanded: ");
    }

    @Override
    public void onAdViewCollapsed(TradPlusView tradPlusView) {

    }

    @Override
    public void onAdsSourceLoaded(Object o) {
    }

    @Override
    protected void onDestroy() {
        //释放资源
        mTradPlusView.destroy();
        super.onDestroy();
    }

}
