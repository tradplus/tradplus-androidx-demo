package com.tradplus.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.interactive.InterActiveAdListener;
import com.tradplus.ads.open.interactive.TPInterActive;
import com.tradplus.utils.TestAdUnitId;

import java.util.HashMap;
import java.util.Map;


public class InterActiveActivity extends AppCompatActivity {
    Button mBtnLoad,  mBtnShow;
    TextView tv;
    TPInterActive interActive;
    private LinearLayout layout_interactive;
    private static final String TAG = "tradpluslog";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive);

        tv = findViewById(R.id.tv);
        layout_interactive = findViewById(R.id.layout_interactive);
        mBtnLoad = (Button)findViewById(R.id.load);
        mBtnShow = (Button)findViewById(R.id.show);

        // load按钮
        mBtnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> smap = new HashMap<>();
                smap.put("width", 120);
                smap.put("height", 120);
                smap.put("need_close", true);

                interActive.setCustomParams(smap);
                interActive.loadAd();
            }
        });

        // show按钮
        mBtnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有可用广告
                if (!interActive.isReady()) {
                    Log.i(TAG, "isReady: 无可用广告");
                    tv.setText("isReady: 无可用广告");
                }else{
                    //展示
                    layout_interactive.removeAllViews();
                    View view = interActive.getInterActiveAd();
                    if (view != null) {
                        if (view.getParent() != null) {
                            ((ViewGroup) view.getParent()).removeView(view);
                        }
                        layout_interactive.addView(view);
                        interActive.showAd("");
                        Log.i(TAG, "showAd: 展示");
                    }
                }
            }
        });

        // 初始化广告
        initAd();
    }

    private void initAd() {

        interActive = new TPInterActive(InterActiveActivity.this, TestAdUnitId.INTERACTIVE_ADUNITID);

        interActive.setAdListener(new InterActiveAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {

            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {

            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {


            }

            @Override
            public void onAdFailed(TPAdError tpAdError) {

            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {


            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {

            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {

            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(interActive != null){
           interActive.onDestroy();
        }
    }
}
