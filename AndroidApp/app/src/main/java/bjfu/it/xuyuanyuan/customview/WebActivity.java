package bjfu.it.xuyuanyuan.customview;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudea.calendarmodule.CalendarView;
import com.cloudea.databasemodule.DataBaseUtils;
import com.cloudea.databasemodule.User;

import bjfu.it.xuyuanyuan.customview.Adapter.DataBaseAdapter;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class WebActivity extends AppCompatActivity {


    WebView webView;
    TextView headerText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        String url = getIntent().getStringExtra("url");
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebClient(this));
        webView.addJavascriptInterface(this, "web");
        webView.addJavascriptInterface(new WebInterfaceForDB(webView), "db");
        webView.loadUrl(url);
        headerText = findViewById(R.id.header_text);
        View backView = findViewById(R.id.back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            this.finish();
        }
    }

    @JavascriptInterface  //返回上一页
    public void goBack(){
        onBackPressed();
    }

    @JavascriptInterface  //设置窗口标题
    public void updateTitle()
    {
        headerText.setText(webView.getTitle());
    }

    @JavascriptInterface //关闭窗口
    public void closeWindow(){
        this.finish();
    }

}


class WebClient extends WebViewClient
{
    WebActivity webActivity;

    public WebClient(WebActivity webActivity)
    {
        this.webActivity = webActivity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(String.valueOf(request.getUrl()));
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        webActivity.updateTitle();
    }

}

//Java 调用 Javascript接口
class WebInterface{
    private WebView webView;
    public WebInterface(WebView webView){
        this.webView = webView;
    }

    protected void javascript(String funcName, String... args){
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:");
        sb.append(funcName);
        sb.append("(");
        for(int i = 0; i < args.length; i++){
            sb.append(args[i]);
            if(i != args.length - 1){
                sb.append(",");
            }
        }
        sb.append(")");
        webView.loadUrl(sb.toString());
    }
}

//操作用户管理的接口
class WebInterfaceForDB extends WebInterface{
    public WebInterfaceForDB(WebView webView){
        super(webView);
    }

    @JavascriptInterface   //发送验证码
    public void sendSMS(String phone){
        DataBaseUtils.getInstance().sendSMS(phone, new DataBaseUtils.OnRequestCompleted() {
            @Override
            public void onSucceed(Object data) {
                javascript("onSucceed");
            }

            @Override
            public void onError(Exception e) {
                javascript("onError", "\"发送验证码失败\"");
            }
        });
    }

    @JavascriptInterface  //注册
    public void signUp(String phone,String name, String password, String code){
        DataBaseUtils.getInstance().signUp(phone, name, password, code, new DataBaseUtils.OnRequestCompleted() {
            @Override
            public void onSucceed(Object data) {
                javascript("onSucceed");
                //防卡顿
                new Thread(){
                    @Override
                    public void run() {
                        DataBaseAdapter.getInstance().SyncEvents();
                    }
                }.start();
            }

            @Override
            public void onError(Exception e) {
                javascript("onError", "\"验证码有误\"");
            }
        });
    }

    @JavascriptInterface  //登录
    public void signIn(String phone, String password){
        DataBaseUtils.getInstance().signIn(phone, password, new DataBaseUtils.OnRequestCompleted() {
            @Override
            public void onSucceed(Object data) {
                DataBaseAdapter.getInstance().SyncEvents();
                javascript("onSucceed");
            }

            @Override
            public void onError(Exception e) {
                javascript("onError", "\"登录失败，请重试\"");
            }
        });
    }

    @JavascriptInterface  //更改密码
    public void changePassword(String newPass) {
        DataBaseUtils utils = DataBaseUtils.getInstance();
        User user = utils.getSignedUser();
        if (user != null) {
            user.setPassword(newPass);
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        javascript("onError", "\"修改失败\"");
                    } else {
                        javascript("onSucceed");
                    }
                }
            });
        } else {
            Log.e("我的", "未知异常");
        }
    }
    @JavascriptInterface    //更改昵称
    public void changeNickname(String nickname){
        DataBaseUtils.getInstance().changeNickname(nickname, new DataBaseUtils.OnRequestCompleted() {
            @Override
            public void onSucceed(Object data) {
                javascript("onSucceed");
            }

            @Override
            public void onError(Exception e) {
                javascript("onError", "网络异常");
            }
        });
    }
}