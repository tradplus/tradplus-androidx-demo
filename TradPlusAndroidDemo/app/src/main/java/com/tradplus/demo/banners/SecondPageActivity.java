package com.tradplus.demo.banners;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tradplus.demo.R;

public class SecondPageActivity extends AppCompatActivity {

    private ViewGroup adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);

        adContainer = findViewById(R.id.ad_container);
        loadBanner();
    }

    private void loadBanner() {
        BannerUtils bannerUtils = BannerUtils.getInstance();
        if (bannerUtils.isReady()) {
            bannerUtils.showBanner(adContainer);
        }else{
            Toast.makeText(SecondPageActivity.this, "无可用广告 or 已经展示", Toast.LENGTH_SHORT).show();
        }
    }
}