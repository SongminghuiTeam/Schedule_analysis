package com.cloudea.basemodule;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.LinkedList;
import java.util.TreeMap;

import cn.bmob.v3.Bmob;

public class BaseApplication extends Application {
    //全局Context对象
    private static Context context;
    public static synchronized Context getContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BaseApplication.context = getApplicationContext();

        //初始化Bmob
        Bmob.initialize(this, "3e5ce8193a371aed088a7263f7d85a1a");
    }


}
