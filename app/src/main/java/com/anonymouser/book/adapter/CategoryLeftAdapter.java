package com.anonymouser.book.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anonymouser.book.R;
import com.anonymouser.book.bean.CategoryItemBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 分类左边的adapter
 * <p>
 * Created by YandZD on 2017/8/30.
 */

public class CategoryLeftAdapter extends BaseAdapter {

    private View mOldView;
    private ArrayList<CategoryItemBean> mBean = new ArrayList<>();
    private int mClickIndex = 0;

    public void setData(ArrayList<CategoryItemBean> bean) {
        mBean.clear();
        mBean.addAll(bean);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mBean.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_left, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.setUI(position);

        return convertView;
    }

    class ViewHolder {
        TextView tvSubject;

        public ViewHolder(View view) {
            tvSubject = (TextView) view.findViewById(R.id.tv_subject);
        }

        public void setUI(int position) {
            tvSubject.setText(mBean.get(position).str);

            if (position == mClickIndex) {
                mOldView = tvSubject;
                tvSubject.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                tvSubject.setBackgroundColor(Color.parseColor("#f0f0f0"));
            }

            tvSubject.setTag(position);
            tvSubject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOldView.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    mOldView = v;
                    mOldView.setBackgroundColor(Color.parseColor("#ffffff"));

                    mClickIndex = (int) v.getTag();
                    CategoryItemBean bean = mBean.get(mClickIndex);
                    EventBus.getDefault().post(bean);
                }
            });
        }


    }
}
