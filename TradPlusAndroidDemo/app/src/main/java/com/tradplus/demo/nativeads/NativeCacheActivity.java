package com.tradplus.demo.nativeads;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tradplus.ads.mobileads.TradPlusErrorCode;
import com.tradplus.ads.mobileads.TradPlusSlot;
import com.tradplus.ads.mobileads.TradPlusView;
import com.tradplus.ads.mobileads.TradPlusViewExt;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

import java.util.List;

public class NativeCacheActivity extends AppCompatActivity implements TradPlusViewExt.TradPlusFeedListener {

    private TradPlusSlot tradPlusSlot;
    private TradPlusViewExt tradPlusViewExt;
    private static final String TAG = "TradPlus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_cache);

        /*
        * 配置相关参数 ：
        *
        * 必须：UnitId ：广告位ID，Tradplus后台申请
        *
        * native_ad_list_item.xml等布局文件可以在Demo中获取，或者在下载下来的tradplus/res文件夹中获取
        *
        * 必须：native_ad_list_item.xml和video_ad_list_item.xml布局文件
        *
        * 可选：原生缓存同时还支持 原生横幅（Facebook的原生横幅） 的缓存，如果你需要增加Facebook的原生横幅这一广告类型，才需要
        * 调用setLayoutBannerName("native_banner_ad_unit")，否则，不用添加
        *
        * 可选：Count缓存数的设置，不设置默认TradPlus后台下发配置
        *
        *
        * */
        //设置相关参数：广告位ID、布局、缓存个数
        tradPlusSlot = new TradPlusSlot.Builder()
                .setUnitId(TestAdUnitId.NATIVE_ADUNITID)
                .setLayoutName("native_ad_list_item")
                .setLayoutNameEx("video_ad_list_item")
                .setLayoutBannerName("native_banner_ad_unit")
                .setAdCount(5)
                .build();

        tradPlusViewExt = new TradPlusViewExt();

        //加载广告
        tradPlusViewExt.loadFeedAd(this,tradPlusSlot,this);

    }

    @Override
    public void onFeedAdLoad(List<TradPlusView> list) {
        //将加载成功的广告add进View
        Log.d(TAG,"缓存广告的个数 TradPlusViews size = " + list.size());
        Toast.makeText(this,"缓存广告的个数 TradPlusViews size = " + list.size(),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClicked(String s, String s1) {
        //广告被点击
        Log.d(TAG,"Native Clicked");
    }

    @Override
    public void onError(TradPlusErrorCode tradPlusErrorCode) {
        //广告加载失败
        Log.d(TAG,"Native Failed");
    }

    @Override
    public void onAdsSourceLoad(List<Object> list) {
        for(Object ads : list){
            Log.d(TAG,"TradPlusViewSource networkname = " +ads);
            Log.d(TAG,"TradPlusViewSource networkname = " +compareAdSources(ads));
        }
    }


    private String compareAdSources(Object obj){
        if(obj instanceof com.mopub.nativeads.StaticNativeAd){
            return "mopub";
        }else if(obj instanceof com.google.android.gms.ads.formats.NativeContentAd){
            return "admob";
        }else if(obj instanceof com.google.android.gms.ads.formats.UnifiedNativeAd){
            return "admob new";
        }else if(obj instanceof com.facebook.ads.NativeAd){
            return "facebook";
        }else if(obj instanceof com.facebook.ads.NativeBannerAd){
            return "facebook nativebanner";
        }else if(obj instanceof com.kwad.sdk.api.KsDrawAd){
            return "kwads";
        }else if(obj instanceof com.bytedance.sdk.openadsdk.TTNativeExpressAd){
            return "pangle(cn)";
        }else if(obj instanceof com.qq.e.comm.pi.AdData){
            return "tencent ads";
        }else if(obj instanceof com.youdao.sdk.nativeads.YouDaoNative){
            return "youdao";
        }else{
            return "unknown";
        }
    }

}
