package com.anonymouser.book.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextUtils
import com.anonymouser.book.bean.BitmapBean
import com.anonymouser.book.bean.ChapterBean
import com.anonymouser.book.bean.FenYe
import com.anonymouser.book.bean.PaintInfo
import taobe.tec.jcc.JChineseConvertor
import java.sql.Date
import java.text.SimpleDateFormat


/**
 * Created by YandZD on 2017/7/7.
 */

class BookPageFactory : Cloneable {
    var mContext: Context? = null
    var mSpace = "\t\t\t\t\t\t"

    var mMarginBottom = Display.mHeight / 26 //正文距离底部
    var mMarginTop = Display.mHeight / 25  //正文距离顶部
    var mMarginLeft = Display.mHeight.toFloat() / 50
    var mChapterTitle = "" //章节名
    var mParagraphList = ArrayList<String>()  //分段list
    var fenye = ArrayList<FenYe>()
    var mText: String? = null

    var mLineHeight = 0f
    var mSingePageLineCount = 0      //一页的行数
    var canvas = Canvas()
    //    var bitmap: Bitmap? = null
    var textPaint = Paint()         //正文
    var titlePaint = Paint()        //顶部标题
    var pagerInfoPaint = Paint(Paint.ANTI_ALIAS_FLAG)         //底部第几页
    var powerBorderPaint = Paint()        //电量边框
    var powerPaint = Paint()        //电量文字

    var mPagerOffsetY = 0f      //底部第几页偏移

    val mDateFormat = SimpleDateFormat("HH:mm")  //格式化时间
    val displayRectF = RectF(0f, 0f, Display.mWidth.toFloat(), Display.mHeight.toFloat()) //屏幕范围，绘制背景图片

    constructor(context: Context) {
        mContext = context
    }

    fun startTast() {
        formatPara()
        fenye()
    }

    //初始化所有画笔
    fun initPaint() {
        textPaint.color = PaintInfo.textColor
        textPaint.textSize = PaintInfo.textSize
//        textPaint.typeface = PaintInfo.mTypeface

        titlePaint.color = PaintInfo.textColor
        titlePaint.textSize = PaintInfo.chapterTitleSize

        pagerInfoPaint.textAlign = Paint.Align.LEFT
        pagerInfoPaint.textSize = PaintInfo.infoSize
        pagerInfoPaint.color = PaintInfo.textColor

        powerBorderPaint.style = Paint.Style.STROKE
        powerBorderPaint.strokeWidth = 2f
        powerBorderPaint.color = PaintInfo.textColor

        powerPaint.style = Paint.Style.FILL
        powerPaint.textSize = Display.mDensity * 7F
        powerPaint.textAlign = Paint.Align.CENTER;
        powerPaint.color = PaintInfo.textColor


        mPagerOffsetY = Display.mHeight.toFloat() * 24 / 25 + Display.mHeight.toFloat() / 120

    }


    var paragraphIndex = 0
    var lineText = ""
    //chapterTitle 章节名称
    fun createBitmap(index: Int, bitmapBean: BitmapBean) {
        if (fenye.size == 0) {
            bitmapBean.hasBitmap = false
            return
        }
        bitmapBean.hasBitmap = true

        paragraphIndex = fenye[index].paragraphIndex
        lineText = fenye[index].lineText

        //全屏的高度，需要隐藏状态栏、虚拟控件
        canvas.setBitmap(bitmapBean.bitmap)

        if (PaintInfo.bgColor != -1) {
            canvas.drawColor(PaintInfo.bgColor)
        } else {
            canvas.drawBitmap(PaintInfo.mBgBitmap, null, displayRectF, null)
        }

        var size = 0
        var y = mLineHeight + mMarginTop
        for (x in 0..mSingePageLineCount) {
            if (mParagraphList.size > paragraphIndex || lineText.isNotEmpty()) {
                if (lineText.isEmpty()) {
                    lineText = mParagraphList?.elementAtOrNull(paragraphIndex)!!
                    paragraphIndex++
                }

                size = textPaint.breakText(lineText, true, Display.mWidth.toFloat() - mMarginLeft * 2, null)

                canvas.drawText(lineText.substring(0, size), mMarginLeft, y, textPaint)

                y += mLineHeight

                lineText = lineText.substring(size)
            } else {
                break
            }
        }

        drawTopChapterTitle(canvas)

        drawInfo(canvas, index + 1)


        return
    }

    var mTempChapterTitle = ""
    //绘制顶部本章标题
    private fun drawTopChapterTitle(canvas: Canvas) {
        mTempChapterTitle = mChapterTitle
        if (!PaintInfo.isSimple) {
            try {
                mTempChapterTitle = JChineseConvertor.getInstance().s2t(mChapterTitle)
            } catch (e: Exception) {

            }
        }
        canvas.drawText(mTempChapterTitle, mMarginLeft, Display.mHeight.toFloat() / 30F, titlePaint)
    }

    private fun drawInfo(canvas: Canvas, index: Int) {
        var infoMarginTop = Display.mHeight.toFloat() / 50

        var pageStr = "$index / ${fenye.size}"
        var pageWidht = pagerInfoPaint.measureText(pageStr)
        canvas.drawText(pageStr, Display.mWidth - mMarginLeft * 2 - pageWidht, mPagerOffsetY + infoMarginTop, pagerInfoPaint)

        //当前系统时间
        canvas.drawText(mDateFormat.format(Date(System.currentTimeMillis())), mMarginLeft * 4f, mPagerOffsetY + infoMarginTop, pagerInfoPaint)

        //电池电量
        val left = mMarginLeft + 10
        val right = mMarginLeft * 2.5f + 10


        val fontMetrics = powerBorderPaint.fontMetrics

        //电池左边部分外框
        var rectF = RectF(left, mPagerOffsetY + infoMarginTop + fontMetrics.top - Display.mDensity * 4, right, mPagerOffsetY + infoMarginTop + fontMetrics.bottom)
        canvas.drawRect(rectF, powerBorderPaint)


        val baseline = (rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 2
        canvas.drawText(PaintInfo.powerNum.toString(), rectF.centerX(), baseline + 2, powerPaint)


//        //电池右边小矩形
        rectF = RectF(right, rectF.top + rectF.height() / 4, right + 0.2f * mMarginLeft, rectF.top + rectF.height() / 4 * 3)
        canvas.drawRect(rectF, powerPaint)
    }


    //数据处理
    fun fenye() {
        if (TextUtils.isEmpty(mText)) {
            fenye?.clear()
            return
        }
        fenye?.clear()
        //每行的高度
        mLineHeight = PaintInfo.textSize * PaintInfo.linkHeight
        //每页的行数
        mSingePageLineCount = ((Display.mHeight - mMarginTop - mMarginBottom) / mLineHeight).toInt() - 1

        var lineText = ""

        var paragraphIndex = 0

        var itemYe: FenYe
        while (mParagraphList?.elementAtOrNull(paragraphIndex) != null || !lineText.isEmpty()) {
            itemYe = FenYe()
            itemYe.paragraphIndex = paragraphIndex
            itemYe.lineText = lineText
            fenye.add(itemYe)

            for (x in 0..mSingePageLineCount) {
                if (mParagraphList?.elementAtOrNull(paragraphIndex) != null || lineText.length > 0) {
                    if (lineText.length <= 0) {
                        lineText = mParagraphList?.elementAtOrNull(paragraphIndex)!!
                        paragraphIndex++
                    }
                    textPaint.breakText(lineText, true, Display.mWidth.toFloat() - mMarginLeft * 2, null)
                    val size = textPaint.breakText(lineText, true, Display.mWidth.toFloat() - mMarginLeft * 2, null)

                    lineText = lineText.substring(size)
                } else {
                    break
                }
            }
        }
    }

    fun notifyTextSize() {
        startTast()
    }

    fun setText(chapter: ChapterBean?) {
        mChapterTitle = chapter?.title ?: ""
        mText = chapter?.content
        startTast()
    }


    //分段落
    fun formatPara() {
        if (TextUtils.isEmpty(mText)) {
            mParagraphList?.clear()
            return
        }
        mParagraphList?.clear()
        var paragraphs = mText?.split("\n")
        var isFirstParas = true
        var paragraph = ""
        for (i in paragraphs!!) {
            if (i.isEmpty()) {
                continue
            }
            if (isFirstParas) {
                paragraph = mSpace + i
                isFirstParas = false
            } else {
                paragraph = mSpace + i
            }

            if (!PaintInfo.isSimple) {
                try {
                    paragraph = JChineseConvertor.getInstance().s2t(paragraph)
                } catch (e: Exception) {

                }
            }

            mParagraphList?.add(paragraph)
        }
    }


    override public fun clone(): Any {
        var obj = super.clone() as BookPageFactory
        obj.mParagraphList = mParagraphList.clone() as ArrayList<String>
        obj.fenye = fenye.clone() as ArrayList<FenYe>
        obj.mText = mText

        return obj
    }


}