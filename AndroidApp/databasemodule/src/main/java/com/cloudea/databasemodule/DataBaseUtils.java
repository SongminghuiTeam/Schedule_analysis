package com.cloudea.databasemodule;

import android.util.Log;

import com.cloudea.basemodule.Data;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class DataBaseUtils {
    private static DataBaseUtils instance;

    private DataBaseUtils(){ }

    public static synchronized DataBaseUtils getInstance() {
        if(instance == null){
            instance = new DataBaseUtils();
        }
        return instance;
    }

    //发送验证码
    public void sendSMS(String phoneString, final OnRequestCompleted onRequestCompleted){
        BmobSMS.requestSMSCode(phoneString, "base", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if(onRequestCompleted != null){
                    if(e == null){
                        onRequestCompleted.onSucceed(null);
                    }else{
                        onRequestCompleted.onError(e);
                    }
                }
            }
        });
    }


    //注册
    public void signUp(String mobilePhoneString, String name, String password, String securityCode, final OnRequestCompleted onRequestCompleted){
        User user = new User();
        user.setMobilePhoneNumber(mobilePhoneString);
        user.setUsername(name);
        user.name = name;
        user.setPassword(password);
        user.signOrLogin(securityCode, new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(onRequestCompleted != null){
                    if(e == null){
                        onRequestCompleted.onSucceed(null);
                    }else{
                        onRequestCompleted.onError(e);
                    }
                }
            }
        });
    }

    //登录
    public void signIn(String mobilePhoneString, String password, final OnRequestCompleted onRequestCompleted){
        User user = new User();
        User.loginByAccount(mobilePhoneString, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(onRequestCompleted != null){
                    if(e == null){
                        onRequestCompleted.onSucceed(null);
                    }else{
                        onRequestCompleted.onError(e);
                    }
                }
            }
        });
    }


    //得到登录状态
    public User getSignedUser(){
        //如果没有登录，则返回null
        if(User.isLogin()){
            return User.getCurrentUser(User.class);
        }else{
            return null;
        }
    }

    //登出
    public void signOut(){
        User.logOut();
    }

    //更新当前用户密码
    public void changePassword(String oldPassword, String newPassword, final OnRequestCompleted onRequestCompleted){
        User.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(onRequestCompleted != null){
                    if(e == null){
                        onRequestCompleted.onSucceed(null);
                    }else{
                        onRequestCompleted.onError(e);
                    }
                }
            }
        });
    }

    /*更改昵称*/
    public void changeNickname(String nickname, final OnRequestCompleted onRequestCompleted){
        User user = User.getCurrentUser(User.class);
        if(user != null){
            User temp = new User();
            temp.setObjectId(user.getObjectId());
            temp.setUsername(nickname);
            user.setUsername(nickname);
            temp.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(onRequestCompleted != null){
                        if(e == null){
                            onRequestCompleted.onSucceed(null);
                        }else{
                            onRequestCompleted.onError(e);
                        }
                    }
                }
            });
        }else{
            if(onRequestCompleted != null){
                onRequestCompleted.onError(new Exception("没有登录"));
            }
        }
    }

    //添加消息及解析结果
    public void addMessage(User u_id, String message, String result, final OnRequestCompleted<Message> onRequestCompleted){
        final Message msg = new Message();
        msg.u_id = u_id;
        msg.message = message;
        msg.result = result;
        msg.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                //s 为返回的id
                if(onRequestCompleted != null){
                    if(e == null){
                        msg.setObjectId(s);
                        onRequestCompleted.onSucceed(msg);
                    }else{
                        onRequestCompleted.onError(e);
                    }
                }
            }
        });
    }

    //增加多条事件记录
    public void addEvents(User u_id, Message m_id, List<Event> events, final OnRequestCompleted<List<String>> onRequestCompleted){
        if(events == null || events.size() == 0){
            Log.e("数据库工具", "传入的数据不能为空");
            return;
        }
        LinkedList<BmobObject> events_send = new LinkedList<>();
        for(Event event : events){
            events_send.add(event);
        }
        //批量上传
        new BmobBatch().insertBatch(events_send).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if(onRequestCompleted != null){
                    if(e == null){
                        LinkedList<String> object_ids = new LinkedList<>();
                        for(BatchResult batchResult : list){
                            if(batchResult.isSuccess()){
                                object_ids.add(batchResult.getObjectId());
                            }else{
                                onRequestCompleted.onError(new Exception("部分数据上传失败"));
                                break;
                            }
                        }

                        onRequestCompleted.onSucceed(object_ids);
                    }else{
                        onRequestCompleted.onError(e);
                    }
                }
            }
        });
    }

    //查询某个用户的所有事件记录
    //TODO:等待改善
    public void queryEvents(User u_id, final OnRequestCompleted<List<Event>> onRequestCompleted){
        BmobQuery<Event> query = new BmobQuery<>();
        //默认是500条，这里设成100条
        query.addWhereEqualTo("user_id", u_id).setLimit(100).findObjects(new FindListener<Event>() {
            @Override
            public void done(List<Event> list, BmobException e) {
                if(onRequestCompleted != null){
                    if(e == null){
                        onRequestCompleted.onSucceed(list);
                    }else{
                        onRequestCompleted.onError(e);
                    }
                }
            }
        });
    }

    //删除一条事件记录
    public void deleteEvent(Event event, final OnRequestCompleted onRequestCompleted){
        event.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(onRequestCompleted != null){
                    if(e == null){
                        onRequestCompleted.onSucceed(null);
                    }else{
                        onRequestCompleted.onError(e);
                    }
                }
            }
        });
    }

    //删除多条记录
    public void deleteEvents(List<BmobObject> events, final OnRequestCompleted<Integer> onRequestCompleted){
        new BmobBatch().deleteBatch(events).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if(onRequestCompleted != null){
                    if(e == null){
                        int sucss = 0;
                        for(BatchResult ret : list){
                            if(ret.isSuccess()){
                                sucss ++;
                            }
                        }
                        onRequestCompleted.onSucceed(sucss);
                    }else{
                        onRequestCompleted.onError(e);
                    }
                }
            }
        });
    }

    //修改一条事件记录
    //并不会改变所属的Message和User
    public void updateEvent(Event event, final  OnRequestCompleted onRequestCompleted){
        Event e = new Event();
        e.setObjectId(event.getObjectId());
        e.content = event.content;
        e.addr = event.addr;
        e.start_time = event.start_time;
        e.end_time = event.end_time;
        e.note_time = event.note_time;
        e.state = event.state;
        e.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    onRequestCompleted.onSucceed(null);
                }else{
                    onRequestCompleted.onError(e);
                }
            }
        });
    }

    //批量更新事件
    public void updateEvents(List<BmobObject> events, final OnRequestCompleted<Integer> onRequestCompleted){
        new BmobBatch().updateBatch(events).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if(e == null){
                    int sucss = 0;
                    for(BatchResult ret : list){
                        if(ret.isSuccess()){
                            sucss ++;
                        }
                    }
                    onRequestCompleted.onSucceed(sucss);
                }else{
                    onRequestCompleted.onError(e);
                }
            }
        });
    }


    //创造一条Event
    public Event createEvent(User user_id, Message message_id, String content, String addr, Date start_time, Date end_time, int note_time, boolean state){
        Event event = new Event();
        event.user_id = user_id;
        event.message_id = message_id;
        event.content = content;
        event.addr = addr;
        event.start_time = new BmobDate(start_time);
        event.end_time = new BmobDate(end_time);
        event.note_time = note_time;
        event.state = state;
        return event;
    }


    //异步回调接口
    public static interface OnRequestCompleted<T>{
        void onSucceed(T data);
        void onError(Exception e);
    }

}
