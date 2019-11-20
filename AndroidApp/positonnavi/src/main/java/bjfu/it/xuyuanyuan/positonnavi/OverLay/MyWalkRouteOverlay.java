package bjfu.it.xuyuanyuan.positonnavi.OverLay;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkStep;

import java.util.List;

public class MyWalkRouteOverlay extends b {
    private WalkPath aa;
    private Context c;

    public MyWalkRouteOverlay(Context var1, AMap var2, WalkPath var3, LatLonPoint var4, LatLonPoint var5) {
        super(var1);
        this.c = var1;
        this.mAMap = var2;
        this.aa = var3;

        this.startPoint = a.a(var4);
        this.endPoint = a.a(var5);
    }

    public void addToMap() {
        List var1 = this.aa.getSteps();

        for (int var2 = 0; var2 < var1.size(); ++var2) {
            WalkStep var3 = (WalkStep) var1.get(var2);
            LatLng var4 = a.a((LatLonPoint) var3.getPolyline().get(0));
            LatLng var8;
            Polyline var10;
            if (var2 < var1.size() - 1) {
                if (var2 == 0) {
                    Polyline var5 = this.mAMap.addPolyline((new PolylineOptions()).add(new LatLng[]{this.startPoint, var4}).color(this.getWalkColor()).width(this.getBuslineWidth()));
                    this.allPolyLines.add(var5);
                }

                var8 = a.a((LatLonPoint) var3.getPolyline().get(var3.getPolyline().size() - 1));
                LatLng var6 = a.a((LatLonPoint) ((WalkStep) var1.get(var2 + 1)).getPolyline().get(0));
                if (!var8.equals(var6)) {
                    Polyline var7 = this.mAMap.addPolyline((new PolylineOptions()).add(new LatLng[]{var8, var6}).color(this.getWalkColor()).width(this.getBuslineWidth()));
                    this.allPolyLines.add(var7);
                }
            } else {
                var8 = a.a((LatLonPoint) var3.getPolyline().get(var3.getPolyline().size() - 1));
                var10 = this.mAMap.addPolyline((new PolylineOptions()).add(new LatLng[]{var8, this.endPoint}).color(this.getWalkColor()).width(this.getBuslineWidth()));
                this.allPolyLines.add(var10);
            }


            /**
             * 在这里设置是否用来添加步行人的图标
             * */
          /*  Marker var9 = this.mAMap.addMarker((new MarkerOptions()).position(var4).title("方向:" + var3.getAction() + "\n道路:" + var3.getRoad()).snippet(var3.getInstruction()).anchor(0.5F, 0.5F).visible(this.mNodeIconVisible).
                    icon(BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(c.getResources(), R.mipmap.walk_select_touming))));*/
//            this.stationMarkers.add(var9);


            var10 = this.mAMap.addPolyline((new PolylineOptions()).addAll(a.a(var3.getPolyline())).color(this.getWalkColor()).width(this.getBuslineWidth()));
            this.allPolyLines.add(var10);
        }

        this.addStartAndEndMarker();
    }

    /**
     * 返回要连线的宽度
     */
    protected float getBuslineWidth() {
        return 4.0F;
    }
}
