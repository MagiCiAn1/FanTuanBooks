package com.anonymouser.book.view

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.anonymouser.book.R
import com.anonymouser.book.bean.DownloadBookEvent
import com.anonymouser.book.event.CacheProgressEvent
import com.anonymouser.book.event.CheckUpdateEvent
import com.anonymouser.book.event.SaveIsShowAdInfo
import com.anonymouser.book.presenter.HomePresenter
import com.anonymouser.book.service.DownloadService
import com.anonymouser.book.utlis.AppUpdate
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by YandZD on 2017/7/13.
 */
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val HIDE_FIRST_LOGO = 0x0001;
    var mPresenter = HomePresenter()
    var mDownloadService = DownloadService()
    var mCheckUpdateEvent: CheckUpdateEvent? = null


    var mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            if (msg?.what == HIDE_FIRST_LOGO) {
                firstLogo.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_home)
        mHandler.sendEmptyMessageDelayed(HIDE_FIRST_LOGO, 2000)

        mPresenter.initJar()

        //删除临时数据
        mPresenter.removeTempDataBase()

        val tablayout = findViewById(R.id.smartTabLayout) as SmartTabLayout
        val viewPager = findViewById(R.id.viewPager) as ViewPager

        val creator = FragmentPagerItems.with(this)
        creator.add(resources.getString(R.string.book_case), BookcaseFragment::class.java)
        creator.add(resources.getString(R.string.category), CategoryFragment::class.java)
        creator.add("排行榜", HotListFragment::class.java)

        val adapter = FragmentPagerItemAdapter(
                supportFragmentManager, creator.create())

        viewPager.adapter = adapter

        tablayout.setViewPager(viewPager)

        viewPager.offscreenPageLimit = 2

        EventBus.getDefault().register(this)

        nav_view.setNavigationItemSelectedListener(this@HomeActivity)

        ivTopMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.notfiyBookCase()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    fun onSearch(view: View) {
        var intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun cacheEvent(event: CacheProgressEvent) {
        if (event.isFinish)
            tvCacheProgress.visibility = View.GONE
        else
            tvCacheProgress.visibility = View.VISIBLE
        tvCacheProgress.text = "缓存中：${event.msg}"
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onDownloadEvent(downloadBookEvent: DownloadBookEvent) {
        mDownloadService.onDownloadEvent(downloadBookEvent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCheckUpdateEvent(event: CheckUpdateEvent): Boolean {
        mCheckUpdateEvent = event
        var update = AppUpdate(this)
        return update.getVersionInfo(mCheckUpdateEvent?.mBean)
    }

    @Subscribe
    fun onSaveIsShowAd(isShowAdInfo: SaveIsShowAdInfo) {
        getSharedPreferences("info", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("isShowAd", isShowAdInfo.isShowAd)
                .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /* 抽屉菜单功能实现 */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_check_app) {
            //版本升级
            if (mCheckUpdateEvent != null) {
                if (onCheckUpdateEvent(mCheckUpdateEvent!!)) {
                    Toast.makeText(this@HomeActivity, "已经是最新版了", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this@HomeActivity, "已经是最新版了", Toast.LENGTH_LONG).show()
            }
        } else if (id == R.id.nav_about) {
            var intent = Intent(this@HomeActivity, AboutActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.nav_feedback) {
            var intent = Intent(this@HomeActivity, FeedbackActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.nav_teach_use) {
            var intent = Intent(this@HomeActivity, UseTeachActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.nav_sharing) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享")
            intent.putExtra(Intent.EXTRA_TEXT, "http://yourbuffslonnol.com")
            intent.putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.app_name))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent.createChooser(intent, "请选择"))
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}