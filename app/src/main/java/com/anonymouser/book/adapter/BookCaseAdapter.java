package com.anonymouser.book.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anonymouser.book.R;
import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.LastChapterBean;
import com.anonymouser.book.presenter.HomePresenter;
import com.anonymouser.book.utlis.ImgLoad;
import com.anonymouser.book.view.ReadActivity;
import com.anonymouser.book.view.ReadZhuiShuActivity;
import com.anonymouser.book.widget.LocaleTextView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * R.layout.item_bookcase
 * Created by YandZD on 2017/7/13.
 */
public class BookCaseAdapter extends SwipeMenuAdapter<BookCaseAdapter.DefaultViewHolder> {

    public static List<BookCaseBean> mBeans = new ArrayList<>();
    SwipeMenuRecyclerView mMenuRecyclerView;
    Context mContext;

    public BookCaseAdapter(SwipeMenuRecyclerView menuRecyclerView) {
        this.mMenuRecyclerView = menuRecyclerView;
    }

    public void setData(List<BookCaseBean> beans) {
        if (beans == null) return;
        mBeans.clear();
        mBeans.addAll(beans);
        notifyDataSetChanged();
    }

    public BookCaseBean getBookCaseBean(int index) {
        if (index >= 0 && index < mBeans.size()) {
            return mBeans.get(index);
        }
        return null;
    }


    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        if (mContext == null) mContext = parent.getContext();
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookcase, null);
        return rootView;
    }

    @Override
    public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mMenuRecyclerView = mMenuRecyclerView;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    public void removeItem(int adapterPosition) {
        mBeans.remove(adapterPosition);
    }

    class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        SwipeMenuRecyclerView mMenuRecyclerView;
        @BindView(R.id.iv_cover)
        ImageView ivCover;
        @BindView(R.id.tv_book_name)
        LocaleTextView tvBookName;
        @BindView(R.id.tv_chapter)
        LocaleTextView tvChapter;
        @BindView(R.id.tv_last_chapter)
        LocaleTextView tvLastChapter;


        View rootView;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.findViewById(R.id.iv_touch).setOnTouchListener(this);
            rootView = itemView;
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookCaseBean bean = (BookCaseBean) v.getTag();
                    if (bean.getIsZhuiShu()) {
                        mContext.startActivity(ReadZhuiShuActivity.Companion.newInstance(mContext, bean));
                    } else {
                        mContext.startActivity(ReadActivity.Companion.newInstance(mContext, bean));
                    }

                }
            });
        }

        public void setData(int position) {
            BookCaseBean bean = mBeans.get(position);
            ImgLoad.baseLoadImg(bean.getImg(), ivCover);
            tvBookName.setText(bean.getBookName());
            tvChapter.setText(bean.getReadChapterTitle());
            rootView.setTag(bean);

            tvLastChapter.setText("");
            
            if (!(TextUtils.isEmpty(bean.getZhuiShuId()) || HomePresenter.mLastChapterBeans == null)) {
                for (LastChapterBean lastChapterBean : HomePresenter.mLastChapterBeans) {
                    if (lastChapterBean.get_id().equals(bean.getZhuiShuId())) {
                        tvLastChapter.setText("最新：" + lastChapterBean.getLastChapter());
                        break;
                    }
                }
            }
            ////缺少非追书获取最新章节

        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mMenuRecyclerView.startDrag(this);
                    break;
                }
            }
            return false;
        }
    }
}
