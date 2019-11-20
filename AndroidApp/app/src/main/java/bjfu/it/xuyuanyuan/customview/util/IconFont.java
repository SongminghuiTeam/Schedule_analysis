package bjfu.it.xuyuanyuan.customview.util;

import android.content.Context;
import android.graphics.Typeface;

public class IconFont {
    private static Typeface face = null;
    public static synchronized Typeface get(Context context)
    {
        if(face == null)
        {
            face = Typeface.createFromAsset(context.getAssets(), "fonts/iconfont.ttf");
        }

        return face;
    }
}
