package bjfu.it.xuyuanyuan.customview.Filter;

import com.cloudea.basemodule.Data;

import java.util.List;

//过滤器模式
public interface TaskFilter{
    List<Data> doFilter(List<Data> data);
}
