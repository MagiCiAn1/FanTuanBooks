package com.anonymouser.book.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anonymouser.book.R;
import com.anonymouser.book.bean.ClickChapterEvent;
import com.anonymouser.book.bean.ZhuiShuChaptersBean;
import com.anonymouser.book.view.ReadActivity;
import com.anonymouser.book.view.ReadZhuiShuActivity;
import com.anonymouser.book.widget.LocaleTextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YandZD on 2017/7/16.
 */

public class DirectoryZhuiShuAdapter extends Adapter<DirectoryZhuiShuAdapter.ViewHolder> {

    public List<ZhuiShuChaptersBean.ChaptersBean> mData = new ArrayList<ZhuiShuChaptersBean.ChaptersBean>();

    //true 为正序，false 为倒叙
    public boolean mUpsideDown = true;
    public ReadZhuiShuActivity mView;
    public String mBookChapter = "";
    public DirectoryZhuiShuAdapter(List<ZhuiShuChaptersBean.ChaptersBean> data, ReadZhuiShuActivity view) {
        mData.addAll(data);
        mView = view;
    }

    public void setData(List<ZhuiShuChaptersBean.ChaptersBean> data) {
        mData.clear();
        mData.addAll(data);
    }
    public void setBookIndex(int bookIndex){
        mBookChapter = mData.get(bookIndex).getTitle();
        notifyDataSetChanged();
    }
    public void onUpsideDown() {
        mUpsideDown = !mUpsideDown;
//        Collections.reverse(mData);

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_directory, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int index;
        if (mUpsideDown) {
            index = position;
        } else {
            index = mData.size() - position - 1;
        }
        holder.setUI(mData.get(index), position);
        if (mBookChapter.equals(holder.tvChapter.getText())){
            holder.tvChapter.setTextColor(mView.getResources().getColor(R.color.colorAccent));
        }else{
            holder.tvChapter.setTextColor(mView.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View ivCache;
        LocaleTextView tvChapter;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivCache = itemView.findViewById(R.id.ivCache);
            tvChapter = (LocaleTextView) itemView.findViewById(R.id.tvChapter);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClickChapterEvent event = new ClickChapterEvent();
                    if (mUpsideDown) {
                        event.index = (int) v.getTag();
                    } else {
                        event.index = mData.size() - (int) v.getTag() - 1;
                    }

                    EventBus.getDefault().post(event);
                }
            });
        }

        public void setUI(ZhuiShuChaptersBean.ChaptersBean chapter, int index) {
            tvChapter.setText(chapter.getTitle());
            rootView.setTag(index);
            if (mView.hasDownload(chapter.getLink())) {
                ivCache.setVisibility(View.VISIBLE);
            } else {
                ivCache.setVisibility(View.GONE);
            }
        }
    }
}
