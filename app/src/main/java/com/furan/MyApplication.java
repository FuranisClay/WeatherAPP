package com.furan;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 如果有其他全局初始化工作，可以在这里完成
        // 例如：初始化日志框架、崩溃收集SDK等

        // 现在不再需要高德地图相关的初始化代码
    }
}