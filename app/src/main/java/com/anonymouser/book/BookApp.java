package com.anonymouser.book;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.LocaleList;
import android.support.multidex.MultiDexApplication;

import com.aitangba.swipeback.ActivityLifecycleHelper;
import com.anonymouser.book.bean.BookCaseBean;
import com.anonymouser.book.bean.BookCaseBeanDao;
import com.anonymouser.book.bean.BookContentDao;
import com.anonymouser.book.bean.BookInfoDao;
import com.anonymouser.book.bean.DaoMaster;
import com.anonymouser.book.bean.PaintInfo;
import com.anonymouser.book.bean.UserInfoDao;
import com.anonymouser.book.bean.ZhuiShuBookContentDao;
import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.greendao.database.Database;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by YandZD on 2017/7/13.
 */

public class BookApp extends MultiDexApplication {
    private static GoogleAnalytics sAnalytics;
    public static Context mContext = null;
    private Tracker mTracker;

    public static boolean isSimple = true;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        sAnalytics = GoogleAnalytics.getInstance(this);
        sAnalytics.setLocalDispatchPeriod(30);

        registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build());

        //设置全局繁简体
        initLanguage();

        LeakCanary.install(this);

        initOkGo();

        initDataBase();
        closeAndroidPDialog();
    }

    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 判断手机字体繁简体 */
    private void initLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else locale = Locale.getDefault();

        String region = locale.getCountry();
        if (region.contains("TW") || region.contains("HK")) {
            isSimple = false;
        } else {
            isSimple = true;
        }

        if (getSharedPreferences("info", MODE_PRIVATE).getBoolean("isFirst", true)) {
            PaintInfo.INSTANCE.setSimple(isSimple);
            getSharedPreferences("info", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirst", false)
                    .commit();
        }
    }

    /* 初始化okgo */
    private void initOkGo() {
        OkGo.getInstance().init(this);
        try {
            OkGo.getInstance().init(this)                       //必须调用初始化
                    .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                    .setRetryCount(3);                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = sAnalytics.newTracker(R.xml.global_tracker);
            sAnalytics.enableAutoActivityReports(this);
        }
        return mTracker;
    }

    private void initDataBase() {
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, "db",
                null);
        new DaoMaster(helper.getWritableDatabase());

    }

    ArrayList<BookCaseBean> beans;

    /* 增加追书api阅读，数据库进行相应升级，从版本3到版本4 */
    public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
        public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, final int oldVersion, final int newVersion) {
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

                        @Override
                        public void onCreateAllTables(Database db, boolean ifNotExists) {
                            DaoMaster.createAllTables(db, ifNotExists);
                            if (oldVersion == 3 && newVersion == 4) {
                                for (BookCaseBean bean : beans) {
                                    db.execSQL(String.format("INSERT INTO BOOK_CASE_BEAN('BOOK_NAME','AUTHER','READ_PROGRESS','READ_PAGE_INDEX','READ_CHAPTER_TITLE','IS_THE_CACHE','BASE_LINK','USE_SOURCE','IMG','IS_ZHUI_SHU') VALUES ('%s','%s','%d','%d','%s','%d','%s','%s','%s','%b')"
                                            , bean.getBookName(), bean.getAuther(), bean.getReadProgress(), bean.getReadPageIndex(), bean.getReadChapterTitle(), 0, bean.getBaseLink(), bean.getUseSource(), bean.getImg(), false));
                                }
                            }
                        }

                        @Override
                        public void onDropAllTables(Database db, boolean ifExists) {
//                            if (oldVersion == 3 && newVersion == 4) {
                            beans = new ArrayList<>();
                            BookCaseBean bean;
                            Cursor cursor = db.rawQuery("select * from BOOK_CASE_BEAN", null);
                            while (cursor.moveToNext()) {
                                bean = new BookCaseBean();
                                bean.setBookName(cursor.getString(0));
                                bean.setAuther(cursor.getString(1));
                                bean.setReadProgress(cursor.getInt(2));
                                bean.setReadPageIndex(cursor.getInt(3));
                                bean.setReadChapterTitle(cursor.getString(4));
                                bean.setBaseLink(cursor.getString(6));
                                bean.setUseSource(cursor.getString(7));
                                bean.setImg(cursor.getString(8));
                                bean.setIsZhuiShu(false);
                                beans.add(bean);
                            }
//                            }


                            DaoMaster.dropAllTables(db, ifExists);
                        }
                    }
                    , UserInfoDao.class
                    , BookInfoDao.class
                    , BookCaseBeanDao.class
                    , BookContentDao.class
                    , ZhuiShuBookContentDao.class);
        }
    }
}
