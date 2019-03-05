package com.anonymouser.book.bean;

import android.graphics.Color;

import com.anonymouser.book.widget.Display;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by YandZD on 2017/7/17.
 */
@Entity
public class UserInfo {
    private boolean isDay = true;
    private float baseTextSize = 20;
    private float coefficient = 0;                  //调节文本大小的系数
    private float textSize = Display.INSTANCE.getMDensity() * (baseTextSize + coefficient);
    private int moonBgColor = Color.parseColor("#ff333232");
    private int moonTextColor = Color.parseColor("#FFFFFF");
    private int dayBgColor = -1;
    private int dayBgImg = 0;
    private int dayTextColor = Color.parseColor("#AA000000");
    private int light = 200;              //亮度默认 230

    private float lineHeight = 1.5f;       //行高 1.2   1.5  1.8
    private boolean isSimple = PaintInfo.INSTANCE.isSimple();       //是否简体
    @Transient
    public int maxLight = 230;

    @Generated(hash = 1818598073)
    public UserInfo(boolean isDay, float baseTextSize, float coefficient, float textSize,
                    int moonBgColor, int moonTextColor, int dayBgColor, int dayBgImg,
                    int dayTextColor, int light, float lineHeight, boolean isSimple) {
        this.isDay = isDay;
        this.baseTextSize = baseTextSize;
        this.coefficient = coefficient;
        this.textSize = textSize;
        this.moonBgColor = moonBgColor;
        this.moonTextColor = moonTextColor;
        this.dayBgColor = dayBgColor;
        this.dayBgImg = dayBgImg;
        this.dayTextColor = dayTextColor;
        this.light = light;
        this.lineHeight = lineHeight;
        this.isSimple = isSimple;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
    }

    public boolean getIsDay() {
        return this.isDay;
    }

    public void setIsDay(boolean isDay) {
        this.isDay = isDay;
    }

    public float getBaseTextSize() {
        return this.baseTextSize;
    }

    public void setBaseTextSize(float baseTextSize) {
        this.baseTextSize = baseTextSize;
    }

    public float getCoefficient() {
        return this.coefficient;
    }

    public void setCoefficient(float coefficient) {
        this.coefficient = coefficient;
    }

    public float getTextSize() {
        return this.textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getMoonBgColor() {
        return this.moonBgColor;
    }

    public void setMoonBgColor(int moonBgColor) {
        this.moonBgColor = moonBgColor;
    }

    public int getMoonTextColor() {
        return this.moonTextColor;
    }

    public void setMoonTextColor(int moonTextColor) {
        this.moonTextColor = moonTextColor;
    }

    public int getDayBgColor() {
        return this.dayBgColor;
    }

    public void setDayBgColor(int dayBgColor) {
        this.dayBgColor = dayBgColor;
    }

    public int getDayBgImg() {
        return this.dayBgImg;
    }

    public void setDayBgImg(int dayBgImg) {
        this.dayBgImg = dayBgImg;
    }

    public int getDayTextColor() {
        return this.dayTextColor;
    }

    public void setDayTextColor(int dayTextColor) {
        this.dayTextColor = dayTextColor;
    }

    public int getLight() {
        return this.light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public float getLineHeight() {
        return this.lineHeight;
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }

    public boolean getIsSimple() {
        return this.isSimple;
    }

    public void setIsSimple(boolean isSimple) {
        this.isSimple = isSimple;
    }


}
