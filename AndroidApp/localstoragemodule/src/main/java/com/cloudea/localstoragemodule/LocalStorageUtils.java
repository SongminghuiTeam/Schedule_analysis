package com.cloudea.localstoragemodule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.cloudea.basemodule.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class LocalStorageUtils {
    private static LocalStorageUtils ourInstance = null;

    public static synchronized LocalStorageUtils getInstance(Context context)
    {
        if(ourInstance == null){
            ourInstance = new LocalStorageUtils(context);
        }
        return ourInstance;
    }


    private LocalSqlLiteHelper helper;

    private LocalStorageUtils(Context context) {
        this.helper = new LocalSqlLiteHelper(context, "localstorage.db", null, 1);
    }

    //增加一个事件
    public void addEvent(Data data){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db =  helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("object_id", data.object_id);
        values.put("content", data.content);
        values.put("addr", data.addr);
        values.put("start_time", sdf.format(data.start_time));
        values.put("end_time", sdf.format(data.end_time));
        values.put("note_time", data.note_time);
        values.put("state", data.state);
        db.insert("event", null, values);
        //查找id并返回
        Cursor cursor = db.rawQuery("select last_insert_rowid() from event",null);
        if(cursor.moveToFirst()){
            data.id = cursor.getInt(0);
            Log.v("获得的ID", data.id + "");
        }
    }

    //删除一个事件
    public void removeEvent(int id){
        SQLiteDatabase db =  helper.getWritableDatabase();
        db.delete("event", "id = ?", new String[]{id + ""});
    }

    //更新一个事件
    public void updateEvent(Data data){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db =  helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("object_id", data.object_id);
        values.put("content", data.content);
        values.put("addr", data.addr);
        values.put("start_time", sdf.format(data.start_time));
        values.put("end_time", sdf.format(data.end_time));
        values.put("note_time", data.note_time);
        values.put("state", data.state);
        db.update("event", values, "id = ?", new String[]{data.id + ""});
    }

    //查询所有事件
    public List<Data> queryEvents(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db =  helper.getWritableDatabase();
        LinkedList<Data> data = new LinkedList<>();
        Cursor cursor = db.query("event", null, null, null, null, null, null);
        while (cursor.moveToNext()){
            Data dataItem = new Data();
            dataItem.id = cursor.getInt(cursor.getColumnIndex("id"));
            dataItem.object_id = cursor.getString(cursor.getColumnIndex("object_id"));
            dataItem.content = cursor.getString(cursor.getColumnIndex("content"));
            dataItem.addr = cursor.getString(cursor.getColumnIndex("addr"));
            dataItem.note_time = cursor.getInt(cursor.getColumnIndex("note_time"));
            try {
                dataItem.start_time = sdf.parse(cursor.getString(cursor.getColumnIndex("start_time")));
                dataItem.end_time = sdf.parse(cursor.getString(cursor.getColumnIndex("end_time")));
                dataItem.state = cursor.getInt(cursor.getColumnIndex("state")) == 0 ? false : true;
            } catch (ParseException e) {
                continue;
            }
            data.add(dataItem);
        }
        return data;
    }

    //清空事件
    public void clearAllEvents(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("event", null, null);
    }

}
