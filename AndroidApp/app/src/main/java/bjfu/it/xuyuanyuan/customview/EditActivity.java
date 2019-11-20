package bjfu.it.xuyuanyuan.customview;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.cloudea.basemodule.Data;
import com.cloudea.localstoragemodule.LocalStorageUtils;

import org.feezu.liuli.timeselector.TimeSelector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import bjfu.it.xuyuanyuan.customview.Adapter.DataBaseAdapter;
import bjfu.it.xuyuanyuan.customview.Adapter.MapAdapter;
import bjfu.it.xuyuanyuan.customview.util.ActionPopupWindow;
import bjfu.it.xuyuanyuan.customview.util.IconText;

public class EditActivity extends AppCompatActivity {

    private boolean editMode = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private int id;
    private String object_id;

    EditText contentView;
    TextView addrView;
    TextView startTimeView;
    TextView endTimeView;
    TextView noteTimeView;
    CheckBox stateView;
    TextView navBtn;
    TextView delBtn;
    TextView saveBtn;
    TextView modeBtn;

    IconText startTimeIcon;
    IconText endTimeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        contentView = findViewById(R.id.content);
        addrView = findViewById(R.id.addr);
        startTimeView = findViewById(R.id.start_time);
        endTimeView = findViewById(R.id.end_time);
        noteTimeView = findViewById(R.id.note_time);
        stateView = findViewById(R.id.state);
        navBtn = findViewById(R.id.nav_btn);
        delBtn = findViewById(R.id.del_btn);
        saveBtn = findViewById(R.id.save_btn);
        modeBtn = findViewById(R.id.mode_btn);
        startTimeIcon = findViewById(R.id.start_time_icon);
        endTimeIcon = findViewById(R.id.end_time_icon);

        //复选框事件
        stateView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(stateView.isChecked()){
                    stateView.setText("已经完成提醒");
                }else {
                    stateView.setText("还未提醒");
                }
            }
        });


        //设置默认内容
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        object_id = intent.getStringExtra("object_id");
        contentView.setText(intent.getStringExtra("content"));
        addrView.setText(intent.getStringExtra("addr"));
        startTimeView.setText(intent.getStringExtra("start_time"));
        endTimeView.setText(intent.getStringExtra("end_time"));
        noteTimeView.setText(intent.getIntExtra("note_time", 0) + "");

        boolean state = intent.getBooleanExtra("state", true);

        //设置复选框状态
        if(state){
            stateView.setChecked(true);
        }else{
            stateView.setChecked(false);
        }
        //第一次设置复选框文本
        if(stateView.isChecked()){
            stateView.setText("已经完成提醒");
        }else {
            stateView.setText("还未提醒");
        }

        setModeSettings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0) {
            String addr = data.getStringExtra("addr");
            if (addr != null && !addr.equals("")) {
                addrView.setText(addr);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(editMode){
            changeMode();
        }else{
            this.finish();
        }
    }

    public void back(View view) {
        this.finish();
    }

    public void swichMode(View view) {
        if(editMode){
            if(!save()){
                return;
            }
        }
        changeMode();
    }

    //删除数据
    public void delete(View view){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("确认删除吗？");
        ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Data dataItem = new Data();
                dataItem.id = id;
                dataItem.object_id = object_id;
                DataBaseAdapter.getInstance().removeEvent(dataItem);
                back(null);
            }
        });
        ab.setNegativeButton("取消", null);
        ab.show();
    }


    //打开时间选择器
    public void awakeTimeSelector(final View view){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 50);
        TimeSelector timeSelector = new TimeSelector(EditActivity.this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {

                    if(view == startTimeView){
                        Date startTime = sdf.parse(startTimeView.getText().toString());
                        Date endTime = sdf.parse(endTimeView.getText().toString());
                        long gap = endTime.getTime() - startTime.getTime();
                        Date newStartTime = sdf.parse(time);
                        Date newEndTime = new Date(newStartTime.getTime() + gap);
                        endTimeView.setText(sdf.format(newEndTime));
                    }
                    TextView textView = (TextView)view;
                    textView.setText(time);
                } catch (ParseException e) {
                    Log.v("编辑界面", "时间格式化出错");
                    e.printStackTrace();
                }
            }
        }, sdf.format(new Date()), sdf.format(calendar.getTime()));

        timeSelector.show();
    }

    //打开位置选择器
    public void awakePositionSelector(View view){
        MapAdapter.getInstance().getPositionOnMap(this, addrView.getText().toString(), 0);
    }

    //打开note_time选择窗口
    public void awateNoteTimeSelector(View view){
        final ActionPopupWindow actionPopupWindow = new ActionPopupWindow(this);
        actionPopupWindow.setTitle("选择提前提醒时间");
        actionPopupWindow.addButton("准时");
        actionPopupWindow.addButton("提前10分钟");
        actionPopupWindow.addButton("提前半个小时");
        actionPopupWindow.addButton("提前一个小时");
        actionPopupWindow.addButton("提前一天");
        actionPopupWindow.setOnClickListener(new ActionPopupWindow.OnButtonClicked() {
            @Override
            public void onClick(int index) {
                String timeString = "0";
                switch (index){
                    case 0: timeString = "0" ; break;
                    case 1: timeString = "10" ; break;
                    case 2: timeString = "30" ; break;
                    case 3: timeString = "60" ; break;
                    case 4: timeString = "1440" ; break;
                }
                noteTimeView.setText(timeString);
                actionPopupWindow.dismiss();
            }
        });
        actionPopupWindow.show();
    }

    //开始导航
    public void startNav(View view){
        MapAdapter.getInstance().startNavigating(addrView.getText().toString());
    }


    //保存数据
    private boolean save(){
        Data dataItem = new Data();
        try {
            dataItem.id = id;
            dataItem.object_id = object_id;
            dataItem.content = contentView.getText().toString();
            dataItem.addr = addrView.getText().toString();
            dataItem.start_time = sdf.parse(startTimeView.getText().toString());
            dataItem.end_time = sdf.parse(endTimeView.getText().toString());
            dataItem.note_time = Integer.parseInt(noteTimeView.getText().toString());
            dataItem.state = stateView.isChecked();
            if (dataItem.note_time < 0 || dataItem.end_time.before(dataItem.start_time)){
                throw new Exception("");
            }

            DataBaseAdapter.getInstance().updateEvent(dataItem, true);
        }catch (Exception e){
            new AlertDialog.Builder(this).setTitle("数据格式有误")
            .setMessage("1.结束时间必须在开始时间后\n2.提前提醒时间只能为正整数")
            .setPositiveButton("确定", null)
            .show();
            return false;
        }
        return true;
    }

    //改变显示模式
    private void changeMode(){
        editMode = !editMode;
        setModeSettings();
    }

    //设置与模式相关的显示
    private void setModeSettings(){
        if(editMode){
            contentView.setEnabled(true);
            addrView.setClickable(true);
            startTimeView.setClickable(true);
            endTimeView.setClickable(true);
            noteTimeView.setClickable(true);
            stateView.setClickable(true);
            navBtn.setVisibility(View.GONE);
            delBtn.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);
            modeBtn.setText("保存");
            startTimeIcon.setVisibility(View.VISIBLE);
            endTimeIcon.setVisibility(View.VISIBLE);
        }else{

            contentView.setEnabled(false);
            addrView.setClickable(false);
            startTimeView.setClickable(false);
            endTimeView.setClickable(false);
            noteTimeView.setClickable(false);
            stateView.setClickable(false);
            navBtn.setVisibility(View.VISIBLE);
            delBtn.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.GONE);
            modeBtn.setText("编辑");
            startTimeIcon.setVisibility(View.INVISIBLE);
            endTimeIcon.setVisibility(View.INVISIBLE);
        }
    }
}
