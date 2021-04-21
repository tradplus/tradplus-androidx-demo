package com.tradplus.demo.offerwall;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.offerwall.OfferWallAdListener;
import com.tradplus.ads.open.offerwall.TPOfferWall;
import com.tradplus.demo.R;
import com.tradplus.demo.interstititals.InterstitialActivity;
import com.tradplus.utils.TestAdUnitId;


public class OfferWallActivity extends AppCompatActivity {


    Button show,load;
    TextView tv;
    TPOfferWall tpOfferWall;
    private static final String TAG = "OfferWallActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        tv = findViewById(R.id.tv);
        load = findViewById(R.id.load);
        show = findViewById(R.id.show);

        tpOfferWall = new TPOfferWall(OfferWallActivity.this, TestAdUnitId.OFFERWALL_ADUNITID,false);
        tpOfferWall.setAdListener(new OfferWallAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded: ");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression: ");
            }

            @Override
            public void onAdFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdFailed: ");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed: ");
            }

            @Override
            public void onAdReward(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdReward: ");
            }
        });


        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpOfferWall.loadAd();
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有可用广告
                if (!tpOfferWall.isReady()) {
                    Log.i(TAG, "isReady: 无可用广告");
                    tv.setText("isReady: 无可用广告");
                }else{
                    //展示
                    tpOfferWall.showAd(OfferWallActivity.this, "");
                    Log.i(TAG, "showAd: 展示");
                }
            }
        });

    }


    @Override
    protected void onResume() {

        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        //释放资源

        super.onDestroy();
    }


}
