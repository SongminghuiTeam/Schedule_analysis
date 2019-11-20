package bjfu.it.xuyuanyuan.customview.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import bjfu.it.xuyuanyuan.customview.R;

public class OptionPopupWindow extends PopupWindow {
    private List<String> opetions = new LinkedList<>();
    private OptionChangeListener optionChangeListener;
    private RecyclerView recyclerView;
    private OptionAdapter optionAdapter;
    private int choosed = 0;

    public OptionPopupWindow(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.window_option, null);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        setContentView(view);
        setWidth(400);
        setHeight(-2);
        setFocusable(false);
    }

    //添加选项
    public void addOption(String option){
        this.opetions.add(option);
    }

    //设置选择监听
    public void setOpetionChangeListener(OptionChangeListener opetionChangeListener){
        this.optionChangeListener = opetionChangeListener;
    }

    //显示选项菜单
    public void show(View parent){
        if(optionAdapter == null){
            optionAdapter = new OptionAdapter(opetions, optionChangeListener, choosed);
            recyclerView.setAdapter(optionAdapter);
        }
        this.showAsDropDown(parent);
    }

    //返回所有选项文本
    public List<String> getOptions(){
        return this.opetions;
    }

    //设置默认选项
    public void setDefaultOption(int defaultOption){
        this.choosed = defaultOption;
    }

    //选项改变回调接口
    public static  interface OptionChangeListener{
        void change(String des, int index);
    }
}

class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.VH>{
    private List<String> options;
    private OptionPopupWindow.OptionChangeListener optionChangeListener;
    private int choosed = 0;

    public OptionAdapter(List<String> options, OptionPopupWindow.OptionChangeListener optionChangeListener, int choosed){
        this.optionChangeListener = optionChangeListener;
        this.options = options;
        if(choosed < options.size()){
            this.choosed = choosed;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_window_option, parent,false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, final int position) {
        if(position == choosed){
            holder.optionIcon.setVisibility(View.VISIBLE);
        }else{
            holder.optionIcon.setVisibility(View.INVISIBLE);
        }
        holder.optionText.setText(options.get(position));
        holder.optionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosed = position;
                notifyDataSetChanged();
                if(optionChangeListener != null){
                    optionChangeListener.change(options.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(options != null){
            return options.size();
        }
        return 0;
    }

    class VH extends RecyclerView.ViewHolder{
        ViewGroup optionView;
        TextView optionIcon;
        TextView optionText;

        public VH(@NonNull View itemView) {
            super(itemView);
            optionView = itemView.findViewById(R.id.option);
            optionIcon = itemView.findViewById(R.id.option_icon);
            optionText = itemView.findViewById(R.id.option_text);
        }
    }
}