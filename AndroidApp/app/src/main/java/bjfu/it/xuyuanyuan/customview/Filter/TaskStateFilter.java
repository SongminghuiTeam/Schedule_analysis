package bjfu.it.xuyuanyuan.customview.Filter;

import com.cloudea.basemodule.Data;

import java.util.LinkedList;
import java.util.List;

public class TaskStateFilter implements TaskFilter {
    boolean state;

    public TaskStateFilter(boolean state){
        this.state = state;
    }

    @Override
    public List<Data> doFilter(List<Data> data) {
        List<Data> dataFilted = new LinkedList<>();

        for(Data dataItem : data){
            if(dataItem.state == this.state){
                dataFilted.add(dataItem);
            }
        }

        return dataFilted;
    }
}
