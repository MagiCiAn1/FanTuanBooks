package com.anonymouser.book.bean

import android.graphics.Bitmap
import com.anonymouser.book.widget.Display

/**
 * Created by YandZD on 2017/7/18.
 */
class BitmapBean {
    var bitmap = Bitmap.createBitmap(Display.mWidth, Display.mHeight, Bitmap.Config.ARGB_8888)
    var hasBitmap = true
}