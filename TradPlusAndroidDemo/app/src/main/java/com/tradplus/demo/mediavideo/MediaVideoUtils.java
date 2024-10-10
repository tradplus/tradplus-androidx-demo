package com.tradplus.demo.mediavideo;

import android.content.Context;
import android.media.AudioManager;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.tradplus.ads.base.common.TPVideoAdPlayer;

import java.util.HashMap;

public class MediaVideoUtils {

    private static MediaVideoUtils sInstance;
    private NewVideoAdPlayerAdapter videoAdPlayerAdpter;
    public HashMap<Object,Object> objectObjectHashMap = new HashMap<>();

    public synchronized static MediaVideoUtils getInstance() {
        if (sInstance == null) {
            sInstance = new MediaVideoUtils();
        }
        return sInstance;
    }
    private static final String TAG = "TradPlusData";

    public TPVideoAdPlayer getVideoAdPlayer(boolean isVideoMute, Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        VideoView videoView = new VideoView(context);
        videoAdPlayerAdpter = new NewVideoAdPlayerAdapter(videoView, audioManager, isVideoMute);
        objectObjectHashMap.put(videoAdPlayerAdpter,videoView);
        return videoAdPlayerAdpter;
    }


    public void showTpMeidaVide(TPVideoAdPlayer tpAdVideoPlayer, ViewGroup AdDisplayContainer, ViewGroup adContainer) {
        if (objectObjectHashMap != null && objectObjectHashMap.containsKey(tpAdVideoPlayer)) {
            Object videoViewObj = objectObjectHashMap.get(tpAdVideoPlayer);
            if (videoViewObj instanceof VideoView) {
                VideoView videoView = (VideoView) videoViewObj;
                if (videoView != null && videoView.getParent() != null) {
                    ((ViewGroup) videoView.getParent()).removeView(videoView);
                }

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                        (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                adContainer.addView(videoView, layoutParams);

                adContainer.addView(AdDisplayContainer);
            }
        }
    }
}
