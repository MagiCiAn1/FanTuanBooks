package com.anonymouser.book.utlis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.widget.RemoteViews;

import com.anonymouser.book.BuildConfig;
import com.anonymouser.book.R;
import com.anonymouser.book.bean.ConfBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.base.Request;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * app 更新
 * Created by YandZD on 2016/12/6.
 */

public class AppUpdate {

    private Activity mActivity;
    private final static int NOTIFICATION_ID = 108;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private RemoteViews contentView;
    private Notification notification;
    private int appLogo = R.drawable.app_logo;
    private static boolean isDownload = false;

    public AppUpdate(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 版本更新
     */
    public boolean getVersionInfo(ConfBean.AppBean appBean) {
        if (isDownload) return false;

        try {
            if (mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionCode < appBean.getVersion()) {
//            if (0 < appBean.getVersion()) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("发现新版本是否下载更新？")
                        .setCancelable(false)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new UpdateVersionListener(appBean.getLink(), "book.apk"))
                        .show();
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {

        }
        return true;
    }

    /**
     * 下载文件并安装
     *
     * @author Administrator
     *         实例化需要传入url路径
     */
    class UpdateVersionListener implements DialogInterface.OnClickListener {
        String urlDown;
        String apkName;

        public UpdateVersionListener(String urlDown, String apkName) {
            this.urlDown = urlDown;
            this.apkName = apkName;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            new Thread() {
                public void run() {
                    OkGo.<File>get(urlDown)
                            .execute(new FileCallback(mActivity.getFilesDir().getAbsolutePath(), apkName) {
                                @Override
                                public void onStart(Request<File, ? extends Request> request) {
                                    super.onStart(request);
                                    isDownload = true;
                                    setNotification();
                                }

                                @Override
                                public void onSuccess(com.lzy.okgo.model.Response<File> response) {
                                    String cmd = "chmod 777 " + response.body().getAbsolutePath();
                                    try {
                                        Runtime.getRuntime().exec(cmd);
                                    } catch (Exception e) {

                                    }

                                    mNotificationManager.cancelAll();
                                    install(response.body());
                                }
                                //文件下载时，可以指定下载的文件目录和文件名

                                @Override
                                public void downloadProgress(Progress progress) {
                                    super.downloadProgress(progress);
                                    setNotificationProgress((int) (progress.fraction * 100));
                                }

                                @Override
                                public void onError(com.lzy.okgo.model.Response<File> response) {
                                    super.onError(response);
                                }
                            });
                }
            }.start();


        }


        private void setNotification() {
            mNotificationManager = (NotificationManager) mActivity.getSystemService(NOTIFICATION_SERVICE);

            mBuilder = new NotificationCompat.Builder(mActivity);

            contentView = new RemoteViews(mActivity.getPackageName(), R.layout.app_updata_progress_bar);
            mBuilder.setContentTitle("饭团小说 下载中")
                    .setTicker("饭团小说 下载中")
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    //震动
                    //.setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(appLogo);

            notification = mBuilder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.contentView = contentView;
            notification.icon = appLogo;
            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }

        private void setNotificationProgress(int progress) {
            contentView.setTextViewText(R.id.tv_updata, String.valueOf(progress) + "%");
            contentView.setProgressBar(R.id.notification_Progress, 100, progress, false);

            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }

    }


    public void install(File filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            /* 7.0 以上的权限不一样 */
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", filePath);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(filePath), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        mActivity.startActivity(intent);
    }


}
