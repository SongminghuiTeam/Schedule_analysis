package com.cloudea.calendarmodule;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CalendarView extends LinearLayoutCompat {


    private List<Data> data;                  //数据
    private OnDataClickListener onDataClickListener;
    private OnPageChangeListener onPageChangeListener;
    private ViewPager viewPager;
    private int viewType = 7;                 //默认为周视图
    private int bound = 3;                    //缓存页面数
    private boolean overScrolled = false;     //是否有加载周页面

    //数据结构
    public class Data extends com.cloudea.basemodule.Data{
        public int color;       //颜色
        public Date start;       //显示的开始时间
        public Date end;         //显示的结束时间
    }

    //数据被单击接口
    public static interface OnDataClickListener{
        void run(Data data);
    }

    //页面改变接口
    public static interface OnPageChangeListener{
        void run(Date date);
    }

    //数据视图类型
    public static final int WEEK_VIEW = 7;
    public static final int THREE_DAY_VIEW = 3;
    public static final int DAY_VIEW = 1;


    //构造函数
    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_calendar, this);
        viewPager = findViewById(R.id.viewpager);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resetView();
    }

    public void setData(List<com.cloudea.basemodule.Data> data){
        if(data == null){
            Log.e("日历", "没有绑定数据");
            return;
        }

        //分割数据
        LinkedList<Data> dataDevided = new LinkedList<>();
        for(com.cloudea.basemodule.Data dataItem : data)
        {
            int day = 24 * 3600 * 1000;
            Data d = new Data();
            d.id = dataItem.id;
            d.object_id = dataItem.object_id;
            d.content = dataItem.content;
            d.addr = dataItem.addr;
            d.start_time = dataItem.start_time;
            d.end_time = dataItem.end_time;
            d.note_time = dataItem.note_time;
            d.state = dataItem.state;
            d.start = dataItem.start_time;
            d.end = dataItem.end_time;
            int rand = dataItem.start_time.getHours() + dataItem.end_time.getHours()
                    + dataItem.start_time.getDay() * dataItem.end_time.getDay()
                    + dataItem.start_time.getMinutes() + dataItem.end_time.getMinutes();
            d.color = getRamdomColor(rand);
            while(d.start.getDate() != d.end.getDate()){
                Data temp = new Data();
                temp.id = d.id;
                temp.object_id = d.object_id;
                temp.content = d.content;
                temp.addr = d.addr;
                temp.start_time = d.start_time;
                temp.end_time = d.end_time;
                temp.note_time = d.note_time;
                temp.state = d.state;
                temp.start = d.start;
                temp.end = d.end;
                temp.color = d.color;
                Date time = new Date(d.start.getTime());
                time.setHours(0);
                time.setMinutes(0);
                time.setSeconds(0);
                time = new Date(time.getTime() + 1L * day);
                temp.end = time;
                d.start = time;
                dataDevided.add(temp);
            }
            dataDevided.add(d);
        }
        Log.v("长度", dataDevided.size() + "");
        this.data = dataDevided;
    }


    public List<Data> getData(){
        return this.data;
    }

    //跳到今天
    public void toToday(){
        ViewPagerAdapter vpa = (ViewPagerAdapter) viewPager.getAdapter();
        if(overScrolled){
            resetView();
        }else {
            viewPager.setCurrentItem(bound);
        }
    }

    //得到当前日期
    public Date getCurrentDate(){
        ViewPagerAdapter vpa = (ViewPagerAdapter) viewPager.getAdapter();
        return vpa.currentFirstDate;
    }

    //设置数据单击事件
    public void setOnDataClickListener(OnDataClickListener onDataClickListener){
        this.onDataClickListener = onDataClickListener;
    }

    //设置页面改变事件
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    //设置视图类型
    public void setViewType(int type){
        if(type == WEEK_VIEW || type == DAY_VIEW || type == THREE_DAY_VIEW){
            this.viewType = type;
        }
    }

    //设置显示的范围
    public void setViewRange(int bound){
        if(bound >= 1 && bound <= 100){
            this.bound = bound;
        }
    }

    //刷新数据显示
    public void notifyChanged(){
        resetView();
    }

    //得到随机颜色
    private int getRamdomColor(int rand){
        final int[] colors = {
            getResources().getColor(R.color.ram1), getResources().getColor(R.color.ram2),
                getResources().getColor(R.color.ram3), getResources().getColor(R.color.ram4),
                getResources().getColor(R.color.ram5), getResources().getColor(R.color.ram6),
                getResources().getColor(R.color.ram7), getResources().getColor(R.color.ram8),
                getResources().getColor(R.color.ram9), getResources().getColor(R.color.ram10),
                getResources().getColor(R.color.ram11), getResources().getColor(R.color.ram12),
                getResources().getColor(R.color.ram13), getResources().getColor(R.color.ram14),
                getResources().getColor(R.color.ram15), getResources().getColor(R.color.ram16)
        };

        return colors[(rand % (colors.length))];
    };


    private View contructPage(){
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        View view = inflater.inflate(R.layout.item_viewpager, null);
        return view;
    }

    private ViewPagerAdapter initView(){
        //设置ViewPager的适配器
        final ViewPagerAdapter vpa = new ViewPagerAdapter();

        //构造页面
        if(vpa.views.size() != 2 *  bound + 1){
            for(int i = 0; i <= 2 * bound ; i++){
                vpa.views.add(contructPage());
            }
        }

        //无限循环
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPager.setAdapter(vpa);
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public int positoin;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                this.positoin = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE){
                    int time = viewType * 24 * 3600 * 1000;

                    //重新设置WeekView
                    //找到所有WeekView
                    LinkedList<WeekView> weekViews = new LinkedList<>();
                    for(View view : vpa.views){
                        weekViews.add((WeekView) view.findViewById(R.id.week_view));
                    }
                    //找到当前页面时间
                    vpa.currentFirstDate = weekViews.get(positoin).getDate();

                    //移动页面
                    if(viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == 2 * bound){
                        for(int i = 0; i <= 2 * bound; i++){
                            Date date = new Date(vpa.currentFirstDate.getTime() + time * ( - bound + i) );
                            weekViews.get(i).setDate(date);
                            weekViews.get(i).updateView();
                        }
                        overScrolled =true;
                        viewPager.setCurrentItem(bound, false);
                    }

                    if(onPageChangeListener != null){
                        onPageChangeListener.run(vpa.currentFirstDate);
                    }
                }
            }
        });

        return vpa;
    }

    //刷新显示：只对数据、今天、显示个数、监听器有效果
    private void resetView() {
        new Thread() {
            @Override
            public void run() {

                //Date t41 = new Date();
                ViewPagerAdapter vpa = (ViewPagerAdapter) viewPager.getAdapter();
                if(vpa == null){
                    vpa = initView();
                }
                overScrolled = false;


                //Date t42 = new Date();
                //找出页面第一个日期
                Calendar cal = Calendar.getInstance();
                if(viewType == WEEK_VIEW){
                    while (cal.get(Calendar.DAY_OF_WEEK) != 1 ){ // 星期日是1，星期一是2....
                        cal.add(Calendar.DAY_OF_YEAR, -1);
                    }
                }

                //Date t43 = new Date();

                //把时间拨回到若干天之前
                cal.add(Calendar.DAY_OF_YEAR,  - viewType * bound);
                for(int i = 0; i <= 2 * bound; i++){
                    View view = vpa.views.get(i);
                    final WeekView weekView = view.findViewById(R.id.week_view);
                    weekView.setMaxDays(viewType);
                    weekView.setDate(cal.getTime());
                    weekView.setData(data);
                    weekView.setOnDataClickListener(onDataClickListener);
                    ((Activity)getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weekView.updateView();
                        }
                    });
                    cal.add(Calendar.DAY_OF_YEAR, viewType);
                }

                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(bound);
                    }
                });

                //Date t44 = new Date();
                //Log.v("时间41", t42.getTime() - t41.getTime() + "ms");
                //Log.v("时间42", t43.getTime() - t42.getTime() + "ms");
                //Log.v("时间43", t44.getTime() - t43.getTime() + "ms");
            }
        }.start();
    }
}

//ViewPager 适配器
class ViewPagerAdapter extends PagerAdapter {
    LinkedList<View> views = new LinkedList<>();
    Date currentFirstDate = new Date();                  //当前查看的第一个日期
    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {

        final View view = views.get(position);
        container.post(new Runnable() {
            @Override
            public void run() {
                container.addView(view);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(@NonNull final ViewGroup container, final int position, @NonNull Object object) {
        container.post(new Runnable() {
            @Override
            public void run() {
                container.removeView(views.get(position));
            }
        });
    }
}

//数据对日期过滤器
class DateFilter{
    List<CalendarView.Data> data;

    public DateFilter(List<CalendarView.Data> data){
        this.data = data;
    }

    public List<CalendarView.Data> doFilter(Date date){
        LinkedList<CalendarView.Data> filtedData = new LinkedList<>();
        for(CalendarView.Data dataItem : data){
            if(dataItem.start.getYear() == date.getYear() &&
                    dataItem.start.getMonth() == date.getMonth() &&
                    dataItem.start.getDate() == date.getDate()){
                filtedData.add(dataItem);
            }
        }
        return  filtedData;
    }
}