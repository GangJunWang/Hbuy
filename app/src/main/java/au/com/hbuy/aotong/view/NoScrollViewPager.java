package au.com.hbuy.aotong.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by yiwe on 2016/7/22.
 */
public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NoScrollViewPager(Context context) {
        super(context);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return false;
    }
}

