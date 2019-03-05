package com.anonymouser.book.bean

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import com.anonymouser.book.BookApp
import com.anonymouser.book.R
import com.anonymouser.book.widget.Display

/**
 * Created by YandZD on 2017/7/7.
 */
object PaintInfo {
    val bgImgs = kotlin.arrayOf(R.drawable.reader_background_brown_big_img
            , R.drawable.reader_background_brown_big_img6
            , R.drawable.reader_background_brown_big_img7)

    var bookName = ""
    var bgColor = Color.parseColor("#ffe7dcbe")       //背景颜色
        get() {
            if (isDay)
                return dayBgColor  //白天
            else
                return moonBgColor
        }
    var textColor = Color.parseColor("#AA000000")       //字体颜色
        get() {
            if (isDay)
                return dayTextColor  //白天
            else
                return moonTextColor
        }

    var textSize = Display.mDensity * 18                //字体大小
    var chapterTitleSize = Display.mDensity * 13
    var infoSize = Display.mDensity * 12
    var powerNum = 50
    var isDay = true
    var linkHeight = 1.5f

    var moonBgColor = Color.parseColor("#ff333232")
    var moonTextColor = Color.parseColor("#FFFFFF")
    var dayBgColor = -1
    var dayTextColor = Color.parseColor("#AA000000")
    var dayBgImg = 0
    var isSimple = true
    var mBgBitmap: Bitmap? = null
        get() {
            if (field == null || field?.isRecycled!!) {
                if (dayBgImg < 0 || dayBgImg >= bgImgs.size)
                    dayBgImg = 0
                field = BitmapFactory.decodeResource(BookApp.mContext.resources, bgImgs[dayBgImg])
            }
            return field
        }

//    var mTypeface: Typeface? = null
//        get() {
//            if (field == null) {
//                field = Typeface.createFromAsset(BookApp.mContext.assets, "fonts/hwzhongsong.ttf")
//            }
//            return field
//        }

    fun setInfo(info: UserInfo) {
        dayBgColor = info.dayBgColor
        dayTextColor = info.dayTextColor
        moonBgColor = info.moonBgColor
        moonTextColor = info.moonTextColor
        dayBgImg = info.dayBgImg
        textSize = info.textSize
        isDay = info.isDay
        isSimple = info.isSimple
    }
}