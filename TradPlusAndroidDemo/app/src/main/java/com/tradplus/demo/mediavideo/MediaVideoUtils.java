package com.tradplus.demo.mediavideo;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tradplus.ads.mgr.mediavideo.TPCustomMediaVideoAd;
import com.tradplus.ads.open.mediavideo.TPMediaVideo;

import java.util.HashMap;
import java.util.Map;

public class MediaVideoUtils {

    private static MediaVideoUtils sInstance;
    private TPCustomMediaVideoAd tpCustomMediaVideoAd;

    public synchronized static MediaVideoUtils getInstance() {
        if (sInstance == null) {
            sInstance = new MediaVideoUtils();
        }
        return sInstance;
    }

    public ViewGroup adContainer;

    public void loadTpMeidaVide(TPMediaVideo tpMediaVideo, Context context) {
        // 设置静音播放
        if (tpMediaVideo == null) return;

        Map<String, Object> mLocalExtras = new HashMap<>();
        mLocalExtras.put("video_mute", 1); // 1 静音；其他有声
        tpMediaVideo.setCustomParams(mLocalExtras);

        adContainer = new FrameLayout(context);
        // 请求广告并且传入容器
        tpMediaVideo.loadAd(adContainer);
    }


    public ViewGroup getAdContainer() {
        return adContainer;
    }

    public boolean isMeidaVideReady(TPMediaVideo tpMediaVideo) {
        if (tpMediaVideo == null) return false;
        return tpMediaVideo.isReady();
    }

    public void showTpMeidaVide(TPMediaVideo tpMediaVideo) {
        if (tpMediaVideo == null) return;

        boolean ready = tpMediaVideo.isReady();
        if (ready) {
            tpCustomMediaVideoAd = tpMediaVideo.getVideoAd();

            // 自定义数据
            Map<String,Object> showData = new HashMap<>();
            showData.put("data",System.currentTimeMillis()+"");
            tpCustomMediaVideoAd.setCustomShowData(showData);

            // 展示广告前 设置 广告场景ID
            tpCustomMediaVideoAd.start("adSceneId");
        }
    }


    public void onAdPause() {
        if (tpCustomMediaVideoAd != null) {
            tpCustomMediaVideoAd.pause();
        }
    }

    public void onAdResume() {
        if (tpCustomMediaVideoAd != null) {
            tpCustomMediaVideoAd.resume();
        }
    }

    public void onAdDestroy() {
        if (tpCustomMediaVideoAd != null) {
            tpCustomMediaVideoAd.onDestroy();
        }
    }
}
