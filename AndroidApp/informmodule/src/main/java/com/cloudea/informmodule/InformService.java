package com.cloudea.informmodule;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cloudea.basemodule.Data;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class InformService extends Service {

    private Thread thread;
    private InformBinder binder;
    private SoundPool soundPool;
    private int soundId;
    private OnInformListener onInformListener;           //当事件状态改变时，回调
    private OnRequestDataListener onRequestDataListener; // 当需要数据时回调
    private OnInformListener onNotificationPressed;
    private Class intentClass;                               //通知被点击时，的行为
    private String channelId = "smartPlanning_infrom_for_foreground";

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //开启线程
    @Override
    public void onCreate() {
        //初始化声音池
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        soundId = soundPool.load(this, R.raw.inform_voice, 1);

        //设置线程
        binder = new InformBinder(this);
        thread = new Thread(){
            @Override
            public void run() {
                while (!interrupted()){
                    if(onRequestDataListener != null){
                        checkAndPlay(onRequestDataListener.requestData());
                    }
                    try {
                        Thread.sleep(60000);           //一分钟检测一次
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //前台通知
        Notification.Builder notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.infrom_icon)
                .setContentTitle("智能通知服务正在运行")
                .setContentText("您可以正常收到我们的通知消息");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(channelId, "通知", NotificationManager.IMPORTANCE_HIGH);
            nc.enableLights(true);
            nc.enableVibration(true);
            nc.setShowBadge(true);
            nc.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nc.setLightColor(Color.RED);
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(nc);
            notification.setChannelId(channelId);
        }

        this.startForeground(8848, notification.build());

        //返回常驻后台标识
        return START_STICKY;
    }

    //关闭通知服务线程
    @Override
    public void onDestroy() {
        thread.interrupt();
    }

    private synchronized void checkAndPlay(List<Data> data){
        //判断是否有数据
        if(data != null && data.size() != 0){
            //挨个判断数据是否需要提醒
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            List<Data> dataInfomed = new LinkedList<>();
            Date date = new Date();
            for(int i = 0; i < data.size(); i++){
                //如果满足条件，则提醒
                Data dataItem = data.get(i);
                Date lowerBound = new Date(dataItem.start_time.getTime() - dataItem.note_time * 60000);
                boolean canInform = date.after(lowerBound) &&
                        dataItem.state == false;
                if(canInform){
                    //发起一个通知
                    Notification.Builder builder = new Notification.Builder(InformService.this);
                    builder.setTicker("您有事情需要立即去做");
                    builder.setAutoCancel(true);
                    builder.setContentTitle(dataItem.content);
                    builder.setContentText(sdf.format(dataItem.start_time) + "前到达" +  (dataItem.addr.equals("") ? "目的地" : dataItem.addr));
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.infrom_icon));
                    builder.setSmallIcon(R.drawable.infrom_icon);
                    builder.setWhen(System.currentTimeMillis());
                    builder.setPriority(Notification.PRIORITY_HIGH);
                    Intent intent = new Intent(this, intentClass);
                    intent.putExtra("id", dataItem.id);
                    intent.putExtra("object_id", dataItem.object_id);
                    intent.putExtra("content", dataItem.content);
                    intent.putExtra("start_time", sdf2.format(dataItem.start_time));
                    intent.putExtra("end_time", sdf2.format(dataItem.end_time));
                    intent.putExtra("addr", dataItem.addr);
                    intent.putExtra("note_time", dataItem.note_time);
                    intent.putExtra("state", dataItem.state);
                    builder.setContentIntent(PendingIntent.getActivity(InformService.this, dataItem.id, intent, PendingIntent.FLAG_ONE_SHOT));

                    //兼容8.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        builder.setChannelId(channelId);
                    }

                    lauchNotification((int) dataItem.start_time.getTime(), builder.build());

                    //添加到列表
                    dataInfomed.add(dataItem);

                    dataItem.state = true;
                }
            }

            if(dataInfomed.size() != 0){

                //播放声音
                soundPool.play(soundId, 1, 1, 1, 0, 1);

                //唤醒屏幕
                PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
                assert pm != null;
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock pw = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
                pw.acquire();
                pw.release();

                //振动
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if(vibrator != null && vibrator.hasVibrator()){
                    vibrator.vibrate(new long[]{500, 500}, -1);
                    Log.v("通知", "振动");
                }

                //回调更新
                if(onInformListener != null){
                    onInformListener.inform(dataInfomed);
                }
            }

        }else {
            Log.v("通知", "没有数据");
        }
    }

    //服务间谍
    public static class InformBinder extends Binder{
        private InformService informService;

        public InformBinder(InformService service){
            informService = service;
        }

        public boolean checkPermission(){
            return informService.checkNotificationPermission();
        }

        public void lauchNotification(Notification notification){
            informService.lauchNotification(0, notification);
        }

        public void setRequestDataListenr(OnRequestDataListener onRequestDataListener){
            informService.onRequestDataListener = onRequestDataListener;
        }

        public void setOnInformListener(OnInformListener onInformListener){
            informService.onInformListener = onInformListener;
        }
        public void setPressIntent(Class intentClass){
            informService.intentClass = intentClass;
        }
    }

    //事件回调接口
    public static interface OnInformListener{
        void inform(List<Data> data);
    }

    //数据请求回调
    public static interface OnRequestDataListener{
        List<Data> requestData();
    }


    //发送一个通知
    public void lauchNotification(int id, Notification notification){
        if(checkNotificationPermission()){
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            manager.notify(id, notification);
        }
    }

    //检查通知权限
    public boolean checkNotificationPermission(){
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if(!manager.areNotificationsEnabled()){
            try{
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", getApplicationInfo().uid);
                startActivity(intent);
            }catch (Exception e){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }
            return false;
        }
        return  true;
    }
}
