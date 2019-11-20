package bjfu.it.xuyuanyuan.positonnavi.OverLay;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteBusWalkItem;
import com.amap.api.services.route.WalkStep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyBusRouteOverlay extends b{
    private BusPath aa;
    private LatLng b;

    public MyBusRouteOverlay(Context var1, AMap var2, BusPath var3, LatLonPoint var4, LatLonPoint var5) {
        super(var1);
        this.aa = var3;
        this.startPoint = a.a(var4);
        this.endPoint = a.a(var5);
        this.mAMap = var2;
    }

    public void addToMap() {
        try {
            List var1 = this.aa.getSteps();

            for (int var2 = 0; var2 < var1.size(); ++var2) {
                BusStep var3 = (BusStep) var1.get(var2);
                if (var2 < var1.size() - 1) {
                    BusStep var4 = (BusStep) var1.get(var2 + 1);
                    if (var3.getWalk() != null && var3.getBusLine() != null) {
                        this.b(var3);
                    }

                    if (var3.getBusLine() != null && var4.getWalk() != null) {
                        this.c(var3, var4);
                    }

                    if (var3.getBusLine() != null && var4.getWalk() == null && var4.getBusLine() != null) {
                        this.b(var3, var4);
                    }

                    if (var3.getBusLine() != null && var4.getWalk() == null && var4.getBusLine() != null) {
                        this.a(var3, var4);
                    }
                }

                if (var3.getWalk() != null && var3.getWalk().getSteps().size() > 0) {
                    this.a(var3);
                } else if (var3.getBusLine() == null) {
                    Polyline var6 = this.a(this.b, this.endPoint);
                    this.allPolyLines.add(var6);
                }

                if (var3.getBusLine() != null) {
                    RouteBusLineItem var7 = var3.getBusLine();
                    this.a(var7);
                    this.b(var7);
                }
            }

            this.addStartAndEndMarker();
        } catch (Throwable var5) {
            var5.printStackTrace();
        }

    }

    private void a(BusStep var1) {
        RouteBusWalkItem var2 = var1.getWalk();
        List var3 = var2.getSteps();

        for (int var4 = 0; var4 < var3.size(); ++var4) {
            WalkStep var5 = (WalkStep) var3.get(var4);
            if (var4 == 0) {
                LatLng var6 = a.a((LatLonPoint) var5.getPolyline().get(0));
                String var7 = var5.getRoad();
                String var8 = this.b(var3);
                this.a(var6, var7, var8);
            }

            ArrayList var11 = a.a(var5.getPolyline());
            this.b = (LatLng) var11.get(var11.size() - 1);
            Polyline var12 = this.a((List) var11);
            this.allPolyLines.add(var12);
            if (var4 < var3.size() - 1) {
                LatLng var13 = (LatLng) var11.get(var11.size() - 1);
                LatLng var9 = a.a((LatLonPoint) ((WalkStep) var3.get(var4 + 1)).getPolyline().get(0));
                if (!var13.equals(var9)) {
                    Polyline var10 = this.a(var13, var9);
                    this.allPolyLines.add(var10);
                }
            }
        }

    }

    private void a(LatLng var1, String var2, String var3) {
      /*  Marker var4 = this.mAMap.addMarker((new MarkerOptions()).position(var1).title(var2).snippet(var3).visible(this.mNodeIconVisible).anchor(0.5F, 0.5F).icon(this.getWalkBitmapDescriptor()));
        this.stationMarkers.add(var4);*/
    }

    private void a(RouteBusLineItem var1) {
        ArrayList var2 = a.a(var1.getPolyline());
        Polyline var3 = this.mAMap.addPolyline((new PolylineOptions()).addAll(var2).color(this.getBusColor()).width(this.getBuslineWidth()));
        this.allPolyLines.add(var3);
    }

    private void b(RouteBusLineItem var1) {
        BusStationItem var2 = var1.getDepartureBusStation();
        Marker var3 = this.mAMap.addMarker((new MarkerOptions()).position(a.a(var2.getLatLonPoint())).title(var1.getBusLineName()).snippet(this.c(var1)).anchor(0.5F, 0.5F).visible(this.mNodeIconVisible).icon(this.getBusBitmapDescriptor()));
        this.stationMarkers.add(var3);
    }

    private void a(BusStep var1, BusStep var2) {
        LatLng var3 = a.a(this.e(var1));
        LatLng var4 = a.a(this.f(var2));
        if (var4.latitude - var3.latitude > 1.0E-4D || var4.longitude - var3.longitude > 1.0E-4D) {
            this.drawLineArrow(var3, var4);
        }

    }

    private void b(BusStep var1, BusStep var2) {
        LatLonPoint var3 = this.e(var1);
        LatLng var4 = a.a(var3);
        LatLonPoint var5 = this.f(var2);
        LatLng var6 = a.a(var5);
        if (!var4.equals(var6)) {
            this.drawLineArrow(var4, var6);
        }

    }

    private void c(BusStep var1, BusStep var2) {
        LatLonPoint var3 = this.e(var1);
        LatLonPoint var4 = this.c(var2);
        if (!var3.equals(var4)) {
            Polyline var5 = this.a(var3, var4);
            this.allPolyLines.add(var5);
        }

    }

    private void b(BusStep var1) {
        LatLonPoint var2 = this.d(var1);
        LatLonPoint var3 = this.f(var1);
        if (!var2.equals(var3)) {
            Polyline var4 = this.a(var2, var3);
            this.allPolyLines.add(var4);
        }

    }

    private LatLonPoint c(BusStep var1) {
        return (LatLonPoint) ((WalkStep) var1.getWalk().getSteps().get(0)).getPolyline().get(0);
    }

    private Polyline a(LatLonPoint var1, LatLonPoint var2) {
        LatLng var3 = a.a(var1);
        LatLng var4 = a.a(var2);
        return this.mAMap != null ? this.a(var3, var4) : null;
    }

    private Polyline a(LatLng var1, LatLng var2) {
        return this.mAMap.addPolyline((new PolylineOptions()).add(new LatLng[]{var1, var2}).width(this.getBuslineWidth()).color(this.getWalkColor()));
    }

    private Polyline a(List<LatLng> var1) {
        return this.mAMap.addPolyline((new PolylineOptions()).addAll(var1).color(this.getWalkColor()).width(this.getBuslineWidth()));
    }

    private String b(List<WalkStep> var1) {
        float var2 = 0.0F;

        WalkStep var4;
        for (Iterator var3 = var1.iterator(); var3.hasNext(); var2 += var4.getDistance()) {
            var4 = (WalkStep) var3.next();
        }

        return "步行" + var2 + "米";
    }

    public void drawLineArrow(LatLng var1, LatLng var2) {
        this.mAMap.addPolyline((new PolylineOptions()).add(new LatLng[]{var1, var2}).width(3.0F).color(this.getBusColor()).width(this.getBuslineWidth()));
    }

    private String c(RouteBusLineItem var1) {
        return "(" + var1.getDepartureBusStation().getBusStationName() + "-->" + var1.getArrivalBusStation().getBusStationName() + ") 经过" + (var1.getPassStationNum() + 1) + "站";
    }

    protected float getBuslineWidth() {
        return 10.0F;
    }

    private LatLonPoint d(BusStep var1) {
        List var2 = var1.getWalk().getSteps();
        WalkStep var3 = (WalkStep) var2.get(var2.size() - 1);
        List var4 = var3.getPolyline();
        return (LatLonPoint) var4.get(var4.size() - 1);
    }

    private LatLonPoint e(BusStep var1) {
        List var2 = var1.getBusLine().getPolyline();
        return (LatLonPoint) var2.get(var2.size() - 1);
    }

    private LatLonPoint f(BusStep var1) {
        return (LatLonPoint) var1.getBusLine().getPolyline().get(0);
    }
}


