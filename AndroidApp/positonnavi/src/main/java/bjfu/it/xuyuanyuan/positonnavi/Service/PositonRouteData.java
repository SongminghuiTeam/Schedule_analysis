package bjfu.it.xuyuanyuan.positonnavi.Service;

import com.amap.api.services.core.LatLonPoint;

public class PositonRouteData {
    public PositonRouteData(){}

    public static int walk_time = 0;
    public static int drive_time = 0;
    public static int bus_time = 0;

    public static LatLonPoint latLonPoint = null;
    //    由文本地址转换得到的经纬度
    public static String address = null;
    //    由经纬度得来的文本地址
    public static LatLonPoint defaultPoint = null;
    //    当前地址的经纬
    public static String defaultAddress;
    //    当前地址的文本信息
}
