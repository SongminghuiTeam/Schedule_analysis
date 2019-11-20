package bjfu.it.xuyuanyuan.positonnavi;


import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteBusWalkItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.FromAndTo;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;

import java.util.List;

import bjfu.it.xuyuanyuan.positonnavi.Service.GetPositonService;
import bjfu.it.xuyuanyuan.positonnavi.Service.GetRouteService;
import bjfu.it.xuyuanyuan.positonnavi.Service.PositonRouteData;

//import com.amap.api.maps.overlay.BusRouteOverlay;

/*实现了路线规划显示*/
public class TestRouteActivity extends Activity {

    Button button;
    TextView textView;
    MapView mapView;
    private AMap aMap;
    private Marker geoMarker, regeoMarker;// 锚点,在地图上进行绘制的点

    private RouteSearch mRouteSearch;
    private final int ResultCode = 1000;/*当公交的路线规划中的resultCode为1000时，就表示路径规划成功了*/

    //北航(39.9860300000,116.3440500000)，北林(40.0019900000,116.3449400000)，人大(39.9710550000,116.3135980000)
    public static int walk_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_route);
        mapView = (MapView) findViewById(R.id.test_route_map);
        mapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mapView.getMap();
            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));// 将geoMarker设置成蓝色
            regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));// 红色
        }
        init();
    }

    private void init() {
        button = (Button) findViewById(R.id.routesearchbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getBusRouteInfo(v);
//                getDriveRouteInfo(v);
                getWalkRouteInfo(v);
            }
        });
        textView = (TextView) findViewById(R.id.routeinfo);
    }

    public void clickOtherInfo(View view) {
        /*Button button = (Button)view;
        button.setText("walik_time = " + PositonRouteData.walk_time);*/
        /*GetPositionAndRoute gPAR = new GetPositionAndRoute(this, (Application) getApplicationContext(),
                this, 0, 0, "39.9710550000,116.313598,40.0019900000,116.34494",
                aMap, (LocationManager) getSystemService(Context.LOCATION_SERVICE));*/
        /*Toast.makeText(this, "bus_time : " + PositonRouteData.bus_time, Toast.LENGTH_SHORT);
        Log.v("Activity中","PositonRouteData.bus_time = " + PositonRouteData.bus_time);*/
//        Log.v("Activity中", "自驾时间为: " + PositonRouteData.drive_time);
        GetPositonService getPositonService = new GetPositonService(this);
        getPositonService.getDefaultLocation((LocationManager)getSystemService(Context.LOCATION_SERVICE),
                this, new GetPositonService.OperateInPositon() {
            @Override
            public void getDefaultAddress(String adderssText, LatLonPoint latLonPoint) {
                Log.v("默认地址信息", adderssText + "--" +
                        latLonPoint.getLatitude() + ", " + latLonPoint.getLongitude());
            }
        });
    }

    /*调用GetRouteService函数*/
    public void getBusRouteInfo(View view) {
        GetRouteService getRouteService = new GetRouteService(getApplicationContext(), this,
                new LatLonPoint(39.970503, 116.313165), new LatLonPoint(40.005875, 116.347459), aMap);
        //getRouteService.getBusRoute();
        Log.v("公交路线总时间", "TestRouteActivity中公交路线总时间：" + getRouteService.busTime);
//        textView.setText(getRouteService.BusRouteLine + "\n总耗时：" + getRouteService.busTime);
        getRouteService.getBusRoute(new GetRouteService.OnPlanningCompletedListener() {
            @Override
            public void done(int minutes) {
                textView.setText("乘坐公交耗时" + minutes + "分钟");
            }
        });
    }

    /*调用GetRouteService函数*/
    public void getDriveRouteInfo(View view) {
        GetRouteService getRouteService = new GetRouteService(getApplicationContext(), this,
                new LatLonPoint(39.970503, 116.313165), new LatLonPoint(40.005875, 116.347459), aMap);
        getRouteService.getDriveRoute(new GetRouteService.OnPlanningCompletedListener() {
            @Override
            public void done(int minutes) {
                textView.setText("自驾耗时" + minutes + "分钟");
            }
        });
    }

    /*调用GetRouteService函数*/
    public void getWalkRouteInfo(View view) {
        GetRouteService getRouteService = new GetRouteService(getApplicationContext(), this,
                new LatLonPoint(40.00199, 116.34494), new LatLonPoint(39.971055, 116.313598), aMap);
        getRouteService.getWalkRoute(new GetRouteService.OnPlanningCompletedListener() {
            @Override
            public void done(int minutes) {
                Log.v("时间", minutes + "");
                walk_time = minutes;
                Log.v("时间walk_time", walk_time + "");
                PositonRouteData.walk_time = minutes;
                textView.setText("步行耗时" + minutes + "分钟");
            }
        });
        Toast.makeText(this,"LongLat:" + PositonRouteData.latLonPoint
                + ", address: " + PositonRouteData.address +", defaultAddress: "
                        + PositonRouteData.defaultAddress + ", defaultLongLat: "
                +PositonRouteData.defaultPoint
                , Toast.LENGTH_SHORT);
    }

    public void get_route(View view) {
        mRouteSearch = new RouteSearch(getApplicationContext());
        // 西单广场
        LatLonPoint startPoint = new LatLonPoint(39.908127, 116.375257);
        // 方恒国际中心
        LatLonPoint endPoint = new LatLonPoint(39.990467, 116.48148);
        FromAndTo fromAndTo = new FromAndTo(startPoint, endPoint);
        BusRouteQuery busRouteQuery = new BusRouteQuery(fromAndTo,
                RouteSearch.BUS_DEFAULT, "010", 0);
        mRouteSearch.setRouteSearchListener(getSearchListener());

        mRouteSearch.calculateBusRouteAsyn(busRouteQuery);
        Log.v("", "calculateBusRouteAsyn(busRouteQuery)结束");
    }

    /*仅仅被get_route引用*/
    private OnRouteSearchListener getSearchListener() {
        return new OnRouteSearchListener() {

            @Override
            public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
                // TODO Auto-generated method stub
                Log.v("行走搜索", "行走搜索");
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
                Log.v("汽车搜索", "汽车搜索");
            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult arg0, int arg1) {
                Log.v("开车搜索", "开车搜索");
            }

            @Override
            public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
//                Log.v("公交搜索开始", "公交搜索开始");
                processBusSearchResult(arg0, arg1);
//                Log.v("公交搜索结束", "公交搜索结束");
            }
        };
    }

    /*仅仅被上面的private OnRouteSearchListener getSearchListener()引用*/
    private void processBusSearchResult(BusRouteResult busRouteResult,
                                        int resultCode) {
        Log.v("", "resultCode的值为：" + resultCode);
        if (resultCode == ResultCode) {
            if (busRouteResult != null && busRouteResult.getPaths() != null
                    && busRouteResult.getPaths().size() > 0) {
                // 以推荐线路的第一条数据为例进行处理
                BusPath busPath = busRouteResult.getPaths().get(0);
                // 分别获取公交线路距离，步行距离，整个线路距离
                String routeInfo = "公交路线长度：" + busPath.getBusDistance()
                        + "  步行 长度" + busPath.getWalkDistance() + "  线路长度："
                        + busPath.getDistance() + "\n";

                Log.v("公交规划", routeInfo);
                List<BusStep> busSteps = busPath.getSteps();
                // 获取每一段换乘所需的步行距离，起始终止站点，经过的站数（不包括起始和终点站），距离和所需时间
                for (BusStep busStep : busSteps) {
                    if (busStep.getWalk() != null) {
                        RouteBusWalkItem walkPath = busStep.getWalk();
                        routeInfo = routeInfo + "需要步行大约"
                                + Math.round(walkPath.getDuration() / 60)
                                + "分钟，步行" + walkPath.getDistance() + "米\n";
                        Log.v("需要在公交路线中步行信息：", routeInfo);
                    }
                    if (busStep.getBusLines() != null && busStep.getBusLines().size() > 0) {
//                        RouteBusLineItem busLineItem = busStep.getBusLine();
                        RouteBusLineItem busLineItem = busStep.getBusLines().get(0);
                        routeInfo = routeInfo
                                + "乘坐"
                                + busLineItem.getBusLineName()
                                + "需要大约"
                                + Math.round(busLineItem.getDuration() / 60)
                                + "分钟，大约"
                                + busLineItem.getDistance()
                                + "米，经过"
                                + busLineItem.getPassStationNum()
                                + "站，从"
                                + busLineItem.getDepartureBusStation()
                                .getBusStationName()
                                + "上车，从"
                                + busLineItem.getArrivalBusStation()
                                .getBusStationName() + "下车\n";
                        Log.v("需要在公交路线中公交站信息：", routeInfo);
                    }

                }
                textView.setText(routeInfo);
            }
        } else {
            Log.v("没有搜索到公交路线", "没有搜索到公交路线");
            Toast.makeText(this, "没有搜索到公交路线", Toast.LENGTH_SHORT);
        }

    }

    //----------------------------------------------------------------------------------//


    /*调用GetPosition根据经纬找文本地址*/
    public void onClick1(View view) {
        /*由于将GeocodeSearch.OnGeocodeSearchListener()作为参数，onRegeocodeSearched不在主线程里，所以不能
         **在一次调用getAddressText(主线程)中就得到addressText结果——主线程和onRegeocodeSearched不在一个线程
         * 而控件id可调用post将线程与主线程进行汇合*/
        final GetPositonService getPositonService = new GetPositonService(this);
        getPositonService.getAddressText("36.001073,116.336873",
                new GeocodeSearch.OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                        /*post将线程调回主线程，只有主线程才可以对UI界面进行操作*/
                        button.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                    }
                });
    }

    /*找到当前地址*/
    public void onClick2(View view) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GetPositonService getPositonService = new GetPositonService(this);
        button.setText(getPositonService.getLocation(locationManager, this));
    }

    /*根据地址找经纬*/
    public void onClick3(View view) {
        /*Button button = (Button)view;
        Intent intent = new Intent(MainActivity.this, TestActivity.class);
        startActivity(intent);*/
        final Button button = (Button) view;
        final GetPositonService getPositonService = new GetPositonService(this);
        String address = "中国人民大学";
        /*getPositonService.getLongLat(address, this, new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                String info = "111111111111";
                getPositonService.addressText = address.getFormatAddress();
                info = address.getFormatAddress() + "对应经纬度为：" + address.getLatLonPoint().getLatitude() + "," + address.getLatLonPoint().getLongitude();
                button.setText(info);
            }
        });*/
    }


    // -----------------------生命周期必须重写的方法-----------------------------

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}