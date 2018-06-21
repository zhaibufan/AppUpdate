package com.zxf.appupdaterdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.zxf.appupdaterdemo.update.updater.Updater;
import com.zxf.appupdaterdemo.update.updater.UpdaterConfig;

public class MainActivity extends AppCompatActivity {

    private static final String APK_URL = "http://mastra-android-apk.oss-cn-shenzhen.aliyuncs.com/892556.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void download(View view) {
        if (!TextUtils.isEmpty(APK_URL)) {
            UpdaterConfig config = new UpdaterConfig.Builder(this)
                    .setTitle("app.apk")
                    .setDescription("正在下载最新版本")
                    .setFileUrl(APK_URL)
                    .setCanMediaScanner(true)
                    .build();
            Updater.get().showLog(true).download(config);
        }
    }
}
