package bjfu.it.xuyuanyuan.customview.Filter;


import com.cloudea.basemodule.Data;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TaskRangeFilter implements TaskFilter{
    private int lower = 0;
    private int uppper = 1;
    public TaskRangeFilter(int lower, int uppper){
        this.lower = lower;
        this.uppper = uppper;
        if(uppper >= 0){
            this.uppper = uppper + 1;
        }
    }

    @Override
    public List<Data> doFilter(List<Data> data) {
        LinkedList<Data> dataFilted = new LinkedList<>();
        int day = 24 * 3600 * 1000;
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        long millionseconds1 = today.getTime() - 1L * day * lower;
        long millionseconds2 = today.getTime() + 1L * day * uppper;
        Date lowerDate = new Date(millionseconds1);
        Date upperDate = new Date(millionseconds2);
        for(Data dataItem : data){
            Date start = dataItem.start_time;
            boolean canPass = lower == -1 && uppper == -1
                    || lower == -1 && start.before(upperDate)
                    || uppper == -1 && start.after(lowerDate)
                    || start.after(lowerDate) && start.before(upperDate);
            if(canPass){
                dataFilted.add(dataItem);
            }
        }
        return dataFilted;
    }
}
