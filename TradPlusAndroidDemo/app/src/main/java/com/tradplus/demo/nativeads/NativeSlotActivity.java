package com.tradplus.demo.nativeads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tradplus.ads.base.adapter.nativead.TPNativeAdView;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.base.common.TPImageLoader;
import com.tradplus.ads.common.util.LogUtil;
import com.tradplus.ads.mgr.nativead.TPCustomNativeAd;
import com.tradplus.ads.mobileads.util.TradPlusListNativeOption;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.ads.open.nativead.TPNativeAdRender;
import com.tradplus.demo.R;
import com.tradplus.utils.TestAdUnitId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * nativeslot是标准原生的一种使用场景，不是一种广告类型。就是在listview或者recyclerview中展示native广告（这里只做基础演示，原理类似，具体要根据开发者的场景来做调试）
 * 这种场景开发者可能会有一次请求多个广告的需求，所以写这个demo做演示
 * native的更多详细功能请参考NativeActivity中的用法
 */
public class NativeSlotActivity extends AppCompatActivity {
    private static final String AD_TAG = "ad";
    private RecyclerView mListView;
    private List<TPCustomNativeAd> mAdData = new ArrayList<>();
    private List<Object> mItemData = new ArrayList<>();
    private TPAdapter myAdapter;
    private Button btn_add;

    private static final int LIST_ITEM_COUNT = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nativelistview);

        // init list content
        addContent(0);
        initListView();
        loadOneAd();
    }

    private void loadOneAd(){
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
                // 保存ad，并通知adapter更新数据
                mAdData.add(tpNative.getNativeAd());
                myAdapter.notifyDataSetChanged();
            }
        });
        tpNative.loadAd();
    }

    @SuppressWarnings("RedundantCast")
    private void initListView() {
        mListView = (RecyclerView) findViewById(R.id.my_list);
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        myAdapter = new TPAdapter(this, mItemData, mAdData);
        mListView.setAdapter(myAdapter);

        // 模拟更新数据
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mItemData.size();
                addContent(index);
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 模拟数据更新
     * 提前把广告位置做好标记，占位
     * @param startIndex 初始位
     */
    private void addContent(int startIndex) {
        for(int i = startIndex; i < startIndex + 20; i++) {
            if(i % LIST_ITEM_COUNT == 0) {
                mItemData.add(AD_TAG);
            } else {
                mItemData.add("index:" + i);
            }
        }
    }

    public class TPAdapter extends RecyclerView.Adapter {

        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_TRADPLUS = 6;//ad

        private List<TPCustomNativeAd> mAds;
        private List<Object> mContents;
        private Context mContext;

        public TPAdapter(Context context, List<Object> data, List<TPCustomNativeAd> ads) {
            this.mContext = context;
            this.mContents = data;
            this.mAds = ads;
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
            if (holder instanceof NormalViewHolder) {
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                normalViewHolder.idle.setText((String)mContents.get(position));
            } else {
                // 这里有两种情况，一种是有广告，一种是没广告，没广告的时候需要把占位的item隐藏
                LogUtil.ownShow("-----position = "+position);
                AdViewHolder adViewHolder = (AdViewHolder) holder;
                ViewGroup adCardView = (ViewGroup) adViewHolder.itemView;
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) adCardView.getLayoutParams();
                if(mContents.get(position) instanceof String) {
                    params.height = 0;
                    params.width = 0;
                    adCardView.setVisibility(View.GONE);
                } else if(mContents.get(position) instanceof TPCustomNativeAd) {
                    TPCustomNativeAd tpNativeAd = (TPCustomNativeAd) mContents.get(position);
                    params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                    adCardView.setVisibility(View.VISIBLE);
                    tpNativeAd.showAd(adCardView,new TPNativeAdRender() {

                        // 获取广告布局文件，用自定义渲染的方式可以自己写布局的id
                        @Override
                        public ViewGroup createAdLayoutView() {
                            LayoutInflater inflater = (LayoutInflater) NativeSlotActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            return (ViewGroup) inflater.inflate(R.layout.tp_native_ad_list_item, null);
                        }

                        @Override
                        public ViewGroup renderAdView(TPNativeAdView tpNativeAdView) {
                            ViewGroup viewGroup = createAdLayoutView();

                            ImageView imageView = viewGroup.findViewById(R.id.tp_mopub_native_main_image);
                            if(imageView != null) {
                                if(tpNativeAdView.getMediaView() != null) {
                                    // 如果三方广告平台有mediaview，需要用三方提供的mediaview来替换原来布局中的imageview
                                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                                    ViewParent viewParent = imageView.getParent();
                                    if(viewParent != null) {
                                        ((ViewGroup)viewParent).removeView(imageView);
                                         if (tpNativeAdView.getMediaView().getParent() != null) {
                                             ((ViewGroup)tpNativeAdView.getMediaView().getParent()).removeView(tpNativeAdView.getMediaView());
                                         }
                                        ((ViewGroup)viewParent).addView(tpNativeAdView.getMediaView(), params);
                                    }
                                } else if(tpNativeAdView.getMainImage() != null) {
                                    // 部分三方平台返回的是drawable，可以直接设置
                                    imageView.setImageDrawable(tpNativeAdView.getMainImage());
                                } else if(tpNativeAdView.getMainImageUrl() != null) {
                                    // 其他三方平台返回的是图片的url，需要先下载图片再填充到view中
                                    TPImageLoader.getInstance().loadImage(imageView, tpNativeAdView.getMainImageUrl());
                                }
                            }

                            ImageView iconView = viewGroup.findViewById(R.id.tp_native_icon_image);
                            if(iconView != null) {
                                if(tpNativeAdView.getIconImage() != null) {
                                    iconView.setImageDrawable(tpNativeAdView.getIconImage());
                                } else if(tpNativeAdView.getIconImageUrl() != null){
                                    TPImageLoader.getInstance().loadImage(iconView, tpNativeAdView.getIconImageUrl());
                                }
                            }

                            TextView titleView = viewGroup.findViewById(R.id.tp_native_title);
                            if(titleView != null && tpNativeAdView.getTitle() != null) {
                                titleView.setText(tpNativeAdView.getTitle());
                            }

                            TextView subTitleView = viewGroup.findViewById(R.id.tp_native_text);
                            if(subTitleView != null && tpNativeAdView.getSubTitle() != null) {
                                subTitleView.setText(tpNativeAdView.getSubTitle());
                            }

                            Button callToActionView = viewGroup.findViewById(R.id.tp_native_cta_btn);
                            if(callToActionView != null && tpNativeAdView.getCallToAction() != null) {
                                callToActionView.setText(tpNativeAdView.getCallToAction());
                            }

                            // facebook会需要一个adchoice的容器来填充adchoice
                            FrameLayout adChoiceView = viewGroup.findViewById(R.id.tp_ad_choices_container);

                            // 把主要的元素设置给三方广告平台，第二个参数是是否可以点击
                            setImageView(imageView, true);
                            setIconView(iconView, true);
                            setTitleView(titleView, true);
                            setSubTitleView(subTitleView, true);
                            setCallToActionView(callToActionView, true);
                            setAdChoicesContainer(adChoiceView, false);

                            return viewGroup;
                        }
                    }, "");
                }
                adCardView.setLayoutParams(params);
            }
        }

        @Override
        public int getItemCount() {
            int count = mContents == null ? 0 : mContents.size();
            return count;
        }

        @Override
        public int getItemViewType(int position) {
            if(mContents != null && position < mContents.size()) {
                if(mContents.get(position) instanceof String) {
                    // 如果是广告占位标致，证明还没填充广告，需要从广告的list中拿出一个来添加到list中
                    if(AD_TAG.equals(mContents.get(position))) {
                        if(mAds != null && mAds.size() > 0) {
                            TPCustomNativeAd temp = mAds.get(0);
                            mAds.remove(temp);
                            mContents.set(position, temp);
                            // 消耗掉一个广告后，加载下一个
                            loadOneAd();
                        }
                        return ITEM_VIEW_TYPE_TRADPLUS;
                    }
                    return ITEM_VIEW_TYPE_NORMAL;
                } else if(mContents.get(position) instanceof TPCustomNativeAd) {
                    return ITEM_VIEW_TYPE_TRADPLUS;
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(int i = 0; i < mAdData.size();i++){
            mAdData.get(i).onDestroy();
        }
    }
}
