package com.tradplus.demo.banners;

import static com.tradplus.utils.TestAdUnitId.BANNER_ADUNITID;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.banner.TPBanner;
import com.tradplus.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Banner 在 RecyclerView 中展示：
 * 1. 按「当前可视页面」展示：滑动停止后，若本屏尚无广告则在可视区域内插入一条
 * 2. 广告滑出可视区域时销毁
 * 3. 滑回后重新加载并展示新的横幅广告
 */
public class BannerRecycleViewActivity extends Activity {

    private RecyclerView recyclerBanner;
    private final List<View> mData = new ArrayList<>();
    private BannerRecycleViewAdapter adapter;
    private TPBanner readyBanner;
    private final Map<Integer, TPBanner> activeBanners = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_native_recycle);
        initData();
        initView();
        loadBanner();
    }

    private void initData() {
        for (int i = 0; i < 200; i++) {
            mData.add(null);
        }
    }

    private void initView() {
        recyclerBanner = findViewById(R.id.recycler_native);
        recyclerBanner.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerBanner.addItemDecoration(new SpacesItemDecoration(1));
        recyclerBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    destroyAdsOutsideVisibleRange();
                    showBannerAdsInVisibleRange();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                destroyAdsOutsideVisibleRange();
            }
        });

        adapter = new BannerRecycleViewAdapter(this, mData);
        recyclerBanner.setAdapter(adapter);
        recyclerBanner.post(this::showBannerAdsInVisibleRange);
    }

    private int[] getVisibleRange() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerBanner.getLayoutManager();
        if (layoutManager == null) {
            return new int[]{0, 0};
        }
        return new int[]{
                layoutManager.findFirstVisibleItemPosition(),
                layoutManager.findLastVisibleItemPosition()
        };
    }

    private boolean hasActiveAdInVisibleRange(int firstVisible, int lastVisible) {
        for (int i = firstVisible; i <= lastVisible; i++) {
            if (mData.get(i) != null) {
                return true;
            }
        }
        return false;
    }

    private int findAdPositionInVisibleRange(int firstVisible, int lastVisible) {
        int center = firstVisible + (lastVisible - firstVisible) / 2;
        if (center >= 0 && center < mData.size() && mData.get(center) == null) {
            return center;
        }
        for (int i = firstVisible; i <= lastVisible; i++) {
            if (mData.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    private void destroyAdsOutsideVisibleRange() {
        int[] range = getVisibleRange();
        int firstVisible = range[0];
        int lastVisible = range[1];

        List<Integer> toDestroy = new ArrayList<>();
        for (Integer position : activeBanners.keySet()) {
            if (position < firstVisible || position > lastVisible) {
                toDestroy.add(position);
            }
        }

        for (Integer position : toDestroy) {
            destroyAdAt(position);
        }
    }

    private void destroyAdAt(int position) {
        TPBanner banner = activeBanners.remove(position);
        if (banner != null) {
            if (banner.getParent() != null) {
                ((ViewGroup) banner.getParent()).removeView(banner);
            }
            banner.onDestroy();
        }
        if (position >= 0 && position < mData.size()) {
            mData.set(position, null);
            adapter.notifyItemChanged(position);
        }
    }

    private void showBannerAdsInVisibleRange() {
        int[] range = getVisibleRange();
        int firstVisible = range[0];
        int lastVisible = range[1];
        if (firstVisible < 0 || lastVisible < 0) {
            return;
        }
        if (hasActiveAdInVisibleRange(firstVisible, lastVisible)) {
            return;
        }
        int adPosition = findAdPositionInVisibleRange(firstVisible, lastVisible);
        if (adPosition < 0) {
            return;
        }
        if (readyBanner != null && readyBanner.isReady()) {
            showAdAt(adPosition, readyBanner);
            readyBanner = null;
            loadBanner();
        } else {
            loadBanner();
        }
    }

    private void showAdAt(int position, TPBanner banner) {
        FrameLayout container = new FrameLayout(this);
        container.addView(banner);
        banner.showAd();
        mData.set(position, container);
        activeBanners.put(position, banner);
        adapter.notifyItemChanged(position);
    }

    private final BannerAdListener bannerAdListener = new BannerAdListener() {
        @Override
        public void onAdClicked(TPAdInfo tpAdInfo) {
        }

        @Override
        public void onAdImpression(TPAdInfo tpAdInfo) {
        }

        @Override
        public void onAdLoaded(TPAdInfo tpAdInfo) {
            recyclerBanner.post(() -> {
                destroyAdsOutsideVisibleRange();
                showBannerAdsInVisibleRange();
            });
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

    private void loadBanner() {
        if (readyBanner != null) {
            return;
        }
        readyBanner = new TPBanner(this);
        readyBanner.closeAutoShow();
        readyBanner.setAutoDestroy(false);
        readyBanner.setAdListener(bannerAdListener);
        readyBanner.loadAd(BANNER_ADUNITID);
    }

    @Override
    protected void onDestroy() {
        for (Integer position : new ArrayList<>(activeBanners.keySet())) {
            destroyAdAt(position);
        }
        if (readyBanner != null) {
            readyBanner.onDestroy();
            readyBanner = null;
        }
        super.onDestroy();
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private final int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;
        }
    }
}
