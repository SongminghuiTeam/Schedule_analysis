package bjfu.it.xuyuanyuan.customview.Fragment;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloudea.basemodule.Data;
import com.cloudea.calendarmodule.CalendarView;
import com.cloudea.databasemodule.DataBaseUtils;
import com.cloudea.databasemodule.User;
import com.google.android.material.card.MaterialCardView;
import com.haibin.calendarview.Calendar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.zip.Inflater;

import bjfu.it.xuyuanyuan.customview.Adapter.DataBaseAdapter;
import bjfu.it.xuyuanyuan.customview.EditActivity;
import bjfu.it.xuyuanyuan.customview.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanningFragment extends Fragment  implements Observer {

    private LinkedList<TextView> tabs = new LinkedList<>();
    private List<Data> data;                                    //数据
    private MaterialCalendarView calendarView1;                 //第一个日历
    private CalendarView calendarView2;                         //第二个日历
    private ViewGroup calContaner1;
    private ViewGroup calContaner2;
    private boolean isCaalendarOpened = true;                   //日历的显示状态
    private RecyclerView recyclerView;

    private ViewGroup drawerTool;
    private TextView drawerToolIcon;
    private TextView drawerToolText;
    private ViewGroup emptyTipView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planning, container, false);
        calendarView1 = view.findViewById(R.id.calendar1);
        calendarView2 = view.findViewById(R.id.calendar2);
        recyclerView = view.findViewById(R.id.recycler_view);
        drawerTool = view.findViewById(R.id.drawer_tool);
        drawerToolIcon = view.findViewById(R.id.drawer_tool_icon);
        drawerToolText = view.findViewById(R.id.drawer_tool_text);
        emptyTipView = view.findViewById(R.id.empty_tip);
        calContaner1 = view.findViewById(R.id.calendar1_container);
        calContaner2 = view.findViewById(R.id.calendar2_container);
        final TextView dateTitle = view.findViewById(R.id.date_title);
        View todayButton = view.findViewById(R.id.todayButton);


        //设置TAB
        tabs.add((TextView) view.findViewById(R.id.tab1));
        tabs.add((TextView) view.findViewById(R.id.tab2));
        for(View v : tabs){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //改变TAB颜色
                    for(TextView v : tabs){
                        v.setTextColor(getResources().getColor(R.color.tabNormal));
                    }
                    TextView textView = (TextView) view;
                    textView.setTextColor(getResources().getColor(R.color.tabSelected));

                    //选择日历
                    if(view.getId() == R.id.tab1){
                        calContaner1.setVisibility(View.VISIBLE);
                        calContaner2.setVisibility(View.GONE);
                        setTitle(calendarView1.getCurrentDate().getDate());
                    }else{
                        calContaner2.setVisibility(View.VISIBLE);
                        calContaner1.setVisibility(View.GONE);
                        setTitle(calendarView2.getCurrentDate());
                    }

                }
            });
        }

        view.post(new Runnable() {
            @Override
            public void run() {
                (tabs.getFirst()).callOnClick();
            }
        });

        //设置第一日历
        calendarView1.setTopbarVisible(false);
        calendarView1.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                setTitle(date.getDate());
            }
        });
        calendarView1.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                setRecyclerView(data, date.getDate());
                setDrawerToolText(date.getDate());
            }
        });
        setDrawerToolText(new Date());

        //设置第二日历
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        calendarView2.setViewType(7);
        calendarView2.setOnDataClickListener(new CalendarView.OnDataClickListener() {
            @Override
            public void run(CalendarView.Data data) {
                Intent intent = new Intent(getContext(), EditActivity.class);
                intent.putExtra("id", data.id);
                intent.putExtra("object_id", data.object_id);
                intent.putExtra("content", data.content);
                intent.putExtra("addr", data.addr);
                intent.putExtra("start_time", sdf.format(data.start_time));
                intent.putExtra("end_time", sdf.format(data.end_time));
                intent.putExtra("note_time", data.note_time);
                intent.putExtra("state", data.state);
                startActivity(intent);
            }
        });
        calendarView2.setOnPageChangeListener(new CalendarView.OnPageChangeListener() {
            @Override
            public void run(Date date) {
                setTitle(date);
            }
        });

        //设置抽屉工具
        drawerTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCaalendarOpened = !isCaalendarOpened;
                if(isCaalendarOpened){
                    calendarView1.setVisibility(View.VISIBLE);
                    drawerToolIcon.setText(getResources().getText(R.string.arrow_up));
                }else{
                    calendarView1.setVisibility(View.GONE);
                    drawerToolIcon.setText(getResources().getText(R.string.arrow_down));
                }
            }
        });

        //设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        //设置“回到今天”按钮
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToToday();
            }
        });

        //设置观察对象
        DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        dataBaseAdapter.addObserver(this);
        update(dataBaseAdapter, null);


        return view;
    }


    @Override
    public void update(Observable observable, Object o) {
        data = (((DataBaseAdapter)observable).queryEvents());
        //Date t1 = new Date();
        //给第一日历加点
        final Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        calendarView1.removeDecorators();
        //添加“今天”背景
        calendarView1.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return equalsDate(day, new Date());
            }

            @Override
            public void decorate(DayViewFacade view) {
                Drawable d = getResources().getDrawable(R.drawable.background_today);
                view.setBackgroundDrawable(d);
            }
        });


        //Date t2 = new Date();
        //添加点
        new Thread(){
            @Override
            public void run() {
                final int dotColorNorm = getResources().getColor(R.color.tabSelected);
                final int dotColorPass = getResources().getColor(R.color.tabNormal);
                final Collection<DayViewDecorator> decorators = new LinkedList<>();
                for(final Data dataItem : data){
                    decorators.add(new DayViewDecorator() {
                        @Override
                        public boolean shouldDecorate(CalendarDay day) {
                            return  equalsDate(day, dataItem.start_time);
                        }

                        @Override
                        public void decorate(final DayViewFacade view) {
                            final int dotColor = dataItem.start_time.before(today) ? dotColorPass : dotColorNorm;
                            view.addSpan(new DotSpan(10, dotColor));
                        }
                    });
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Date t21 = new Date();
                        calendarView1.addDecorators(decorators);
                        //Date t22 = new Date();
                        //Log.v("添加点时间", t22.getTime() - t21.getTime() + "ms");
                    }
                });
            }
        }.start();




        //Date t3 = new Date();
        //给第二日历填充数据
        calendarView2.setData(data);

        //Date t4 = new Date();
        calendarView2.notifyChanged();


        //Date t5 = new Date();
        //给Recyvlerview填充数据
        setRecyclerView(data, new Date());

        //Date t6 = new Date();
        //Log.v("时间1", t2.getTime() - t1.getTime() + "ms");
        //Log.v("时间2", t3.getTime() - t2.getTime() + "ms");
        //Log.v("时间3", t4.getTime() - t3.getTime() + "ms");
        //Log.v("时间4", t5.getTime() - t4.getTime() + "ms");
        //Log.v("时间5", t6.getTime() - t5.getTime() + "ms");
    }

    //设置标题
    public  void setTitle(Date date){
        String title = date.getYear() + 1900 + "年" + (date.getMonth() + 1 ) + "月";
        final TextView dateTitle = getView().findViewById(R.id.date_title);
        dateTitle.setText(title);
    }

    //设置抽屉按钮
    public void setDrawerToolText(Date date){
        drawerToolText.setText((date.getMonth() + 1) + "月" + date.getDate() + "日");
    }

    //判断日期是相等
    public boolean equalsDate(CalendarDay day, Date date){
        boolean eq = day.getYear() == date.getYear() + 1900
                && day.getMonth() == date.getMonth()
                && day.getDay() == date.getDate();
        return eq;
    }

    //显示某天的事件
    public void setRecyclerView(List<Data> data, Date date){
        List<Data> dataToday = new DataFilter(date).doFilter(data);
        recyclerView.setAdapter(new EventDayAdapter(dataToday));
        recyclerView.getAdapter().notifyDataSetChanged();
        renderTip(dataToday.size() == 0);
    }

    //根据是否有数据决定提示是否显示
    public void renderTip(boolean isEmpty){
        if(isEmpty){
            emptyTipView.setVisibility(View.VISIBLE);
        }else{
            emptyTipView.setVisibility(View.GONE);
        }
    }

    //回到今天
    public void backToToday(){
        if(calContaner1.getVisibility() == View.VISIBLE){
            Date today = new Date();
            Date current = calendarView1.getCurrentDate().getDate();
            while (today.getYear() != current.getYear() || today.getMonth() != current.getMonth()){
                if(today.before(current)){
                    calendarView1.goToPrevious();
                }else{
                    calendarView1.goToNext();
                }
                current = calendarView1.getCurrentDate().getDate();
            }
        }else{
            calendarView2.toToday();
        }
    }
}

class EventDayAdapter extends RecyclerView.Adapter<EventDayAdapter.VH>{
    private List<Data> data;
    private SimpleDateFormat sdf;

    public EventDayAdapter(List<Data> rowData){
        this.data = rowData;
        this.sdf  = new SimpleDateFormat("HH:mm");
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_day, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Data dataItem = data.get(position);
        holder.startTimeView.setText(sdf.format(dataItem.start_time));
        String msg = dataItem.content;
        if(!dataItem.addr.equals("")){
            msg += " 地址:" + dataItem.addr;
        }
        holder.msgView.setText(msg);
    }

    @Override
    public int getItemCount() {
        if(data != null){
            return  data.size();
        }
        return 0;
    }

    class VH extends RecyclerView.ViewHolder{
        TextView startTimeView;
        TextView msgView;

        public VH(@NonNull View itemView) {
            super(itemView);
            startTimeView = itemView.findViewById(R.id.start_time);
            msgView = itemView.findViewById(R.id.msg);

        }
    }
}

class DataFilter{
    private Date date;
    public DataFilter(Date date){
        this.date = date;
    }

    public List<Data> doFilter(List<Data> data){
        if(data != null){
            LinkedList<Data> dataClean = new LinkedList<>();
            for(Data dataItem : data){
                boolean canPass = dataItem.start_time.getYear() == date.getYear()
                        && dataItem.start_time.getMonth() == date.getMonth()
                        && dataItem.start_time.getDate() == date.getDate();
                if(canPass){
                    dataClean.add(dataItem);
                }
            }

            //排序
            if(dataClean.size() >= 2){
                Data[] dataArray = dataClean.toArray(new Data[dataClean.size()]);
                Arrays.sort(dataArray, new Comparator<Data>() {
                    @Override
                    public int compare(Data data, Data t1) {
                        int distance = (int) (data.start_time.getTime() - t1.start_time.getTime());
                        return distance == 0 ? -1 : distance;
                    }
                });
                dataClean.clear();
                for(Data dataItem : dataArray){
                    dataClean.add(dataItem);
                }
            }
            return dataClean;
        }

        return null;
    }
}