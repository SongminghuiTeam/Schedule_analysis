package bjfu.it.xuyuanyuan.customview;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.cloudea.basemodule.Data;
import com.cloudea.informmodule.InformService;
import com.cloudea.informmodule.InformUtils;
import com.cloudea.localstoragemodule.LocalStorageUtils;
import com.cloudea.viewandeditmodule.ComfirnWindow;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import bjfu.it.xuyuanyuan.customview.Adapter.DataBaseAdapter;
import bjfu.it.xuyuanyuan.customview.Adapter.MapAdapter;
import bjfu.it.xuyuanyuan.customview.Adapter.MyFragmentPagerAdapter;
import bjfu.it.xuyuanyuan.customview.Fragment.CollectionFragment;
import bjfu.it.xuyuanyuan.customview.Fragment.MineFragment;
import bjfu.it.xuyuanyuan.customview.Fragment.PlanningFragment;
import bjfu.it.xuyuanyuan.customview.util.WaitPopupWindow;
import bjfu.it.xuyuanyuan.positonnavi.Service.DynamicPermissions;
import bjfu.it.xuyuanyuan.positonnavi.Service.GetPositonService;
import cn.bmob.v3.BmobObject;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewPagerActivity extends AppCompatActivity {
    public List<Fragment> fragmentList;

    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;
    private FragmentManager fragmentManager;
    private ComfirnWindow comfirnWindow;
    private InformUtils informUtils;

    //TAB
    List<View> tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        BaseActivity.context = this;

        //其它初始化
        viewPager = findViewById(R.id.viewPager);
        fragmentList = new ArrayList<>();
        fragmentList.add(new CollectionFragment());
        fragmentList.add(new PlanningFragment());
        fragmentList.add(new MineFragment());
        fragmentManager = getSupportFragmentManager();
        adapter = new MyFragmentPagerAdapter(fragmentManager, fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);


        //设置TAB
        final View defaultTab = findViewById(R.id.tab1);
        final View secondTab = findViewById(R.id.tab2);
        final View thirdTab = findViewById(R.id.tab3);
        tabs = new LinkedList<>();
        tabs.add(defaultTab);
        tabs.add(secondTab);
        tabs.add(thirdTab);

        //页面切换时
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        swichTab(defaultTab);break;
                    case 1:
                        swichTab(secondTab);break;
                    case 2:
                        swichTab(thirdTab);break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });


        //初始化TAB
        swichTab(defaultTab);

        //开启服务
        final DataBaseAdapter dataBaseAdapter = DataBaseAdapter.getInstance();
        informUtils = InformUtils.getInstance(this);
        informUtils.setInformIntent(EditActivity.class);
        informUtils.setInformListener(new InformService.OnInformListener() {
            @Override
            public void inform(List<Data> data) {
                dataBaseAdapter.updateEvents(data, new DataBaseAdapter.OnRequestCompleted() {
                    @Override
                    public void finish() {
                        return;
                    }
                }, true);
            }
        });
        informUtils.setRequestDataListener(new InformService.OnRequestDataListener() {
            @Override
            public List<Data> requestData() {
                return dataBaseAdapter.queryEvents();
            }
        });
        informUtils.startServer();

        //申请所有与定位有关的权限
        MapAdapter.getInstance().requestPermissions(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        final String clipString = getClipboard();
        //如果剪切板内容不为空
        if(clipString != null && !clipString.trim().equals("")){
            final String[] clipStringFragments = clipString.split("[\n]{2}");
            final LinkedList<String> clipStringFragmentList = new LinkedList<>();
            for(String i : clipStringFragments){
                //第一次过滤文本
                if(!i.equals("")){
                    clipStringFragmentList.add(i);
                }
            }
            //第二次过滤文本
            if(clipStringFragmentList.size() > 0){
                MapAdapter.getInstance().getPositionString(new MapAdapter.OnMapRequestFinished<String>() {
                    @Override
                    public void finish(final String result) {
                        if(result == null){
                            Toast.makeText(ViewPagerActivity.this, "获取地址出错", Toast.LENGTH_SHORT).show();
                        }else{
                            new Thread(){
                                @Override
                                public void run() {
                                    final LinkedList<String> message = new LinkedList<>();
                                    final LinkedList<String> results = new LinkedList<>();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    OkHttpClient client = new OkHttpClient();
                                    for(String i : clipStringFragmentList){
                                        HttpUrl url = new HttpUrl.Builder()
                                                .scheme("http")
                                                .host("120.27.248.84")
                                                .port(8080)
                                                .addPathSegment("TextAnalyzer")
                                                .addPathSegment("login")
                                                .build();
                                        FormBody formBody = new FormBody.Builder()
                                                .add("text", i)
                                                .add("currenttime", sdf.format(new Date()))
                                                .add("currentlocation", result)
                                                .build();
                                        Request request = new Request.Builder()
                                                .url(url).post(formBody).build();
                                        try {
                                            Response response = client.newCall(request).execute();
                                            message.add(i);
                                            results.add(response.body().string());
                                        } catch (IOException e) {
                                            continue;
                                        }
                                    }

                                    /*Log.v("结果个数", results.size() + "");
                                    for (int i = 0; i < results.size(); i++){
                                        Log.v("返回结果" + i, results.get(i));
                                    }*/


                                    if(results.size() != 0){
                                        if(comfirnWindow == null){
                                            comfirnWindow = new ComfirnWindow(ViewPagerActivity.this, 800 ,1000);
                                            comfirnWindow.setOnComfirmListener(new ComfirnWindow.OnComfirmListener() {
                                                @Override
                                                public void comfirm(List<ComfirnWindow.CleanData> cleanData) {
                                                    if(cleanData != null && cleanData.size() > 0){
                                                        final int[] finisehd = {0,0};
                                                        final int size = cleanData.size();
                                                        final DataBaseAdapter dba =  DataBaseAdapter.getInstance();
                                                        final WaitPopupWindow wait = new WaitPopupWindow(ViewPagerActivity.this);
                                                        wait.showAtLocation(ViewPagerActivity.this.getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
                                                        for(final ComfirnWindow.CleanData cleanDataItem : cleanData){
                                                            //将数据加入数据库
                                                           dba.addEvents(cleanDataItem.events, cleanDataItem.message, cleanDataItem.result, new DataBaseAdapter.OnRequestCompleted() {
                                                               @Override
                                                               public void finish() {
                                                                    finisehd[0]++;
                                                                    if(finisehd[0] == size){
                                                                        //计算一下路径时间
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                dba.refresh();
                                                                                wait.dismiss();
                                                                            }
                                                                        });

                                                                    }
                                                               }
                                                           }, false);

                                                        }
                                                    }else{
                                                        Log.v("分析结果", "没有结果");
                                                    }
                                                }
                                            });
                                        }
                                        if(comfirnWindow.setRowData(message, results)){
                                            viewPager.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    comfirnWindow.show();
                                                }
                                            });
                                        }
                                        //清空剪切板
                                        clearClipboard();
                                    }else{
                                        Toast.makeText(viewPager.getContext(), "获取数据出错", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }.start();
                        }
                    }
                });

            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(!MapAdapter.getInstance().requestResults(requestCode, permissions,grantResults)){
            Log.v("权限申请", "失败");
            System.exit(0);
        }
    }

    public void swichTab(View view){
        if(tabs.indexOf(view) != -1){
            View currentChoosed = tabs.get(tabs.size() - 1);
            TextView tabIcon = currentChoosed.findViewById(R.id.tab_icon);
            TextView tabText = currentChoosed.findViewById(R.id.tab_text);
            tabIcon.setTextColor(getResources().getColor( R.color.tabNormal));
            tabText.setTextColor(getResources().getColor( R.color.tabNormal));
            tabIcon = view.findViewById(R.id.tab_icon);
            tabText = view.findViewById(R.id.tab_text);
            tabIcon.setTextColor(getResources().getColor( R.color.tabSelected));
            tabText.setTextColor(getResources().getColor( R.color.tabSelected));
            tabs.remove(view);
            tabs.add(view);
        }

        //选择Fragment
        switch (view.getId()){
            case R.id.tab1:
                viewPager.setCurrentItem(0, false);break;
            case R.id.tab2:
                viewPager.setCurrentItem(1, false);break;
            case R.id.tab3:
                viewPager.setCurrentItem(2, false);break;
        }

        fragmentList.get(0).onPause();
    }

    public void showTaskCountInMineFragment(int runningcount, int commingCount, int completedCount){
        MineFragment mineFragment = (MineFragment) fragmentList.get(2);
        mineFragment.showTaskCount(runningcount, commingCount, completedCount);
    }


    /*得到剪贴板的第一个粘贴文本*/
     String getClipboard() {
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        // 返回数据
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            // 从数据集中获取（粘贴）第一条文本数据
            return clipData.getItemAt(0).getText().toString();
        }
        return null;
    }

    private void clearClipboard() {
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(null, ""));
    }
}
