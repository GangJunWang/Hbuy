package au.com.hbuy.aotong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.socks.library.KLog;

import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.base.impl.ImageDetailFragment;
import au.com.hbuy.aotong.base.impl.OrderUserGuideFragment;
import au.com.hbuy.aotong.utils.AdViewpagerUtil;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.view.NoScrollViewPager;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserOrderGuideActivity extends FragmentActivity {
    @Bind(R.id.viewpager)
    NoScrollViewPager viewpager;
    @Bind(R.id.bt_enter)
    Button btEnter;
    private UserOrderGuideActivity mActivity = UserOrderGuideActivity.this;
    private int mBeform = 0;
    private int[] mImgs = new int[] {R.drawable.order1, R.drawable.order2, R.drawable.order3, R.drawable.order4, R.drawable.order5};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_userguide);
        ButterKnife.bind(this);

        ImagePagerAdapter mAdapter = new ImagePagerAdapter(this.getSupportFragmentManager(), mImgs);
        viewpager.setAdapter(mAdapter);

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                KLog.d(position);
                if (position == mImgs.length - 1) {
                    btEnter.setText("我知道了");
                } else {
                    btEnter.setText("下一步");
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hint = btEnter.getText().toString().trim();
                if ("下一步".equals(hint)) {
                    ++mBeform;
                    viewpager.setCurrentItem(mBeform);
                } else if ("我知道了".equals(hint)) {
                    AppUtils.showActivity(mActivity, GetAddressRepoActivity.class);
                    SharedUtils.putBoolean(mActivity, "use_is_guide", true);
                    finish();
                }
            }
        });
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public int[] fileList;
        public ImagePagerAdapter(FragmentManager fm, int[] fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.length;
        }
        @Override
        public Fragment getItem(int position) {
            int url = fileList[position];
            return OrderUserGuideFragment.newInstance(url, mActivity);
        }
    }
}
