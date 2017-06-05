package au.com.hbuy.aotong.base.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import au.com.hbuy.aotong.AddPkgActivity;
import au.com.hbuy.aotong.AddressRepoActivity;
import au.com.hbuy.aotong.FaqActivity;
import au.com.hbuy.aotong.HandingActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.TransferActivity;
import au.com.hbuy.aotong.adapter.MyPagerAdapter;
import au.com.hbuy.aotong.base.BaseMainPage;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.domain.GroupItem;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.view.NoScrollViewPager;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class OrderPage extends BaseMainPage implements OnClickListener {
    private View mView;
    public final static int FROM_ADD_PKG = 1;
    private NoScrollViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private List<BasePage> mPagerList;
    private MyPagerAdapter myPagerAdapter;

    public OrderPage(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.order_page, null);
        mRadioGroup = (RadioGroup) mView.findViewById(R.id.rg_group);
        mViewPager = (NoScrollViewPager) mView.findViewById(R.id.vp_content);

        mView.findViewById(R.id.add).setOnClickListener(this);
        mView.findViewById(R.id.iv_chat).setOnClickListener(this);
        mView.findViewById(R.id.tv_user_guide).setOnClickListener(this);
        mView.findViewById(R.id.tv_query).setOnClickListener(this);
        mView.findViewById(R.id.tv_pkg_packing).setOnClickListener(this);
        mView.findViewById(R.id.iv_transfer).setOnClickListener(this);
        mView.findViewById(R.id.to_layout).setOnClickListener(this);
        flContent.addView(mView);
    }

    @Override
    public void initData() {
        mPagerList = new ArrayList();
        mPagerList.add(new InlandItemPage(mActivity, false));
        mPagerList.add(new InlandItemPage(mActivity, true));
        mRadioGroup.check(R.id.rb_inland);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_inland:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_foreign:
                        mViewPager.setCurrentItem(1, false);
                        break;
                }
            }
        });

        myPagerAdapter = new MyPagerAdapter(mPagerList);
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
        mViewPager.setCurrentItem(0, false);
        mPagerList.get(0).initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                mActivity.startActivityForResult(new Intent(mActivity, AddPkgActivity.class), FROM_ADD_PKG);
                break;
            case R.id.iv_chat:
                AppUtils.goChat(mActivity);
                break;
            case R.id.to_layout:
                Intent faq = new Intent(mActivity, FaqActivity.class);
                faq.putExtra("url", ConfigConstants.faq_url);
                mActivity.startActivity(faq);
                break;
            case R.id.tv_user_guide:
                AppUtils.showActivity(mActivity, AddressRepoActivity.class);
                break;
            case R.id.tv_query:
                Intent i = new Intent(mActivity, FaqActivity.class);
                i.putExtra("url", ConfigConstants.TRACK_URL);
                mActivity.startActivity(i);
                break;
            case R.id.tv_pkg_packing:
                Intent packing = new Intent(mActivity, HandingActivity.class);
                packing.putExtra("id", "1");
                mActivity.startActivity(packing);
                break;
            case R.id.iv_transfer:
                AppUtils.showActivity(mActivity, TransferActivity.class);
                break;

        }
    }
}
