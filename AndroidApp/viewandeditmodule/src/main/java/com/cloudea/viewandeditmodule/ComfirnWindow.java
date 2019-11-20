package com.cloudea.viewandeditmodule;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudea.basemodule.Data;
import com.cloudea.viewandeditmodule.util.ComfirmWindowRecyclerViewAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ComfirnWindow extends PopupWindow {

    private Context context;
    private List<RowData> rowData;
    private RecyclerView recyclerView;

    public ComfirnWindow(Context context, int width, int height) {
        this.context = context;

        //设置popupwindow
        View view = LayoutInflater.from(context).inflate(R.layout.viewandedit_activity_main, null);
        setContentView(view);
        setWidth(width);
        setHeight(height);
        setFocusable(true);

        //设置layout
        View cacelButton = view.findViewById(R.id.cacel_button);
        cacelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComfirnWindow.this.dismiss();
            }
        });

        //设置recyclerview
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
    }

    public void show(){
        this.showAtLocation(((Activity)context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }


    public boolean setRowData(List<String> messages, List<String> results){
        this.rowData = new LinkedList<>();
        JsonParser jp = new JsonParser();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //解析原数据
        for(int i = 0; i < messages.size(); i++){
            RowData r = new RowData();
            try {
                JsonObject jsonObject = jp.parse(results.get(i)).getAsJsonObject();
                r.status = jsonObject.get("status").getAsString().equals("true") ? true : false;
                if(r.status != true){
                    continue;
                }
                r.count =  Integer.parseInt(jsonObject.get("count").getAsString());
                r.start =  (jsonObject.get("start").getAsString());
                r.end = (jsonObject.get("end").getAsString());
                r.summary = jsonObject.get("summary").getAsString();
                r.count = Integer.parseInt(jsonObject.get("count").getAsString());
                r.destination = new LinkedList<>();
                r.repeat_day = new LinkedList<>();
                r.repeat_month = new LinkedList<>();
                r.repeat_week = new LinkedList<>();
                JsonArray destinationArray = jsonObject.get("destination").getAsJsonArray();
                JsonArray monthArray = jsonObject.get("repeat_month").getAsJsonArray();
                JsonArray weekArray = jsonObject.get("repeat_week").getAsJsonArray();
                JsonArray dayArray = jsonObject.get("repeat_day").getAsJsonArray();
                for(int j = 0; j < destinationArray.size(); j++){
                    r.destination.add(destinationArray.get(j).getAsString());
                }
                for(int j = 0; j < monthArray.size(); j++){
                    r.repeat_month.add( Integer.parseInt(monthArray.get(j).getAsString()));
                }
                for(int j = 0; j < weekArray.size(); j++){
                    r.repeat_week.add( Integer.parseInt(weekArray.get(j).getAsString()));
                }
                for(int j = 0; j < dayArray.size(); j++){
                    r.repeat_day.add( Integer.parseInt(dayArray.get(j).getAsString()));
                }

                if(r.destination.size() > 0){
                    r.destinationSelected = r.destination.get(0);
                }else{
                    r.destinationSelected = "";
                }

            } catch (Exception e) {
                Log.e("确定弹窗", "数据格式有误");
                rowData = null;
                continue;
            }
            r.message = messages.get(i);
            r.result = results.get(i);
            rowData.add(r);

            //分析成具体日期
            r.__dates = new LinkedList<>();
            try {
                if(r.count == 1){
                    r.__dates.add(sdf2.parse(r.start));
                }else {
                    Calendar calendar = Calendar.getInstance();
                    int toYear = 1900 +  new Date().getYear();
                    for(int j = 0;  r.__dates.size() < r.count; j ++){
                        calendar.add(Calendar.DAY_OF_YEAR, j == 0 ? 0 : 1);
                        int year = calendar.get(Calendar.YEAR);
                        //如果超过3年
                        if(year - toYear >= 3){
                            break;
                        }

                        boolean matched = r.repeat_month.indexOf(Integer.valueOf(calendar.get(Calendar.MONTH )+ 1)) != -1 &&
                                r.repeat_week.indexOf(Integer.valueOf(calendar.get(Calendar.DAY_OF_WEEK))) != -1 &&
                                r.repeat_day.indexOf(Integer.valueOf(calendar.get(Calendar.DATE))) != -1;
                        if(matched){
                            Date date = calendar.getTime();
                            r.__dates.add(date);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("确认弹窗", "分析数据错误");
                e.printStackTrace();
                rowData = null;
                return false;
            }
        }

        //显示到recylerview上
        recyclerView.setAdapter(new ComfirmWindowRecyclerViewAdapter(rowData));
        if(rowData != null && rowData.size() > 0){
            return true;
        }
        return false;
    }

    private List<CleanData> getCleanData(){
        if(this.rowData == null || this.rowData.size() == 0){
            Log.e("确认弹窗", "没有设置原始数据");
            return null;
        }

        //得到数据库可用格式
        LinkedList<CleanData> cleanData = new LinkedList<>();
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for(RowData rowDataItem : rowData){
            CleanData cleanDataItem = new CleanData();
            cleanDataItem.message = rowDataItem.message;
            cleanDataItem.result = rowDataItem.result;
            cleanDataItem.events = new LinkedList<>();
            for(Date date : rowDataItem.__dates){
                Data data = new Data();
                Date startTime = null;
                Date endTime = null;
                try {
                    if(rowDataItem.count == 1){
                        startTime = sdf2.parse(rowDataItem.start);
                        endTime = sdf2.parse(rowDataItem.end);
                    }else {
                        startTime = sdf1.parse(rowDataItem.start);
                        endTime = sdf1.parse(rowDataItem.end);
                        startTime.setYear(date.getYear());
                        startTime.setMonth(date.getMonth());
                        startTime.setDate(date.getDate());
                        endTime.setYear(date.getYear());
                        endTime.setMonth(date.getMonth());
                        endTime.setDate(date.getDate());
                    }
                } catch (ParseException e) {
                    Log.e("确认弹窗", "分析数据错误");
                    continue;
                }
                data.start_time = startTime;
                data.end_time = endTime;
                data.state = false;
                data.note_time = 30;
                data.addr = rowDataItem.destinationSelected;
                data.content = rowDataItem.summary;
                cleanDataItem.events.add(data);
            }
            cleanData.add(cleanDataItem);
        }

        return cleanData;
    }

    //按确认按钮事件
    public void setOnComfirmListener(final OnComfirmListener onComfirmListener){
        View comfirmButton = getContentView().findViewById(R.id.confirm_button);
        comfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onComfirmListener != null){
                    onComfirmListener.comfirm(getCleanData());
                }
                ComfirnWindow.this.dismiss();
            }
        });
    }

    public static interface  OnComfirmListener{
        void comfirm(List<CleanData> cleanData);
    }


    public static class RowData{
        public boolean status;
        public String summary;
        public String start;
        public String end;
        public List<String> destination;
        public List<Integer> repeat_month;
        public List<Integer> repeat_week;
        public List<Integer> repeat_day;
        public int count;
        //动态生成,为获取cleanData准备的字段
        public String destinationSelected;
        public List<Date> __dates;
        public String message;
        public String result;
    }

    public static class CleanData{
        public String message;
        public String result;
        public List<Data> events;
    }
}
