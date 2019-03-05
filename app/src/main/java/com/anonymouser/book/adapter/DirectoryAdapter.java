package com.anonymouser.book.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anonymouser.book.R;
import com.anonymouser.book.bean.ClickChapterEvent;
import com.anonymouser.book.bean.DirectoryBean;
import com.anonymouser.book.module.BookModule;
import com.anonymouser.book.view.ReadActivity;
import com.anonymouser.book.widget.LocaleTextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by YandZD on 2017/7/16.
 */

public class DirectoryAdapter extends Adapter<DirectoryAdapter.ViewHolder> {

    public JsonArray mData = new JsonArray();

    //true 为正序，false 为倒叙
    public boolean mUpsideDown = true;
    public ReadActivity mView;
    public String mBookChapter = "";
    public DirectoryAdapter(JsonArray data, ReadActivity view) {
        mData = data;
        mView = view;
        mBookChapter = ((JsonObject)mData.get(0)).get("title").getAsString();
    }
    public void setData(JsonArray data) {
        mData = data;
    }
    public void setBookIndex(int bookIndex){
        mBookChapter = ((JsonObject)mData.get(bookIndex)).get("title").getAsString();
        notifyDataSetChanged();
//        notifyItemChanged(bookIndex);
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
        holder.setUI((JsonObject) mData.get(index), position);
        if (mBookChapter.equals(holder.tvChapter.getText())){
            holder.tvChapter.setTextColor(mView.getResources().getColor(R.color.colorAccent));
        }else{
            holder.tvChapter.setTextColor(mView.getResources().getColor(R.color.black));;
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

        public void setUI(JsonObject chapter, int index) {
            tvChapter.setText(chapter.get("title").getAsString());
            rootView.setTag(index);
            if (mView.hasDownload(chapter.get("link").getAsString())) {
                ivCache.setVisibility(View.VISIBLE);
            } else {
                ivCache.setVisibility(View.GONE);
            }

        }
    }
}
