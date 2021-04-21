package com.tradplus.demo.nativeads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.common.util.LogUtil;
import com.tradplus.ads.mgr.nativead.TPCustomNativeAd;
import com.tradplus.ads.mobileads.util.TradPlusListNativeOption;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NativeSlotActivity extends AppCompatActivity {

    private TradPlusListNativeOption option;
    private RadioGroup mRadioGroupManager;
    private RadioGroup mRadioGroupOri;
    private int mScrollOrientation = RecyclerView.VERTICAL;
    private RecyclerView mListView;
    private List<TPCustomNativeAd> mData;
    private TPAdapter myAdapter;
    private Button btn_add,btn_start;
    private CheckBox check_layout;

    private static int LIST_ITEM_COUNT = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nativelistview);


        //参数一interval，表示间隔
        //参数二maxLength 表示当前列表可支持的最大行数
        option = new TradPlusListNativeOption(6,50);
        LIST_ITEM_COUNT = option.getMaxLength();

        initListView();
    }

    private int loadAdNum = 0;
    private void loadListAd(int startIndex){
        for(int i = 0; i < LIST_ITEM_COUNT; i++) {
            mData.add(null);
        }
        myAdapter.notifyDataSetChanged();
        loadAdNum = startIndex;

        TPNative tpNative = new TPNative(this, TestAdUnitId.NATIVE_ADUNITID);
        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                LogUtil.ownShow("onAdClicked = " + tpAdInfo.toString());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                LogUtil.ownShow("onAdImpression = " + tpAdInfo.toString());
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                LogUtil.ownShow("onAdLoadFailed = " + error.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
            }

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                loadAdNum++;

                int index = option.getInterval() * loadAdNum - 1;
                LogUtil.ownShow("random = " + index);
                if(loadAdNum > mData.size() || index >= mData.size()) {
                    loadAdNum--;
                    return;
                }
                mData.set(index, tpNative.getNativeAd());
                tpNative.loadAd();
                myAdapter.notifyDataSetChanged();
            }
        });
        tpNative.loadAd();
    }

    private String compareAdSources(Object obj){
        if(obj instanceof com.mopub.nativeads.StaticNativeAd){
            return "mopub";
        }else if(obj instanceof com.google.android.gms.ads.formats.NativeContentAd){
            return "admob";
        }else if(obj instanceof com.google.android.gms.ads.formats.UnifiedNativeAd){
            return "admob new";
        }else if(obj instanceof com.facebook.ads.NativeAd){
            return "facebook";
        }else if(obj instanceof com.facebook.ads.NativeBannerAd){
            return "facebook nativebanner";
        }else if(obj instanceof com.kwad.sdk.api.KsDrawAd){
            return "kwads";
        }else if(obj instanceof com.bytedance.sdk.openadsdk.TTNativeExpressAd){
            return "pangle(cn)/toutiao";
        }else if(obj instanceof com.qq.e.comm.pi.AdData){
            return "tencent ads";
        }else{
            return "unknown";
        }
    }

    @SuppressWarnings("RedundantCast")
    private void initListView() {
        mListView = (RecyclerView) findViewById(R.id.my_list);
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mData = new ArrayList<>();
        myAdapter = new TPAdapter(this, mData);
        mListView.setAdapter(myAdapter);



        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadListAd(loadAdNum);
            }
        });
        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadListAd(0);
            }
        });

        check_layout = findViewById(R.id.check_layout);
    }

    private void initSelectMode() {
        mRadioGroupManager = (RadioGroup) findViewById(R.id.rg_fra_group);
        mRadioGroupOri = (RadioGroup) findViewById(R.id.rg_fra_group_orientation);


        mRadioGroupManager.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mListView == null || mData == null || myAdapter == null) {
                    return;
                }

                RecyclerView.LayoutManager manager = null;
                mRadioGroupOri.setVisibility(View.VISIBLE);
                switch (checkedId) {
                    case R.id.rb_fra_linear:
                        manager = new LinearLayoutManager(NativeSlotActivity.this, mScrollOrientation, false);
                        break;
                    case R.id.rb_fra_grid:
                        mRadioGroupOri.setVisibility(View.GONE);
                        manager = new GridLayoutManager(NativeSlotActivity.this, 2);
                        break;
                    case R.id.rb_fra_staggered:
                        manager = new StaggeredGridLayoutManager(2, mScrollOrientation);
                        break;
                }
                if (manager != null) {
                    mListView.setLayoutManager(manager);
                    mData.clear();
                    myAdapter.notifyDataSetChanged();
                    loadListAd(0);
                }
            }
        });

        mRadioGroupOri.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mListView == null || mData == null || myAdapter == null) {
                    return;
                }

                RecyclerView.LayoutManager manager = mListView.getLayoutManager();
                if (manager != null) {
                    if (checkedId == R.id.rb_fra_orientation_v) {
                        mScrollOrientation = RecyclerView.VERTICAL;
                    } else if (checkedId == R.id.rb_fra_orientation_h) {
                        mScrollOrientation = RecyclerView.HORIZONTAL;
                    }

                    if (manager instanceof LinearLayoutManager) {
                        ((LinearLayoutManager) manager).setOrientation(mScrollOrientation);
                    } else if (manager instanceof StaggeredGridLayoutManager) {
                        ((StaggeredGridLayoutManager) manager).setOrientation(mScrollOrientation);
                    }
                    mData.clear();
                    myAdapter.notifyDataSetChanged();
                    loadListAd(0);
                }
            }
        });
    }

    public class TPAdapter extends RecyclerView.Adapter {

        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_TRADPLUS = 6;//竖版图片

        private List<TPCustomNativeAd> mData;
        private Context mContext;
        private RecyclerView mRecyclerView;

        public TPAdapter(Context context, List<TPCustomNativeAd> data) {
            this.mContext = context;
            this.mData = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ITEM_VIEW_TYPE_TRADPLUS) {
                return new AdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad, parent,false));
            }
            return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_normal, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int count = mData.size();
            if (holder instanceof NormalViewHolder) {
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                normalViewHolder.idle.setText("Recycler item " + position);
//                normalViewHolder.idle.setTextColor(getColorRandom());
            } else {
                LogUtil.ownShow("-----position = "+position);
                AdViewHolder adViewHolder = (AdViewHolder) holder;

                TPCustomNativeAd tpNativeAd = mData.get(position);
                ViewGroup adCardView = (ViewGroup) adViewHolder.itemView;



                tpNativeAd.showAd(adCardView, R.layout.native_ad_list_item, "");

            }
        }

        private int getColorRandom() {
            int a = Double.valueOf(13 * 255).intValue();
            int r = Double.valueOf(13 * 255).intValue();
            int g = Double.valueOf(13 * 255).intValue();
            int b = Double.valueOf(13 * 255).intValue();
            return Color.argb(a, r, g, b);
        }


        @Override
        public int getItemCount() {
            int count = mData == null ? 0 : mData.size();
            return count;
        }

        @Override
        public int getItemViewType(int position) {
            if (mData != null) {
//                int count = mData.size();
//                if (position >= count) {
//                    return ITEM_VIEW_TYPE_LOAD_MORE;
//                } else {
                TPCustomNativeAd ad = mData.get(position);
                if (ad == null) {
                    return ITEM_VIEW_TYPE_NORMAL;
                } else {
                    return ITEM_VIEW_TYPE_TRADPLUS;
                }
//                }

            }
            return super.getItemViewType(position);
        }

        private class AdViewHolder extends RecyclerView.ViewHolder {
            LinearLayout tradPlusView;

            public AdViewHolder(View itemView) {
                super(itemView);

                tradPlusView = (LinearLayout) itemView.findViewById(R.id.tpview);
            }
        }

        private class NormalViewHolder extends RecyclerView.ViewHolder {
            TextView idle;

            @SuppressWarnings("RedundantCast")
            public NormalViewHolder(View itemView) {
                super(itemView);

                idle = (TextView) itemView.findViewById(R.id.text_idle);

            }
        }

    }

    private String printConfigMap(Map<String, String> map){
        String s = "";
        if(map != null) {
            List<String> keys = new ArrayList(map.keySet());
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = map.get(key);
                s += "key = "+key+" value = "+value+";";
            }
        }
        return s;
    }
}
