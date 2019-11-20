package bjfu.it.xuyuanyuan.customview.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;


public class IconText extends AppCompatTextView {
    public IconText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setTypeface(IconFont.get(context));
        this.setIncludeFontPadding(false);
    }
}

