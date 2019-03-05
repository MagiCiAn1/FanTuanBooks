package com.anonymouser.book.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anonymouser.book.R;
import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.SearchBookInfoBean;
import com.anonymouser.book.event.AddBookCaseEvent;
import com.anonymouser.book.module.BookModule;
import com.anonymouser.book.utlis.ImgLoad;
import com.anonymouser.book.view.ReadActivity;
import com.anonymouser.book.view.ReadZhuiShuActivity;
import com.anonymouser.book.widget.LocaleTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by YandZD on 2017/7/19.
 */

public class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.ViewHolder> {

    View mShowingDetails = null;
    int mShowingIndex = -1;

    ArrayList<SearchBookInfoBean> mBeans = new ArrayList<>();
    Context mContext;

    public void setData(List<SearchBookInfoBean> beans) {
        if (beans != null) {
            mShowingDetails = null;
            mShowingIndex = -1;
            mBeans.clear();
            mBeans.addAll(beans);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_book, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setUI(position);
    }

    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_book_info)
        View mBookInfo;
        @BindView(R.id.view_details)
        View mDetails;
        @BindView(R.id.iv_logo)
        ImageView ivLogo;
        @BindView(R.id.tv_book_name)
        LocaleTextView tvBookName;
        @BindView(R.id.tv_auther)
        LocaleTextView tvAuther;
        @BindView(R.id.tv_type)
        LocaleTextView tvType;
        @BindView(R.id.tv_source)
        LocaleTextView tvSource;
        @BindView(R.id.tv_introduction)
        LocaleTextView tvIntroduction;
        @BindView(R.id.bt_read)
        Button btRead;
        @BindView(R.id.bt_add_bookcase)
        Button btAddBookCase;
        @BindView(R.id.bt_no_book)
        Button btNoBook;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mBookInfo.setTag(mDetails);
        }

        @OnClick(R.id.view_book_info)
        public void onBookInfo(View view) {
            View details = (View) view.getTag();
            if (details.isShown()) {
                details.setVisibility(View.GONE);
                mShowingDetails = null;
                mShowingIndex = -1;
            } else {
                if (mShowingDetails != null)
                    mShowingDetails.setVisibility(View.GONE);
                details.setVisibility(View.VISIBLE);
                mShowingDetails = details;
                mShowingIndex = (int) details.getTag();
            }

            int index = (int) details.getTag();
            SearchBookInfoBean bean = mBeans.get(index);
            tvIntroduction.setText(bean.getIntro().replaceAll("\\\\n", "\n"));
        }

        @OnClick(R.id.bt_read)
        public void onRead(View v) {
            SearchBookInfoBean bean = (SearchBookInfoBean) v.getTag();
            BookCaseBean bookCaseBean = BookModule.getBookCaseBean(bean.getBookName());

            if (bookCaseBean != null) {
                if (bookCaseBean.getIsZhuiShu()) {
                    mContext.startActivity(ReadZhuiShuActivity.Companion.newInstance(mContext, bookCaseBean));
                } else {
                    mContext.startActivity(ReadActivity.Companion.newInstance(mContext, bookCaseBean));
                }
            } else {
                if (bean.isZhuiShu()) {
                    mContext.startActivity(ReadZhuiShuActivity.Companion.newInstance(mContext, bean));
                } else {
                    mContext.startActivity(ReadActivity.Companion.newInstance(mContext, bean));
                }
            }
        }

        @OnClick(R.id.bt_add_bookcase)
        public void onAddBookCase(View view) {
            SearchBookInfoBean bean = (SearchBookInfoBean) view.getTag();

            AddBookCaseEvent event = new AddBookCaseEvent();
            event.setBeanFromSearchBookInfoBean(bean);
            EventBus.getDefault().post(event);

            new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("").show();

            view.setEnabled(false);
            view.setBackgroundColor(Color.parseColor("#909090"));
        }

        public void setUI(int index) {
            mDetails.setTag(index);
            if (mShowingDetails != null && mShowingIndex == index) {
                mDetails.setVisibility(View.VISIBLE);
            } else {
                mDetails.setVisibility(View.GONE);
            }


            SearchBookInfoBean bean = mBeans.get(index);
            ImgLoad.baseLoadImg(bean.getImg(), ivLogo);
            tvBookName.setText(bean.getBookName());
            tvAuther.setText("作者：" + bean.getAuthor());
            tvType.setText("类型：" + bean.getType());
            tvSource.setText("来源：" + bean.getTag());
//            tvIntroduction.setTag(intro);
            btRead.setTag(bean);
            btAddBookCase.setTag(bean);

            if ((TextUtils.isEmpty(bean.getBaseLink()) || TextUtils.equals(bean.getTag(), "未收录")) && !bean.isZhuiShu()) {
                btAddBookCase.setVisibility(View.GONE);
                btRead.setVisibility(View.GONE);

                btNoBook.setVisibility(View.VISIBLE);
            } else {
                btAddBookCase.setVisibility(View.VISIBLE);
                btRead.setVisibility(View.VISIBLE);

                btNoBook.setVisibility(View.GONE);

                //判断是否在书架中
                boolean isExist = false;
                if (BookCaseAdapter.mBeans != null) {
                    for (BookCaseBean itemBean : BookCaseAdapter.mBeans) {
                        if (TextUtils.equals(itemBean.getBookName(), bean.getBookName())) {
                            isExist = true;
                            break;
                        }
                    }
                }

                //如果已经存在在书架上则隐藏
                if (isExist) {
                    btAddBookCase.setEnabled(false);
                    btAddBookCase.setBackgroundColor(Color.parseColor("#909090"));
                } else {
                    btAddBookCase.setEnabled(true);
                    btAddBookCase.setBackgroundColor(Color.parseColor("#B63327"));
                }
            }
        }
    }

}
