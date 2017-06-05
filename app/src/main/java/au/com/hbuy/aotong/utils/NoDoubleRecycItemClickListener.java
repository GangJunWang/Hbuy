package au.com.hbuy.aotong.utils;

import android.view.View;

import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.Calendar;


/**
 * Created by yangwei on 2016/11/25--17:08.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public abstract class NoDoubleRecycItemClickListener implements RecyclerArrayAdapter.OnItemClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onItemClick(int position) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClickItem(position);
        }
    }

    public abstract void onNoDoubleClickItem(int position);
}
