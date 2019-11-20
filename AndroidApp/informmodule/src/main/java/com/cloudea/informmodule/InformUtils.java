package com.cloudea.informmodule;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.LinearLayout;

import com.cloudea.basemodule.Data;

import java.util.List;

//外观模式
public class InformUtils {
    private static InformUtils instance = null;
    public static synchronized InformUtils getInstance(Context context){
        if(instance == null){
            instance = new InformUtils(context);
        }
        return  instance;
    }

    //运行上下文
    private Context context;
    private InformService.InformBinder binder;
    private ClockReceiver clockReceiver;
    private Intent intent;
    private Class intentClass;
    private InformService.OnInformListener onInformListener;
    private InformService.OnRequestDataListener onRequestDataListener;
    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (InformService.InformBinder) iBinder;
            binder.checkPermission();
            if(onInformListener != null){
                binder.setOnInformListener(onInformListener);
            }
            if(onRequestDataListener != null){
                binder.setRequestDataListenr(onRequestDataListener);
            }
            if(intentClass != null){
                binder.setPressIntent(intentClass);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
        }
    };

    private InformUtils(Context context){
        this.context = context;
    }

    //启动服务
    public void startServer(){
        intent = new Intent(context, InformService.class);
        context.startService(intent);
        context.bindService(intent, con, 0);

        //注册广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        clockReceiver = new ClockReceiver();
        context.registerReceiver(clockReceiver, intentFilter);
        Log.v("通知", "广播已经注册");
    }

    //停止服务
    public void stopServer(){
        context.unbindService(con);
        context.stopService(intent);

        //注销广播
        context.unregisterReceiver(clockReceiver);

    }


    public void  setRequestDataListener(InformService.OnRequestDataListener onRequestDataListener){
        if(isRunning()){
            binder.setRequestDataListenr(onRequestDataListener);
        }else{
            this.onRequestDataListener = onRequestDataListener;
            startServer();
        }
    }


    public void setInformListener(InformService.OnInformListener onInformListener){
        if(isRunning()){
            binder.setOnInformListener(onInformListener);
        }else{
            this.onInformListener = onInformListener;
            startServer();
        }
    }

    public void setInformIntent(Class intentClass){
        if(isRunning()){
            binder.setPressIntent(intentClass);
        }else{
            this.intentClass = intentClass;
            startServer();
        }
    }

    //返回服务的状态
    public boolean isRunning(){
        return binder != null;
    }
}
