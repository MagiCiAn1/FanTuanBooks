package com.anonymouser.book

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.anonymouser.book.view.HomeActivity

/**
 * 保留第一屏
 *
 * Created by YandZD on 2017/7/13.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
//        copyJar()

//        initAd()

//        val tracker = (application as BookApp).defaultTracker
//        tracker.send(HitBuilders.EventBuilder()
//                .setCategory("Action")
//                .setAction("Search_Book")
//                .build())
    }

//    //包括复制和下载新的jar包 
//    fun jarInfo() {
//        Observable.just(getSharedPreferences，("init", Context.MODE_PRIVATE).getBoolean("isCopyJar", false))
//                .subscribeOn(Schedulers.io())
//                .map {
//                    t ->
//                    if (!t) {
//                        var file = File(filesDir, "/jar")
//                        file.mkdir()
//
//                        var paths = assets.list("jar")
//                        var input: InputStream
//                        var output: BufferedOutputStream
//                        var byte = ByteArray(2048)
//                        for (filePath in paths) {
//                            input = assets.open("jar/" + filePath)
//
//                            println(file.absoluteFile)
//                            output = BufferedOutputStream(FileOutputStream(File(filesDir, "/jar/" + filePath)))
//
//                            while (input.read(byte) > 0) {
//                                output.write(byte)
//                            }
//                        }
//                        getSharedPreferences("init", Context.MODE_PRIVATE).edit()
//                                .putBoolean("isCopyJar", true).commit()
//                    }
//                }
//                .map {
//                    //获取接口jar版本
//                    var call = OkGo.get<String>(ServiceApi.HEADAPI + "jar")
//                            .converter(object : StringCallback() {
//                                override fun onSuccess(response: Response<String>?) {
//                                }
//                            })
//                            .adapt()
//                    var response = call.execute()
//                    response.body()
//                }
//                .flatMap {
//                    t: String ->
//                    //需要下载的jar链接
//                    var needDownloadJar = ArrayList<JarBean>()
//
//                    var beans = Gson().fromJson(t, Array<JarBean>::class.java)
//                    for (x in beans) {
//                        try {
//                            if (BookLoadFactory(x.tag, null).version < x.version) {
//                                needDownloadJar.add(x)
//                            }
//                        } catch (e: Exception) {
//                            needDownloadJar.add(x)
//                        }
//                    }
//
//                    Observable.fromIterable(needDownloadJar)
//                            .subscribeOn(Schedulers.io())
//                }
//                .map {
//                    t ->
//                    OkGo.get<File>(t.link)
//                            .execute(object : FileCallback(BookApp.mContext.filesDir.absolutePath + "/jar", t.tag) {
//                                override fun onSuccess(response: Response<File>?) {
//
//                                }
//                            })
//                    "s"
//                }
//                .subscribe()
//    }
}