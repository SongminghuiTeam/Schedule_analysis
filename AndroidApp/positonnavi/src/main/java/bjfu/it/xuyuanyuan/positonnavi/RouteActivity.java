package bjfu.it.xuyuanyuan.positonnavi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.autonavi.tbt.TrafficFacilityInfo;


/*RouteActivity实现了事实导航
 * TestRouteActivity实现了三种路线规划的地图显示*/


/**
 * 在mAMapNavi成功实例化之后，就会调用onInitNaviSuccess函数，然后在onInitNaviSuccess，
 * 抵用mAMapNavi.calculateWalkRoute计算步行路线规划(传入起始地点)，路线计算成功之后会进入onCalculateRouteSuccess，
 * 此时就可以开始实时导航了
 mAMapNavi.startNavi(NaviType.GPS);
 */

public class RouteActivity extends Activity {

    public final int ResultCode = 1000;

    AMapNaviView mAMapNaviView;
    AMapNavi mAMapNavi;
    LatLonPoint start = new LatLonPoint(40.005875,116.347459);
    LatLonPoint end = new LatLonPoint(40.004973,116.357129);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        //获取 AMapNaviView 实例
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
//        mAMapNaviView.setAMapNaviViewListener(OnAMapNaviViewListener());
        mAMapNaviView.onCreate(savedInstanceState);
        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
//添加监听回调，用于处理算路成功
        mAMapNavi.addAMapNaviListener(OnAMapNaviListener());
    }

    public AMapNaviListener OnAMapNaviListener(){
        return new AMapNaviListener() {
            @Override
            public void onCalculateRouteSuccess(int[] ints) {
                Log.v("onCalculateRouteSuccess", "(int[] ints)路线规划成功");
//                onCalculateRouteSuccess(ints);
                mAMapNavi.startNavi(NaviType.GPS);
            }

            @Override
            public void onInitNaviSuccess() {
                Log.v("onInitNaviSuccess()", "AMapNavi对象初始化成功");
//                onInitNaviSuccess();
                mAMapNavi.calculateWalkRoute(new NaviLatLng(40.005875,116.347459),
                        new NaviLatLng(40.004973,116.357129));
//                LatLonPoint startPoint = new LatLonPoint(39.971055,116.313598);
//                // 方恒国际中心
//                LatLonPoint endPoint = new LatLonPoint(40.00199,116.34494);
//                FromAndTo fromAndTo = new FromAndTo(startPoint, endPoint);
//                boolean b = mAMapNavi.calculateDriveRoute(startPoint, endPoint, fromAndTo, DRIVING_MULTIPLE_PRIORITY_SPEED_COST_DISTANCE);
            }

            @Override
            public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
                Log.v("onCalculateRouteSuccess", "230--(int[] ints)路线规划成功");
                mAMapNavi.startNavi(NaviType.GPS);
            }


            @Override
            public void onInitNaviFailure() {
                Log.v("onInitNaviFailure()", "AMapNavi对象初始化失败");
            }
            @Override
            public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {
                Log.v("onCalculateRouteFailure", "237--(AMapCalcRouteResult aMapCalcRouteResult)路线规划失败");
            }


            @Override
            public void onStartNavi(int i) {
                Log.v("开始导航", "onStartNavi调用");
            }

            @Override
            public void onTrafficStatusUpdate() {

            }

            @Override
            public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

            }

            @Override
            public void onGetNavigationText(int i, String s) {

            }

            @Override
            public void onGetNavigationText(String s) {

            }

            @Override
            public void onEndEmulatorNavi() {

            }

            @Override
            public void onArriveDestination() {

            }

            @Override
            public void onCalculateRouteFailure(int i) {
                Log.v("onCalculateRouteFailure", "(int i)失败");
            }

            @Override
            public void onReCalculateRouteForYaw() {

            }

            @Override
            public void onReCalculateRouteForTrafficJam() {

            }

            @Override
            public void onArrivedWayPoint(int i) {

            }

            @Override
            public void onGpsOpenStatus(boolean b) {

            }

            @Override
            public void onNaviInfoUpdate(NaviInfo naviInfo) {

            }

            @Override
            public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

            }

            @Override
            public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

            }

            @Override
            public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

            }

            @Override
            public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

            }

            @Override
            public void showCross(AMapNaviCross aMapNaviCross) {

            }

            @Override
            public void hideCross() {

            }

            @Override
            public void showModeCross(AMapModelCross aMapModelCross) {

            }

            @Override
            public void hideModeCross() {

            }

            @Override
            public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

            }

            @Override
            public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

            }

            @Override
            public void hideLaneInfo() {

            }
            @Override
            public void notifyParallelRoad(int i) {

            }

            @Override
            public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

            }

            @Override
            public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

            }

            @Override
            public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

            }

            @Override
            public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

            }

            @Override
            public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

            }

            @Override
            public void onPlayRing(int i) {

            }
            @Override
            public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

            }
        };
    }

    public AMapNaviViewListener OnAMapNaviViewListener(){
        return new AMapNaviViewListener() {
            @Override
            public void onNaviSetting() {

            }

            @Override
            public void onNaviCancel() {

            }

            @Override
            public boolean onNaviBackClick() {
                return false;
            }

            @Override
            public void onNaviMapMode(int i) {

            }

            @Override
            public void onNaviTurnClick() {

            }

            @Override
            public void onNextRoadClick() {

            }

            @Override
            public void onScanViewButtonClick() {

            }

            @Override
            public void onLockMap(boolean b) {

            }

            @Override
            public void onNaviViewLoaded() {

            }

            @Override
            public void onMapTypeChanged(int i) {

            }

            @Override
            public void onNaviViewShowMode(int i) {

            }
        };
    }

    //----------------------------------------------生存周期函数重写---------------------------------
    /*** 接下来的四个方法必须要重写*/

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle = getIntent().getExtras();
        AMapNavi.getInstance(this).setAMapNaviListener(OnAMapNaviListener());
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
        AMapNavi.getInstance(this).removeAMapNaviListener(OnAMapNaviListener());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
    }

}
