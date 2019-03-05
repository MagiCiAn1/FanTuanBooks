package com.anonymouser.book.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import com.anonymouser.book.BookApp
import com.anonymouser.book.R
import com.anonymouser.book.bean.*
import com.anonymouser.book.event.SaveUserInfoEvent
import com.anonymouser.book.event.SelectBGColorEvent
import com.anonymouser.book.module.BookModule
import com.anonymouser.book.receiver.PowerReceiver
import com.anonymouser.book.widget.Display
import com.google.android.gms.analytics.HitBuilders
import kotlinx.android.synthetic.main.activity_read.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 阅读页面的基类
 *
 * Created by YandZD on 2017/7/18.
 */
abstract class BaseReadActivity : Activity() {

    var SEND_READING_GA = 0x001

    var mBookIndex = 0
    //正序逆序
    var mUpsideDown = true
    //上面显示隐藏view动画
    var mHiddenTopAction: TranslateAnimation? = null
    var mShowTopAction: TranslateAnimation? = null

    //下面显示隐藏view动画
    var mHiddenBottomAction: TranslateAnimation? = null
    var mShowBottomAction: TranslateAnimation? = null

    //左边显示隐藏view动画
    var mHiddenLeftAction: TranslateAnimation? = null
    var mShowLeftAction: TranslateAnimation? = null

    //菊花框消息动画
    var mLoadHiddenAnimation: AlphaAnimation? = null


    var mUserInfo = UserInfo()
    var mBookCaseBean: BookCaseBean? = null

    //上一页为空link，当cacheSuccessEvent的link跟这个链接相同时，说明后台缓存到了这章节内容，使Pagerview加载上一页内容
    var mPreNullLink = ""
    var mNextNullLink = ""
    var mThisNullLink = ""
    var mPowerReceiver = PowerReceiver()
    var mSearchBookInfoBean: SearchBookInfoBean? = null

    var mBookId = ""
    var mBookName = ""
    var mBookLink = ""      //第三方的书本链接，
    var mBaseLink = ""

    /* GA每次会话持续时间5分钟，每隔5分钟需要提交一次会话 */
    var sendReadingGADelay = 1000 * 60 * 5L
    var mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            if (msg?.what == SEND_READING_GA) {
                val tracker = (application as BookApp).defaultTracker
                tracker.send(HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("reading")
                        .build())
                sendEmptyMessageDelayed(SEND_READING_GA, sendReadingGADelay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_read)
        setGA()
        PowerReceiver()
        registerReceiver(mPowerReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //显示菊花框
        setLoadingView(true)

        EventBus.getDefault().register(this@BaseReadActivity)

        //初始化
        init()

        entrance()
    }


    override fun onResume() {
        super.onResume()
        mHandler.sendEmptyMessageDelayed(SEND_READING_GA, sendReadingGADelay)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeMessages(SEND_READING_GA)
    }

    override fun onDestroy() {
        super.onDestroy()
        PagerView.notifyPageIndex()
        //反注册电量改变接收者
        unregisterReceiver(mPowerReceiver);

        EventBus.getDefault().unregister(this@BaseReadActivity)
    }


    /* 设置 google analytics */
    private fun setGA() {
        val t = (application as BookApp).getDefaultTracker()

        // Set screen name.
        t.setScreenName("readbook")

        // Start a new session with the hit.
        t.send(HitBuilders.ScreenViewBuilder()
                .setNewSession()
                .build())
    }

    //提醒用户换源
    fun setPromptChageSource() {
        setLoadingView(false)
        Toast.makeText(this, "客官本站没有此章，请选择另一个源", Toast.LENGTH_SHORT).show()
        onReplaceSource(null)
    }

    /* 构造动画对象 */
    fun setAnim() {
        mHiddenTopAction = TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f)

        mHiddenTopAction?.duration = 300

        mShowTopAction = TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
                0f)
        mShowTopAction?.duration = 300

        mHiddenBottomAction = TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                1.0f)
        mHiddenBottomAction?.duration = 300

        mShowBottomAction = TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0f)
        mShowBottomAction?.duration = 300


        mHiddenLeftAction = TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0f)
        mHiddenLeftAction?.duration = 300

        mShowLeftAction = TranslateAnimation(Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0f)
        mShowLeftAction?.duration = 300

        mLoadHiddenAnimation = AlphaAnimation(1.0f, 0f)
        mLoadHiddenAnimation?.duration = 500
    }

    /* 当点击中间显示出菜单时，会同时覆盖一层透明view，点击菜单外的部分会隐藏菜单，回到阅读状态 */
    fun onCenterView(view: View) {
        hiddenTopBottomView()
    }

    /* 点击底部 目录 按钮 */
    fun onDirectory(view: View) {
        hiddenWeight()

        directory.visibility = View.VISIBLE
        directory.startAnimation(mShowLeftAction)

        setDirectoryView()
    }

    /* 关闭目录列表 */
    fun quitDirectory(view: View) {
        directory.visibility = View.GONE
        directory.startAnimation(mHiddenLeftAction)
    }

    //白天黑夜模式
    fun onChangeDarkLight(view: View?) {
        mUserInfo.isDay = !mUserInfo.isDay
        PaintInfo.isDay = mUserInfo.isDay
        setMoonSunIcon()
        PagerView.setMoonAndDay()
        saveUserInfoEvent(mUserInfo)
    }

    //设置 白天黑夜模式icon
    fun setMoonSunIcon() {
        if (mUserInfo.isDay) {
            moonSunIcon.setImageResource(R.drawable.ic_moon)
            moonSunTxt.text = "夜间"
        } else {
            moonSunIcon.setImageResource(R.drawable.ic_sun)
            moonSunTxt.text = "日间"
        }
    }


    //text减小
    fun onTextReduceSize(view: View) {
        if (mUserInfo.baseTextSize + (mUserInfo.coefficient - 1) < 12) {
            return
        }
        mUserInfo.coefficient--

        mUserInfo.textSize = Display.mDensity * (mUserInfo.baseTextSize + mUserInfo.coefficient)
        PaintInfo.textSize = mUserInfo.textSize
        PagerView.initBitmap()
    }

    //text增大
    fun onTextAddSize(view: View) {
        if (mUserInfo.baseTextSize + (mUserInfo.coefficient + 1) > 32) {
            return
        }
        mUserInfo.coefficient++

        mUserInfo.textSize = Display.mDensity * (mUserInfo.baseTextSize + mUserInfo.coefficient)
        PaintInfo.textSize = mUserInfo.textSize
        PagerView.initBitmap()
    }

    //行高 小
    fun onLinkSmall(view: View) {
        setLineModule(1.2f)
    }

    //行高 默认
    fun onLinkDefult(view: View) {
        setLineModule(1.5f)
    }

    //行高 大
    fun onLinkBig(view: View) {
        setLineModule(1.8f)
    }

    /* 设置行高 */
    private fun setLineModule(size: Float) {
        mUserInfo.lineHeight = size
        PaintInfo.linkHeight = mUserInfo.lineHeight

        reductionHang()

        PagerView.initBitmap()
    }

    //设置 三个行距的ic
    fun reductionHang() {
        hangSmall.setBackgroundResource(R.drawable.bg_btn)
        hangSmall.setImageResource(R.drawable.ic_hang_small)

        hangDefult.setBackgroundResource(R.drawable.bg_btn)
        hangDefult.setImageResource(R.drawable.ic_hang_defult)

        hangBig.setBackgroundResource(R.drawable.bg_btn)
        hangBig.setImageResource(R.drawable.ic_hang_big)


        if (mUserInfo.lineHeight == 1.2F) {
            hangSmall.setBackgroundResource(R.drawable.pre_bg_btn)
            hangSmall.setImageResource(R.drawable.ic_pre_hang_small)
        } else if (mUserInfo.lineHeight == 1.5F) {
            hangDefult.setBackgroundResource(R.drawable.pre_bg_btn)
            hangDefult.setImageResource(R.drawable.ic_pre_hang_defult)
        } else if (mUserInfo.lineHeight == 1.8F) {
            hangBig.setBackgroundResource(R.drawable.pre_bg_btn)
            hangBig.setImageResource(R.drawable.ic_pre_hang_big)
        }
    }

    //繁体 点击事件
    fun onTraditional(view: View) {
        mUserInfo.isSimple = !mUserInfo.isSimple
        PaintInfo.isSimple = mUserInfo.isSimple

        if (mUserInfo.isSimple) {
            traditional.setTextColor(Color.parseColor("#e0e0e0"))
            traditional.setBackgroundResource(R.drawable.bg_btn)
        } else {
            traditional.setTextColor(resources.getColor(R.color.baseColor))
            traditional.setBackgroundResource(R.drawable.pre_bg_btn)
        }

        PagerView.initBitmap()
    }

    //点击设置
    fun onFontSetting(view: View) {
        if (fontSetting.visibility == View.VISIBLE) {
            fontSetting.visibility = View.GONE
            fontSetting.startAnimation(mHiddenBottomAction)
        } else {
            hiddenWeight()
            fontSetting.visibility = View.VISIBLE
            fontSetting.startAnimation(mShowBottomAction)
        }
    }


    //点击缓存
    fun onCache(view: View) {
        cacheDialog()
    }


    fun onReturn(view: View) {
        onReturnCheckAddCase()

    }


    var mAddCaseDialog: AlertDialog? = null

    fun onReturnCheckAddCase() {
        if (mBookCaseBean == null) {
            if (mAddCaseDialog == null) {
                var builder = AlertDialog.Builder(this)
                builder.setMessage("是否将本书加入书架？")
                        .setPositiveButton("加入", DialogInterface.OnClickListener { dialog, which ->
                            isInBookCase()
                            finish()
                        })

                        .setNegativeButton("不了", DialogInterface.OnClickListener { dialog, which ->
                            finish()
                        })
                mAddCaseDialog = builder.create()
            }
            mAddCaseDialog?.show()
        } else {
            finish()
        }
    }


    /* 隐藏顶部、底部两个view */
    fun hiddenTopBottomView() {
        hiddenWeight()

        centerView.visibility = View.GONE

        //隐藏
        topView.visibility = View.GONE
        topView.startAnimation(mHiddenTopAction)

        bottomView.visibility = View.GONE
        bottomView.startAnimation(mHiddenBottomAction)
    }

    /* 隐藏小控件，如目录 */
    fun hiddenWeight() {
        if (fontSetting.visibility == View.VISIBLE) {
            fontSetting.visibility = View.GONE
            fontSetting.startAnimation(mHiddenBottomAction)
        }
        if (directory.visibility == View.VISIBLE) {
            directory.visibility = View.GONE
            directory.startAnimation(mHiddenLeftAction)
        }
    }

    /* 重新加载本页 */
    fun loadThisPage() {
        PagerView.loadThisPage()
    }

    /* 是否显示加载框 */
    fun setLoadingView(isShow: Boolean) {
        if (isShow) {
            rotateLoading.start()
            loadingView.visibility = View.VISIBLE
        } else if (loadingView.isShown && !isShow) {
            rotateLoading.stop()

            loadingView.startAnimation(mLoadHiddenAnimation)
            loadingView.visibility = View.GONE
        }
    }

    fun getChapterIndex(): Int {
        return mBookIndex
    }

    /* 显示需要缓存的模式dialog */
    private var cacheWindown: AlertDialog? = null

    private fun cacheDialog() {
        if (cacheWindown == null) {
            var builder = AlertDialog.Builder(this)  //先得到构造器

            var dialogView = layoutInflater.inflate(R.layout.dialog_cache, null);
            //  载入布局
            builder.setView(dialogView);

            dialogView.findViewById<TextView>(R.id.tvCacheBehind50).setOnClickListener {
                isInBookCase()
                var event = DownloadBookEvent()
                event.bean = mBookCaseBean
                event.downloadModel = 0
                EventBus.getDefault().post(event)

                cacheWindown?.dismiss()
                Toast.makeText(this, "已添加到缓存队列", Toast.LENGTH_SHORT).show()
            }
            dialogView.findViewById<TextView>(R.id.tvCacheBehindAll).setOnClickListener {
                isInBookCase()
                var event = DownloadBookEvent()
                event.bean = mBookCaseBean
                event.downloadModel = 1
                EventBus.getDefault().post(event)

                cacheWindown?.dismiss()
                Toast.makeText(this, "已添加到缓存队列", Toast.LENGTH_SHORT).show()
            }
            dialogView.findViewById<TextView>(R.id.tvCacheAll).setOnClickListener {
                isInBookCase()
                var event = DownloadBookEvent()
                event.bean = mBookCaseBean
                event.downloadModel = 2
                EventBus.getDefault().post(event)

                cacheWindown?.dismiss()
                Toast.makeText(this, "已添加到缓存队列", Toast.LENGTH_SHORT).show()
            }

            //  显示
            cacheWindown = builder.create()
        }
        cacheWindown?.show()
    }


    /* 进行缓存的时候判断是否已经在书架上，没有在书架上则调用 onAddBookCase() */
    private fun isInBookCase() {
        if (BookModule.getBookCaseBean(mBookName) == null) {
            onAddBookCase()
        }
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

    //点击目录里面的章节事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onClickChapter(event: ClickChapterEvent) {
        setLoadingView(true)
        mBookIndex = event.index
        PagerView.mBitmapIndex = 0
        PagerView.initBitmap()

        hiddenTopBottomView()

        //保存当前的章位置
        PagerView.notifyPageIndex()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun selectBGColor(event: SelectBGColorEvent) {
        if (!PaintInfo.isDay) {
            onChangeDarkLight(null)
        }

        mUserInfo.dayBgColor = event.color
        mUserInfo.dayBgImg = event.bitmapId

        PaintInfo.dayBgColor = event.color
        PaintInfo.dayBgImg = event.bitmapId

        if (event.bitmapId != -1) {
            PaintInfo.mBgBitmap = BitmapFactory.decodeResource(resources, PaintInfo.bgImgs[event.bitmapId])
        } else {
            if (PaintInfo.mBgBitmap != null && !PaintInfo.mBgBitmap?.isRecycled!!) {
                PaintInfo.mBgBitmap?.recycle()
            }
        }
        PagerView.initBitmap()
    }


    /* 进入 */
    abstract fun entrance()

    abstract fun init()

    abstract fun setPager()

    abstract fun setDirectoryView()

    abstract fun onReplaceSource(view: View?)

    abstract fun onAddBookCase()

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    abstract fun saveUserInfo(info: SaveUserInfoEvent)

    @Subscribe(threadMode = ThreadMode.MAIN)
    abstract fun saveUserInfoEvent(info: UserInfo)

    @Subscribe(threadMode = ThreadMode.MAIN)
    abstract fun cacheSuccessEvent(content: ZhuiShuBookContent)

    @Subscribe(threadMode = ThreadMode.MAIN)
    abstract fun cacheSuccessEvent(content: BookContent)

}