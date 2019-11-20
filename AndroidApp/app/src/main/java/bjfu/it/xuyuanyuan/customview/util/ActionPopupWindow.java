package bjfu.it.xuyuanyuan.customview.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import bjfu.it.xuyuanyuan.customview.R;

public class ActionPopupWindow extends PopupWindow {
    private TextView titleView;
    private RecyclerView recyclerView;
    private TextView cancelButton;
    private OnButtonClicked onButtonClicked;
    private OnCancelClicked onCancelClicked;
    private List<String> buttons = new LinkedList<>();

    public ActionPopupWindow(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.window_actionsheet, null);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        titleView = view.findViewById(R.id.title);
        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setContentView(view);
        setWidth(-1);
        setHeight(-2);
        setFocusable(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setAlpha(1.0f);
            }
        });
    }

    //添加按钮
    public void addButton(String text){
        buttons.add(text);
    }

    //添加按钮事件
    public void setOnClickListener(OnButtonClicked onClickListener){
        this.onButtonClicked = onClickListener;
    }

    //添加取消事件
    public void setOnCancelClicked(OnCancelClicked onCancelClicked){
        this.onCancelClicked = onCancelClicked;
    }

    //设置标题
    public void setTitle(String text){
        titleView.setText(text);
    }

    public void setCancel(String text, final OnCancelClicked onCancelClicked){
        cancelButton.setText(text);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onCancelClicked != null){
                    onCancelClicked.onClick();
                }
            }
        });
    }

    //显示
    public void show(){
        recyclerView.setAdapter(new ActionAdapter(buttons, onButtonClicked));
        setAlpha(0.5f);
        this.showAtLocation(getContentView().getRootView(), Gravity.BOTTOM, -1, -2);
    }


    private void setAlpha(float alpha){
        Window window = ((Activity)(getContentView()).getContext()).getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.alpha = alpha;
        window.setAttributes(wl);
    }

    public static interface OnButtonClicked{
        void onClick(int index);
    }

    public static interface OnCancelClicked{
        void onClick();
    }
}

class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.VH>{
    private List<String> buttons;
    private ActionPopupWindow.OnButtonClicked onButtonClicked;

    public ActionAdapter(List<String> buttons, ActionPopupWindow.OnButtonClicked onButtonClicked){
        this.buttons = buttons;
        this.onButtonClicked = onButtonClicked;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actionsheet, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, final int position) {
        holder.textView.setText(buttons.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(onButtonClicked != null){
                    onButtonClicked.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(buttons != null){
            return  buttons.size();
        }
        return 0;
    }

    class VH extends RecyclerView.ViewHolder{
        TextView textView;
        public VH(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.button);
        }
    }
}