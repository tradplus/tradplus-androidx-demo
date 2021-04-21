package com.tradplus.demo.nativeads;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tradplus.ads.mobileads.TradPlusErrorCode;
import com.tradplus.ads.mobileads.TradPlusView;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

public class NativeActivity extends AppCompatActivity implements TradPlusView.FSAdViewListener {

    private TradPlusView mTradPlusNative;
    private static final String TAG = "TradPlus";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        RelativeLayout main_layout = (RelativeLayout)findViewById(R.id.activity_native);
        main_layout.setBackgroundColor(Color.parseColor("#E6E6FA"));

        mTradPlusNative = findViewById(R.id.NativeView);
        //创建广告位
        mTradPlusNative.setAdUnitId(TestAdUnitId.NATIVE_ADUNITID);
        //设置广告尺寸，可以选择在布局文件中设置
        //单位：dp，宽度：320左右 高度：300～480
        mTradPlusNative.setAdSize(320, 340);

        //native_ad_list_item.xml布局文件
        //可自定义布局，但布局文件中View对应的id不可改变；
        mTradPlusNative.setAdLayoutName("native_ad_list_item");

        //video_ad_item.xml只用于mopub的原生，如您不需要接入该类型，则无需添加；
        //可自定义布局，但布局文件中View对应的id不可改变；切勿调换位置
//		mTradPlusNative.setAdLayoutName("native_ad_list_item","video_ad_item");

        //进入广告位所在界面时调用
        mTradPlusNative.entryAdScenario();
        //设置监听
        mTradPlusNative.setAdViewListener(this);
        //加载广告
        mTradPlusNative.loadAd();

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
        Log.d(TAG, "onAdViewFailed: code : "+ code + " , errormessage :" + errormessage);
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
        mTradPlusNative.destroy();
        super.onDestroy();
    }

}
