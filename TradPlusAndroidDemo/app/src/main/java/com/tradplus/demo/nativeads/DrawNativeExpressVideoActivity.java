package com.tradplus.demo.nativeads;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.dingmouren.layoutmanagergroup.viewpager.OnViewPagerListener;
import com.dingmouren.layoutmanagergroup.viewpager.ViewPagerLayoutManager;
import com.kwad.sdk.api.KsDrawAd;
import com.tradplus.ads.common.IDrawNativeListVideoViewListener;
import com.tradplus.ads.mobileads.TradPlusErrorCode;
import com.tradplus.ads.mobileads.TradPlusView;
import com.tradplus.demo.R;
import com.tradplus.utils.NetworkUtils;
import com.tradplus.utils.TToast;
import com.tradplus.utils.TestAdUnitId;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DrawNativeExpressVideoActivity extends AppCompatActivity {

    private static final String TAG = "DrawExpressActivity";
    private static final int TYPE_COMMON_ITEM = 1;
    private static final int TYPE_AD_ITEM = 2;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Throwable ignore) {
        }
        setContentView(R.layout.activity_draw_native_video);
        if (NetworkUtils.getNetworkType(this) == NetworkUtils.NetworkType.NONE) {
            return;
        }


        initView();
        initListener();


        mContext = this;
        TradPlusView tradPlusView = new TradPlusView(this);

        tradPlusView.setAdViewListener(fsAdViewListener);

        tradPlusView.setDrawNativeListVideoView(new IDrawNativeListVideoViewListener() {
            @Override
            public void updateAdView(List list) {
                System.out.println("tradplus=========" + list.toString());
                initAdView(list);
            }
        });

        tradPlusView.setAdUnitId(TestAdUnitId.DRAW_ADUNITID);
        tradPlusView.loadAd();

    }

    TradPlusView.FSAdViewListener fsAdViewListener = new TradPlusView.FSAdViewListener() {
        @Override
        public void onAdViewLoaded(TradPlusView banner) {
            Log.i(TAG, "onAdViewLoaded: ");
        }

        @Override
        public void onAdViewFailed(TradPlusView banner, TradPlusErrorCode errorCode) {
            Log.i(TAG, "onAdViewFailed: "+errorCode);
        }

        @Override
        public void onAdViewClicked(TradPlusView banner) {
            Log.i(TAG, "onAdViewClicked: ");
        }

        @Override
        public void onAdViewExpanded(TradPlusView banner) {
            Log.i(TAG, "onAdViewExpanded: ");
        }

        @Override
        public void onAdViewCollapsed(TradPlusView banner) {
            Log.i(TAG, "onAdViewCollapsed: ");
        }

        @Override
        public void onAdsSourceLoaded(Object object) {
            Log.i(TAG, "onAdsSourceLoaded: ");
        }
    };

    private void initAdView(List ads) {
        Log.i(TAG, "initAdView: "+ads);
        for (int i = 0; i < 5; i++) {
            int random = (int) (Math.random() * 100);
            int index = random % videos.length;
            datas.add(new Item(TYPE_COMMON_ITEM, null, videos[index], imgs[index]));
        }
        for (Object ad : ads) {
            //点击监听器必须在getAdView之前调
            if(ad instanceof TTNativeExpressAd) {
                Log.i(TAG, "initAdView if: ");
                initTaotiaoDrawList((TTNativeExpressAd)ad);
            } else if(ad instanceof KsDrawAd){
                Log.i(TAG, "initAdView else if: ");
                initKWadDrawList((KsDrawAd)ad);
            }
        }
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

    private void initKWadDrawList(KsDrawAd ksDrawAd) {
        int random = (int) (Math.random() * 100);
        int index = random % videos.length;
        if (index == 0) {
            index++;
        }
        datas.add(index, new Item(TYPE_AD_ITEM, ksDrawAd, -1, -1));
        mAdapter.notifyDataSetChanged();
    }

    private void initTaotiaoDrawList(TTNativeExpressAd ad) {
        ad.setCanInterruptVideoPlay(true);
        int random = (int) (Math.random() * 100);
        int index = random % videos.length;
        if (index == 0) {
            index++;
        }
        datas.add(index, new Item(TYPE_AD_ITEM, ad, -1, -1));
        mAdapter.notifyDataSetChanged();
        ad.render();
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
        Log.i(TAG, "playVideo 000: "+videoLayout.getChildAt(0).getClass().getName());
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            View view = new View(mContext);
            Item item = null;
            if (datas != null) {
                item = datas.get(position);
                if (item.type == TYPE_COMMON_ITEM) {
                    holder.img_thumb.setImageResource(item.ImgId);
                    view = getView();
                    ((VideoView) view).setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + item.videoId));
                } else if (item.type == TYPE_AD_ITEM && item.ad != null) {
                    if (item.ad instanceof TTNativeExpressAd) {
                        Log.i(TAG, "onBindViewHolder if: ");
                        view = ((TTNativeExpressAd)item.ad).getExpressAdView();
                    }else if(item.ad instanceof KsDrawAd){
                        Log.i(TAG, "onBindViewHolder else if: ");
                        view =  ((KsDrawAd) item.ad).getDrawView(DrawNativeExpressVideoActivity.this);
                    }

                }
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
        public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img_thumb;
            CircleImageView img_head_icon;
            ImageView img_play;
            RelativeLayout rootView;
            FrameLayout videoLayout;
            LinearLayout verticalIconLauout;

            public ViewHolder(View itemView) {
                super(itemView);
                img_thumb = itemView.findViewById(R.id.img_thumb);
                videoLayout = itemView.findViewById(R.id.video_layout);
                img_play = itemView.findViewById(R.id.img_play);
                rootView = itemView.findViewById(R.id.root_view);
                verticalIconLauout = itemView.findViewById(R.id.vertical_icon);
                img_head_icon = itemView.findViewById(R.id.head_icon);

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

    private void showToast(String msg) {
        TToast.show(this, msg);
    }

    private static class Item {
        public int type = 0;
        public Object ad;
        public int videoId;
        public int ImgId;

        public Item(int type, Object ad, int videoId, int imgId) {
            this.type = type;
            this.ad = ad;
            this.videoId = videoId;
            ImgId = imgId;
        }
    }


}