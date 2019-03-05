package com.anonymouser.book.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateInterpolator
import com.anonymouser.book.bean.ChapterBean
import com.anonymouser.book.bean.PaintInfo
import android.view.animation.LinearInterpolator
import com.anonymouser.book.bean.BitmapBean
import com.anonymouser.book.event.SaveUserInfoEvent
import org.greenrobot.eventbus.EventBus


/**
 *
 * Created by YandZD on 2017/7/7.
 */
class PagerView : View {

    var mBitmapIndex = 0
    var bookPageFactoryMiddle = BookPageFactory(context)
    var bookPageFactoryLeft = BookPageFactory(context)
    var bookPageFactoryRight = BookPageFactory(context)

    var mBitmapMiddle = BitmapBean()
    var mBitmapLeft = BitmapBean()
    var mBitmapRight = BitmapBean()

    var mBitmaps = kotlin.arrayOf(mBitmapLeft, mBitmapMiddle, mBitmapRight)
    var middleIndex = 1     //标示正在现在的页面 ，如刚启动 是1 则是mBitmapMiddle 右滑1次 变为2 中间页为mBitmapRight

    var mListener: Listener? = null
    var offerX = 0f
    var mDiagonalLength: Float = 0f //对角线长度
    var mClickFlag = 0f //判断是翻页还是点击事件的距离
    var isClickFlag = false //判断是否是点击事件

    var isOver = false

    constructor(context: Context) : this(context, null, 0) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet?, flag: Int) : super(context, attributeSet, flag) {
        init()
    }

    fun init() {
        mDiagonalLength = Math.hypot(Display.mWidth.toDouble(), Display.mHeight.toDouble()).toFloat()
        mClickFlag = mDiagonalLength / 40f
    }

    fun setListener(listener: Listener) {
        this.mListener = listener
        //第一次进来
        initBitmap()
    }

    fun loadThisPage() {
        initBitmap()
    }

    fun loadPrePage() {
        var textTemp = mListener?.getPreChapter()
        bookPageFactoryLeft.setText(textTemp)
        setBitmap(0)
    }

    fun loadNextPage() {
        var temptext = mListener?.getNextChapter()
        bookPageFactoryRight.setText(temptext)
        setBitmap(0)
    }

    fun initBitmap() {
        bookPageFactoryLeft.initPaint()
        bookPageFactoryMiddle.initPaint()
        bookPageFactoryRight.initPaint()


        var text = mListener?.getMiddleChapter()

        bookPageFactoryMiddle.setText(text)
        var textTemp = mListener?.getPreChapter()
        bookPageFactoryLeft.setText(textTemp)
        var temptext = mListener?.getNextChapter()
        bookPageFactoryRight.setText(temptext)

        if (mBitmapIndex >= bookPageFactoryMiddle.fenye.size || mBitmapIndex < 0) mBitmapIndex = 0

//        getSpecifiedPager(0)
        setBitmap(0)

        invalidate()

        EventBus.getDefault().post(SaveUserInfoEvent())
    }


    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
        if (mBitmapMiddle == null) return
        if (offerX == 0f) {
            canvas?.drawBitmap(getMiddleBitmapBean().bitmap, 0F, 0F, Paint())
            if (isOver) {
                isOver = false
                offerX = 0f
                isAminEnd = true
            }
        }

        //右滑 显示左边图片和中间图片
        if (offerX > 0f && getPreBitmapBean().hasBitmap) {
            canvas?.drawBitmap(getMiddleBitmapBean().bitmap, offerX, 0F, Paint())
            canvas?.drawBitmap(getPreBitmapBean().bitmap, -(Display.mWidth - offerX), 0f, Paint())
            if (isOver) {
                isOver = false
                prePager()
                offerX = 0f
                isAminEnd = true
            }
        } else if (offerX < 0f && getNextBitmapBean().hasBitmap) {
            canvas?.drawBitmap(getMiddleBitmapBean().bitmap, offerX, 0F, Paint())
            canvas?.drawBitmap(getNextBitmapBean().bitmap, Display.mWidth + offerX, 0f, Paint())
            if (isOver) {
                isOver = false
                nextPager()
                offerX = 0f
                isAminEnd = true
            }
        }
    }

    val PagerReplacementStart = 0x001 //页面改变

    var myHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            if (PagerReplacementStart == msg?.what) {
                var isThisPager = msg?.obj as Boolean

                aminIndex++
                var t1 = mInterpolator.getInterpolation(startValue + aminIndex * singeVlue)


                if (offerX >= 0 && !isThisPager) {
                    //右滑
                    offerX = startMoveX + (Display.mWidth - startMoveX) * t1
                } else if (!isThisPager) {
                    //左滑
                    offerX = startMoveX + (-Display.mWidth - startMoveX) * t1
                } else if (isThisPager) {

                    offerX = startMoveX + (0 - startMoveX) * t1
                }


                if (t1 >= 1.0) {
                    isOver = true
                    if (offerX >= 0 && !isThisPager) {
                        //右滑
                        offerX = Display.mWidth.toFloat()
                    } else if (!isThisPager) {
                        //左滑
                        offerX = -Display.mWidth.toFloat()
                    } else if (isThisPager) {
                        offerX = 0f
                    }

                    invalidate()
                } else {
                    invalidate()
                    var newMsg = Message()
                    newMsg.what = msg.what
                    newMsg.obj = msg.obj
                    sendMessageDelayed(newMsg, intervalValue.toLong())
                }
            }
        }
    }

    var aminIndex = 0
    var time = 100f           //动画持续时间
    var startValue = 0f       //开始的值
    var intervalValue = 5f   //动画间隔

    var mInterpolator = AccelerateDecelerateInterpolator()  //插值器
    var singeVlue = (1f - startValue) / (time / intervalValue)    //每次插值器增加值
    var isAminEnd = true
    fun aminInter(isThisPager: Boolean = false) {
        isAminEnd = false
        startMoveX = offerX
        aminIndex = 0

        var msg = Message()
        msg.obj = isThisPager
        msg.what = PagerReplacementStart
        myHandler.sendMessage(msg)
    }

    var startMoveX = 0f
    var oldOfferX = 0f
    var downX = 0f
    var downY = 0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isAminEnd) return true

        if (event?.action == MotionEvent.ACTION_DOWN) {
            myHandler.removeMessages(PagerReplacementStart)
            oldOfferX = offerX

            downX = event.x
            downY = event.y

            isOver = false
            isClickFlag = true
        }

        if (event?.action == MotionEvent.ACTION_MOVE) {

            offerX = event.x - downX + oldOfferX

            if (Math.abs(offerX) > mClickFlag) {
                isClickFlag = false
            }

            if (offerX > 0 && getPreBitmapBean().hasBitmap) {
                invalidate()
            }

            if (offerX < 0 && getNextBitmapBean().hasBitmap) {
                invalidate()
            }
        }

        if (event?.action == MotionEvent.ACTION_UP) {
            //在这里确定是否跳到上一章或者跳到下一章完成
            //上一页 下一页 或者还是本页

            if (isClickFlag) {
                isOver = true
                //点击事件
                if (event?.x.toInt() in (Display.mWidth / 2 - Display.mWidth / 5)..(Display.mWidth / 2 + Display.mWidth / 5)
                        && event?.y.toInt() in (Display.mHeight / 2 - Display.mHeight / 7)..(Display.mHeight / 2 + Display.mHeight / 7)) {
                    offerX = 0f
                    invalidate()
                    mListener?.onSetting()
                } else {
                    if (getNextBitmapBean().hasBitmap) {
                        offerX = -Display.mWidth.toFloat()
                        invalidate()
                    }
                }

            } else {
                //滑动事件
                if (offerX > Display.mWidth / 5 && getPreBitmapBean().hasBitmap) {
                    //上一页
                    aminInter()
                } else if (-offerX > Display.mWidth / 5 && getNextBitmapBean().hasBitmap) {
                    //下一页
                    aminInter()
                } else {
                    if (offerX > 0 && getPreBitmapBean().hasBitmap
                            || offerX < 0 && getNextBitmapBean().hasBitmap) {
                        aminInter(true)
                    } else {
                        offerX = 0f
                        isOver = true
                    }

                    //本页
//                    isOver = true
//                    invalidate()
                }
            }
        }
        return true

    }

    private fun getBitmap() {
        bookPageFactoryMiddle.createBitmap(mBitmapIndex, getMiddleBitmapBean())
    }

    fun nextPager() {
        mBitmapIndex++
        middleIndex = getWheelValue(middleIndex + 1)

        setBitmap(1)

        notifyPageIndex()
    }

    fun prePager() {
        mBitmapIndex--
        middleIndex = getWheelValue(middleIndex - 1)

        //上一页需要获取左边bitmap
        setBitmap(2)

        notifyPageIndex()
    }

    fun notifyPageIndex() {
        mListener?.notifyPageIndex(mBitmapIndex, bookPageFactoryMiddle.mChapterTitle)
    }

    fun getMiddleBitmapBean(): BitmapBean {
        return mBitmaps[middleIndex]
    }

    fun getPreBitmapBean(): BitmapBean {
        var index = getWheelValue(middleIndex - 1)
        return mBitmaps[index]
    }

    fun getNextBitmapBean(): BitmapBean {
        var index = getWheelValue(middleIndex + 1)
        return mBitmaps[index]
    }

    //得到轮子值
    fun getWheelValue(value: Int): Int {
        if (value > mBitmaps.size - 1) {
            return 0
        } else if (value < 0) {
            return mBitmaps.size - 1
        } else {
            return value
        }
    }


    /**
     * flag 0 没有翻页，1 右滑 ，2 左滑
     */
    fun setBitmap(flag: Int) {
        if (flag == 1) {
//            mBitmapMiddle = Bitmap.createBitmap(mBitmapRight)
        } else if (flag == 2) {
//            mBitmapMiddle = Bitmap.createBitmap(mBitmapLeft)
        } else if (flag == 0) {
//            var fenye = bookPageFactoryMiddle.fenye[mBitmapIndex]
            bookPageFactoryMiddle?.createBitmap(mBitmapIndex, getMiddleBitmapBean())
        }

        //第一页
        if (mBitmapIndex == 0 && bookPageFactoryMiddle.fenye.size != 1) {
            if (bookPageFactoryLeft?.fenye.size == 0) {
                bookPageFactoryLeft.setText(mListener?.getPreChapter())
            }

            bookPageFactoryLeft?.createBitmap(bookPageFactoryLeft.fenye.size - 1, getPreBitmapBean())
            bookPageFactoryMiddle?.createBitmap(1, getNextBitmapBean())

            //第2页到倒数第二页
        } else if (mBitmapIndex in 1..bookPageFactoryMiddle.fenye.size - 2 && bookPageFactoryMiddle.fenye.size >= 3) {
            bookPageFactoryMiddle.createBitmap(mBitmapIndex - 1, getPreBitmapBean())
            bookPageFactoryMiddle.createBitmap(mBitmapIndex + 1, getNextBitmapBean())

            //在最后一页
        } else if (mBitmapIndex == bookPageFactoryMiddle.fenye.size - 1 && bookPageFactoryMiddle.fenye.size != 1) {
            if (bookPageFactoryRight.fenye.size == 0) {
                bookPageFactoryRight.setText(mListener?.getNextChapter())
            }

            bookPageFactoryMiddle.createBitmap(mBitmapIndex - 1, getPreBitmapBean())
            bookPageFactoryRight.createBitmap(0, getNextBitmapBean())

        } else if (bookPageFactoryMiddle.fenye.size == 1 && mBitmapIndex == 0) {
            bookPageFactoryLeft?.createBitmap(bookPageFactoryLeft.fenye.size - 1, getPreBitmapBean())
            bookPageFactoryRight.createBitmap(0, getNextBitmapBean())
        } else if (mBitmapIndex == -1) {
            mListener?.actionTurnPage(false)

            bookPageFactoryRight = bookPageFactoryMiddle.clone() as BookPageFactory
            bookPageFactoryMiddle = bookPageFactoryLeft.clone() as BookPageFactory
            bookPageFactoryLeft.setText(mListener?.getPreChapter())

            mBitmapIndex = bookPageFactoryMiddle.fenye.size - 1
            setBitmap(3)
        } else if (mBitmapIndex == bookPageFactoryMiddle.fenye.size) {
            mListener?.actionTurnPage(true)

            bookPageFactoryLeft = bookPageFactoryMiddle.clone() as BookPageFactory
            bookPageFactoryMiddle = bookPageFactoryRight.clone() as BookPageFactory
            bookPageFactoryRight.setText(mListener?.getNextChapter())

            mBitmapIndex = 0
            setBitmap(3)
        }
    }

    //设置白天和黑夜 模式
    fun setMoonAndDay() {
        bookPageFactoryLeft.initPaint()
        bookPageFactoryMiddle.initPaint()
        bookPageFactoryRight.initPaint()

        setBitmap(0)
        invalidate()
    }

    //加减字体
    fun setFont(isAdd: Boolean) {
        if (isAdd) {
            PaintInfo.textSize = PaintInfo.textSize + 5
        } else {
            PaintInfo.textSize = PaintInfo.textSize - 5
        }
        //字体改变页数会改变 需要重新到会第一页
        mBitmapIndex = 0

        //字体改变需要重新计算分页
        bookPageFactoryMiddle.startTast()
        bookPageFactoryLeft.startTast()
        bookPageFactoryRight.startTast()


        setBitmap(0)
        invalidate()
    }


    interface Listener {
        //得到章节 每次第一次进入 index：-1
        fun getChapter(index: Int): ChapterBean?

        fun getNextChapter(): ChapterBean?

        fun getPreChapter(): ChapterBean?

        fun getMiddleChapter(): ChapterBean?

        //翻到上一章或者下一章章节回调 是否下一章
        fun actionTurnPage(isNext: Boolean)

        fun onSetting()

        //通知数据库更新，用户看的最新的页数
        fun notifyPageIndex(bitmapIndex: Int, chapterTitle: String)
    }

    fun destory() {
        mBitmapMiddle.bitmap?.recycle()
        mBitmapLeft.bitmap?.recycle()
        mBitmapRight.bitmap?.recycle()

    }

}