package bjfu.it.xuyuanyuan.positonnavi.Service;

import android.content.Context;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

import bjfu.it.xuyuanyuan.positonnavi.R;


/*使用案例——ClickLatlongActivity——得到点击点的经纬和文本位置*/
public class GetClickPositionService {

    AMap aMap;
    Context context;

    public GetClickPositionService(AMap aMap, Context context){
        this.aMap = aMap;
        this.context = context;
    }

    public void getClickPoint(final PositionInClick positionInClick) {
        if (aMap != null) {
            aMap.showIndoorMap(true);
            LatLng latLng = new LatLng(39.92448, 116.518295);
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));//设置中心点
            aMap.moveCamera(CameraUpdateFactory.zoomTo(18)); // 设置地图可视缩放大小
            aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
                @Override
                public void onMapClick(final LatLng latLng) {
                    aMap.clear();
                    double latitude = latLng.latitude;
                    double longtitude = latLng.longitude;
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dashboard_black_24dp));
                    markerOptions.position(latLng);
                    aMap.addMarker(markerOptions);
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
//                    注释此句之后就点击定位的时候就不会出现闪屏
                    Log.v("经纬度信息为", latitude + ", " + longtitude);
                    GetPositonService getPositonService = new GetPositonService(context);
                    getPositonService.getAddressText(latitude + "," + longtitude,
                            new GeocodeSearch.OnGeocodeSearchListener() {
                                @Override
                                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                                    String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                                    Log.v("点击的地址为", address);
                                    positionInClick.getAddressFromClick(address, latLng);
                                }

                                @Override
                                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                                }
                            });
                }
            });
        }
    }

    public interface PositionInClick{
        /**
         *
         * @param adderssText 得到在图上点击点的经纬度和文本地址
         * @param latLng
         */
        public void getAddressFromClick(String adderssText, LatLng latLng);
    }
}
