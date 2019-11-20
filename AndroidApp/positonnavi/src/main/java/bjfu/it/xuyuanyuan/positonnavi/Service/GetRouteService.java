package bjfu.it.xuyuanyuan.positonnavi.Service;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteBusWalkItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.FromAndTo;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;

import java.util.List;

import bjfu.it.xuyuanyuan.positonnavi.OverLay.MyBusRouteOverlay;
import bjfu.it.xuyuanyuan.positonnavi.OverLay.MyDriveRouteOverlay;
import bjfu.it.xuyuanyuan.positonnavi.OverLay.MyWalkRouteOverlay;

public class GetRouteService {
    private Application application;
    private Context mContext;
    AMap aMap;
    /*开始的经纬度*/
    private LatLonPoint startPoint;
    /*目的地经纬度*/
    private LatLonPoint endPoint;
    /*// 西单广场
    LatLonPoint startPoint = new LatLonPoint(39.908127, 116.375257);
    // 方恒国际中心
    LatLonPoint endPoint = new LatLonPoint(39.990467, 116.48148);*/
    /*中间路径*/

    public int busTime = 0;/*为0时表示公交车规划失败，但是在*/
    public String BusRouteLine = "";
    public BusRouteResult mBusRouteResult;

    public int driveTime = 0;
    public String DriveRouteLine = "";
    public DriveRouteResult mDriveRouteResult;

    public int walkTime = 0;
    public String WalkRouteLine = "";
    public WalkRouteResult mWalkRouteResult;

    public FromAndTo fromAndTo;
    public final int ResultCode = 1000;
    RouteSearch mRouteSearch;

    public GetRouteService(Object application, Context context, LatLonPoint startPoint, LatLonPoint endPoint, AMap aMap) {
        this.application = (Application) application;
        this.mContext = context;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.aMap = aMap;
    }

    public void getBusRoute(OnPlanningCompletedListener onPlanningCompletedListener) {
        mRouteSearch = new RouteSearch(application);
        // 西单广场
        //LatLonPoint startPoint = new LatLonPoint(39.908127, 116.375257);
        // 方恒国际中心
        //LatLonPoint endPoint = new LatLonPoint(39.990467, 116.48148);
        FromAndTo fromAndTo = new FromAndTo(startPoint, endPoint);
        BusRouteQuery busRouteQuery = new BusRouteQuery(fromAndTo,
                RouteSearch.BUS_DEFAULT, "010", 0);
        //第三个参数："010"城市名称/城市区号/电话区号，此项不能为空；当进行跨城查询时，该参数对应起点的城市
        mRouteSearch.setRouteSearchListener(getSearchListener(onPlanningCompletedListener, aMap));

        mRouteSearch.calculateBusRouteAsyn(busRouteQuery);
        Log.v("", "calculateBusRouteAsyn(busRouteQuery)结束");
    }

    public void getDriveRoute(OnPlanningCompletedListener onPlanningCompletedListener) {
        mRouteSearch = new RouteSearch(application);
        // 西单广场
        // 方恒国际中心
        FromAndTo fromAndTo = new FromAndTo(startPoint, endPoint);
        DriveRouteQuery driveRouteQuery = new DriveRouteQuery(fromAndTo, RouteSearch.BUS_DEFAULT,
                null, null, "");
        //第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，
        //第四个参数表示避让区域，第五个参数表示避让道路
        mRouteSearch.setRouteSearchListener(getSearchListener(onPlanningCompletedListener, aMap));
        mRouteSearch.calculateDriveRouteAsyn(driveRouteQuery);
        Log.v("", "getDriveRoute()结束, driveTime = " + driveTime);
    }

    public void getWalkRoute(OnPlanningCompletedListener onPlanningCompletedListener) {
        mRouteSearch = new RouteSearch(application);
        FromAndTo fromAndTo = new FromAndTo(startPoint, endPoint);
        WalkRouteQuery driveRouteQuery = new WalkRouteQuery(fromAndTo, RouteSearch.WALK_MULTI_PATH);
        /*/*第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，
         *第四个参数表示避让区域，第五个参数表示避让道路*/
        mRouteSearch.setRouteSearchListener(getSearchListener(onPlanningCompletedListener, aMap));
        mRouteSearch.calculateWalkRouteAsyn(driveRouteQuery);
        Log.v("", "getWalkRoute()结束, walkTime = " + walkTime);
    }

    private OnRouteSearchListener getSearchListener(final OnPlanningCompletedListener onPlanningCompletedListener,
                                                    final AMap aMap) {
        return new OnRouteSearchListener() {

            @Override
            public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
                // TODO Auto-generated method stub
                Log.v("开始行走搜索", "开始行走搜索");
                processWalkSearchResult(arg0, arg1, onPlanningCompletedListener);
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
                Log.v("汽车搜索", "汽车搜索");
            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult arg0, int arg1) {
                Log.v("开车搜索", "开车搜索");
                processDriveSearchResult(arg0, arg1, onPlanningCompletedListener);
            }

            @Override
            public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
//                Log.v("公交搜索开始", "公交搜索开始");
                processBusSearchResult(arg0, arg1, onPlanningCompletedListener, aMap);
//                Log.v("公交搜索结束", "公交搜索结束");
            }
        };
    }

    private void processBusSearchResult(BusRouteResult busRouteResult, int resultCode,
                                        OnPlanningCompletedListener onPlanningCompletedListener, AMap aMap) {
        Log.v("", "resultCode的值为：" + resultCode);
        if (resultCode == ResultCode) {
            busTime = 0;
            if (busRouteResult != null && busRouteResult.getPaths() != null
                    && busRouteResult.getPaths().size() > 0) {
                mBusRouteResult = busRouteResult;
                // 以推荐线路的第一条数据为例进行处理
                BusPath busPath = busRouteResult.getPaths().get(0);


                if (aMap != null) {
                    aMap.clear();// 清理地图上的所有覆盖物
                    //第一个参数是context，2.是地图,3.公交线路，4,5参数没找到，api上面没有，连相关的方法都没看到
                    MyBusRouteOverlay routeOverlay = new MyBusRouteOverlay(mContext, aMap,
                            busPath, busRouteResult.getStartPos(),
                            busRouteResult.getTargetPos());
                    routeOverlay.removeFromMap();//去掉BusRouteOverlay上所有的Marker
                    routeOverlay.addToMap();//添加公交线路到地图
                    routeOverlay.zoomToSpan();//移动镜头当前视角
                }


                // 分别获取公交线路距离，步行距离，整个线路距离
                String routeInfo = "公交路线长度：" + busPath.getBusDistance()
                        + "  步行长度" + busPath.getWalkDistance() + "  线路长度："
                        + busPath.getDistance() + "\n";

                Log.v("整个公交规划", routeInfo + "\n\n");
                List<BusStep> busSteps = busPath.getSteps();
                // 获取每一段换乘所需的步行距离，起始终止站点，经过的站数（不包括起始和终点站），距离和所需时间
                for (BusStep busStep : busSteps) {
                    if (busStep.getWalk() != null) {
                        RouteBusWalkItem walkPath = busStep.getWalk();
                        routeInfo = routeInfo + "需要步行大约"
                                + Math.round(walkPath.getDuration() / 60)
                                + "分钟，步行" + walkPath.getDistance() + "米\n";
//                        Log.v("需要在公交路线中步行信息：", routeInfo);
                        busTime += Math.round(walkPath.getDuration() / 60);
//                        Log.v("", "在GetRouteService的步行中，busTime = " + busTime);
                    }
                    if (busStep.getBusLines() != null && busStep.getBusLines().size() > 0) {
//                        RouteBusLineItem busLineItem = busStep.getBusLine();
                        RouteBusLineItem busLineItem = busStep.getBusLines().get(0);
                        routeInfo = routeInfo + "乘坐" + busLineItem.getBusLineName()
                                + "需要大约" + Math.round(busLineItem.getDuration() / 60)
                                + "分钟，大约" + busLineItem.getDistance()
                                + "米，经过" + busLineItem.getPassStationNum()
                                + "站，从" + busLineItem.getDepartureBusStation().getBusStationName()
                                + "上车，从" + busLineItem.getArrivalBusStation().getBusStationName() + "下车\n";
                        busTime += Math.round(busLineItem.getDuration() / 60);
//                        Log.v("", "在GetRouteService的乘车中，busTime = " + busTime);
                    }
                }
                Log.v("", "在公交路线中公交站信息：" + routeInfo);
                onPlanningCompletedListener.done(busTime);
                BusRouteLine += routeInfo;
                Log.v("公交路线总时间", "processBusSearchResult中busTime为：" + busTime);
            }
        } else {
            Log.v("没有搜索到公交路线", "没有搜索到公交路线");
        }
        Log.v("公交路线总时间", "GetRouteService中getBusRoute()函数的结束中总时间：" + busTime);
    }

    /**/
    private void processDriveSearchResult(DriveRouteResult driveRouteResult, int resultCode,
                                          OnPlanningCompletedListener onPlanningCompletedListener) {
        Log.v("自驾规划中", "resultCode的值为--" + resultCode);
        if (resultCode == ResultCode) {
            driveTime = 0;
            if (driveRouteResult != null && driveRouteResult.getPaths() != null &&
                    driveRouteResult.getPaths().size() > 0) {
                driveRouteResult = driveRouteResult;
                DrivePath drivePath = driveRouteResult.getPaths().get(0);

                if (aMap != null) {
                    aMap.clear();// 清理地图上的所有覆盖物
                    MyDriveRouteOverlay drivingRouteOverlay = new MyDriveRouteOverlay(
                            mContext, aMap, drivePath, driveRouteResult.getStartPos(),
                            driveRouteResult.getTargetPos());
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                }

                String driveRouteInfo = "自驾策略: " + drivePath.getStrategy()
                        + ", 距离：" + drivePath.getDistance()
                        + ", 经过的十字路口数目: " + drivePath.getTotalTrafficlights()
                        + ", 驾车耗时: " + Math.round(drivePath.getDuration() / 60) + "分钟\n";
                Log.v("综合自驾行驶信息", driveRouteInfo);
                driveTime = Math.round(drivePath.getDuration() / 60);
                onPlanningCompletedListener.done(driveTime);

                List<DriveStep> driveSteps = drivePath.getSteps();
                driveRouteInfo = "";
                for (DriveStep driveStep : driveSteps) {
                    driveTime += Math.round(driveStep.getDuration() / 60);
                    Log.v("改变的driveTime", String.valueOf(driveTime));
                    driveRouteInfo += ("路段信息: getRoad()--" + driveStep.getRoad()
                            + ", 执行动作--" + driveStep.getAction()
                            + ", 耗时--" + Math.round(driveStep.getDuration() / 60));
                    driveRouteInfo += "\n";
                }
                Log.v("需要在自驾路线中信息", driveRouteInfo);
            } else {
                Toast.makeText(mContext, "对不起，没查询到自驾结果", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.v("自驾搜索失败", "自驾搜索失败");
        }
        Log.v("自驾路线总时间", "processDriveSearchResult()函数的结束中总时间：" + driveTime);
    }

    /**/
    private void processWalkSearchResult(WalkRouteResult walkRouteResult, int resultCode,
                                         OnPlanningCompletedListener onPlanningCompletedListener) {
        Log.v("", "步行规划中resultCode的值为" + resultCode);
        if (resultCode == ResultCode) {
            walkTime = 0;
            if (walkRouteResult != null && walkRouteResult.getPaths() != null && walkRouteResult.getPaths().size() > 0) {
                String walkRouteInfo = "null";
                if (walkRouteResult != null && walkRouteResult.getPaths() != null) {
                    if (walkRouteResult.getPaths().size() > 0) {
                        WalkRouteResult mWalkRouteResult = walkRouteResult;
                        final WalkPath walkPath = mWalkRouteResult.getPaths()
                                .get(0);

                        if (aMap != null) {
                            aMap.clear();// 清理地图上的所有覆盖物
                            MyWalkRouteOverlay walkRouteOverlay = new MyWalkRouteOverlay(mContext,
                                    aMap, walkPath, walkRouteResult.getStartPos(),
                                    walkRouteResult.getTargetPos());
                            walkRouteOverlay.removeFromMap();
                            walkRouteOverlay.addToMap();
                            walkRouteOverlay.zoomToSpan();
                        }

                        walkRouteInfo = "步行规划总信息:路程 " + walkPath.getDistance()
                                + "m，时间" + Math.round(walkPath.getDuration() / 60) + "分钟\n";
                        Log.v("步行规划综合信息", walkRouteInfo);
                        //回调时间
                        walkTime = Math.round(walkPath.getDuration() / 60);
                        onPlanningCompletedListener.done(Math.round(walkPath.getDuration() / 60));

                        walkRouteInfo = "";
                        List<WalkStep> walkSteps = walkPath.getSteps();
                        for (WalkStep walkStep : walkSteps) {
                            walkRouteInfo += (
                                    (walkStep.getRoad() != null && walkStep.getRoad().length() > 0 ? ("在" + walkStep.getRoad()) : "")
                                            + (walkStep.getAction() != null && walkStep.getAction().length() > 0 ? walkStep.getAction() : "")
                                            + (walkStep.getInstruction() != null && walkStep.getInstruction().length() > 0 ? (", " + walkStep.getInstruction()) : "")
                                            + (Math.round(walkStep.getDuration() / 60) != 0 ?
                                            (", 步行" + Math.round(walkStep.getDuration() / 60) + "分钟\n") : "")
                            );
//                            Log.v("单程步行信息", walkRouteInfo);
//                            walkTime += Math.round(walkStep.getDuration() / 60);
                        }
                        Log.v("步行规划信息", walkRouteInfo);
                    }
                }
            } else {
                Log.v("没有步行规化结果", "对不起，没查询到步行规划结果");
            }
            Log.v("步行路线总时间", "processWalkSearchResult()函数的结束中总时间：" + walkTime);
        }
    }

    //路径时间获取接口
    public static interface OnPlanningCompletedListener {
        void done(int minutes);
    }

}
