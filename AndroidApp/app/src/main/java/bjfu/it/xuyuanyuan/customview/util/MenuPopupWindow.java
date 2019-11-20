package bjfu.it.xuyuanyuan.customview.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import bjfu.it.xuyuanyuan.customview.R;

public class MenuPopupWindow extends PopupWindow {
    private MenuPopupWindowListAdapter adapter;

    public MenuPopupWindow(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.window_popuplist, null);
        setContentView(view);
        setWidth(350);
        setHeight(-2);
        setFocusable(true);

        //初始化List
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        adapter = new MenuPopupWindowListAdapter();
        recyclerView.setAdapter(adapter);
    }

    //添加按钮
    public void addButton(String name){
        if(name != null){
            adapter.getButtons().add(name);
        }
    }

    //添加事件
    public void setOnButtonClicked( OnButtonClicked onButtonClicked){
        adapter.setOnButtonClicked(onButtonClicked);
    }


    public interface OnButtonClicked{
        void cliked(int index);
    }
}


class MenuPopupWindowListAdapter extends RecyclerView.Adapter<MenuPopupWindowListAdapter.VH>{

    private List<String> buttons = new LinkedList<>();
    private MenuPopupWindow.OnButtonClicked onButtonClicked;

    public List<String> getButtons() {
        return buttons;
    }

    public void setOnButtonClicked(MenuPopupWindow.OnButtonClicked onButtonClicked) {
        this.onButtonClicked = onButtonClicked;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_window_popuplist, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, final int position) {
        if(onButtonClicked != null){
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onButtonClicked.cliked(position);
                }
            });
        }

        holder.text.setText(buttons.get(position));

        if(position != buttons.size() -1){
            holder.seperator.setVisibility(View.VISIBLE);
        }else{
            holder.seperator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return buttons.size();
    }



    class VH extends RecyclerView.ViewHolder{
        TextView text;
        View seperator;
        public VH(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            seperator = itemView.findViewById(R.id.separator);
        }
    }

}
