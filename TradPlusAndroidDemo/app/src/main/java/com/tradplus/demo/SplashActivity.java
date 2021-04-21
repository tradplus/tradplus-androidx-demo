package com.tradplus.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import com.tradplus.ads.mobileads.TradPlusErrorCode;
import com.tradplus.ads.mobileads.TradPlusView;
import com.tradplus.utils.TestAdUnitId;

public class SplashActivity extends AppCompatActivity implements TradPlusView.FSAdViewListener,TradPlusView.SplashAdViewListener{

    private TradPlusView tradPlusView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tradPlusView = (TradPlusView) findViewById(R.id.splash_view);
        //创建广告位
        tradPlusView.setAdUnitId(TestAdUnitId.SPLASH_ADUNITID);

        //设置广告尺寸
        tradPlusView.setAdSize(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //Kuaishou splash 需要引入布局文件
        tradPlusView.setAdLayoutName("splash_view_ad_layout");
        //进入广告位场景的时候调用
        tradPlusView.entryAdScenario();
        //设置监听
        tradPlusView.setAdViewListener(this);
        tradPlusView.setSplashAdViewListener(this);
        //加载广告
        tradPlusView.loadAd();

    }

    @Override
    public void onAdViewLoaded(TradPlusView tradPlusView) {
        //广告加载成功
        //广告位下每一个广告源都会回调
        Log.d("TradPlus","Splash onAdViewLoaded");
    }

    @Override
    public void onAdViewFailed(TradPlusView tradPlusView, TradPlusErrorCode tradPlusErrorCode) {
        //广告加载失败
        //广告位下每一个广告源都会回调
        Log.d("TradPlus","Splash onAdViewFailed");
    }

    @Override
    public void onAdViewClicked(TradPlusView tradPlusView) {
        //广告被点击
        Log.d("TradPlus","Splash onAdViewClicked");
    }

    @Override
    public void onAdViewExpanded(TradPlusView tradPlusView) {
        //广告展示
        Log.d("TradPlus","Splash onAdViewExpanded");
    }

    @Override
    public void onAdViewCollapsed(TradPlusView tradPlusView) {

    }

    @Override
    public void onAdsSourceLoaded(Object object) {

    }

    @Override
    public void onADDismissed() {
        //广告关闭
        Log.d("TradPlus","Splash Closed");
    }

    @Override
    public void onADTick(long l) {

    }

    //开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK==keyCode|| KeyEvent.KEYCODE_HOME==keyCode){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        //释放资源
        tradPlusView.destroy();
        super.onDestroy();
    }

}
