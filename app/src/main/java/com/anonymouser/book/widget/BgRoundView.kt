package com.anonymouser.book.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.anonymouser.book.R
import com.anonymouser.book.widget.Display


/**
 * Created by YandZD on 2017/7/18.
 */
class BgRoundView : View {

    var radius = 10F  //半径
    var color = Color.parseColor("#000000")
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context) : this(context, null, 0) {
    }

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.bg_round)
        radius = ta.getDimension(R.styleable.bg_round_radius, Display.mDensity * 10)
    }

    constructor(context: Context, attributeSet: AttributeSet?, flag: Int) : super(context, attributeSet, flag) {
    }


    override fun onDraw(canvas: Canvas?) {
        var paint = Paint()
        paint.color = color
        var rectf = RectF(0f, 0f, radius * 2, radius * 2)
        canvas?.drawRoundRect(rectf, radius, radius, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


        setMeasuredDimension((radius * 2).toInt(), (radius * 2).toInt())
    }
}