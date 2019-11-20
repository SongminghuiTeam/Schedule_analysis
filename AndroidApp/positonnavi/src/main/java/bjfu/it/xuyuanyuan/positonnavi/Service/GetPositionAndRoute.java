package bjfu.it.xuyuanyuan.positonnavi.Service;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

/*此类调用GetRouteService和GetPosition服务*/
public class GetPositionAndRoute {
    Application application;
    Context context;
    Activity activity;
    LocationManager locationManager;
    AMap aMap;

    /**
     * @param serviceType 选择服务类型:0:路线规划；1：地址转换
     * @param mode        路线规划下的规划模式——0（公交，默认），1（步行），2（自驾）
     *                    地址转换下的服务类型——0（由经纬得到文本地址），1（由文本地址得到经纬度），2（得到当前地址）
     * @param info        字符串类型数据
     *                    路线规划中为经纬数据：格式为——35.543532,325435,40.345678,116.98789（前纬度，后经度，两个点的经纬数据，中间没有空格，只有一个逗号）
     *                    地址转换：
     *                    经纬转文本地址中为——35.543532,325435（前纬度，后经度，中间没有空格，只有一个逗号）
     *                    文本转经纬中为——文本地址
     *                    得到当前经纬——null（此操作会同时对PositionAndRouteData的defaultAddress好defaultPoint进行操作）
     * @param aMap        地址转换服务下为null
     * @param locationManager   用于查看计算当前默认地址
     */

    public GetPositionAndRoute(Activity activity, Application application, final Context context, int serviceType, int mode, final String info, AMap aMap, LocationManager locationManager) {
        this.activity = activity;
        this.application = application;
        this.context = context;
        this.locationManager = locationManager;
        switch (serviceType) {
//          路线规划服务(三种测试成功)
            case 0: {
                double startLat = Double.parseDouble(info.split(",")[0]);
                double startLong = Double.parseDouble(info.split(",")[1]);
                double endLat = Double.parseDouble(info.split(",")[2]);
                double endLong = Double.parseDouble(info.split(",")[3]);
                LatLonPoint startPoint = new LatLonPoint(startLat, startLong);
                LatLonPoint endPoint = new LatLonPoint(endLat, endLong);
                switch (mode) {
//                  公交
                    case 0: getBusRouteInfo(startPoint, endPoint, aMap);
                        break;
//                  步行
                    case 1: getWalkRouteInfo(startPoint, endPoint, aMap);
                        break;
//                  自驾
                    case 2: getDriveRouteInfo(startPoint, endPoint, aMap);
                        break;
                }
            }
            break;
//          地址服务
            case 1: {
                switch (mode) {
//                  经纬->文本
                    case 0:{
                        double Lat = Double.parseDouble(info.split(",")[0]);
                        double Long = Double.parseDouble(info.split(",")[1]);
                        LatLonPoint point = new LatLonPoint(Lat, Long);
                        GetPositonService getPositonService = new GetPositonService(context);
                        getPositonService.getAddressText(info, new GeocodeSearch.OnGeocodeSearchListener() {
                            @Override
                            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                                Log.v(info, "对应地址为：" + regeocodeResult.getRegeocodeAddress().getFormatAddress());
                                Toast.makeText(context, info + "对应地址为" +
                                        regeocodeResult.getRegeocodeAddress().getFormatAddress(), Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                            }
                        });
                    }
                    break;
//                  文本->经纬
                    case 1:{
                        GetPositonService getPositonService = new GetPositonService(context);
                        getPositonService.getLongLat(info, context, new GetPositonService.OperateInPositon() {
                            @Override
                            public void getDefaultAddress(String adderssText, LatLonPoint latLonPoint) {

                            }
                        });

                    }
                        break;
//                  当前地址
                    case 2:{
                        GetPositonService getPositonService = new GetPositonService(context);
                        getPositonService.getLocation(locationManager, context);
                        Log.v("当前地址为", PositonRouteData.defaultAddress
                                + ", " + PositonRouteData.defaultPoint);
                        Toast.makeText(context,"" + PositonRouteData.defaultAddress
                                + ", " + PositonRouteData.defaultPoint, Toast.LENGTH_SHORT);
                    }
                        break;
                }
            }
            break;
            default: break;
        }
    }

    /*调用GetRouteService函数*/
    public void getBusRouteInfo(LatLonPoint startPoint, LatLonPoint endPoint, AMap aMap) {
        GetRouteService getRouteService = new GetRouteService(application, context,
                startPoint, endPoint, aMap);
        getRouteService.getBusRoute(new GetRouteService.OnPlanningCompletedListener() {
            @Override
            public void done(int minutes) {
                PositonRouteData.bus_time = minutes;
//                Log.v("PositonRouteData", "bus_time = " + PositonRouteData.bus_time);
                Toast.makeText(context, "公交规划耗时：" + PositonRouteData.bus_time, Toast.LENGTH_SHORT);
            }
        });
    }

    /*调用GetRouteService函数*/
    public void getDriveRouteInfo(LatLonPoint startPoint, LatLonPoint endPoint, AMap aMap) {
        GetRouteService getRouteService = new GetRouteService(application, context,
                startPoint, endPoint, aMap);
        getRouteService.getDriveRoute(new GetRouteService.OnPlanningCompletedListener() {
            @Override
            public void done(int minutes) {
                PositonRouteData.drive_time = minutes;
                Log.v("PositonRouteData", "drive_time = " + PositonRouteData.drive_time);
                Toast.makeText(context, "自驾规划耗时：" + PositonRouteData.drive_time, Toast.LENGTH_SHORT);
            }
        });
    }

    /*调用GetRouteService函数*/
    public void getWalkRouteInfo(LatLonPoint startPoint, LatLonPoint endPoint, AMap aMap) {
        GetRouteService getRouteService = new GetRouteService(application, context,
                startPoint, endPoint, aMap);
        getRouteService.getWalkRoute(new GetRouteService.OnPlanningCompletedListener() {
            @Override
            public void done(int minutes) {
                PositonRouteData.walk_time = minutes;
                Log.v("PositonRouteData", "walk_time = " + PositonRouteData.walk_time);
                Toast.makeText(context, "步行规划耗时：" + PositonRouteData.walk_time, Toast.LENGTH_SHORT);
            }
        });
    }

}
