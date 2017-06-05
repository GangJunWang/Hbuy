package au.com.hbuy.aotong.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.socks.library.KLog;

import java.util.List;

import au.com.hbuy.aotong.base.BasePage;

/**
 * Created by yangwei on 2017/5/26--13:00.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class MyPagerAdapter extends PagerAdapter {
    private List<BasePage> mList;
    public MyPagerAdapter(List<BasePage> mList) {
        this.mList = mList;
    }
    @Override
    public int getCount() {
        KLog.d(mList.size());
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BasePage basePager = mList.get(position);
        container.addView(basePager.mRootView);
        return basePager.mRootView;
    }
}
