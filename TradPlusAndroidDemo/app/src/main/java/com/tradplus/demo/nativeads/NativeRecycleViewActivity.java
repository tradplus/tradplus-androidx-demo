package com.tradplus.demo.nativeads;

import static com.tradplus.utils.TestAdUnitId.NATIVE_ADUNITID;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.mgr.nativead.TPCustomNativeAd;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.demo.R;

import java.util.ArrayList;
import java.util.List;

public class NativeRecycleViewActivity extends Activity {
    private RecyclerView recycler_native;
    private TPNative tpNative;
    private List<TPCustomNativeAd> mData = new ArrayList<>();
    private NativeRecycleViewAdapter adapter;
    private int newState;
    private int firstVisible;
    private int lastVisible;

    public static final int INTERVAL = 5;
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
        recycler_native = findViewById(R.id.recycler_native);
        recycler_native.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_native.addItemDecoration(new SpacesItemDecoration(1));

        recycler_native.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                NativeRecycleViewActivity.this.newState = newState;
                checkNeedAddNativeAdToData();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        adapter = new NativeRecycleViewAdapter(this, mData);
        recycler_native.setAdapter(adapter);
    }

    private void checkNeedAddNativeAdToData() {
        if (newState == 0) {
            setStopScrollVisibleCount();

            addNativeAdToData();
        }
    }

    private void setStopScrollVisibleCount() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recycler_native.getLayoutManager();
        if (layoutManager != null) {
            firstVisible = layoutManager.findFirstVisibleItemPosition();
            lastVisible = layoutManager.findLastVisibleItemPosition();
        }
    }

    //可视范围内判断是否需要添加
    private void addAndReloadAds(int i) {
        TPCustomNativeAd tpCustomNativeAd;
        if (i % INTERVAL == 0 && i != 0) {
            if (mData.get(i) == null) {
                tpCustomNativeAd = getNativeAd();
                mData.set(i, tpCustomNativeAd);
                adapter.notifyItemChanged(i);
                loadAd();
            }
        }

    }

    private void addNativeAdToData() {
        if(addAdsStatus == STATUS_INSIDE){
            for (int i = firstVisible; i < lastVisible; i++) {
                addAndReloadAds(i);
            }
        }else {
            if (addAdsStatus == STATUS_UP || addAdsStatus == STATUS_UPDOWN) {
                int upIndex = firstVisible < INTERVAL ? 0 : firstVisible - INTERVAL;
                for (int i = upIndex; i < firstVisible; i++) {
                    addAndReloadAds(i);
                }
            }
            if (addAdsStatus == STATUS_DOWN || addAdsStatus == STATUS_UPDOWN) {
                int downEndIndex = lastVisible + INTERVAL > mData.size() ? mData.size() : lastVisible + INTERVAL;
                for (int i = lastVisible; i < downEndIndex; i++) {
                    addAndReloadAds(i);
                }
            }
        }

    }

    private void initNative() {
        tpNative = new TPNative(this, NATIVE_ADUNITID);
        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                checkNeedAddNativeAdToData();
            }
        });
    }

    private TPCustomNativeAd getNativeAd() {
        TPCustomNativeAd tpCustomNativeAd = null;
        if (tpNative != null) {
            tpCustomNativeAd = tpNative.getNativeAd();
            if (tpCustomNativeAd == null) {
                loadAd();
            }
        }
        return tpCustomNativeAd;
    }

    private void loadAd() {
        if (tpNative != null && !tpNative.isReady()) {
            tpNative.loadAd();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tpNative != null){
            tpNative.onDestroy();
        }
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
}
