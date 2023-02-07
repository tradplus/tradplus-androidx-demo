package com.tradplus.demo.nativeads;

import static com.tradplus.utils.TestAdUnitId.NATIVEBANNER_ADUNITID;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.nativead.TPNativeAdRender;
import com.tradplus.ads.open.nativead.TPNativeBanner;
import com.tradplus.demo.R;

import java.util.ArrayList;
import java.util.List;

public class NativeBannerRecycleViewActivity extends Activity {
    private RecyclerView recycler_nativebanner;
    private TPNativeBanner tpNativeBanner;
    private List<View> mData = new ArrayList<>();
    private NativeBannerRecycleViewAdapter adapter;
    private int newState;
    private int firstVisible;
    private int lastVisible;

    public static final int INTERVAL = 15;
    //0 inside; 1 up; 2;down;3 up down
    public static final int STATUS_INSIDE = 0;
    public static final int STATUS_UP = 1;
    public static final int STATUS_DOWN = 2;
    public static final int STATUS_UPDOWN = 3;

    private int addAdsStatus = STATUS_INSIDE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_native_recycle);

        initData();
        initView();
        initNative();
    }

    private void initData() {
        for (int i = 0; i < 200; i++) {
            mData.add(null);
        }
    }

    private void initView() {
        recycler_nativebanner = findViewById(R.id.recycler_native);
        recycler_nativebanner.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_nativebanner.addItemDecoration(new SpacesItemDecoration(1));
        recycler_nativebanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                NativeBannerRecycleViewActivity.this.newState = newState;
                checkNeedAddNativeAdToData();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        adapter = new NativeBannerRecycleViewAdapter(this, mData);
        recycler_nativebanner.setAdapter(adapter);
    }

    private void checkNeedAddNativeAdToData() {
        if (newState == 0) {
            setStopScrollVisibleCount();

            addNativeAdToData();
        }
    }

    private void setStopScrollVisibleCount() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recycler_nativebanner.getLayoutManager();
        if (layoutManager != null) {
            firstVisible = layoutManager.findFirstVisibleItemPosition();
            lastVisible = layoutManager.findLastVisibleItemPosition();
        }
    }

    //可视范围内判断是否需要添加
    private boolean addAndReloadAds(int i) {
        boolean isReady = tpNativeBanner.isReady();

        if(!isReady){
            return false;
        }
        if (i % INTERVAL == 0 && i != 0) {
            if (mData.get(i) == null) {
                if(tpNativeBanner != null) {
                    FrameLayout frameLayout = new FrameLayout(this);
                    frameLayout.addView(tpNativeBanner);
                    tpNativeBanner.showAd();
                    mData.set(i, frameLayout);
                    adapter.notifyItemChanged(i);
                    loadAd();
                }
            }
        }

        return isReady;
    }

    private void addNativeAdToData() {
        boolean isReady;
        if (addAdsStatus == STATUS_INSIDE) {
            for (int i = firstVisible; i < lastVisible; i++) {
                isReady = addAndReloadAds(i);
                if(!isReady){
                    loadAd();
                    break;
                }
            }
        } else {
            if (addAdsStatus == STATUS_UP || addAdsStatus == STATUS_UPDOWN) {
                int upIndex = firstVisible < INTERVAL ? 0 : firstVisible - INTERVAL;
                for (int i = upIndex; i < firstVisible; i++) {
                    isReady = addAndReloadAds(i);
                    if(!isReady){
                        loadAd();
                        break;
                    }
                }
            }
            if (addAdsStatus == STATUS_DOWN || addAdsStatus == STATUS_UPDOWN) {
                int downEndIndex = lastVisible + INTERVAL > mData.size() ? mData.size() : lastVisible + INTERVAL;
                for (int i = lastVisible; i < downEndIndex; i++) {
                    isReady = addAndReloadAds(i);
                    if(!isReady){
                        loadAd();
                        break;
                    }
                }
            }
        }

    }

    private void initNative() {
        loadAd();
    }

    private BannerAdListener bannerAdListener = new BannerAdListener() {
        @Override
        public void onAdClicked(TPAdInfo tpAdInfo) {
        }

        @Override
        public void onAdImpression(TPAdInfo tpAdInfo) {
        }

        @Override
        public void onAdLoaded(TPAdInfo tpAdInfo) {
        }

        @Override
        public void onAdLoadFailed(TPAdError error) {
        }

        @Override
        public void onAdClosed(TPAdInfo tpAdInfo) {
        }

        @Override
        public void onAdShowFailed(TPAdError error, TPAdInfo tpAdInfo) {

        }
    };

    private void loadAd() {
        tpNativeBanner = new TPNativeBanner(this);
        tpNativeBanner.closeAutoShow();
        tpNativeBanner.setAutoDestroy(false);
        tpNativeBanner.setNativeAdRender(new CustomAdRender());
        tpNativeBanner.setAdListener(bannerAdListener);
        tpNativeBanner.loadAd(NATIVEBANNER_ADUNITID);
    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;//空白间隔

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;//左边空白间隔
            outRect.right = space;//右边空白间隔
            outRect.bottom = space;//下方空白间隔
            outRect.top = space;//上方空白间隔
        }
    }

    public class CustomAdRender extends TPNativeAdRender {
        @Override
        public ViewGroup createAdLayoutView() {
            LayoutInflater inflater = (LayoutInflater) NativeBannerRecycleViewActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup adLayout = (ViewGroup) inflater.inflate(R.layout.tp_demo_native_banner_ad_unit, null);

            // 设置标题
            TextView nativeTitleView = adLayout.findViewById(R.id.tp_native_title);
            setTitleView(nativeTitleView, true);

            // 设置内容
            TextView nativeSubTitleView = adLayout.findViewById(R.id.tp_native_text);
            setSubTitleView(nativeSubTitleView, true);

            // 设置下载按钮
            TextView nativeCTAView = adLayout.findViewById(R.id.tp_native_cta_btn);
            setCallToActionView(nativeCTAView, true);

            // 设置icon
            ImageView nativeIconImageView = adLayout.findViewById(R.id.tp_native_icon_image);
            setIconView(nativeIconImageView, true);

            // 设置角标
            FrameLayout adChoiceView = adLayout.findViewById(R.id.tp_ad_choices_container);
            setAdChoicesContainer(adChoiceView, false);

            // 设置main AdChoice
            ImageView nativeAdChoice = adLayout.findViewById(R.id.tp_native_ad_choice);
            setAdChoiceView(nativeAdChoice, true);

            return adLayout;
        }
    }
}
