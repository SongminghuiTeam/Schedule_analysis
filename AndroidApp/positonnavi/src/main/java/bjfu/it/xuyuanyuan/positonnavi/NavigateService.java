package bjfu.it.xuyuanyuan.positonnavi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

import bjfu.it.xuyuanyuan.positonnavi.Service.GetPositonService;

public class NavigateService {

    public void startNavi(Context context, String start, String end, double a1, double b1, double a2, double b2) {
        Poi startP = new Poi(start, new LatLng(a1, b1), "");
        Poi endP = new Poi(end, new LatLng(a2, b2), "");
        AmapNaviParams params = new AmapNaviParams(startP, null, endP, AmapNaviType.DRIVER);
        params.setUseInnerVoice(true);
        params.setMultipleRouteNaviMode(true);
        params.setNeedDestroyDriveManagerInstanceWhenNaviExit(true);
        //发起导航
        AmapNaviPage.getInstance().showRouteActivity(context, new AmapNaviParams(startP, null, endP, AmapNaviType.DRIVER), null);
    }

    /*通过地名进行导航*/
    public void navigate(final Context context,final String start, final String end) {
        GetPositonService service1 = new GetPositonService(context);
        service1.getLongLat(start, context, new GetPositonService.OperateInPositon() {
            @Override
            public void getDefaultAddress(String adderssText, final LatLonPoint startPoint) {
                if(startPoint != null){
                    GetPositonService service2 = new GetPositonService(context);
                    service2.getLongLat(end, context, new GetPositonService.OperateInPositon() {
                        @Override
                        public void getDefaultAddress(String adderssText, LatLonPoint endPoint) {
                            if(endPoint != null){
                                startNavi(context, start, end, startPoint.getLatitude(), startPoint.getLongitude(),
                                        endPoint.getLatitude(), endPoint.getLongitude());
                            }else{
                                Log.v("导航错误", "结束地点有误");
                            }
                        }
                    });
                }else{
                    Log.v("导航错误", "开始地点有误");
                }
            }
        });
    }

    /*通过地名进行导航*/
    public void navigate(final Context context, final double a1, final double b1, final double a2, final double b2) {
        GetPositonService service1 = new GetPositonService(context);
        service1.getAddressText(a1 + "," + b1, new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if (i == 1000) {
                    if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {
                        Log.v("导航逆编码中1", "逆编码成功1");
                        final String start = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                        GetPositonService service2 = new GetPositonService(context);
                        service2.getAddressText(a2 + "," + b2, new GeocodeSearch.OnGeocodeSearchListener() {
                            @Override
                            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                                if (i == 1000) {
                                    if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {
                                        Log.v("导航逆编码中2", "逆编码成功2");
                                        String end = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                                        startNavi(context, start, end, a1, b1, a2, b2);
                                    } else {
                                        Log.v("导航逆编码中2", "逆编码成功但结果失败2");
                                    }
                                } else {
                                    Log.v("导航逆编码中2", "逆编码失败2");
                                }
                            }
                            @Override
                            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                            }
                        });
                    } else {
                        Log.v("导航逆编码中1", "逆编码成功但结果失败1");
                    }
                } else {
                    Log.v("导航逆编码中1", "逆编码失败1");
                }
            }
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
    }

    /*将经纬度传入参数代开高德APP进行导航*/
    public void startAPP(Context context, double a1, double b1, double a2, double b2) {
        String slat = String.valueOf(a1), slon = String.valueOf(b1);
        String dlat = String.valueOf(a2), dlon  = String.valueOf(b2);
        if (isInstallApp(context)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setPackage("com.autonavi.minimap");
            String uri = "androidamap://route?" + "sourceApplication=" + context.getString(R.string.app_name);
            //如果设置了起点
            if (!TextUtils.isEmpty(slat) && !TextUtils.isEmpty(slon)) {
                uri += "&slat=" + slat + "&slon=" + slon;
            }
            uri += "&dlat=" + dlat +
                    "&dlon=" + dlon +
                    "&dev=" + 0 +
                    "&t=" + 0 +
                    "&t=" + 0;
            intent.setData(Uri.parse(uri));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity(intent);
        } else {
            String uri = "https://uri.amap.com/navigation?";
            //如果设置了起点
            if (!TextUtils.isEmpty(slat) && !TextUtils.isEmpty(slon)) {
                uri += "from=" + slon + "," + slat + ",起点";
            }
            uri += "&to=" + dlon + "," + dlat + ",终点" +
                    "&mode=car";
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(uri));
            context.startActivity(intent);
        }
    }

    //将地址名称度传入参数代开高德APP进行导航
    public void startAPP(final Context context, String start, final String end){
        {
            GetPositonService service1 = new GetPositonService(context);
            service1.getLongLat(start, context, new GetPositonService.OperateInPositon() {
                @Override
                public void getDefaultAddress(String adderssText, final LatLonPoint startPoint) {
                    if(startPoint != null){
                        GetPositonService service2 = new GetPositonService(context);
                        service2.getLongLat(end, context, new GetPositonService.OperateInPositon() {
                            @Override
                            public void getDefaultAddress(String adderssText, LatLonPoint endPoint) {
                                if(endPoint != null){
                                    startAPP(context,
                                            startPoint.getLatitude(), startPoint.getLongitude(),
                                            endPoint.getLatitude(), endPoint.getLongitude());
                                }else{
                                    Log.v("导航错误", "结束地点有误");
                                }
                            }
                        });
                    }else{
                        Log.v("导航错误", "开始地点有误");
                    }
                }
            });
        }
    }


     // 检测高德应用是否安装
    public boolean isInstallApp(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("com.autonavi.minimap", 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }
}
