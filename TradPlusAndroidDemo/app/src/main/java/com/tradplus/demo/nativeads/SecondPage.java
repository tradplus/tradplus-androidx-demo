package com.tradplus.demo.nativeads;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tradplus.demo.R;
import com.tradplus.demo.banners.BannerUtils;
import com.tradplus.demo.banners.SecondPageActivity;
import com.tradplus.utils.TestAdUnitId;

public class SecondPage extends AppCompatActivity {

    private ViewGroup adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);

        adContainer = findViewById(R.id.ad_container);
        Bundle extras = getIntent().getExtras();
        if(TestAdUnitId.TYPE_NATIVE.equals(extras.get("type"))) {
            loadNative();
        }

        if (TestAdUnitId.TYPE_NATIVEBANNER.equals(extras.get("type"))) {
            loadNativeBanner();
        }
    }

    private void loadNative() {
        NativeUtils nativerUtils = NativeUtils.getInstance();
        if (nativerUtils.isReady()) {
            nativerUtils.showNative(adContainer);
        }else{
            Toast.makeText(SecondPage.this, "无可用广告 or 已经展示", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNativeBanner() {
        NativeUtils nativerUtils = NativeUtils.getInstance();
        if (nativerUtils.isReadyNativeBanner()) {
            nativerUtils.showNativeBanner(adContainer);
        }else{
            Toast.makeText(SecondPage.this, "无可用广告 or 已经展示", Toast.LENGTH_SHORT).show();
        }
    }
}