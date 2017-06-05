package au.com.hbuy.aotong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.base.impl.LoginPage;
import au.com.hbuy.aotong.base.impl.RegisterPage;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.view.NoScrollViewPager;

public class CompleteActivity extends BaseFragmentActivity {
    private NoScrollViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private MyPagerAdapter myPagerAdapter;

    private List<BasePage> mPagerList;
    private RegisterPage mRegisterPage;
    private ImageView mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
       init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                AppUtils.showActivity(CompleteActivity.this, LoginActivity.class);
                finish();
                break;
        }
        return false;
    }

    private void init() {
        mPagerList = new ArrayList<BasePage>();
        mRegisterPage = new RegisterPage(this);
        mPagerList.add(mRegisterPage);
        mPagerList.add(new LoginPage(this));
        mBack = (ImageView) findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.showActivity(CompleteActivity.this, LoginActivity.class);
                finish();
            }
        });
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_group);
        mRadioGroup.check(R.id.rb_login);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_register:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_login:
                        mViewPager.setCurrentItem(1, false);
                        break;
                }
            }
        });
        mViewPager = (NoScrollViewPager) findViewById(R.id.vp_content);

        myPagerAdapter = new MyPagerAdapter();
        mViewPager.setAdapter(myPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                mPagerList.get(arg0).initData();// 获取当前被选中的页面, 初始化该页面数据
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        mViewPager.setCurrentItem(1, false);
        mPagerList.get(1).initData();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.d(requestCode + "--" + resultCode);
        if (requestCode == 1) {
            if (null != data) {
                Bundle bunde = data.getExtras();
                String code = bunde.getString("code");
                ((TextView) mRegisterPage.setCode()).setText("+" + code);
            }
         }
    }
}
