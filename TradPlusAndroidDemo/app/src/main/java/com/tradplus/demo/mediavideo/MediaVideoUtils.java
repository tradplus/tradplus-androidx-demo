package com.tradplus.demo.mediavideo;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.widget.RelativeLayout;
import android.widget.VideoView;

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

    public RelativeLayout adContainer;
    public VideoView videoView;

    public void loadTpMeidaVide(TPMediaVideo tpMediaVideo, Context context) {
        adContainer = new RelativeLayout(context);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        videoView = new VideoView(context);
        // 最后一个参数是否静音，默认静音
        // Adx & IMA
        NewVideoAdPlayerAdapter videoAdPlayerAdpter = new NewVideoAdPlayerAdapter(videoView, audioManager, true);

        // Only IMA
//        VideoAdPlayerAdapter videoAdPlayerAdpter = new VideoAdPlayerAdapter(videoView, audioManager, true);
        // 请求广告,传入展示广告容器和videoAdPlayer
        tpMediaVideo.loadAd(adContainer, videoAdPlayerAdpter);
    }


    public RelativeLayout getAdContainer() {
        return adContainer;
    }

    public boolean isMeidaVideReady(TPMediaVideo tpMediaVideo) {
        if (tpMediaVideo == null) return false;
        return tpMediaVideo.isReady();
    }

    public void showTpMeidaVide(TPMediaVideo tpMediaVideo) {
        boolean ready = tpMediaVideo.isReady();
        if (ready) {
            tpCustomMediaVideoAd = tpMediaVideo.getVideoAd();

            // VideoVideo居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            adContainer.addView(videoView,layoutParams);

            // 自定义数据
            Map<String, Object> showData = new HashMap<>();
            showData.put("data", System.currentTimeMillis() + "");
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
