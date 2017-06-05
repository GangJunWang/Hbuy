package au.com.hbuy.aotong.base.impl;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import au.com.hbuy.aotong.AddBuyActivity;
import au.com.hbuy.aotong.AddWaitPayActivity;
import au.com.hbuy.aotong.BuyListActivity;
import au.com.hbuy.aotong.GuideActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.WaitPayListActivity;
import au.com.hbuy.aotong.adapter.MyPagerAdapter;
import au.com.hbuy.aotong.base.BaseMainPage;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.view.NoScrollViewPager;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class BuyPage extends BaseMainPage {
    private boolean mIsBuy = true;
    public final static int FROM_DEL = 2;
    private View mView;
    private NoScrollViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private List<BasePage> mPagerList;
    private MyPagerAdapter myPagerAdapter;

    public BuyPage(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.buy_page, null);
        mRadioGroup = (RadioGroup) mView.findViewById(R.id.rg_group);
        mViewPager = (NoScrollViewPager) mView.findViewById(R.id.vp_content);
        mView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                List<String> list = new ArrayList<>();
                list.add("代购商品");
                list.add("我要代付");
                new SuperDialog.Builder(mActivity)
                        .setAlpha(1f)
                        .setCanceledOnTouchOutside(false)
                        .setItems(list, new SuperDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                switch (position) {
                                    case 0:
                                        mActivity.startActivityForResult(new Intent(mActivity, BuyListActivity.class), FROM_DEL);
                                        break;
                                    case 1:
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .build();*/
                if (mIsBuy) {
                    mActivity.startActivity(new Intent(mActivity, AddBuyActivity.class));
                } else {
                   // mActivity.startActivityForResult(new Intent(mActivity, WaitPayListActivity.class), FROM_DEL);
                    mActivity.startActivity(new Intent(mActivity, AddWaitPayActivity.class));
                }
            }
        });

        mView.findViewById(R.id.tv_user_guide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mActivity, GuideActivity.class);
                i.putExtra("type", "2");
                mActivity.startActivity(i);
            }
        });

        mView.findViewById(R.id.iv_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.goChatByBuy(mActivity);
            }
        });

        flContent.addView(mView);
    }

    @Override
    public void initData() {
        mPagerList = new ArrayList();
        mPagerList.add(new BuyItemPage(mActivity));
        mPagerList.add(new WaitPayItemPage(mActivity));
        mRadioGroup.check(R.id.rb_daigou);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_daigou:
                        mViewPager.setCurrentItem(0, false);
                        mIsBuy = true;
                        break;
                    case R.id.rb_daifu:
                        mViewPager.setCurrentItem(1, false);
                        mIsBuy = false;
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
}
