package bjfu.it.xuyuanyuan.customview.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.cloudea.basemodule.BaseApplication;

import bjfu.it.xuyuanyuan.positonnavi.ClickLatlongActivity;
import bjfu.it.xuyuanyuan.positonnavi.NavigateService;
import bjfu.it.xuyuanyuan.positonnavi.Service.DynamicPermissions;
import bjfu.it.xuyuanyuan.positonnavi.Service.GetPositonService;
import bjfu.it.xuyuanyuan.positonnavi.Service.GetRouteService;


//单例模式
//适配器模式
public class MapAdapter {
    private static MapAdapter instance;
    public static MapAdapter getInstance(){
        if(instance == null){
            instance = new MapAdapter();
        }
        return instance;
    }


    private DynamicPermissions dynamicPermissions;
    private Context context;

    private MapAdapter(){
        dynamicPermissions = DynamicPermissions.getInstance();
        this.context = BaseApplication.getContext();
    }

    //用于程序开始时申请所有需要的权限
    public void requestPermissions(Activity activity){
        dynamicPermissions.privilege(activity);
    }

    //放在Activity对应函数中，用来代替默认的权限申请
    public boolean requestResults(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        return dynamicPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //功能一：得到当前位置的字符串描述
    public void getPositionString(final OnMapRequestFinished<String> onMapRequestFinished){
        new GetPositonService(context).getDefaultLocation((LocationManager) context.getSystemService(Context.LOCATION_SERVICE), context, new GetPositonService.OperateInPositon() {
            @Override
            public void getDefaultAddress(String adderssText, LatLonPoint latLonPoint) {
                if(onMapRequestFinished != null){
                    onMapRequestFinished.finish(adderssText);
                }
            }
        });
    }

    //功能二：得到当前位置到目的位置的时间（分钟）
    public void getPositionTime(final String destination, final OnMapRequestFinished<Integer> onMapRequestFinished){
        final GetPositonService getPositonService = new GetPositonService(context);
        getPositonService.getDefaultLocation((LocationManager) context.getSystemService(Context.LOCATION_SERVICE), context,
                new GetPositonService.OperateInPositon() {
            @Override
            public void getDefaultAddress(String adderssText, final LatLonPoint start) {
                GetPositonService gps = new GetPositonService(context);
                gps.getLongLat(destination, context, new GetPositonService.OperateInPositon() {
                    @Override
                    public void getDefaultAddress(String adderssText, LatLonPoint end) {
                        if(onMapRequestFinished != null){
                            if(end!= null){
                                GetRouteService gRS = new GetRouteService(context, context, start,
                                        end, null);
                                gRS.getBusRoute(new GetRouteService.OnPlanningCompletedListener() {
                                    @Override
                                    public void done(int minutes) {
                                        onMapRequestFinished.finish(minutes);
                                    }
                                });
                            }else{
                                onMapRequestFinished.finish(-1);
                            }
                        }
                    }
                });
            }
        });
    }

    //功能三：打开Activity，选择一个地址作为目的地址，得到其字符串表示
    public void getPositionOnMap(Activity activity, String addr, int requestCode){
        Intent intent = new Intent(activity, ClickLatlongActivity.class);
        intent.putExtra("addr", addr);
        activity.startActivityForResult(intent, requestCode);
    }

    //功能四：打开Activity，传入目的地点，开始导航
    public void startNavigating(final String addr){
        getPositionString(new OnMapRequestFinished<String>() {
            @Override
            public void finish(String result) {
                if(result != null){
                    NavigateService navigateService = new NavigateService();
                    if(navigateService.isInstallApp(context)){
                        navigateService.startAPP(context, result, addr);
                    }else{
                        navigateService.navigate(context, result, addr);
                    }
                }else{
                    Toast.makeText(context, "无法获得当前位置", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //适配器回调接口
    public static interface OnMapRequestFinished<T>{
        void finish(T result);
    }
}
