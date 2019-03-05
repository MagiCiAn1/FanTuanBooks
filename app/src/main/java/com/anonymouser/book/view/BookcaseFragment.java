package com.anonymouser.book.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymouser.book.R;
import com.anonymouser.book.adapter.BookCaseAdapter;
import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.DownloadBookEvent;
import com.anonymouser.book.bean.NotifyBookcaseDataEvent;
import com.anonymouser.book.event.AddBookCaseEvent;
import com.anonymouser.book.event.NotifyBookCaseLastChaptersEvent;
import com.anonymouser.book.presenter.HomePresenter;
import com.anonymouser.book.view.lazyFragment.LazyFragment;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 书架 fragment
 * Created by YandZD on 2017/7/13.
 */
public class BookcaseFragment extends LazyFragment {

    @BindView(R.id.recyclerView)
    SwipeMenuRecyclerView mBookRecyclerView;
    @BindView(R.id.tv_find_book)
    TextView tvFindBook;
    @BindView(R.id.view_no_more)
    View noMore;

    HomePresenter mPresenter = new HomePresenter();
    BookCaseAdapter mBookCaseadApter;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_bookcase);
        ButterKnife.bind(this, getContentView());
        EventBus.getDefault().register(this);
        init();

        mPresenter.notfiyBookCase();
    }

    RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mBookCaseadApter.getItemCount() == 0) {
                noMore.setVisibility(View.VISIBLE);
            } else {
                noMore.setVisibility(View.GONE);
            }
        }
    };

    private void init() {
        mBookCaseadApter = new BookCaseAdapter(mBookRecyclerView);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBookRecyclerView.setAdapter(mBookCaseadApter);

        mBookRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
        mBookRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mBookRecyclerView.setLongPressDragEnabled(true); // 开启拖拽
        mBookRecyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽，更新UI。
        mBookRecyclerView.setItemViewSwipeEnabled(false); // 开启滑动删除。

        mBookCaseadApter.registerAdapterDataObserver(mAdapterDataObserver);

        tvFindBook.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvFindBook.getPaint().setAntiAlias(true);

        tvFindBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//        mBookCaseadApter.unregisterAdapterDataObserver(mAdapterDataObserver);
//    }


    @Override
    protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
//        mPresenter.notfiyBookCase();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        EventBus.getDefault().unregister(this);
        mBookCaseadApter.unregisterAdapterDataObserver(mAdapterDataObserver);
    }

    /**
     * 当Item移动的时候。
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(mBookCaseadApter.mBeans, fromPosition, toPosition);
            mBookCaseadApter.notifyItemMoved(fromPosition, toPosition);
            mPresenter.notfiyDataBase(mBookCaseadApter.mBeans);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {
        }
    };

    /**
     * 菜单创建器。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.ic_book_case_height);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem closeItem = new SwipeMenuItem(getActivity())
                        .setImage(R.drawable.ic_cache_black)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。

                SwipeMenuItem addItem = new SwipeMenuItem(getActivity())
                        .setBackgroundColor(getResources().getColor(R.color.baseColor))
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加一个按钮到右侧菜单。
            }
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        @Override
        public void onItemClick(Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                if (menuPosition == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage("是否将本书从书架删除？")
                            .setPositiveButton("不了", null)
                            .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BookCaseBean bean = mBookCaseadApter.mBeans.get(adapterPosition);
                                    mBookCaseadApter.removeItem(adapterPosition);
                                    mBookCaseadApter.notifyItemRemoved(adapterPosition);
                                    mPresenter.removeBookCase(bean);
                                    mAdapterDataObserver.onChanged();
                                }
                            });
                    AlertDialog mDeleteBookDialog = builder.create();
                    mDeleteBookDialog.show();
                } else if (menuPosition == 0) {
                    cacheDialog(adapterPosition);
                }
            }
        }
    };

    private AlertDialog cacheWindow;
    private int mDownloadIndex;

    private void cacheDialog(int index) {
        mDownloadIndex = index;
        if (cacheWindow == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  //先得到构造器

            View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_cache, null);
            //  载入布局
            builder.setView(dialogView);

            dialogView.findViewById(R.id.tvCacheBehind50).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadBook(0);
                }
            });

            dialogView.findViewById(R.id.tvCacheBehindAll).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadBook(1);
                }
            });


            dialogView.findViewById(R.id.tvCacheAll).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadBook(2);
                }
            });
            cacheWindow = builder.create();
        }


        //  显示
        cacheWindow.show();
    }

    private void downloadBook(int downloadModel) {
        DownloadBookEvent event = new DownloadBookEvent();
        event.bean = mBookCaseadApter.getBookCaseBean(mDownloadIndex);
        event.downloadModel = downloadModel;
        EventBus.getDefault().post(event);

        cacheWindow.dismiss();
        Toast.makeText(getActivity(), "已添加到缓存队列", Toast.LENGTH_SHORT).show();
    }


    @Subscribe
    public void onAddBookCaseEvent(AddBookCaseEvent event) {
        mPresenter.addBookCase(event.mBookCaseBean);
    }

    @Subscribe
    public void NotifyBookcaseData(NotifyBookcaseDataEvent event) {
        mBookCaseadApter.setData(event.beans);
        //查找追书更新的最新章节
        mPresenter.getLastChapters(event.beans);
    }

    @Subscribe
    public void NotifyBookcaseDataLastChapters(NotifyBookCaseLastChaptersEvent event) {
        mBookCaseadApter.notifyDataSetChanged();
    }
}
