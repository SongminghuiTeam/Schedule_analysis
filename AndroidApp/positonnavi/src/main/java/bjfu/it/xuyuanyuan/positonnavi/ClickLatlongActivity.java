package bjfu.it.xuyuanyuan.positonnavi;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

import bjfu.it.xuyuanyuan.positonnavi.Service.GetClickPositionService;
import bjfu.it.xuyuanyuan.positonnavi.Service.GetPositonService;

public class ClickLatlongActivity extends Activity {

    private MapView mapView = null;
    private AMap aMap = null;
    private TextView addressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_latlong);
        Intent intent = new Intent();
        intent.putExtra("addr", "");
        setResult(0, intent);

        mapView = findViewById(R.id.clickMapView);
        mapView.onCreate(savedInstanceState);
        addressView = findViewById(R.id.address);
        addressView.setText(getIntent().getStringExtra("addr"));

        getClickPoint();
    }

//    此函数得到点击点并对其信息进行操作
    public void getClickPoint() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.showIndoorMap(true);
            new GetPositonService(this).getDefaultLocation((LocationManager) getSystemService(LOCATION_SERVICE), this, new GetPositonService.OperateInPositon() {
                @Override
                public void getDefaultAddress(String adderssText, LatLonPoint latLonPoint) {
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()), 15, 30, 30)));
                }
            });
        }
        GetClickPositionService getClickPositionService = new GetClickPositionService(aMap, this);
        getClickPositionService.getClickPoint(new GetClickPositionService.PositionInClick() {
            @Override
            public void getAddressFromClick(String adderssText, LatLng latLng) {
                //在此处对点击得到的文本地址和经纬地址进行操作
                //.setText(adderssText + ", 经纬度为：" + latLng.latitude + ", " + latLng.longitude);
                addressView.setText(adderssText);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    public void comform(View view) {
        Intent intent = new Intent();
        intent.putExtra("addr", addressView.getText().toString());
        setResult(0, intent);
        this.finish();
    }
}
