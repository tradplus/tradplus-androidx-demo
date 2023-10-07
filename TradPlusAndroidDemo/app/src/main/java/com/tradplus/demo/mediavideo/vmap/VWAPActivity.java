package com.tradplus.demo.mediavideo.vmap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.tradplus.demo.R;

public class VWAPActivity extends Activity {

    private VideoView videoView;
    private String URL = "https://storage.googleapis.com/gvabox/media/samples/stock.mp4";
    private ViewGroup mContainer;
    private VideoPlayerController videoPlayerController;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_vwap);

        initView();
        initVideo();
    }

    private void initVideo() {
        VideoPlayerWithAdPlayback videoPlayerWithAdPlayback =
                findViewById(R.id.videoPlayerWithAdPlayback);

        VideoPlayerController.Logger logger =
                new VideoPlayerController.Logger() {
                    @Override
                    public void log(String message) {
                    }
                };
        videoPlayerController =
                new VideoPlayerController(
                        this,
                        videoPlayerWithAdPlayback,
                        findViewById(R.id.playButton),
                        null,
                        "en",
                        mContainer,
                        logger);
        videoPlayerController.setContentVideo(URL);

    }

    private void initView() {
        videoView = findViewById(R.id.videoPlayer);
        mContainer = findViewById(R.id.adUiContainer);

    }

    @Override
    protected void onResume() {
        if (videoPlayerController != null) {
            videoPlayerController.resume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (videoPlayerController != null) {
            videoPlayerController.pause();
        }
        super.onPause();
    }
}
