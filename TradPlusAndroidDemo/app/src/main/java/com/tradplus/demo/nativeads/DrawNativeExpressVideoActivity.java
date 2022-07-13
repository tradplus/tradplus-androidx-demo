package com.tradplus.demo.nativeads;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.dingmouren.layoutmanagergroup.viewpager.OnViewPagerListener;
import com.dingmouren.layoutmanagergroup.viewpager.ViewPagerLayoutManager;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.common.serialization.JSON;
import com.tradplus.ads.common.util.DeviceUtils;
import com.tradplus.ads.mgr.nativead.TPCustomNativeAd;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.demo.R;
import com.tradplus.utils.NetworkUtils;
import com.tradplus.utils.TestAdUnitId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class DrawNativeExpressVideoActivity extends Activity {

    private static final String TAG = "DrawExpressActivity";
    private static final int TYPE_COMMON_ITEM = 1;
    private static final int TYPE_AD_ITEM = 2;
    private static final int TYPE_UNIFIED_AD_ITEM = 3;
    private RecyclerView mRecyclerView;
    private LinearLayout mBottomLayout;
    private RelativeLayout mTopLayout;
    private MyAdapter mAdapter;
    private ViewPagerLayoutManager mLayoutManager;
    private int[] imgs = {R.mipmap.video11, R.mipmap.video12, R.mipmap.video13, R.mipmap.video14, R.mipmap.img_video_2};
    private int[] videos = {R.raw.video11, R.raw.video12, R.raw.video13, R.raw.video14, R.raw.video_2};
    private Context mContext;
    private List<Item> datas = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<View> mDrawNativeAdList;
    private TPNative mTPNative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置全屏
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Throwable ignore) {
        }

        setContentView(R.layout.activity_draw_native_video);

        // 查看网络设置
        if (NetworkUtils.getNetworkType(this) == NetworkUtils.NetworkType.NONE) {
            return;
        }


        initView();
        initListener();
        mContext = this;

        // 加载draw信息流广告（加载方法和普通native一样，只是loaded结果不同）
        // 这里的adunitid必须使用draw信息流的id，不能用普通native的
        mTPNative = new TPNative(this, TestAdUnitId.DRAW_ADUNITID);
        mTPNative.setAdListener(nativeAdListener);
        mTPNative.loadAd();

    }

    final NativeAdListener nativeAdListener = new NativeAdListener() {
        @Override
        public void onAdClicked(TPAdInfo tpAdInfo) {
            super.onAdClicked(tpAdInfo);
        }

        @Override
        public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
            super.onAdLoaded(tpAdInfo, tpBaseAd);
            Log.i("TradPlusData", "onAdLoaded:" + JSON.toJSONString(tpAdInfo));
            TPCustomNativeAd tpCustomNativeAd = mTPNative.getNativeAd();
            if (tpAdInfo.adSourceName.equals("Tencent Ads")) {
                List<Object> drawNativeAdObjectList = tpCustomNativeAd.getDrawNativeAdObjectList();
                initNativeUnifiedADView(drawNativeAdObjectList);
            } else {
                mDrawNativeAdList = tpCustomNativeAd.getDrawNativeAdList();
                Log.i(TAG, "onAdLoaded: size" + mDrawNativeAdList.size());
                initNativeADView(mDrawNativeAdList);
            }
        }

        @Override
        public void onAdImpression(TPAdInfo tpAdInfo) {
            super.onAdImpression(tpAdInfo);
            Log.i("TradPlusData", "onAdImpression:" + JSON.toJSONString(tpAdInfo));

        }

        @Override
        public void onAdShowFailed(TPAdError error, TPAdInfo tpAdInfo) {
            super.onAdShowFailed(error, tpAdInfo);
            Log.i("TradPlusData", "onAdShowFailed:" + JSON.toJSONString(tpAdInfo));
        }

        @Override
        public void onAdLoadFailed(TPAdError error) {
            super.onAdLoadFailed(error);

        }

        @Override
        public void onAdClosed(TPAdInfo tpAdInfo) {
            super.onAdClosed(tpAdInfo);
            Log.i("TradPlusData", "onAdClosed:" + JSON.toJSONString(tpAdInfo));
        }
    };

    private void initNativeADView(List<View> drawNativeAdList) {
        if (drawNativeAdList == null || drawNativeAdList.size() <= 0) {
            return;
        }

        // mock测试数据
        for (int i = 0; i < 5; i++) {
            int random = (int) (Math.random() * 100);
            int index = random % videos.length;
            datas.add(new Item(TYPE_COMMON_ITEM, null, videos[index], imgs[index]));
        }

        // 把广告随机插入到视频流的位置
        for (View view : drawNativeAdList) {
            int random = (int) (Math.random() * 100);
            int index = random % videos.length;
            if (index == 0) {
                index++;
            }
            datas.add(index, new Item(TYPE_AD_ITEM, view, -1, -1));
        }

        mAdapter.notifyDataSetChanged();
    }

    private void initNativeUnifiedADView(List<Object> drawNativeAdList) {
        if (drawNativeAdList == null || drawNativeAdList.size() <= 0) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            int random = (int) (Math.random() * 100);
            int index = random % videos.length;
            datas.add(new Item(TYPE_COMMON_ITEM, null, videos[index], imgs[index]));
        }
        for (Object o : drawNativeAdList) {
            int random = (int) (Math.random() * 100);
            int index = random % videos.length;
            if (index == 0) {
                index++;
            }
            datas.add(index, new Item(TYPE_UNIFIED_AD_ITEM, o, -1, -1));
        }

        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        datas.add(new Item(TYPE_COMMON_ITEM, null, videos[0], imgs[0]));
        mRecyclerView = findViewById(R.id.recycler);
        mBottomLayout = findViewById(R.id.bottom);
        mTopLayout = findViewById(R.id.top);
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        mAdapter = new MyAdapter(datas);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private View getView() {
        FullScreenVideoView videoView = new FullScreenVideoView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(layoutParams);
        return videoView;
    }


    private void initListener() {
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                onLayoutComplete();
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.e(TAG, "释放位置:" + position + " 下一页:" + isNext);
                int index = 0;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                if (datas.get(position).type == TYPE_COMMON_ITEM)
                    releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.e(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                if (datas.get(position).type == TYPE_COMMON_ITEM) {
                    playVideo(0);
                    changeBottomTopLayoutVisibility(true);
                } else if (datas.get(position).type == TYPE_AD_ITEM) {
                    changeBottomTopLayoutVisibility(false);
                }
            }

            private void onLayoutComplete() {
                if (datas.get(0).type == TYPE_COMMON_ITEM) {
                    playVideo(0);
                    changeBottomTopLayoutVisibility(true);
                } else if (datas.get(0).type == TYPE_AD_ITEM) {
                    changeBottomTopLayoutVisibility(false);
                }
            }

        });
    }

    private void changeBottomTopLayoutVisibility(boolean visibility) {
        mBottomLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mTopLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private void playVideo(int position) {
        if (isFinishing()) {
            return;
        }

        View itemView = mRecyclerView.getChildAt(0);
        if (itemView == null) {
            return;
        }
        final FrameLayout videoLayout = itemView.findViewById(R.id.video_layout);

        if (videoLayout == null) return;
        Log.i(TAG, "playVideo 000: " + videoLayout.getChildAt(0).getClass().getName());
        View view = videoLayout.getChildAt(0);
        if (view instanceof VideoView) {
            Log.i(TAG, "playVideo 111: " + view);
            final VideoView videoView = (VideoView) videoLayout.getChildAt(0);
            final ImageView imgPlay = itemView.findViewById(R.id.img_play);
            final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
            final MediaPlayer[] mediaPlayer = new MediaPlayer[1];
            videoView.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        mediaPlayer[0] = mp;
                        Log.e(TAG, "onInfo");
                        mp.setLooping(true);
                        imgThumb.animate().alpha(0).setDuration(200).start();
                        return false;
                    }
                });
            }
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                }
            });

            imgPlay.setOnClickListener(new View.OnClickListener() {
                boolean isPlaying = true;

                @Override
                public void onClick(View v) {
                    if (videoView.isPlaying()) {
                        Log.e(TAG, "isPlaying:" + videoView.isPlaying());
                        imgPlay.animate().alpha(1f).start();
                        videoView.pause();
                        isPlaying = false;
                    } else {
                        Log.e(TAG, "isPlaying:" + videoView.isPlaying());
                        imgPlay.animate().alpha(0f).start();
                        videoView.start();
                        isPlaying = true;
                    }
                }
            });
        }
    }

    private void releaseVideo(int index) {
        if (isFinishing()) {
            return;
        }

        View itemView = mRecyclerView.getChildAt(index);
        if (itemView != null) {
            final FrameLayout videoLayout = itemView.findViewById(R.id.video_layout);
            if (videoLayout == null) return;
            View view = videoLayout.getChildAt(0);
            if (view instanceof VideoView) {
                final VideoView videoView = (VideoView) videoLayout.getChildAt(0);
                final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
                final ImageView imgPlay = itemView.findViewById(R.id.img_play);
                videoView.stopPlayback();
                imgThumb.animate().alpha(1).start();
                imgPlay.animate().alpha(0f).start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLayoutManager != null) {
            mLayoutManager.setOnViewPagerListener(null);
        }
        mHandler.removeCallbacksAndMessages(null);

        if(mTPNative != null){
            mTPNative.onDestroy();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        List<Item> datas;

        public MyAdapter(List<Item> datas) {
            this.datas = datas;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if(viewType == TYPE_UNIFIED_AD_ITEM){
                view = LayoutInflater.from(mContext).inflate(R.layout.activity_native_unified_ad_full_screen, parent, false);
            }else{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, parent, false);

            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            View view = new View(mContext);
            Item item = null;
            if (datas != null) {
                item = datas.get(position);
                if (item.type == TYPE_UNIFIED_AD_ITEM && item.adObject != null) {
                    initADItemView(position,holder);
                }else {
                    if (item.type == TYPE_COMMON_ITEM) {
                        holder.img_thumb.setImageResource(item.ImgId);
                        view = getView();
                        ((VideoView) view).setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + item.videoId));
                    } else if (item.type == TYPE_AD_ITEM && item.ad != null) {
                        view = item.ad;
                    }
                    holder.videoLayout.removeAllViews();
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    holder.videoLayout.addView(view);
                    if (item != null) {
                        changeUIVisibility(holder, item.type);
                    }
                }
            }

        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public int getItemViewType(int position) {
            Log.d(TAG, "getItemViewType--" + position);

            return datas.get(position).type;
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
        }

        private void initADItemView(int position, final ViewHolder holder) {
            Item item = datas.get(position);
            final NativeUnifiedADData ad = (NativeUnifiedADData) item.adObject;
            // 视频广告
            if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                holder.poster.setVisibility(View.INVISIBLE);
                holder.mediaView.setVisibility(View.VISIBLE);
            } else {
                holder.poster.setVisibility(View.VISIBLE);
                holder.mediaView.setVisibility(View.INVISIBLE);
            }
            List<View> clickableViews = new ArrayList<>();
            List<View> customClickableViews = new ArrayList<>();
//            if (mBindToCustomView) {
//                customClickableViews.addAll(holder.adInfoView.getClickableViews());
//            } else {
//                clickableViews.addAll(holder.adInfoView.getClickableViews());
//            }
            ArrayList<ImageView>imageViews = new ArrayList<>();
            if(ad.getAdPatternType() == AdPatternType.NATIVE_2IMAGE_2TEXT ||
                    ad.getAdPatternType() == AdPatternType.NATIVE_1IMAGE_2TEXT){
                // 双图双文、单图双文：注册mImagePoster的点击事件
                clickableViews.add(holder.poster);
                imageViews.add(holder.poster);
            }
            FrameLayout.LayoutParams adLogoParams = new FrameLayout.LayoutParams(DeviceUtils.dip2px(mContext
                    , 46),
                    DeviceUtils.dip2px(mContext, 14));
            adLogoParams.gravity = Gravity.END | Gravity.BOTTOM;
            adLogoParams.rightMargin = DeviceUtils.dip2px(mContext, 10);
            adLogoParams.bottomMargin = DeviceUtils.dip2px(mContext, 10);
            //作为customClickableViews传入，点击不进入详情页，直接下载或进入落地页，图文、视频广告均生效，
            ad.bindAdToView(DrawNativeExpressVideoActivity.this, holder.container, adLogoParams,
                    clickableViews, customClickableViews);

            if (!imageViews.isEmpty()) {
                ad.bindImageViews(imageViews, 0);
            }
            if (holder.mediaView != null) {
                try {
                    ad.bindMediaView(holder.mediaView,null, null);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

//            setAdListener(holder, ad);
//            holder.adInfoView.updateAdAction(ad);
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img_thumb;
            CircleImageView img_head_icon;
            ImageView img_play;
            RelativeLayout rootView;
            FrameLayout videoLayout;
            LinearLayout verticalIconLauout;

            public MediaView mediaView;
            public ImageView poster;
            public NativeAdContainer container;

            public ViewHolder(View itemView) {
                super(itemView);
                img_thumb = itemView.findViewById(R.id.img_thumb);
                videoLayout = itemView.findViewById(R.id.video_layout);
                img_play = itemView.findViewById(R.id.img_play);
                rootView = itemView.findViewById(R.id.root_view);
                verticalIconLauout = itemView.findViewById(R.id.vertical_icon);
                img_head_icon = itemView.findViewById(R.id.head_icon);

                mediaView = itemView.findViewById(R.id.gdt_media_view);
                poster = itemView.findViewById(R.id.img_poster);
                container = itemView.findViewById(R.id.native_ad_container);

            }
        }
    }

    private void changeUIVisibility(MyAdapter.ViewHolder holder, int type) {
        boolean visibilable = true;
        if (type == TYPE_AD_ITEM) {
            visibilable = false;
        }
        Log.d(TAG, "是否展示：visibilable=" + visibilable);

        holder.img_play.setVisibility(visibilable ? View.VISIBLE : View.GONE);
        holder.img_thumb.setVisibility(visibilable ? View.VISIBLE : View.GONE);

    }

    private static class Item {
        public int type = 0;
        public View ad;
        public int videoId;
        public int ImgId;
        public Object adObject;



        public Item(int type, View ad, int videoId, int imgId) {
            this.type = type;
            this.ad = ad;
            this.videoId = videoId;
            ImgId = imgId;
        }
        public Item(int type, Object adObject, int videoId, int imgId) {
            this.type = type;
            this.adObject = adObject;
            this.videoId = videoId;
            ImgId = imgId;
        }
    }

    private String printConfigMap(Map<String, String> map) {
        String s = "";
        if (map != null) {
            List<String> keys = new ArrayList(map.keySet());
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = map.get(key);
                s += "key = " + key + " value = " + value + ";";
            }
        }
        return s;
    }

}
