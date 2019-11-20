package com.cloudea.viewandeditmodule.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudea.viewandeditmodule.ComfirnWindow;
import com.cloudea.viewandeditmodule.R;
import com.qmuiteam.qmui.widget.QMUIFloatLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ComfirmWindowRecyclerViewAdapter  extends  androidx.recyclerview.widget.RecyclerView.Adapter<ComfirmWindowRecyclerViewAdapter.VH>{

    private List<ComfirnWindow.RowData> data;

    public ComfirmWindowRecyclerViewAdapter( List<ComfirnWindow.RowData> data){
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewandedit_item_message, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, final int position) {
        holder.summaryView.setText(data.get(position).summary);
        holder.startView.setText(data.get(position).start.toString());
        holder.endView.setText(data.get(position).end.toString());
        holder.countView.setText(data.get(position).count + "");
        holder.backupView.removeAllViews();

        if(position == data.size() - 1){
            holder.seperator.setVisibility(View.INVISIBLE);
        }

        //设置默认的的目的地
        if(data.get(position).destination.size() == 0){
            holder.destinationView.setText("");
        }else{
            holder.destinationView.setText(data.get(position).destination.get(0));
        }

        //设置目的地列表
        for(String i : data.get(position).destination){
            View view = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.viewandedit_element_label, null);
            final TextView label = view.findViewById(R.id.label);
            label.setText(i);
            label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String choosedDes = label.getText().toString();
                    data.get(position).destinationSelected = choosedDes;
                    holder.destinationView.setText(choosedDes);
                }
            });
            holder.backupView.addView(view);
        }

        //设置日期
        SimpleDateFormat sdf3 = new SimpleDateFormat("MM-dd");
        StringBuilder sb = new StringBuilder();
        for(Date i : data.get(position).__dates){
            sb.append(sdf3.format(i) + ",");
        }
        if(sb.length() > 0){
            sb.deleteCharAt(sb.length() - 1);
        }
        holder.dateView.setText(sb.toString());

        //设置清除目的地
        holder.deleteAddrIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.get(position).destinationSelected = "";
                holder.destinationView.setText("");
            }
        });
    }

    @Override
    public int getItemCount() {
        if(data != null){
            return data.size();
        }
        return 0;
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView summaryView;
        TextView startView;
        TextView endView;
        TextView countView;
        TextView dateView;
        TextView destinationView;
        QMUIFloatLayout backupView;
        ImageView deleteAddrIconButton;
        View seperator;

        public VH(@NonNull View itemView) {
            super(itemView);
            summaryView = itemView.findViewById(R.id.summary);
            startView = itemView.findViewById(R.id.start);
            endView = itemView.findViewById(R.id.end);
            countView = itemView.findViewById(R.id.count);
            dateView = itemView.findViewById(R.id.date);
            destinationView = itemView.findViewById(R.id.destination);
            backupView = itemView.findViewById(R.id.backup);
            deleteAddrIconButton = itemView.findViewById(R.id.delete);
            seperator = itemView.findViewById(R.id.sep);
        }
    }
}
