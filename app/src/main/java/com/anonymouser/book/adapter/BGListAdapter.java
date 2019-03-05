package com.anonymouser.book.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anonymouser.book.R;
import com.anonymouser.book.event.SelectBGColorEvent;
import com.anonymouser.book.utlis.ImgLoad;
import com.anonymouser.book.widget.BgRoundView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by YandZD on 2017/7/18.
 */

public class BGListAdapter extends RecyclerView.Adapter<BGListAdapter.ViewHolder> {

    private Context mContext;

    int[] bgId = new int[]{R.drawable.ic_bg_1
            , R.drawable.ic_bg_2
            , R.drawable.ic_bg_3
            , R.color.reader_background_defult_color
            , R.color.reader_background_show10_color
            , R.color.reader_background_show8_color
            , R.color.reader_background_show9_color
            , R.color.reader_background_white_color
            , R.color.reader_background_white_green};

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bg, null);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setUI(position);
    }

    @Override
    public int getItemCount() {
        return bgId.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBg;
        BgRoundView gbView;
        View mRootView;

        public ViewHolder(View itemView) {
            super(itemView);
            ivBg = (ImageView) itemView.findViewById(R.id.iv_bg);
            gbView = (BgRoundView) itemView.findViewById(R.id.bg_view);
            mRootView = itemView;
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectBGColorEvent event = (SelectBGColorEvent) v.getTag();
                    EventBus.getDefault().post(event);
                }
            });
        }

        private void setUI(int position) {
            SelectBGColorEvent event = new SelectBGColorEvent();
            if (position < 3) {
                ivBg.setVisibility(View.VISIBLE);
                gbView.setVisibility(View.GONE);

                event.bitmapId = position;
                ImgLoad.toImgViewRoundById(bgId[position], ivBg);
//                ivBg.setImageResource(bgId[position]);
            } else {
                ivBg.setVisibility(View.GONE);
                gbView.setVisibility(View.VISIBLE);

                event.color = mContext.getResources().getColor(bgId[position]);
                gbView.setColor(event.color);
            }

            mRootView.setTag(event);
        }
    }
}
