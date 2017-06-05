package au.com.hbuy.aotong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import au.com.hbuy.aotong.domain.Android;
import au.com.hbuy.aotong.domain.Images;
import au.com.hbuy.aotong.domain.Version;
import au.com.hbuy.aotong.service.DownloadService;
import au.com.hbuy.aotong.utils.AdViewpagerUtil;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespByUpdateCallback;
import au.com.hbuy.aotong.view.DirectionControlView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserGuideActivity extends BaseActivity {
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.ly_dots)
    LinearLayout lyDots;
    @Bind(R.id.bt_enter)
    Button btEnter;
    private UserGuideActivity mActivity = UserGuideActivity.this;
    private AdViewpagerUtil adViewpagerUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userguide);
        ButterKnife.bind(this);
        final int[] urls = new int[]{R.drawable.splash_01, R.drawable.splash_02, R.drawable.splash_03, R.drawable.splash_04};
        adViewpagerUtil = new AdViewpagerUtil(this, viewpager, lyDots, 8, 4, urls);
        adViewpagerUtil.initVps();

        adViewpagerUtil.setOnAdPageChangeListener(new AdViewpagerUtil.OnAdPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == 4) {
                    btEnter.setVisibility(View.VISIBLE);
                } else {
                    btEnter.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick(R.id.bt_enter)
    public void onClick() {
        SharedUtils.putBoolean(mActivity, "use_first", true);
        final boolean isLogin = SharedUtils.getBoolean(this, "use_is_login", false);
        if (!isLogin) {
            startActivity(new Intent(mActivity, LoginActivity.class));
        } else {
            startActivity(new Intent(mActivity, MainActivity.class));
        }
        finish();
    }
}
