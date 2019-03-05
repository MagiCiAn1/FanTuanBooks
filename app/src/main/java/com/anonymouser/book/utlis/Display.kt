package com.anonymouser.book.widget

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager
import com.anonymouser.book.BookApp
import android.opengl.ETC1.getHeight
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.view.DisplayCutout
import com.lzy.okgo.OkGo.post







/**
 * Created by YandZD on 2017/7/7.
 */
object Display {
    private var tempWidth = 0
    private var tempHeight = 0
    private var tempDensity = 0f

    var mWidth = 0
        get() {
            getCalWidthAndHeight()
            return tempWidth
        }

    var mHeight = 0
        get() {
            getCalWidthAndHeight()
            return tempHeight
        }
    var mDensity = 0f
        get() {
            getCalWidthAndHeight()
            return tempDensity
        }

    //获取屏幕的宽高
    private fun getCalWidthAndHeight() {
        var hasNavigationBar = checkDeviceHasNavigationBar(BookApp.mContext)
        Log.d("checkNavigationBar", hasNavigationBar.toString())
        if (tempWidth == 0) {
            val windowManager = BookApp.mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = DisplayMetrics()
            if (hasNavigationBar){
                windowManager.defaultDisplay.getRealMetrics(metrics)
                tempWidth = metrics.widthPixels
                tempHeight = metrics.heightPixels
                tempDensity = metrics.density
                var resourceId  = BookApp.mContext?.resources?.getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId != null) {
                    if (resourceId > 0){
                        var statusBarHeight = BookApp.mContext?.getResources()?.getDimensionPixelSize(resourceId)
                        if (statusBarHeight!=null)
                            tempHeight = tempHeight- (statusBarHeight / tempDensity + 0.5f).toInt()
                    }
                }
            }else{
                windowManager.defaultDisplay.getMetrics(metrics)
                tempWidth = metrics.widthPixels
                tempHeight = metrics.heightPixels
                tempDensity = metrics.density
            }

//            if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
//                try {
//                    var point = Point()
//                    windowManager.defaultDisplay.getSize(point)
//                    tempWidth = point.x
//                    tempHeight = point.y
//
//                } catch (ignored: Exception) {
//                }
//            if (Build.VERSION.SDK_INT >= 17)
//                try {
//                    val realSize = Point()
//                    windowManager.defaultDisplay.getRealSize(realSize)
//                    tempWidth = realSize.x
//                    tempHeight= realSize.y
//                } catch (ignored: Exception) {
//                }

        }
    }

    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val rs = context.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {
        }

        return hasNavigationBar
    }

}