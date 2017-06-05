package au.com.hbuy.aotong;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.base.impl.InformMsgPage;
import au.com.hbuy.aotong.base.impl.TakeMsgPage;

public class MessageManagerActivity extends BaseFragmentActivity {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private MyPagerAdapter myPagerAdapter;

    private List<BasePage> mPagerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_manager);
        init();
    }

    private void init() {
        mPagerList = new ArrayList<BasePage>();
        mPagerList.add(new TakeMsgPage(this));
        mPagerList.add(new InformMsgPage(this));
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_group);
        mRadioGroup.check(R.id.rb_take_mgs);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View checkView = findViewById(checkedId);
                if (!checkView.isPressed()) {
                    return;
                }
                switch (checkedId) {
                    case R.id.rb_take_mgs:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_inform_mgs:
                        mViewPager.setCurrentItem(1, false);
                        break;
                }
            }
        });
        mViewPager = (ViewPager) findViewById(R.id.vp_content);
        myPagerAdapter = new MyPagerAdapter();
        mViewPager.setAdapter(myPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                switch (arg0) {
                    case 0:
                        mRadioGroup.check(R.id.rb_take_mgs);
                        break;
                    case 1:
                        mRadioGroup.check(R.id.rb_inform_mgs);
                        break;
                }
                mPagerList.get(arg0).initData();// 获取当前被选中的页面, 初始化该页面数据
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        // mViewPager.setCurrentItem(0, false);
        mPagerList.get(0).initData();// 获取当前被选中的页面, 初始化该页面数据
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mPagerList.size();
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
            BasePage basePager = mPagerList.get(position);
            container.addView(basePager.mRootView);
            return basePager.mRootView;
        }
    }
}
