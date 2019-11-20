package bjfu.it.xuyuanyuan.customview.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloudea.calendarmodule.CalendarView;
import com.cloudea.databasemodule.DataBaseUtils;
import com.cloudea.databasemodule.User;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.Observable;
import java.util.Observer;

import bjfu.it.xuyuanyuan.customview.R;
import bjfu.it.xuyuanyuan.customview.ViewPagerActivity;
import bjfu.it.xuyuanyuan.customview.WebActivity;

public class MineFragment extends Fragment{

    //用户信息
    TextView nicknameView;

    //功能按钮
    ViewGroup toolEditName;
    ViewGroup toolEditPass;
    ViewGroup toolLogOut;
    ViewGroup toolExitApp;
    ViewGroup toolAbout;

    ViewGroup toolEditNameCon;
    ViewGroup toolEditPassCon;
    ViewGroup toolLogOutCon;

    //任务信息
    TextView runningCountView;
    TextView commingCountView;
    TextView completedCountView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        //获取元素
        nicknameView = view.findViewById(R.id.nickname);
        toolEditName = view.findViewById(R.id.account_tool_editnickname);
        toolEditPass = view.findViewById(R.id.account_tool_editpassword);
        toolLogOut = view.findViewById(R.id.account_tool_logout);
        toolAbout = view.findViewById(R.id.account_tool_about);
        toolExitApp = view.findViewById(R.id.account_tool_exit);

        toolEditNameCon = view.findViewById(R.id.account_tool_editnickname_con);
        toolEditPassCon = view.findViewById(R.id.account_tool_editpassword_con);
        toolLogOutCon = view.findViewById(R.id.account_tool_logout_con);

        runningCountView = view.findViewById(R.id.running_count);
        commingCountView = view.findViewById(R.id.comming_count);
        completedCountView = view.findViewById(R.id.completed_count);

        //设置元素
        nicknameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("url", "file:///android_asset/aui/login.html");
                startActivity(intent);
            }
        });

        toolLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseUtils.getInstance().signOut();
                onResume();
            }
        });

        toolEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = DataBaseUtils.getInstance().getSignedUser().getUsername();
                String phone = DataBaseUtils.getInstance().getSignedUser().getMobilePhoneNumber();
                jumpToWebActivity("change_password.html?" + "nickname=" + nickname + "&phone=" + phone);
            }
        });

        toolEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToWebActivity("change_nickname.html");
            }
        });

        toolAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToWebActivity("about.html");
            }
        });

        toolExitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);     // "android.intent.action.MAIN"
                intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
                startActivity(intent);
            }
        });

        ViewPagerActivity activity = (ViewPagerActivity) getActivity();
        CollectionFragment collectionFragment = (CollectionFragment) activity.fragmentList.get(0);
        EventAdapter eventAdapter = (EventAdapter) collectionFragment.eventList.getAdapter();
        eventAdapter.notifyMineFragment();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //设置帐户信息显示
        User user = DataBaseUtils.getInstance().getSignedUser();
        TextView nicknameView = getView().findViewById(R.id.nickname);
        ViewGroup phoneBlock = getView().findViewById(R.id.phone_block);
        TextView phoneView = getView().findViewById(R.id.phone);
        if(user != null){
            phoneBlock.setVisibility(View.VISIBLE);
            nicknameView.setText(user.getUsername());
            phoneView.setText(user.getMobilePhoneNumber().replaceAll("\\d{4}$", "****"));
            nicknameView.setClickable(false);
            toolEditNameCon.setVisibility(View.VISIBLE);
            toolEditPassCon.setVisibility(View.VISIBLE);
            toolLogOutCon.setVisibility(View.VISIBLE);
        }else{
            phoneBlock.setVisibility(View.GONE);
            nicknameView.setText("登录 / 注册");
            nicknameView.setClickable(true);
            toolEditNameCon.setVisibility(View.GONE);
            toolEditPassCon.setVisibility(View.GONE);
            toolLogOutCon.setVisibility(View.GONE);
        }
    }

    //设置任务数量的显示
    public void showTaskCount(int runningcount, int commingCount, int completedCount){
        runningCountView.setText(runningcount + "");
        commingCountView.setText(commingCount +"");
        completedCountView.setText(completedCount + "");
    }

    //打开WebView
    private void jumpToWebActivity(String url){
        Intent intent = new Intent(getContext(), WebActivity.class);
        intent.putExtra("url", "file:///android_asset/aui/" + url);
        startActivity(intent);
    }
}
