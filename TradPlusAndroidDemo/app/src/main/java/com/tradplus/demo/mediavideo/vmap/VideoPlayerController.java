// Copyright 2014 Google Inc. All Rights Reserved.

package com.tradplus.demo.mediavideo.vmap;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.tradplus.ads.base.adapter.mediavideo.TPMediaVideoAdapter;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.common.serialization.JSON;
import com.tradplus.ads.mgr.mediavideo.TPCustomMediaVideoAd;
import com.tradplus.ads.open.mediavideo.MediaVideoAdListener;
import com.tradplus.ads.open.mediavideo.TPMediaVideo;

import java.util.HashMap;
import java.util.Map;

/** Ads logic for handling the IMA SDK integration code and events. */
public class VideoPlayerController {


  /** Log interface, so we can output the log commands to the UI or similar. */
  public interface Logger {
    void log(String logMessage);
  }

  private final String TAG = this.getClass().getSimpleName();


  // Ad-enabled video player.
  private VideoPlayerWithAdPlayback videoPlayerWithAdPlayback;

  // Button the user taps to begin video playback and ad request.

  // VAST ad tag URL to use when requesting ads during video playback.
  private String currentAdTagUrl;

  // URL of content video.
  private String contentVideoUrl;

  // ViewGroup to render an associated companion ad into.
  private ViewGroup companionViewGroup;

  // Tracks if the SDK is playing an ad, since the SDK might not necessarily use the video
  // player provided to play the video ad.
  private boolean isAdPlaying;

  // View that handles taps to toggle ad pause/resume during video playback.

  // View that we can write log messages to, to display in the UI.
  private Logger log;

  private double playAdsAfterTime = -1;

  private boolean videoStarted;
  private ViewGroup mContainer;

  // Inner class implementation of AdsLoader.AdsLoaderListener.
  private Context context;
  public VideoPlayerController(
      Context context,
      VideoPlayerWithAdPlayback videoPlayerWithAdPlayback,
      View playButton,
      View playPauseToggle,
      String language,
      ViewGroup mContainer,
      Logger log) {
    this.videoPlayerWithAdPlayback = videoPlayerWithAdPlayback;
    isAdPlaying = false;
    this.mContainer = mContainer;
    this.log = log;
    this.context = context;

    // When Play is clicked, request ads and hide the button.
    playButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            playButton.setVisibility(View.GONE);
            requestAndPlayAds(-1);
          }
        });
  }

  private void log(String message) {
    if (log != null) {
      log.log(message + "\n");
    }
  }

  private void pauseContent() {
    videoPlayerWithAdPlayback.pauseContentForAdPlayback();
    isAdPlaying = true;
  }

  private void resumeContent() {
    videoPlayerWithAdPlayback.resumeContentAfterAdPlayback();
    isAdPlaying = false;
  }

  /** Set the ad tag URL the player should use to request ads when playing a content video. */
  public void setAdTagUrl(String adTagUrl) {
    currentAdTagUrl = adTagUrl;
  }

  public String getAdTagUrl() {
    return currentAdTagUrl;
  }

  /** Request and subsequently play video ads from the ad server. */
  public void requestAndPlayAds(double playAdsAfterTime) {

    initTPMediaView();
//    // Since we're switching to a new video, tell the SDK the previous video is finished.
//    if (adsManager != null) {
//      adsManager.destroy();
//    }
//
//
//    // Create the ads request.
//    AdsRequest request = sdkFactory.createAdsRequest();
//    request.setAdTagUrl(currentAdTagUrl);
//    request.setContentProgressProvider(videoPlayerWithAdPlayback.getContentProgressProvider());
//
//    this.playAdsAfterTime = playAdsAfterTime;
//
//    // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
//    adsLoader.requestAds(request);

  }
  private TPCustomMediaVideoAd tpCustomMediaVideoAd;

  private TPMediaVideo tpMediaVideo;
  private void initTPMediaView() {
    tpMediaVideo = new TPMediaVideo(context, "682C56A44DB4410BA1D380D51A45495C");// 插播
    Map<String , Object> map = new HashMap<>();
    map.put("ima_content_provider",videoPlayerWithAdPlayback.getContentProgressProvider());
    tpMediaVideo.setCustomParams(map);
    tpMediaVideo.setAdListener(new MediaVideoAdListener() {
      @Override
      public void onAdLoaded(TPAdInfo tpAdInfo) {
        Log.i(TAG, "onAdLoaded: "+JSON.toJSONString(tpAdInfo) );
        boolean ready = tpMediaVideo.isReady();
        if (ready) {
          Log.i(TAG, "isReady: " + ready);
          tpCustomMediaVideoAd = tpMediaVideo.getVideoAd();
        }

        tpCustomMediaVideoAd.setIMAEventListener(new TPMediaVideoAdapter.OnIMAEventListener() {
          @Override
          public void onEvent(Object adEvent) {
            Log.i(TAG,"AdEvent = "+((AdEvent)adEvent).getType());
            switch (((AdEvent)adEvent).getType()) {
              case LOADED:
                // AdEventType.LOADED will be fired when ads are ready to be
                // played. AdsManager.start() begins ad playback. This method is
                // ignored for VMAP or ad rules playlists, as the SDK will
                // automatically start executing the playlist.
                break;
              case CONTENT_PAUSE_REQUESTED:
                // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before
                // a video ad is played.
                pauseContent();
                break;
              case CONTENT_RESUME_REQUESTED:
                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is
                // completed and you should start playing your content.
                resumeContent();
                break;
              case PAUSED:
                isAdPlaying = false;
                videoPlayerWithAdPlayback.enableControls();
                break;
              case RESUMED:
                isAdPlaying = true;
                videoPlayerWithAdPlayback.disableControls();
                break;
              case ALL_ADS_COMPLETED:

                break;
              case AD_BREAK_READY:
                tpCustomMediaVideoAd.start("");

                break;
              case AD_BREAK_FETCH_ERROR:
                log("Ad Fetch Error. Resuming content.");
                // A CONTENT_RESUME_REQUESTED event should follow to trigger content playback.
                break;
              default:
                break;
            }
          }
        });
        tpCustomMediaVideoAd.start("");
      }

      @Override
      public void onAdFailed(TPAdError error) {
        Log.i(TAG, "onAdFailed: " + error.getErrorMsg());
      }

      @Override
      public void onAdClicked(TPAdInfo tpAdInfo) {
        Log.i(TAG, "onAdClicked: " + JSON.toJSONString(tpAdInfo));
        Log.i("Fanny", "onAdClicked: ");
      }

      @Override
      public void onAdResume(TPAdInfo tpAdInfo) {
        Log.i(TAG, "onAdResume: " + JSON.toJSONString(tpAdInfo));
        Log.i("Fanny", "onAdResume: ");
      }

      @Override
      public void onAdPause(TPAdInfo tpAdInfo) {
        Log.i(TAG, "onAdPause: " + JSON.toJSONString(tpAdInfo));
        Log.i("Fanny", "onAdPause: ");
      }

      @Override
      public void onAdVideoStart(TPAdInfo tpAdInfo) {
        Log.i(TAG, "onAdVideoStart: " + JSON.toJSONString(tpAdInfo));
        mContainer.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAdVideoEnd(TPAdInfo tpAdInfo) {
        Log.i(TAG, "onAdVideoEnd: " + JSON.toJSONString(tpAdInfo));
        Log.i("Fanny", "onAdVideoEnd: ");
//                if (tpCustomMediaVideoAd != null) {
//                    tpCustomMediaVideoAd.onDestroy();
//                }
        mContainer.removeAllViews();
      }

      @Override
      public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError error) {
        Log.i(TAG, "onAdVideoError: " + error.getErrorMsg());
      }

      @Override
      public void onAdSkiped(TPAdInfo tpAdInfo) {
        Log.i(TAG, "onAdSkiped: " + JSON.toJSONString(tpAdInfo));
        Log.i("Fanny", "onAdSkiped: ");
//                if (tpCustomMediaVideoAd != null) {
//                    tpCustomMediaVideoAd.onDestroy();
//                }

        mContainer.removeAllViews();
      }

      @Override
      public void onAdTapped(TPAdInfo tpAdInfo) {
        Log.i(TAG, "onAdTapped: " + JSON.toJSONString(tpAdInfo));
        Log.i("Fanny", "onAdTapped: ");
      }

      @Override
      public void onAdProgress(TPAdInfo tpAdInfo, float progress, double totaltime) {
        Log.i(TAG, "onAdProgress: " + progress + ", totaltime :" + totaltime);
      }
    });
    tpMediaVideo.loadAd(mContainer,videoPlayerWithAdPlayback.getVideoAdPlayer());
  }

  /** Remove the play/pause on touch behavior. */
  private void removePlayPauseOnAdTouch() {
//    playPauseToggle.setOnTouchListener(null);
  }

  /**
   * Set metadata about the content video. In more complex implementations, this might more than
   * just a URL and could trigger additional decisions regarding ad tag selection.
   */
  public void setContentVideo(String videoPath) {
    videoPlayerWithAdPlayback.setContentVideoPath(videoPath);
    contentVideoUrl = videoPath;
  }

  public String getContentVideoUrl() {
    return contentVideoUrl;
  }

  /**
   * Save position of the video, whether content or ad. Can be called when the app is paused, for
   * example.
   */
  public void pause() {
    videoPlayerWithAdPlayback.savePosition();
    if (tpCustomMediaVideoAd != null && videoPlayerWithAdPlayback.getIsAdDisplayed()) {
      tpCustomMediaVideoAd.pause();
    } else {
      videoPlayerWithAdPlayback.pause();
    }
  }

  /**
   * Restore the previously saved progress location of the video. Can be called when the app is
   * resumed.
   */
  public void resume() {
    videoPlayerWithAdPlayback.restorePosition();
    if (tpCustomMediaVideoAd != null && videoPlayerWithAdPlayback.getIsAdDisplayed()) {
      tpCustomMediaVideoAd.resume();
    } else {
      videoPlayerWithAdPlayback.play();
    }
  }

  public void destroy() {
    if (tpCustomMediaVideoAd != null) {
      tpCustomMediaVideoAd.onDestroy();
      tpCustomMediaVideoAd = null;
    }
  }

  /** Seeks to time in content video in seconds. */
  public void seek(double time) {
    videoPlayerWithAdPlayback.seek((int) (time * 1000.0));
  }

  /** Returns the current time of the content video in seconds. */
  public double getCurrentContentTime() {
    return ((double) videoPlayerWithAdPlayback.getCurrentContentTime()) / 1000.0;
  }

  public boolean hasVideoStarted() {
    return videoStarted;
  }
}
