package bjfu.it.xuyuanyuan.customview.util;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import bjfu.it.xuyuanyuan.customview.R;

public class WaitPopupWindow extends PopupWindow {

    public WaitPopupWindow(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.window_wait, null);
        setContentView(view);
        setWidth(-1);
        setHeight(-1);

        ImageView waitIcon = view.findViewById(R.id.icon_wait);
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(waitIcon, "rotation", 360f);
        rotateAnim.setDuration(1000);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.start();
    }
}
