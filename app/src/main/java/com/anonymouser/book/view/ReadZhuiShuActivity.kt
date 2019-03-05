package com.anonymouser.book.view

/**
 * Created by YandZD on 2017/7/7.
 */

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.anonymouser.book.R
import com.anonymouser.book.adapter.BGListAdapter
import com.anonymouser.book.adapter.DirectoryZhuiShuAdapter
import com.anonymouser.book.bean.*
import com.anonymouser.book.event.AddBookCaseEvent
import com.anonymouser.book.event.SaveUserInfoEvent
import com.anonymouser.book.presenter.ReadZhuiShuPresenter
import com.anonymouser.book.widget.PagerView
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.activity_read.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *  使用追书神器的API，打开的activity
 */
class ReadZhuiShuActivity : BaseReadActivity() {

    var mPresenter = ReadZhuiShuPresenter(this)
    var mTag = ""           //第三方书本的tag 如biquw
        set(value) {
            if (mBookCaseBean != null) {
                mBookCaseBean?.useSource = value
                mPresenter.updateBookCaseBook(mBookCaseBean)
            }
            if (mSearchBookInfoBean != null) {
                mSearchBookInfoBean?.tag = value
            }

            field = value
        }


    companion object {
        //书本的链接，书的名称
        fun newInstance(context: Context, searchBookInfoBean: SearchBookInfoBean): Intent {
            var intent = Intent(context, ReadZhuiShuActivity::class.java)
            intent.putExtra("searchBookInfoBean", searchBookInfoBean)
            return intent
        }

        //从书架进入的
        fun newInstance(context: Context, bookCaseBean: BookCaseBean): Intent {
            var intent = Intent(context, ReadZhuiShuActivity::class.java)
            intent.putExtra("bookCaseBean", bookCaseBean)
            return intent
        }
    }


    //根据入口区别
    override fun entrance() {
        //这是临时阅读
        mSearchBookInfoBean = intent.getSerializableExtra("searchBookInfoBean") as SearchBookInfoBean?

        //从书架入口进入
        var bookCaseBean = intent.getSerializableExtra("bookCaseBean")


        if (bookCaseBean != null) {
            mBookCaseBean = bookCaseBean as BookCaseBean
            mBookName = mBookCaseBean?.bookName ?: ""

            mBookIndex = mBookCaseBean?.readProgress ?: 0
            PagerView.mBitmapIndex = mBookCaseBean?.readPageIndex ?: 0

            mBookId = mBookCaseBean?.zhuiShuId ?: ""
            mTag = mBookCaseBean?.useSource ?: ""
        } else {
            mBookName = mSearchBookInfoBean?.bookName ?: ""
            mBookId = mSearchBookInfoBean?.id ?: ""
            mTag = mSearchBookInfoBean?.tag ?: ""
        }

        topBookName.text = mBookName

        //本文基本信息
        mPresenter.baseReading(mTag, mBookIndex, mBookId, mBookName)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.dispose()
        mPresenter.saveUserInfo(mUserInfo)
        mPresenter.mView = null
    }

    //一些必要的初始化
    override fun init() {
        setAnim()

        clickUpsideDown.setOnClickListener {
            if (directoryView.adapter != null) {
                (directoryView.adapter as DirectoryZhuiShuAdapter).onUpsideDown()
            }
        }

        //读取用户信息,初始化阅读环境
        mUserInfo = mPresenter.loadUserInfo()
        PaintInfo.setInfo(mUserInfo)

        setMoonSunIcon()

        var text = Integer.toHexString(mUserInfo.maxLight - mUserInfo.light).toString()
        if (text.length == 1) {
            text = "0$text"
        }
        lightControlView.setBackgroundColor(Color.parseColor("#${text}000000"))

//        lightControl.progress = mUserInfo.light

        lightControl.setProgress(mUserInfo.light.toFloat())

        //设置繁简体颜色
        if (mUserInfo.isSimple) {
            traditional.setTextColor(Color.parseColor("#e0e0e0"))
            traditional.setBackgroundResource(R.drawable.bg_btn)
        } else {
            traditional.setTextColor(resources.getColor(R.color.baseColor))
            traditional.setBackgroundResource(R.drawable.pre_bg_btn)
        }

        reductionHang()


        //读取用户信息,初始化阅读环境  End

        //文字设置控件里面的背景列表
        bgListView.layoutManager = LinearLayoutManager(this@ReadZhuiShuActivity, LinearLayoutManager.HORIZONTAL, false)
        bgListView.adapter = BGListAdapter()

        lightControl.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(progress: Int, progressFloat: Float) {
                lightControlView.visibility = View.VISIBLE
                mUserInfo.light = progress

                var text = Integer.toHexString(mUserInfo.maxLight - progress).toString()
                if (text.length == 1) {
                    text = "0$text"
                }
                lightControlView.setBackgroundColor(Color.parseColor("#${text}000000"))

                mPresenter.saveUserInfo(mUserInfo)

            }

            override fun getProgressOnActionUp(progress: Int, progressFloat: Float) {
            }

            override fun getProgressOnFinally(progress: Int, progressFloat: Float) {
            }
        }

    }

    override fun setPager() {
        setLoadingView(false)
        PagerView.setListener(object : PagerView.Listener {


            override fun actionTurnPage(isNext: Boolean) {
                if (isNext) {
                    mBookIndex++
                } else {
                    mBookIndex--
                }
            }

            override fun getMiddleChapter(): ChapterBean? {
                mThisNullLink = mPresenter.getChapterLink(mBookIndex)
                var bean = mPresenter.getBookContent(mBookIndex)
                if (!TextUtils.isEmpty(bean?.content)) {
                    mThisNullLink = ""
                    setLoadingView(false)
                }
                return bean
            }

            override fun getChapter(index: Int): ChapterBean? {
                return mPresenter.getBookContent(index)
            }

            override fun getNextChapter(): ChapterBean? {
                mNextNullLink = mPresenter.getChapterLink(mBookIndex + 1)
                var bean = mPresenter.getBookContent(mBookIndex + 1)
                if (!TextUtils.isEmpty(bean?.content)) {
                    mNextNullLink = ""
                }
                return bean
            }

            override fun getPreChapter(): ChapterBean? {
                mPreNullLink = mPresenter.getChapterLink(mBookIndex - 1)
                var bean = mPresenter.getBookContent(mBookIndex - 1)
                if (!TextUtils.isEmpty(bean?.content)) {
                    mPreNullLink = ""
                }
                return bean
            }

            override fun onSetting() {
                if (topView.visibility == View.VISIBLE) {
                    hiddenTopBottomView()

                } else {
                    centerView.visibility = View.VISIBLE

                    //显示
                    topView.visibility = View.VISIBLE
                    topView.startAnimation(mShowTopAction)

                    bottomView.visibility = View.VISIBLE
                    bottomView.startAnimation(mShowBottomAction)
                }
            }

            override fun notifyPageIndex(bitmapIndex: Int, chapterTitle: String) {
                if (mBookCaseBean == null) return
                mPresenter.notifyPageIndex(bitmapIndex, mBookIndex, chapterTitle, mBookCaseBean!!)
            }
        })
    }


    //设置目录view
    override fun setDirectoryView() {
        if (directoryView.adapter == null) {
            bookName.text = mBookName
            directoryView.setItemViewCacheSize(0)
            directoryView.layoutManager = LinearLayoutManager(this@ReadZhuiShuActivity)
            directoryView.adapter = DirectoryZhuiShuAdapter(mPresenter.getBookDirectory(), this@ReadZhuiShuActivity)
        }
        (directoryView.adapter as DirectoryZhuiShuAdapter).setBookIndex(mBookIndex)
        directoryView.scrollToPosition(mBookIndex)
    }


    //换源
    override fun onReplaceSource(view: View?) {
        var builder = AlertDialog.Builder(this)  //先得到构造器

        var dialogView = layoutInflater.inflate(R.layout.dialog_replace_source, null);
        //  载入布局
        builder.setView(dialogView)

        var viewContent = dialogView?.findViewById<LinearLayout>(R.id.viewContent) as LinearLayout

        //  显示
        var windown = builder.create()
        windown.setOnCancelListener(DialogInterface.OnCancelListener {
            finish()
            windown.dismiss()
        })
        if (mPresenter.mSourceList.isEmpty()){
            var textView = dialogView?.findViewById<TextView>(R.id.changeSourceTextView) as TextView
            textView.setText("对不起,暂无此小说")
        }
//        var links = Gson().fromJson(mPresenter.mSourceList, Array<SearchBookInfoBean.BaseLink>::class.java)
        for (link in mPresenter.mSourceList) {
            var text = LayoutInflater.from(this).inflate(R.layout.item_replace_source, null) as TextView
            text.text = link.source
            text.tag = link
            if (link.source.equals(mTag)) {
                text.setTextColor(resources.getColor(R.color.baseColor))
                text.isEnabled = false
            } else {
                text.setTextColor(Color.BLACK)
                text.isEnabled = true
            }

            viewContent.addView(text)

            text.setOnClickListener {
                var link = text.tag as ZhuiShuSourceBean

                mTag = link.source
                mBookLink = link.link

                if (mBookCaseBean != null) {
                    mBookCaseBean?.useSource = mTag
                    mPresenter.updateBookCaseBook(mBookCaseBean!!)
                    startActivity(ReadZhuiShuActivity.newInstance(this, mBookCaseBean!!))
                } else {
                    if (mBookIndex < 0) mBookIndex = 0
                    mSearchBookInfoBean?.tag = mTag
                    mSearchBookInfoBean?.setmBookIndex(mBookIndex)
                    startActivity(ReadZhuiShuActivity.newInstance(this, mSearchBookInfoBean!!))
                }
                finish()

                windown.dismiss()
            }

        }


        windown.show()
    }

    fun hasDownload(link: String): Boolean {
        return mPresenter.isDownload(link)
    }

    override fun onAddBookCase() {
        var event = AddBookCaseEvent()

        event.setZhuiShuBeanFromSearchBookInfoBean(mSearchBookInfoBean, mBookIndex
                , PagerView.bookPageFactoryMiddle.mChapterTitle
                , PagerView.mBitmapIndex, mTag)

        mBookCaseBean = event.mBookCaseBean

        EventBus.getDefault().post(event)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        //可以使用centerView中间蒙版判定是否有悬浮控件
        if (centerView.isShown) {
            hiddenTopBottomView()
        } else {
            onReturnCheckAddCase()
        }
    }


    var mErrorCacheDialog: SweetAlertDialog? = null

    //后台缓存章节成功回调事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun cacheSuccessEvent(content: ZhuiShuBookContent) {
        if (!TextUtils.isEmpty(mPreNullLink)) {
            if (TextUtils.equals(content.link, mPreNullLink)) {
                PagerView.loadPrePage()
                mPreNullLink = ""
            }
        }

        if (!TextUtils.isEmpty(mNextNullLink)) {
            if (TextUtils.equals(content.link, mNextNullLink)) {
                PagerView.loadNextPage()
                mNextNullLink = ""
            }
        }

        if (!TextUtils.isEmpty(mThisNullLink)) {
            if (TextUtils.equals(content.link, mThisNullLink)) {
                if (TextUtils.isEmpty(content.content)) {
                    setLoadingView(false)

                    if (mErrorCacheDialog == null || !mErrorCacheDialog?.isShowing!!) {
                        mErrorCacheDialog = SweetAlertDialog(this@ReadZhuiShuActivity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("数据源有问题，请选择其他源。")
                        mErrorCacheDialog?.show()
                    }

                } else {
                    PagerView.loadThisPage()
                    mThisNullLink = ""
                }
            }
        }
    }

    override fun saveUserInfoEvent(info: UserInfo) {
        mPresenter.saveUserInfo(info)
    }

    override fun saveUserInfo(event: SaveUserInfoEvent) {
        mPresenter.saveUserInfo(mUserInfo)
    }

    override fun cacheSuccessEvent(content: BookContent) {

    }
}

