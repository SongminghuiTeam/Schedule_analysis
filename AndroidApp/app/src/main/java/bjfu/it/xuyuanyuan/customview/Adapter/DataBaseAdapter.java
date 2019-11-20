package bjfu.it.xuyuanyuan.customview.Adapter;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.cloudea.basemodule.BaseApplication;
import com.cloudea.basemodule.Data;
import com.cloudea.databasemodule.DataBaseUtils;
import com.cloudea.databasemodule.Event;
import com.cloudea.databasemodule.Message;
import com.cloudea.databasemodule.User;
import com.cloudea.localstoragemodule.LocalStorageUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import bjfu.it.xuyuanyuan.customview.BaseActivity;
import bjfu.it.xuyuanyuan.customview.Filter.TaskFilter;
import cn.bmob.v3.BmobObject;

import static android.util.Log.v;

//适配器模式
//代理模式
public class DataBaseAdapter extends Observable {
    private static DataBaseAdapter instance = null;
    public static synchronized DataBaseAdapter getInstance(  ){
        if(instance == null){
            instance = new DataBaseAdapter();
        }
        return instance;
    }

    private List<Data> data = null;
    private LocalStorageUtils localStorageUtils = null;
    private DataBaseUtils dataBaseUtils = null;

    //构造函数
    private DataBaseAdapter(){
        localStorageUtils = LocalStorageUtils.getInstance(BaseApplication.getContext());
        dataBaseUtils = DataBaseUtils.getInstance();
    }

    //获得数据
    public List<Data> queryEvents(){
        if(data == null){
            data = LocalStorageUtils.getInstance(BaseApplication.getContext()).queryEvents();
        }
        return data;
    }

    //更新数据并通知观察们
    public void refresh(){
        //Date t1 = new Date();
        data = LocalStorageUtils.getInstance(BaseApplication.getContext()).queryEvents();
        //Date t2 = new Date();
        setChanged();
        //Date t3 = new Date();
        notifyObservers();
        //Date t4 = new Date();
        //Log.v("观察者变化时间", t4.getTime() - t3.getTime() + "ms");
    }

    //同步数据
    public void SyncEvents(){
        final User user = dataBaseUtils.getSignedUser();
        if(user != null){
            dataBaseUtils.queryEvents(user, new DataBaseUtils.OnRequestCompleted<List<Event>>() {
                @Override
                public void onSucceed(List<Event> data) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    localStorageUtils.clearAllEvents();
                    for(Event event : data){
                        Data dataItem = new Data();
                        dataItem.object_id = event.getObjectId();
                        dataItem.content = event.content;
                        dataItem.addr = event.addr;
                        dataItem.note_time = event.note_time;
                        dataItem.state = event.state;
                        try {
                            dataItem.start_time = sdf.parse(event.start_time.getDate());
                            dataItem.end_time = sdf.parse(event.end_time.getDate());
                        } catch (ParseException e) {
                            continue;
                        }
                        localStorageUtils.addEvent(dataItem);
                    }
                    refresh();
                }

                @Override
                public void onError(Exception e) {
                    Log.e("获取数据", "失败：" + e.getMessage());
                    toast("获取数据发生错误");
                }
            });
        }else{
            toast("你没有登录");
        }
    }

    //插入数据

    /**
     *
     * @param data 事件列表
     * @param message  事件原文
     */
    public void addEvents(final List<Data> data, String message, String result, final OnRequestCompleted onRequestCompleted, final boolean refresh){
        final User user = dataBaseUtils.getSignedUser();
        if(user != null){
            dataBaseUtils.addMessage(user, message, result, new DataBaseUtils.OnRequestCompleted<Message>() {
                @Override
                public void onSucceed(Message msg) {
                    //数据转换
                    final LinkedList<Event> events = new LinkedList<>();
                    for (Data dataItem : data){
                        events.add(dataBaseUtils.createEvent(user, msg, dataItem.content, dataItem.addr, dataItem.start_time, dataItem.end_time, dataItem.note_time, dataItem.state));
                    }
                    dataBaseUtils.addEvents(user, msg, events, new DataBaseUtils.OnRequestCompleted<List<String>>() {
                        @Override
                        public void onSucceed(List<String> obj_ids) {
                            for (int i = 0; i < data.size(); i++){
                                final Data dataItem = data.get(i);
                                dataItem.object_id = obj_ids.get(i);
                                localStorageUtils.addEvent(dataItem);
                            }

                            if(refresh){
                                refresh();
                            }

                            onRequestCompleted.finish();
                            toast("添加成功");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("添加数据", "失败：" + e.getMessage());
                            onRequestCompleted.finish();
                            toast("添加失败");
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.e("添加数据", "失败：" + e.getMessage());
                    onRequestCompleted.finish();
                    toast("添加失败");
                }
            });
        }else {
            onRequestCompleted.finish();
            toast("你没有登录");
        }
    }

    //删除一条事件
    public void removeEvent(final Data dataItem){
        final User user = dataBaseUtils.getSignedUser();
        if(user != null){
            Event event = new Event();
            event.setObjectId(dataItem.object_id);
            dataBaseUtils.deleteEvent(event, new DataBaseUtils.OnRequestCompleted() {
                @Override
                public void onSucceed(Object obj) {
                    localStorageUtils.removeEvent(dataItem.id);
                    refresh();
                    Log.v("删除数据", "成功");
                }

                @Override
                public void onError(Exception e) {
                    Log.e("删除数据", "失败：" + e.getMessage());
                    toast("删除失败");
                }
            });
        }else {
            toast("你没有登录");
        }
    }


    //根据过滤器来删除多条事件
    public void removeEvents(TaskFilter taskFilter,  final OnRequestCompleted onRequestCompleted){
        final User user = dataBaseUtils.getSignedUser();
        if(data != null && user != null){
            List<BmobObject> events = new LinkedList<>();
            List<Data> dataFilted = taskFilter == null ? data : taskFilter.doFilter(data);
            for(Data dataItem : dataFilted){
                Event event = new Event();
                event.setObjectId(dataItem.object_id);
                events.add(event);
            }

            dataBaseUtils.deleteEvents(events, new DataBaseUtils.OnRequestCompleted<Integer>() {
                @Override
                public void onSucceed(Integer data) {
                    toast(data + "个成功");
                    SyncEvents();
                    onRequestCompleted.finish();
                }

                @Override
                public void onError(Exception e) {
                    toast("删除失败");
                    onRequestCompleted.finish();
                }
            });
        }else{
            toast("你没有登录");
            onRequestCompleted.finish();
        }
    }

    /*更新事件*/
    public void updateEvent(final Data dataItem, final boolean refresh){
        final User user = dataBaseUtils.getSignedUser();
        if(user != null){
            Event event = dataBaseUtils.createEvent(null, null, dataItem.content, dataItem.addr, dataItem.start_time, dataItem.end_time, dataItem.note_time, dataItem.state);
            event.setObjectId(dataItem.object_id);
            dataBaseUtils.updateEvent(event, new DataBaseUtils.OnRequestCompleted() {
                @Override
                public void onSucceed(Object data) {
                    localStorageUtils.updateEvent(dataItem);
                    if(refresh){
                        refresh();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("更新数据", "失败：" + e.getMessage());
                    toast("更新失败");
                }
            });
        }else{
            toast("你没有登录");
        }
    }

    public void updateEvents(final List<Data> data, final OnRequestCompleted onRequestCompleted, final boolean refresh){
        final User user = dataBaseUtils.getSignedUser();
        if(user != null){
            List<BmobObject> objects = new LinkedList<>();
            for(Data dataItem : data){
                Event obj = new Event();
                obj.state = dataItem.state;
                obj.setObjectId(dataItem.object_id);
                objects.add(obj);
            }
            dataBaseUtils.updateEvents(objects, new DataBaseUtils.OnRequestCompleted<Integer>() {
                @Override
                public void onSucceed(Integer count) {
                    for(Data dataItem : data){
                        localStorageUtils.updateEvent(dataItem);
                    }

                    if(refresh){
                        refresh();
                    }

                    onRequestCompleted.finish();
                }

                @Override
                public void onError(Exception e) {
                    Log.e("更新数据", "失败：" + e.getMessage());
                    toast("更新失败");
                    onRequestCompleted.finish();
                }
            });
        }else{
            toast("你没有登录");
            onRequestCompleted.finish();
        }
    }

    private void toast(final String msg){
        BaseActivity.getContext().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public interface OnRequestCompleted{
        void finish();
    }
}
