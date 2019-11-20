package bjfu.it.xuyuanyuan.customview.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudea.basemodule.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import bjfu.it.xuyuanyuan.customview.Adapter.DataBaseAdapter;
import bjfu.it.xuyuanyuan.customview.Adapter.MapAdapter;
import bjfu.it.xuyuanyuan.customview.EditActivity;
import bjfu.it.xuyuanyuan.customview.Filter.TaskClassFilter;
import bjfu.it.xuyuanyuan.customview.Filter.TaskFilter;
import bjfu.it.xuyuanyuan.customview.Filter.TaskRangeFilter;
import bjfu.it.xuyuanyuan.customview.Filter.TaskStateFilter;
import bjfu.it.xuyuanyuan.customview.R;
import bjfu.it.xuyuanyuan.customview.ViewPagerActivity;
import bjfu.it.xuyuanyuan.customview.WebActivity;
import bjfu.it.xuyuanyuan.customview.other.TaskStatus;
import bjfu.it.xuyuanyuan.customview.other.TimeStruct;
import bjfu.it.xuyuanyuan.customview.util.IconText;
import bjfu.it.xuyuanyuan.customview.util.MenuPopupWindow;
import bjfu.it.xuyuanyuan.customview.util.OptionPopupWindow;
import bjfu.it.xuyuanyuan.customview.util.WaitPopupWindow;

import static bjfu.it.xuyuanyuan.customview.other.TimeStruct.getState;


public class CollectionFragment extends Fragment {
    RecyclerView eventList;
    private boolean initFinished;
    private EventAdapter adapter;
    private View filterTool;
    private TextView filterToolIcon;
    private TextView filterToolText;
    private boolean isOptionOpened = false;
    private OptionPopupWindow optionPopupWindow;
    private int rangeIndex = 4;
    private String[] options = {
            "仅今天",
            "近3天",
            "近7天",
            "近15天",
            "近30天",
            "显示全部"
    };

    ViewGroup emptyTipView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        rangeIndex = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("range_index", 4);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        //查找View
        eventList = view.findViewById(R.id.event_list);
        filterTool = view.findViewById(R.id.filter_tool);
        filterToolIcon = view.findViewById(R.id.filter_tool_icon);
        filterToolText =view.findViewById(R.id.filter_tool_text);
        View addTool = view.findViewById(R.id.add_tool);
        emptyTipView = view.findViewById(R.id.empty_tip);

        //设置RecyclerView
        eventList.setLayoutManager(new LinearLayoutManager(this.getActivity(), RecyclerView.VERTICAL, false));
        adapter = new EventAdapter(this, this.getContext());
        adapter.rangeIndex = rangeIndex;
        DataBaseAdapter.getInstance().addObserver(adapter);
        eventList.setAdapter(adapter);

        //设置OptionPopupWindow
        optionPopupWindow = new OptionPopupWindow(this.getContext());
        for(String i : options){
            optionPopupWindow.addOption(i);
        }
        optionPopupWindow.setDefaultOption(rangeIndex);
        optionPopupWindow.setOpetionChangeListener(new OptionPopupWindow.OptionChangeListener() {
            @Override
            public void change(String des, int index) {
                optionPopupWindow.dismiss();
                filterTool.callOnClick();
                filterToolText.setText(des);
                rangeIndex = index;   //TODO: 需要进一步查看
                adapter.rangeIndex = index;
                adapter.update();
                //保存设置
                SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                editor.putInt("range_index", index);
                editor.commit();
            }
        });

        //设置选项按钮
        filterTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOptionOpened = !isOptionOpened;
                renderOption(isOptionOpened);
            }
        });
        filterToolText.setText(options[rangeIndex]);
        renderOption(isOptionOpened);

        addTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("url", "file:///android_asset/aui/add_event.html");
                startActivity(intent);
            }
        });

        initFinished = true;
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(eventList != null){
            EventAdapter ea = (EventAdapter) eventList.getAdapter();
            ea.update();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //关闭选项窗口
        if(initFinished){
            renderOption(false);
        }
    }


    //根据状态设置选项按钮的颜色
    private void renderOption(boolean isOpened){
        int color = 0;
        if(isOpened){
            color =  getResources().getColor(R.color.tabSelected);
            optionPopupWindow.show(filterTool);
        }else{
            color = getResources().getColor(R.color.tabNormal);
            optionPopupWindow.dismiss();
        }
        filterToolIcon.setTextColor(color);
        filterToolText.setTextColor(color);
        isOptionOpened = isOpened;
    }

    //根据是否有数据决定提示是否显示
    public void renderTip(boolean isEmpty){
        if(isEmpty){
            emptyTipView.setVisibility(View.VISIBLE);
        }else{
            emptyTipView.setVisibility(View.GONE);
        }
    }
}

class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> implements Observer {

    private List<Data> data;
    private CollectionFragment collection;
    private Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private int runningCount;
    private int commingCount;
    private int completedCount;
    private int[] lowers = {0, 3, 7, 15, 30, -1};
    private int[] uppers = lowers;
    private String[] menus = {"编辑", "导航", "删除", "清除己完成", "清除己提醒", "清除所有"};
    //范围的索引，可由外部改变
    public int rangeIndex = 4;

    public  EventAdapter(CollectionFragment collection, Context context){
        this.collection = collection;
        this.context = context;
        SharedPreferences preferences = ((Activity) context).getPreferences(Activity.MODE_PRIVATE);
        rangeIndex = preferences.getInt("range_index", 4);
        update();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, final int position) {
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                holder.xOffset = (int) motionEvent.getX();
                holder.yRawOffset = (int) motionEvent.getRawY();
                return false;
            }
        });

        //单击事件
        final Data dataItem =  data.get(position);
        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("id", dataItem.id);
                intent.putExtra("object_id", dataItem.object_id);
                intent.putExtra("content", dataItem.content);
                intent.putExtra("addr", dataItem.addr);
                intent.putExtra("start_time", sdf.format(dataItem.start_time));
                intent.putExtra("end_time", sdf.format(dataItem.end_time));
                intent.putExtra("note_time", dataItem.note_time);
                intent.putExtra("state", dataItem.state);
                context.startActivity(intent);
            }
        };
        holder.itemView.setOnClickListener(onClickListener);

        //长按事件
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(30);
                //弹出菜单
                int separator = ((Activity)context).getWindow().getWindowManager().getDefaultDisplay().getHeight() / 2;
                final MenuPopupWindow popupWindow = new MenuPopupWindow(view.getContext());
                for(String menu : menus){
                    popupWindow.addButton(menu);
                }
                popupWindow.setOnButtonClicked(new MenuPopupWindow.OnButtonClicked() {
                    @Override
                    public void cliked(final int index) {
                        switch (index){
                            case 0: onClickListener.onClick(holder.itemView);break;
                            case 1:MapAdapter.getInstance().startNavigating(dataItem.addr);break;
                            case 2:DataBaseAdapter.getInstance().removeEvent(dataItem);break;
                            case 3: ;
                            case 4: ;
                            case 5:
                                AlertDialog.Builder ab = new AlertDialog.Builder(holder.iconView.getContext());
                                ab.setTitle("确定" + menus[index] + "吗?");
                                ab.setMessage("此操作将不可恢复！");
                                ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Activity activity = (Activity) holder.itemView.getContext();
                                        final WaitPopupWindow win = new WaitPopupWindow(activity);
                                        win.showAtLocation(activity.getWindow().getDecorView(), Gravity.NO_GRAVITY,0,0);
                                        TaskFilter taskFilter = null;
                                        switch (index){
                                            case 3: taskFilter = new TaskClassFilter(TaskStatus.TASK_COMPLETED);break;
                                            case 4: taskFilter = new TaskStateFilter(true);break;
                                            case 5: taskFilter = null;break;
                                        }
                                        DataBaseAdapter.getInstance().removeEvents(taskFilter, new DataBaseAdapter.OnRequestCompleted() {
                                            @Override
                                            public void finish() {
                                                win.dismiss();
                                            }
                                        });
                                    }
                                });
                                ab.setNegativeButton("取消", null);
                                ab.show();
                        }
                        popupWindow.dismiss();
                    }
                });

                if(holder.yRawOffset < separator){
                    popupWindow.showAsDropDown(view, holder.xOffset, 0);
                }else {
                    int[] position = new int[2];
                    holder.itemView.getLocationInWindow(position);
                    popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    popupWindow.showAtLocation(holder.itemView, Gravity.NO_GRAVITY, holder.xOffset, position[1] - popupWindow.getContentView().getMeasuredHeight());
                }
                return true;
            }
        });


        //设置标题和地址
        holder.contentView.setText(dataItem.content);
        holder.addrView.setText(dataItem.addr);

        //计算并显示时间
        TimeStruct timeStruct = getState(dataItem);
        holder.timeView.setText(timeStruct.statusDescribeText);
        Resources res = context.getResources();
        int titleColor = res.getColor(R.color.taskTitle);
        int normalColor = res.getColor(R.color.taskNormal);
        switch (timeStruct.taskStatus){
            case TASK_COMMING:
                holder.stateView.setText("未开始");
                holder.stateView.setTextColor(res.getColor(R.color.taskComming));
                holder.iconView.setTextColor(res.getColor(R.color.taskComming));
                holder.timeView.setTextColor(normalColor);
                holder.addrView.setTextColor(normalColor);
                holder.contentView.setTextColor(titleColor);
                break;
            case TASK_COMPLETED:
                holder.stateView.setText("已完成");
                int color = res.getColor(R.color.taskCompleted);
                holder.iconView.setTextColor(color);
                holder.timeView.setTextColor(color);
                holder.addrView.setTextColor(color);
                holder.stateView.setTextColor(color);
                holder.contentView.setTextColor(color);
                break;
            case TASK_RUNNING:
                holder.stateView.setText("进行中");
                holder.stateView.setTextColor(res.getColor(R.color.taskRunning));
                holder.iconView.setTextColor(res.getColor(R.color.taskRunning));
                holder.timeView.setTextColor(normalColor);
                holder.addrView.setTextColor(normalColor);
                holder.contentView.setTextColor(titleColor);
                break;
        }

        //设置闹钟图标的显示
        if(dataItem.state == true){
            holder.iconView.setVisibility(View.INVISIBLE);
        }else{
            holder.iconView.setVisibility(View.VISIBLE);
        }

        //设置分割线的显示
        if(position == data.size() -1){
            holder.lineView.setVisibility(View.INVISIBLE);
        }else{
            holder.lineView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if(data != null){
            return data.size();
        }
        return 0;
    }

    //重新加载数据
    public void update(){
        List<Data> data = DataBaseAdapter.getInstance().queryEvents();

        //对数据进行范围挑选
        data = new TaskRangeFilter(lowers[rangeIndex], uppers[rangeIndex]).doFilter(data);

        //对数据进行挑选-分类挑选
        List<Data> dataRunning = new TaskClassFilter(TaskStatus.TASK_RUNNING).doFilter(data);
        List<Data> dataComming = new TaskClassFilter(TaskStatus.TASK_COMMING).doFilter(data);
        List<Data> datacompleted = new TaskClassFilter(TaskStatus.TASK_COMPLETED).doFilter(data);
        this.data = new LinkedList<>();
        for (Data dataItem : dataRunning){
            this.data.add(dataItem);
        }
        for (Data dataItem : dataComming){
            this.data.add(dataItem);
        }
        for (Data dataItem : datacompleted){
            this.data.add(dataItem);
        }

        //显示不同任务的数量
        runningCount = dataRunning.size();
        commingCount = dataComming.size();
        completedCount = datacompleted.size();

        notifyMineFragment();
        notifyDataSetChanged();

        //设置提示
        collection.renderTip(this.data.size() == 0);
    }

    public void notifyMineFragment(){
        ViewPagerActivity activity = (ViewPagerActivity)context;
        try {
            activity.showTaskCountInMineFragment(runningCount, commingCount, completedCount);
        }catch (Exception e){
            Log.v("收集箱", "MineFragment还没有准备好");
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        update();
    }

    class VH extends RecyclerView.ViewHolder {
        IconText iconView;
        TextView contentView;
        TextView addrView;
        TextView timeView;
        TextView stateView;
        View lineView;

        int xOffset = 0;
        int yRawOffset = 0;

        public VH(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.icon);
            contentView = itemView.findViewById(R.id.content);
            addrView = itemView.findViewById(R.id.addr);
            timeView = itemView.findViewById(R.id.time);
            stateView = itemView.findViewById(R.id.state);
            lineView = itemView.findViewById(R.id.line);
        }
    }
}
