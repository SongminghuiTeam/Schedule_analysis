package bjfu.it.xuyuanyuan.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends AppCompatActivity {
    //为了全局获取context
    protected static Activity context = null;
    public static Activity getContext(){
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Intent intent = new Intent(this, ViewPagerActivity.class);
        startActivity(intent);
        this.finish();
    }
}
