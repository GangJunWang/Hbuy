package au.com.hbuy.aotong.utils;

import android.view.View;

import java.util.Calendar;

/**
 * Created by yangwei on 2016/11/25--17:08.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    @Override
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(view);
        }
    }

    public abstract void onNoDoubleClick(View view);
}
