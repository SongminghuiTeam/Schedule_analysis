package com.cloudea.informmodule;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

public class ClockReceiver extends BroadcastReceiver {

    //当收到系统广播时或自己的广播时
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean active = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices(Integer.MAX_VALUE);
        for(ActivityManager.RunningServiceInfo info : serviceInfos){
            if(info.service.getClassName().equals("com.cloudea.informmodule.InformService")){
                active = true;
                break;
            }
        }


        if(!active){
            InformUtils.getInstance(context).startServer();
            Toast.makeText(context, "服务正重启", Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(context, "服务正运行", Toast.LENGTH_SHORT).show();
        }
    }
}
