package au.com.hbuy.aotong.base.impl;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import au.com.hbuy.aotong.FaqActivity;
import au.com.hbuy.aotong.ParitiesManagerActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.base.BaseMainPage;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class ServicePage extends BaseMainPage implements View.OnClickListener {
    private View mView;
    private LinearLayout layout_01,layout_02, layout_03, layout_05, layout_06, layout_07;
    public ServicePage(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.service_page, null);
        flContent.addView(mView);
    }

    @Override
    public void initData() {
        initView();
    }

    private void initView() {
        layout_01 = (LinearLayout) mView.findViewById(R.id.layout_01);
        layout_01.setOnClickListener(this);
        layout_02 = (LinearLayout) mView.findViewById(R.id.layout_02);
        layout_02.setOnClickListener(this);
        layout_03 = (LinearLayout) mView.findViewById(R.id.layout_03);
        layout_03.setOnClickListener(this);
     /*   layout_04 = (LinearLayout) mView.findViewById(R.id.layout_04);
        layout_04.setOnClickListener(this);*/
        layout_05 = (LinearLayout) mView.findViewById(R.id.layout_05);
        layout_05.setOnClickListener(this);
        layout_06 = (LinearLayout) mView.findViewById(R.id.layout_06);
        layout_06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.showActivity(mActivity, ParitiesManagerActivity.class);
            }
        });
        layout_07 = (LinearLayout) mView.findViewById(R.id.layout_07);
        layout_07.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(mActivity, FaqActivity.class);
        switch (view.getId()) {
            case R.id.layout_01:
                i.putExtra("url", ConfigConstants.faq_url);
                break;
            case R.id.layout_02:
                i.putExtra("url", ConfigConstants.SHIPING);
                break;
            case R.id.layout_03:
                i.putExtra("url", ConfigConstants.flow_url);
                break;
        /*    case R.id.layout_04:
                i.putExtra("url", ConfigConstants.us_url);
                break;*/
            case R.id.layout_05:
                i.putExtra("url", ConfigConstants.FREIGHT_URL);
                break;
            case R.id.layout_07:
                i.putExtra("url", ConfigConstants.FREIGHT_IMG_URL);
                break;
        }
        mActivity.startActivity(i);
    }
}
