package com.tradplus.demo.mediavideo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.VideoView;

import com.tradplus.ads.base.bean.TPAdMediaInfo;
import com.tradplus.ads.base.common.TPVideoAdPlayer;
import com.tradplus.ads.base.common.TPVideoProgressUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// IMA's  & Adx's VideoAdPlayer interface
public class NewVideoAdPlayerAdapter implements TPVideoAdPlayer {

    private static final String TAG = "NewVideoAdPlayerAdapter";
    public static long POLLING_TIME_MS = 250; // 可自定义
    public static long INITIAL_DELAY_MS = 250;
    private final VideoView videoPlayer;
    private final AudioManager audioManager;
    private boolean mMute;
    private final List<TPVideoAdPlayerCallback> adCallbacks = new ArrayList<>();
    private Timer timer;
    private int adDuration;

    // The saved ad position, used to resumed ad playback following an ad click-through.
    private int savedAdPosition = 0;
    private int savedContentPosition = 0;

    private TPAdMediaInfo adMediaInfo;
    private boolean isAdDisplayed = false;


    public NewVideoAdPlayerAdapter(VideoView videoPlayer, AudioManager audioManager, boolean mute) {
        this.videoPlayer = videoPlayer;

        this.audioManager = audioManager;
        this.mMute = mute;
    }

    @Override
    public void loadAd(TPAdMediaInfo info, Object adPodInfo) {
        Log.i(TAG, "loadad: ");
        adMediaInfo = info;
        isAdDisplayed = false;
    }

    @Override
    public void pauseAd(TPAdMediaInfo adMediaInfo) {
        savePosition();
        stopAdTracking();
        Log.i(TAG, "pauseAd: " + savedAdPosition);
        if (isAdDisplayed) {
            for (TPVideoAdPlayerCallback callback : adCallbacks) {
                Log.i(TAG, "onPause: ");
                callback.onPause(adMediaInfo);
            }
        }
        // 暂停
        videoPlayer.pause();
    }

    @Override
    public void playAd(TPAdMediaInfo adMediaInfo) {
        Log.i(TAG, "playad: ");
        videoPlayer.setVideoURI(Uri.parse(adMediaInfo.getUrl()));
        videoPlayer.setOnCompletionListener(
                (MediaPlayer mediaPlayer) -> notifyImaOnContentCompleted());
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
            for (TPVideoAdPlayerCallback callback : adCallbacks) {
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
    public void stopAd(TPAdMediaInfo adMediaInfo) {
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
    public void addCallback(TPVideoAdPlayerCallback videoAdPlayerCallback) {
        Log.i(TAG, "addCallback: ");
        adCallbacks.add(videoAdPlayerCallback);
    }

    @Override
    public void removeCallback(TPVideoAdPlayerCallback videoAdPlayerCallback) {
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
                        TPVideoProgressUpdate progressUpdate = getAdProgress();
                        notifyImaSdkAboutAdProgress(progressUpdate);
                    }
                };

        timer.schedule(updateTimerTask, POLLING_TIME_MS, INITIAL_DELAY_MS);
    }


    public void notifyImaSdkAboutAdEnded() {
        Log.i(TAG, "notifyImaSdkAboutAdEnded");
        savedAdPosition = 0;
        for (TPVideoAdPlayerCallback callback : adCallbacks) {
            callback.onEnded(adMediaInfo);
        }
    }

    public void notifyImaSdkAboutAdProgress(TPVideoProgressUpdate adProgress) {
        for (TPVideoAdPlayerCallback callback : adCallbacks) {
            callback.onAdProgress(adMediaInfo, adProgress);
        }
    }

    /**
     * @param errorType Media player's error type as defined at
     *                  https://cs.android.com/android/platform/superproject/+/master:frameworks/base/media/java/android/media/MediaPlayer.java;l=4335
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
        for (TPVideoAdPlayerCallback callback : adCallbacks) {
            callback.onEnded(adMediaInfo);
        }
        return true;
    }


    public void notifyImaOnContentCompleted() {
        Log.i(TAG, "notifyImaOnContentCompleted");
        for (TPVideoAdPlayerCallback callback : adCallbacks) {
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

    public TPVideoProgressUpdate getAdProgress() {

        if (!isAdDisplayed || adDuration <= 0) {
            return TPVideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        try {
            adPosition = videoPlayer.getCurrentPosition();
            Log.i(TAG, "getAdProgress adPosition: " + adPosition);
            return new TPVideoProgressUpdate(adPosition, adDuration);
        } catch (Throwable throwable) {
            return TPVideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }

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
            savedAdPosition = (int) adPosition;
            Log.i(TAG, "savePosition: savedAdPosition ：" + savedAdPosition);
        } else {
            savedContentPosition = (int) adPosition;
            Log.i(TAG, "savePosition: savedContentPosition ： " + savedContentPosition);
        }
    }

    public boolean getIsAdDisplayed() {
        return isAdDisplayed;
    }

}
