package com.cloudea.calendarmodule;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.qmuiteam.qmui.widget.QMUILoadingView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class WeekView extends LinearLayoutCompat {

    private List<CalendarView.Data> data;                                              //存储的数据
    private CalendarView.OnDataClickListener onDataClickListener;

    private List<View> dataViews = new LinkedList<>();                             //动态添加的view
    private List<View> dateViews = new LinkedList<>();
    private List<View> spaceViews = new LinkedList<>();
    private int maxDays = 7;
    private ViewGroup dataContainer;
    private ViewGroup dateContainer;
    private ViewGroup spaceContainer;
    private Date date = new Date();                                                //第一天从什么时候开始
    private ScrollView scrollView;

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater =  LayoutInflater.from(context);
        inflater.inflate(R.layout.view_week, this);
        dataContainer = findViewById(R.id.data_container);
        dateContainer = findViewById(R.id.date_container);
        spaceContainer = findViewById(R.id.space_container);
        scrollView = findViewById(R.id.scroll);
    }

    //清除数据显示
    public void clearData(){
        for(View view : dataViews){
            dataContainer.removeAllViews();
        }
        dataViews.clear();
    }

    //更新日历结构
    public void updateView(){
        //清空View
        for(View view : dateViews){
            dateContainer.removeView(view);
        }
        for(View view : spaceViews){
            spaceContainer.removeView(view);
        }
        dateViews.clear();
        spaceViews.clear();
        clearData();
        //回到原来的位置
        scrollView.scrollTo(0,0);

        //添加View
        LayoutInflater inflater =  LayoutInflater.from(getContext());
        final int dayStamp = 24 * 3600 * 1000;
        final DateFilter dateFilter = new DateFilter(data);
        final Date today = new Date();
        for(int i = 0; i < maxDays; i++){
            View dateView =inflater.inflate(R.layout.item_date, null);
            final View spaceView =inflater.inflate(R.layout.item_space, null);
            dateContainer.addView(dateView);
            spaceContainer.addView(spaceView);
            dateViews.add(dateView);
            spaceViews.add(spaceView);
            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) dateView.getLayoutParams();
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) spaceView.getLayoutParams();
            params1.weight = 1;
            params2.weight = 1;

            //设置每个日期的显示
            TextView dayView = dateView.findViewById(R.id.day);
            TextView weekView = dateView.findViewById(R.id.week);
            Date current = new Date(date.getTime() + i * dayStamp);
            dayView.setText(current.getDate() + "");
            String weekString = "";
            switch (current.getDay()){
                case 0 : weekString = "周日";break;
                case 1 : weekString = "周一";break;
                case 2 : weekString = "周二";break;
                case 3 : weekString = "周三";break;
                case 4 : weekString = "周四";break;
                case 5 : weekString = "周五";break;
                case 6 : weekString = "周六";break;
            }
            weekView.setText(weekString);
            if(!weekString.equals("周日") && !weekString.equals("周六")){
                weekView.setTextColor(getResources().getColor(R.color.weekday));
            }else{
                weekView.setTextColor(getResources().getColor(R.color.weekend));
            }

            boolean isToday = current.getYear() == today.getYear()
                    && current.getMonth() == today.getMonth()
                    && current.getDate() == today.getDate();
            if(isToday){
                dateView.setBackgroundColor(0xFFDEDEDE);
                spaceView.setBackgroundColor(0xFFEFEFEF);
            }else{
                dateView.setBackgroundColor(0);
                spaceView.setBackgroundColor(0);
            }
        }

        //绘制事件

        View demoView = findViewById(R.id.demo_view);
        View demoBorder = findViewById(R.id.demo_border);
        demoView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        demoBorder.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        for(int i = 0; i < maxDays; i++) {
            Date current = new Date(date.getTime() + i * dayStamp);//获得过滤后的数据,
            if(data != null && data.size() != 0){
                final List<CalendarView.Data> dataFilted = dateFilter.doFilter(current);
                for(CalendarView.Data dataItem : dataFilted){
                    locateData(demoView.getMeasuredWidth(),  demoView.getMeasuredHeight(), demoBorder.getMeasuredHeight(), i, dataItem);
                }
            }
        }

        invalidate();
    }



    //定位数据块
    private void locateData(int w, int h, int b, int offset, final CalendarView.Data dataItem){
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.item_data, null);
        FrameLayout frameLayout = (FrameLayout) dataContainer;
        frameLayout.addView(view);
        dataViews.add(view);

        //改变颜色和文字
        TextView contentView = view.findViewById(R.id.content);
        View contentCard = view.findViewById(R.id.content_card);
        GradientDrawable drawable = (GradientDrawable) contentCard.getBackground();
        drawable.setColor(dataItem.color);
        drawable.setAlpha(128);
        contentView.setText(dataItem.content);


        //确认大小和位置
        Activity activity = (Activity) getContext();
        w =  dip2px(this.getContext(), 50);
        h = dip2px(this.getContext(), 50);
        b = dip2px(this.getContext(), 0.5f);
        int screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        int widthPerSpace = (screenWidth - w) / maxDays;
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        float topPercent = (dataItem.start.getHours() * 60 +  dataItem.start.getMinutes()) * 1.0f / ( 24 * 60);
        float heightPercent = (dataItem.end.getTime() - dataItem.start.getTime()) * 1.0f / 60000 / (24 * 60);
        params.height = h + b;
        params.width = widthPerSpace + b;
        params.leftMargin = w + (screenWidth - w) * offset / maxDays;
        params.topMargin = (int) (topPercent * params.height * 24);
        params.height = (int) (heightPercent * params.height * 24);

        //Log.v("宽度", params.width + "");
        //Log.v("高度", params.height + "");
        //Log.v("左边", params.leftMargin + "");
        //Log.v("顶边", params.topMargin + "");
        //绑定事件
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onDataClickListener != null){
                    onDataClickListener.run(dataItem);
                }
            }
        });
    }

    public void setMaxDays(int maxDays) {
        this.maxDays = maxDays;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setOnDataClickListener(CalendarView.OnDataClickListener onDataClickListener) {
        this.onDataClickListener = onDataClickListener;
    }

    public void setData(List<CalendarView.Data> data) {
        this.data = data;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
