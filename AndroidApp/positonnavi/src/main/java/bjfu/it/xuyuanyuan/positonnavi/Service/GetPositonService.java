package bjfu.it.xuyuanyuan.positonnavi.Service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.List;

public class GetPositonService {
    public static String addressText = null;
    public static String LongLatitude = "GetPositonServiceLongLatiitude";
    public Context cthis;
    private final int requestCode = 123;


    public GetPositonService(Context mActivity) {
        cthis = mActivity;
    }

    /*由文本得知得到经纬度*/
    public void getLongLat(final String name, Context context, final OperateInPositon operateInPositon) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(context);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                if (i == 1000 && geocodeResult != null && geocodeResult.getGeocodeAddressList().size() > 0) {
                    LatLonPoint point = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
                    operateInPositon.getDefaultAddress(name, point);
                } else {
                    operateInPositon.getDefaultAddress(null, null);
                }
                ;
            }
        });
        GeocodeQuery query = new GeocodeQuery(name, null);
        // 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，或citycode、adcode，null为全国
        geocodeSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /*此处可对由经纬(字符串)得到的文本地址进行操作，在使用时new GeocodeSearch.OnGeocodeSearchListener即可*/
    public void getAddressText(String Latlng, GeocodeSearch.OnGeocodeSearchListener onGeocodeSearchListener) {
        LatLonPoint lp = new LatLonPoint(Double.parseDouble(Latlng.split(",")[0]),
                Double.parseDouble(Latlng.split(",")[1]));
        GeocodeSearch geocoderSearch = new GeocodeSearch(cthis);
//        geocoderSearch.setOnGeocodeSearchListener(this);
        geocoderSearch.setOnGeocodeSearchListener(onGeocodeSearchListener);

        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.GPS);
        geocoderSearch.getFromLocationAsyn(query);
        return;
    }

    /**
     * 返回的是经纬* @return
     */
    /*利用android原生定位功能进行经纬度定位,参数为系统位置管理器locationManager*/
    public String getLocation(final LocationManager locationManager, final Context context) {
        double latitude = 0.0;
        double longitude = 0.0;
        final Location[] location = new Location[1];

//        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            // Provider被enable时触发此函数，比如GPS被打开
            @Override
            public void onProviderEnabled(String provider) {
                locationManager.getLastKnownLocation(provider);
                location[0] = getBestLastLocation(locationManager, context);
            }

            // Provider被disable时触发此函数，比如GPS被关闭
            @Override
            public void onProviderDisabled(String provider) {
            }

            //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            @Override
            public void onLocationChanged(Location location) {
                location = getBestLastLocation(locationManager, context);
                if (location != null) {
                    Log.e("Map", "Location changed : Lat: "
                            + location.getLatitude() + " Lng: "
                            + location.getLongitude());
                    LongLatitude = "" + location.getLatitude() + ","
                            + location.getLongitude();
                }
            }
        };
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    3, locationListener);
            location[0] = getBestLastLocation(locationManager, context);
            if (location[0] != null) {
                latitude = location[0].getLatitude();
                longitude = location[0].getLongitude();
//                LongLatiitude = "" + latitude + "," + longitude;
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            location[0] = getBestLastLocation(locationManager, context);
            if (location[0] != null) {
                latitude = location[0].getLatitude(); //纬度
                longitude = location[0].getLongitude(); //经度
//                LongLatiitude = "" + latitude + "," + longitude;
            }
        }

        return ("" + latitude + "," + longitude);
    }


    /*利用android原生定位功能进行经纬度定位,参数为系统位置管理器locationManager
     * 返回的是LatLonPoint类型的经纬*/
    public LatLonPoint getLocationLatlng(final LocationManager locationManager, final Context context) {
        double latitude = 0.0;
        double longitude = 0.0;
        final Location[] location = new Location[1];

//        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            // Provider被enable时触发此函数，比如GPS被打开
            @Override
            public void onProviderEnabled(String provider) {
                locationManager.getLastKnownLocation(provider);
                location[0] = getBestLastLocation(locationManager, context);
            }

            // Provider被disable时触发此函数，比如GPS被关闭
            @Override
            public void onProviderDisabled(String provider) {
            }

            //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            @Override
            public void onLocationChanged(Location location) {
                location = getBestLastLocation(locationManager, context);
                if (location != null) {
                    Log.e("Map", "Location changed : Lat: "
                            + location.getLatitude() + " Lng: "
                            + location.getLongitude());
                    LongLatitude = "" + location.getLatitude() + ","
                            + location.getLongitude();
                }
            }
        };
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    3, locationListener);
            location[0] = getBestLastLocation(locationManager, context);
            if (location[0] != null) {
                latitude = location[0].getLatitude();
                longitude = location[0].getLongitude();
//                LongLatiitude = "" + latitude + "," + longitude;
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            location[0] = getBestLastLocation(locationManager, context);
            if (location[0] != null) {
                latitude = location[0].getLatitude(); //纬度
                longitude = location[0].getLongitude(); //经度
//                LongLatiitude = "" + latitude + "," + longitude;
            }
        }

        return (new LatLonPoint(latitude, longitude));
    }

    /*可对默认地址和经纬进行利用
    利用android原生定位功能进行经纬度定位，得到默认经纬和文本位置,参数为系统位置管理器locationManager*/
    public String getDefaultLocation(final LocationManager locationManager, final Context context,
                                     final OperateInPositon operateInPositon) {
        double latitude = 0.0;
        double longitude = 0.0;
        final Location[] location = new Location[1];

//        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            // Provider被enable时触发此函数，比如GPS被打开
            @Override
            public void onProviderEnabled(String provider) {

                locationManager.getLastKnownLocation(provider);
                location[0] = getBestLastLocation(locationManager, context);
            }

            // Provider被disable时触发此函数，比如GPS被关闭
            @Override
            public void onProviderDisabled(String provider) {
            }

            //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            @Override
            public void onLocationChanged(Location location) {
                location = getBestLastLocation(locationManager, context);
                if (location != null) {
                    Log.e("Map", "Location changed : Lat: "
                            + location.getLatitude() + " Lng: "
                            + location.getLongitude());
                    LongLatitude = "" + location.getLatitude() + ","
                            + location.getLongitude();
                }
            }
        };
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    3, locationListener);
            location[0] = getBestLastLocation(locationManager, context);
            if (location[0] != null) {
                latitude = location[0].getLatitude();
                longitude = location[0].getLongitude();
//                LongLatiitude = "" + latitude + "," + longitude;
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            location[0] = getBestLastLocation(locationManager, context);
            if (location[0] != null) {
                latitude = location[0].getLatitude(); //纬度
                longitude = location[0].getLongitude(); //经度
//                LongLatiitude = "" + latitude + "," + longitude;
            }
        }


        String latLngStr = latitude + "," + longitude;
        final LatLonPoint latLonPoint = new LatLonPoint(latitude, longitude);


        GeocodeSearch geocoderSearch = new GeocodeSearch(cthis);
//        geocoderSearch.setOnGeocodeSearchListener(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if (i == 1000) {
                    String addressText = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                    operateInPositon.getDefaultAddress(addressText, latLonPoint);
                }else{
                    operateInPositon.getDefaultAddress(null, null);
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.GPS);
        geocoderSearch.getFromLocationAsyn(query);

        return latLngStr;
    }


    /*以此方法得到最佳的LocationProvider，因为直接调用getLastKnownLocation一般是返回为空*/
    public Location getBestLastLocation(LocationManager locationManager, Context context) {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public interface OperateInPositon {
        public void getDefaultAddress(String adderssText, LatLonPoint latLonPoint);
    }
}
