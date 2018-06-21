package com.zxf.appupdaterdemo.update.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import com.zxf.appupdaterdemo.update.updater.FileDownloadManager;
import com.zxf.appupdaterdemo.update.updater.Logger;
import com.zxf.appupdaterdemo.update.updater.UpdaterUtils;

import java.io.File;


public class ApkInstallReceiver extends BroadcastReceiver {

    private static final String TAG = "ApkInstallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long localDownloadId = UpdaterUtils.getLocalDownloadId(context);
            if (downloadApkId == localDownloadId) {
                Logger.get().d("download complete. downloadId is " + downloadApkId);
                installApk(context, downloadApkId);
            }
        } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            //处理 如果还未完成下载，用户点击Notification
            Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewDownloadIntent);
        }
    }

    private static void installApk(Context context, long downloadApkId) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
        if (downloadFileUri != null) {
            Logger.get().d("file location " + downloadFileUri.toString());
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    FileDownloadManager fdm = FileDownloadManager.get();
                    String downloadPath = fdm.getDownloadPath(context, downloadApkId);

                    // 对apk路径做处理
                    int index = downloadPath.indexOf('s');
                    String substring = downloadPath.substring(index - 1);
                    Logger.get().d(" substring = "+substring);

                    File file = (new File(substring));
                    Uri apkUri = FileProvider.getUriForFile(context, "com.zxf.appupdaterdemo.fileprovider", file);
                    Logger.get().d("apkUri="+apkUri);
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //该Flag必须在FLAG_ACTIVITY_NEW_TASK的后面，要不会被覆盖
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    context.startActivity(install);
                }catch (Exception e) {
                    e.printStackTrace();
                    Logger.get().d("Exception="+e.getMessage().toString());
                } finally {
                }

            } else {
                Logger.get().d("7.0以下");
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                context.startActivity(install);
            }

        } else {
            Logger.get().d("download failed");
        }
    }
}
