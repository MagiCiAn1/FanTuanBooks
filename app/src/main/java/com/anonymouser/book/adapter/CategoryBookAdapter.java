package com.anonymouser.book.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anonymouser.book.R;
import com.anonymouser.book.bean.CategoryBookItemBean;
import com.anonymouser.book.bean.RankBean;
import com.anonymouser.book.utlis.ImgLoad;
import com.anonymouser.book.view.SearchActivity;
import com.anonymouser.book.widget.LocaleTextView;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YandZD on 2017/7/25.
 */

public class CategoryBookAdapter extends RecyclerView.Adapter<CategoryBookAdapter.ViewHolder> {

    List<CategoryBookItemBean.BooksBean> mDatas = new ArrayList<>();
    Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) mContext = parent.getContext();
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank, null);
        return new ViewHolder(rootView);
    }

    public void setData(List<CategoryBookItemBean.BooksBean> data) {
        if (data != null) {
            mDatas.clear();
            mDatas.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void addData(List<CategoryBookItemBean.BooksBean> data) {
        if (data != null) {
            mDatas.addAll(data);
            notifyDataSetChanged();
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setUI(position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_logo)
        ImageView ivLogo;
        @BindView(R.id.tv_book_name)
        LocaleTextView tvBookName;
        @BindView(R.id.tv_auther)
        LocaleTextView tvAuther;
        @BindView(R.id.tv_intro)
        LocaleTextView tvIntro;

        View mRootView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mRootView = itemView;
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String bookName = (String) v.getTag();
                    mContext.startActivity(SearchActivity.getSearchIntent(mContext, bookName));
                }
            });
        }

        public void setUI(int position) {
            CategoryBookItemBean.BooksBean bean = mDatas.get(position);
            try {
                String urlLink = URLDecoder.decode(bean.getCover(), "UTF-8").replace("/agent/", "");
                ImgLoad.baseLoadImg(urlLink, ivLogo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvBookName.setText(bean.getTitle());
            tvAuther.setText(bean.getAuthor());
            tvIntro.setText(bean.getShortIntro());

            mRootView.setTag(bean.getTitle());
        }
    }

}
