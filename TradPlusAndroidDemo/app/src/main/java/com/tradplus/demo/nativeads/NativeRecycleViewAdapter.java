package com.tradplus.demo.nativeads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tradplus.ads.mgr.nativead.TPCustomNativeAd;
import com.tradplus.demo.R;

import java.util.List;

public class NativeRecycleViewAdapter extends RecyclerView.Adapter {

    private static final int ITEM_VIEW_TYPE_NORMAL = 0;
    private static final int ITEM_VIEW_TYPE_TRADPLUS = 6;

    private List<TPCustomNativeAd> mData;
    private Context context;


    public NativeRecycleViewAdapter(Context context, List<TPCustomNativeAd> mData) {
        this.mData = mData;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == ITEM_VIEW_TYPE_TRADPLUS) {
            return new AdViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_ad, viewGroup, false));
        }
        return new NormalViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_normal, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) viewHolder;
            normalViewHolder.idle.setText("Recycler item " + i);
            normalViewHolder.title.setText("Recycler title " + i);
            normalViewHolder.subtitle.setText("Recycler subtitle " + i);
        } else {
            AdViewHolder adViewHolder = (AdViewHolder) viewHolder;
            TPCustomNativeAd tpNativeAd = mData.get(i);
            ViewGroup adCardView = (ViewGroup) adViewHolder.itemView;
            adCardView.removeAllViews();
            tpNativeAd.showAd(adCardView, R.layout. tp_native_ad_list_item, "");
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData != null) {
            TPCustomNativeAd ad = mData.get(position);
            if (ad == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_TRADPLUS;
            }

        }
        return super.getItemViewType(position);
    }


    private class AdViewHolder extends RecyclerView.ViewHolder {
        LinearLayout tradPlusView;

        public AdViewHolder(View itemView) {
            super(itemView);

            tradPlusView = itemView.findViewById(R.id.tpview);
        }
    }

    private class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView idle;
        TextView title;
        TextView subtitle;

        public NormalViewHolder(View itemView) {
            super(itemView);

            idle = itemView.findViewById(R.id.text_idle);
            title = itemView.findViewById(R.id.text_title);
            subtitle = itemView.findViewById(R.id.text_subtitle);

        }
    }
}
