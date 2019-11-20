package bjfu.it.xuyuanyuan.positonnavi.OverLay;

import android.content.Context;
import android.graphics.Bitmap;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveStep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyDriveRouteOverlay extends b{
    private DrivePath aa;
    private Bitmap b;
    protected List<Marker> mPassByMarkers = new ArrayList();
    private List<LatLonPoint> c;
    private boolean d = true;

    public MyDriveRouteOverlay(Context var1, AMap var2, DrivePath var3, LatLonPoint var4, LatLonPoint var5) {
        super(var1);
        this.mAMap = var2;
        this.aa = var3;
        this.startPoint = a.a(var4);
        this.endPoint = a.a(var5);
    }

    public MyDriveRouteOverlay(Context var1, AMap var2, DrivePath var3, LatLonPoint var4, LatLonPoint var5, List<LatLonPoint> var6) {
        super(var1);
        this.mAMap = var2;
        this.aa = var3;
        this.startPoint = a.a(var4);
        this.endPoint = a.a(var5);
        this.c = var6;
    }

    public void addToMap() {
        List var1 = this.aa.getSteps();

        for (int var2 = 0; var2 < var1.size(); ++var2) {
            DriveStep var3 = (DriveStep) var1.get(var2);
            LatLng var4 = a.a((LatLonPoint) var3.getPolyline().get(0));
            LatLng var8;
            Polyline var10;
            if (var2 < var1.size() - 1) {
                if (var2 == 0) {
                    Polyline var5 = this.mAMap.addPolyline((new PolylineOptions()).add(new LatLng[]{this.startPoint, var4}).color(this.getDriveColor()).width(this.getBuslineWidth()));
                    this.allPolyLines.add(var5);
                }

                var8 = a.a((LatLonPoint) var3.getPolyline().get(var3.getPolyline().size() - 1));
                LatLng var6 = a.a((LatLonPoint) ((DriveStep) var1.get(var2 + 1)).getPolyline().get(0));
                if (!var8.equals(var6)) {
                    Polyline var7 = this.mAMap.addPolyline((new PolylineOptions()).add(new LatLng[]{var8, var6}).color(this.getDriveColor()).width(this.getBuslineWidth()));
                    this.allPolyLines.add(var7);
                }
            } else {
                var8 = a.a((LatLonPoint) var3.getPolyline().get(var3.getPolyline().size() - 1));
                var10 = this.mAMap.addPolyline((new PolylineOptions()).add(new LatLng[]{var8, this.endPoint}).color(this.getDriveColor()).width(this.getBuslineWidth()));
                this.allPolyLines.add(var10);
            }

           /* Marker var9 = this.mAMap.addMarker((new MarkerOptions()).position(var4).title("方向:" + var3.getAction() + "\n道路:" + var3.getRoad()).snippet(var3.getInstruction()).anchor(0.5F, 0.5F).visible(this.mNodeIconVisible).icon(this.getDriveBitmapDescriptor()));
            this.stationMarkers.add(var9);*/
            var10 = this.mAMap.addPolyline((new PolylineOptions()).addAll(a.a(var3.getPolyline())).color(this.getDriveColor()).width(this.getBuslineWidth()));
            this.allPolyLines.add(var10);
        }

        this.a();
        this.addStartAndEndMarker();
    }

    private void a() {
        if (this.c != null && this.c.size() != 0) {
            Iterator var1 = this.c.iterator();

            while (var1.hasNext()) {
                LatLonPoint var2 = (LatLonPoint) var1.next();
                LatLng var3 = a.a(var2);
                Marker var4 = this.mAMap.addMarker((new MarkerOptions()).position(var3).title("途经点").visible(this.d).icon(this.getPassedByBitmapDescriptor()));
                this.mPassByMarkers.add(var4);
            }

        }
    }

    public void removeFromMap() {
        super.removeFromMap();
        Iterator var1 = this.mPassByMarkers.iterator();

        while (var1.hasNext()) {
            Marker var2 = (Marker) var1.next();
            var2.remove();
        }

    }

    /*自己修改过，postInvalidate应该是重载界面*/
    public void setThroughPointIconVisibility(boolean var1) {
        this.d = var1;
        Iterator var2 = this.mPassByMarkers.iterator();

        while (var2.hasNext()) {
            Marker var3 = (Marker) var2.next();
            var3.setVisible(var1);
        }

        this.mAMap.reloadMap();//自己添加的，重载地图显示
//        this.mAMap.postInvalidate();  //自己注释
    }

    protected float getBuslineWidth() {
        return 10.0F;
    }

    protected BitmapDescriptor getPassedByBitmapDescriptor() {
        return this.getBitDes(this.b, "amap_throughpoint.png");
    }

    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder var1 = LatLngBounds.builder();
        var1.include(new LatLng(this.startPoint.latitude, this.startPoint.longitude));
        var1.include(new LatLng(this.endPoint.latitude, this.endPoint.longitude));
        if (this.c != null && this.c.size() > 0) {
            for (int var2 = 0; var2 < this.c.size(); ++var2) {
                var1.include(new LatLng(((LatLonPoint) this.c.get(var2)).getLatitude(), ((LatLonPoint) this.c.get(var2)).getLongitude()));
            }
        }

        return var1.build();
    }
}
