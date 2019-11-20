package bjfu.it.xuyuanyuan.positonnavi.Service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class DynamicPermissions {
    private static DynamicPermissions dynamicPermissions;
    private String TAG = "DynamicPermissions";
    public PermissionsTo mPermissionsTo;
    private int KEY = 101;
    private List<String> mList = new ArrayList<>();
    public Context mContext;


    private String[] mPermissionsGroup = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    private DynamicPermissions() {
    }

    //实现单例模式
    public static DynamicPermissions getInstance() {
        if (dynamicPermissions == null) {
            dynamicPermissions = new DynamicPermissions();
        }
        return dynamicPermissions;
    }

    //导入数据
    public void setInitData(Context context, String[] permissionsGroup, PermissionsTo permissionsTo) {
        //this.mContext = context;
        //this.mPermissionsTo = permissionsTo;
        //privilege(context);
    }

    //授权
    public void privilege(Activity context) {
        mList.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < mPermissionsGroup.length; i++) {
                if (ActivityCompat.checkSelfPermission(context, mPermissionsGroup[i]) != PackageManager.PERMISSION_GRANTED) {
                    mList.add(mPermissionsGroup[i]);
                }
            }
            if (!mList.isEmpty()) {
                String[] permissions = mList.toArray(new String[mList.size()]);
                ActivityCompat.requestPermissions(context, permissions, KEY);
            } else {
                if (mPermissionsTo != null) {
                    this.mPermissionsTo.hasAuthorizeinit(mContext);
                }
            }
        } else {
            //"privilege:6.0以下的版本无需授权";
            if (mPermissionsTo != null) {
                this.mPermissionsTo.noAuthorizeinit(mContext);
            }
        }
    }

    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == KEY) {
            int j = 0;
            if (grantResults.length > 0) {//安全写法，如果小于0，肯定会出错了
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "onRequestPermissionsResult：授权成功" + permissions[i].toString());
                        j++;
                    } else {
                        Toast.makeText(mContext, "授权失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onRequestPermissionsResult：授权失败" + permissions[i].toString());
//                        ((Activity) mContext).finish();
                    }
                }
            }
            if (j == grantResults.length) {
                Log.e(TAG, "onRequestPermissionsResult：全部权限都授权成功");
                if (mPermissionsTo != null) {
                    this.mPermissionsTo.authorizeinitFinish(mContext);
                }
                return true;
            }
        }
        return false;
    }

    // 创建回调接口，在1.版本低不需要动态授权 2.已经授权过 3.授权完成后  这3个地方的后续运行逻辑进行统一的接口调出
    public interface PermissionsTo {
        void hasAuthorizeinit(Context context);

        void noAuthorizeinit(Context context);

        void authorizeinitFinish(Context context);
    }
}
