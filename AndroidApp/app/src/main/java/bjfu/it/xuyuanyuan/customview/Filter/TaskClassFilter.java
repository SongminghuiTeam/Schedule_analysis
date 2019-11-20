package bjfu.it.xuyuanyuan.customview.Filter;

import com.cloudea.basemodule.Data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import bjfu.it.xuyuanyuan.customview.other.TaskStatus;
import bjfu.it.xuyuanyuan.customview.other.TimeStruct;

public class TaskClassFilter implements TaskFilter{
    private TaskStatus taskStatus;
    public TaskClassFilter(final TaskStatus taskStatus){
        this.taskStatus = taskStatus;
    }

    @Override
    public List<Data> doFilter(List<Data> data){
        LinkedList<Data> dataFilted = new LinkedList<>();
        //过滤
        for(Data dataItem : data){
            TimeStruct state = TimeStruct.getState(dataItem);
            if(state.taskStatus == this.taskStatus){
                dataFilted.add(dataItem);
            }
        }

        //排序
        Data[] dataArray = new Data[dataFilted.size()];
        dataArray = dataFilted.toArray(dataArray);
        Arrays.sort(dataArray, new Comparator<Data>() {
            @Override
            public int compare(Data data, Data t1) {
                long time1 = data.start_time.getTime();
                long time2 = t1.start_time.getTime();
                switch (taskStatus){
                    case TASK_RUNNING:
                    case TASK_COMMING:
                        return (time1 - time2) < 0 ? -1 : 1;
                    case TASK_COMPLETED:
                        return (time2 - time1) < 0 ? -1 : 1;
                }
                return 0;
            }
        });

        //重新变成链表
        dataFilted.clear();
        for(Data dataItem : dataArray){
            dataFilted.add(dataItem);
        }

        return dataFilted;
    }
}
