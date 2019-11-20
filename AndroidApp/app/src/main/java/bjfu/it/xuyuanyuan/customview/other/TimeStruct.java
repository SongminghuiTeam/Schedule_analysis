package bjfu.it.xuyuanyuan.customview.other;

import com.cloudea.basemodule.Data;

import java.util.Date;

public class TimeStruct{
    public TaskStatus taskStatus;
    public String statusDescribeText;


    public static TimeStruct getState(Data dataItem){
        Date now = new Date();
        TimeStruct timeStruct = new TimeStruct();
        if(now.before(dataItem.start_time)){
            timeStruct.taskStatus = TaskStatus.TASK_COMMING;
            timeStruct.statusDescribeText = "还有" + getTimeDesString(dataItem.start_time.getTime() - now.getTime()) + "开始";
        }else if(now.after(dataItem.end_time)){
            timeStruct.taskStatus = TaskStatus.TASK_COMPLETED;
            timeStruct.statusDescribeText = ("已经结束" + getTimeDesString(now.getTime() - dataItem.end_time.getTime()));
        }else{
            timeStruct.taskStatus = TaskStatus.TASK_RUNNING;
            timeStruct.statusDescribeText = ("开始了" + getTimeDesString(now.getTime() - dataItem.start_time.getTime()));
        }
        return timeStruct;
    }

    //计算时间描述字符串
    public static String getTimeDesString(long gap){
        int day = (int) (gap / (24 * 3600 * 1000));
        if(day != 0){
            return day + "天";
        }
        int hours = (int) (gap / (3600 * 1000));
        if(hours != 0){
            return hours + "小时";
        }
        int minutes = (int) (gap / (60 * 1000));

        return minutes + "分钟";
    }
}
