package com.tradplus.demo.mediavideo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.VideoView;

import com.google.ads.interactivemedia.v3.api.AdPodInfo;
import com.google.ads.interactivemedia.v3.api.player.AdMediaInfo;
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.tradplus.ads.base.GlobalTradPlus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// IMA's VideoAdPlayer interface
public class VideoAdPlayerAdapter implements VideoAdPlayer {

    private static final String TAG = "VideoAdPlayerAdapter";
    public static long POLLING_TIME_MS = 250; // 可自定义
    public static long INITIAL_DELAY_MS = 250;
    private final VideoView videoPlayer;
    private final AudioManager audioManager;
    private boolean mMute;
    private final List<VideoAdPlayerCallback> adCallbacks = new ArrayList<>();
    private Timer timer;
    private int adDuration;

    // The saved ad position, used to resumed ad playback following an ad click-through.
    private int savedAdPosition = 0;
    private int savedContentPosition = 0;

    private AdMediaInfo adMediaInfo;
    private boolean isAdDisplayed = false;


    public VideoAdPlayerAdapter(VideoView videoPlayer, AudioManager audioManager, boolean mute) {
        this.videoPlayer = videoPlayer;
        this.videoPlayer.setOnCompletionListener(
                (MediaPlayer mediaPlayer) -> notifyImaOnContentCompleted());
        this.audioManager = audioManager;
        this.mMute = mute;
    }

    @Override
    public void loadAd(AdMediaInfo info, AdPodInfo adPodInfo) {
        adMediaInfo = info;
        isAdDisplayed = false;
    }

    @Override
    public void pauseAd(AdMediaInfo adMediaInfo) {
        savePosition();
        stopAdTracking();
        Log.i(TAG, "pauseAd: " + savedAdPosition);
        if (isAdDisplayed) {
            for (VideoAdPlayerCallback callback : adCallbacks) {
                callback.onPause(adMediaInfo);
            }
        }
        // 暂停
        videoPlayer.pause();
    }

    @Override
    public void playAd(AdMediaInfo adMediaInfo) {
        videoPlayer.setVideoURI(Uri.parse(adMediaInfo.getUrl()));
        videoPlayer.setOnPreparedListener(
                mediaPlayer -> {
                    adDuration = mediaPlayer.getDuration();
                    isAdDisplayed = true;
                    if (savedAdPosition > 0) {
                        mediaPlayer.seekTo(savedAdPosition);
                    }
                    Log.i(TAG, "playAd start : " + savedAdPosition);
                    if (mMute) {
                        mediaPlayer.setVolume(0f, 0f);
                    }

                    mediaPlayer.start();
                    startAdTracking();
                });

        if (isAdDisplayed) {
            for (VideoAdPlayerCallback callback : adCallbacks) {
                callback.onResume(adMediaInfo);
            }
        }

        videoPlayer.setOnErrorListener(
                (mediaPlayer, errorType, extra) -> notifyImaSdkAboutAdError(errorType));

        videoPlayer.setOnCompletionListener(
                mediaPlayer -> {
                    savedAdPosition = 0;
                    notifyImaSdkAboutAdEnded();
                });
    }

    @Override
    public void stopAd(AdMediaInfo adMediaInfo) {
        Log.i(TAG, "stopAd: ");
        stopAdTracking();
        // 停止播放
        videoPlayer.stopPlayback();
        isAdDisplayed = false;

    }

    @Override
    public void release() {
        Log.i(TAG, "release: ");
        // any clean up that needs to be done
    }

    @Override
    public void addCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
        Log.i(TAG, "addCallback: ");
        adCallbacks.add(videoAdPlayerCallback);
    }

    @Override
    public void removeCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
        Log.i(TAG, "removeCallback: ");
        adCallbacks.remove(videoAdPlayerCallback);
    }


    // 设置广告跟踪
    public void startAdTracking() {
        Log.i(TAG, "startAdTracking: ");
        if (timer != null) {
            return;
        }
        timer = new Timer();
        TimerTask updateTimerTask =
                new TimerTask() {
                    @Override
                    public void run() {
                        VideoProgressUpdate progressUpdate = getAdProgress();
                        notifyImaSdkAboutAdProgress(progressUpdate);
                    }
                };

        timer.schedule(updateTimerTask, POLLING_TIME_MS, INITIAL_DELAY_MS);
    }


    public void notifyImaSdkAboutAdEnded() {
        Log.i(TAG, "notifyImaSdkAboutAdEnded");
        savedAdPosition = 0;
        for (VideoAdPlayerCallback callback : adCallbacks) {
            callback.onEnded(adMediaInfo);
        }
    }

    public void notifyImaSdkAboutAdProgress(VideoProgressUpdate adProgress) {
        for (VideoAdPlayerCallback callback : adCallbacks) {
            callback.onAdProgress(adMediaInfo, adProgress);
        }
    }

    /**
     * @param errorType Media player's error type as defined at
     *     https://cs.android.com/android/platform/superproject/+/master:frameworks/base/media/java/android/media/MediaPlayer.java;l=4335
     * @return True to stop the current ad playback.
     */
    public boolean notifyImaSdkAboutAdError(int errorType) {
        Log.i(TAG, "notifyImaSdkAboutAdError");

        switch (errorType) {
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Log.e(TAG, "notifyImaSdkAboutAdError: MEDIA_ERROR_UNSUPPORTED");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Log.e(TAG, "notifyImaSdkAboutAdError: MEDIA_ERROR_TIMED_OUT");
                break;
            default:
                break;
        }
        for (VideoAdPlayerCallback callback : adCallbacks) {
            callback.onError(adMediaInfo);
        }
        return true;
    }


    public void notifyImaOnContentCompleted() {
        Log.i(TAG, "notifyImaOnContentCompleted");
        for (VideoAdPlayerCallback callback : adCallbacks) {
            callback.onContentComplete();
        }
    }

    public void stopAdTracking() {
        Log.i(TAG, "stopAdTracking: ");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    long adPosition;
    @Override
    public VideoProgressUpdate getAdProgress() {
        if (!isAdDisplayed || adDuration <= 0) {
            return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        try{
            adPosition = videoPlayer.getCurrentPosition();
            Log.i(TAG, "getAdProgress adPosition: " + adPosition);
        }catch(Throwable throwable) {
            return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }

        return new VideoProgressUpdate(adPosition, adDuration);

    }

    /** Returns current volume as a percent of max volume. */
    @Override
    public int getVolume() {
        Context context = GlobalTradPlus.getInstance().getContext();
        if (context == null) return 0;
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            double volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            double max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            if (max <= 0) {
                return 0;
            }
            // Return a range from 0-100.
            return (int) ((volume / max) * 100.0f);
        }
        return 0;
    }


    public void restorePosition() {
        if (isAdDisplayed) {
            // 跳转到设定时间
            videoPlayer.seekTo(savedAdPosition);
            Log.i(TAG, "restorePosition: " + savedAdPosition);
        } else {
            videoPlayer.seekTo(savedContentPosition);
        }
    }

    public void savePosition() {
        if (isAdDisplayed) {
            savedAdPosition = (int)adPosition;
            Log.i(TAG, "savePosition: savedAdPosition ：" + savedAdPosition);
        } else {
            savedContentPosition = (int)adPosition;
            Log.i(TAG, "savePosition: savedContentPosition ： " + savedContentPosition);
        }
    }

    public boolean getIsAdDisplayed() {
        return isAdDisplayed;
    }

}
