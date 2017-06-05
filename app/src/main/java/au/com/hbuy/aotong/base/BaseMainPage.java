package au.com.hbuy.aotong.base;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import au.com.hbuy.aotong.R;

/**
 * 主页下3个子页面的基类
 *
 */
public class BaseMainPage {
    public AppCompatActivity mActivity;
    public View mRootView;// 布局对象

    public FrameLayout flContent;// 内容

    public BaseMainPage(AppCompatActivity activity) {
        mActivity = activity;
        initViews();
    }

    /**
     * 初始化布局
     */
    public void initViews() {
        mRootView = View.inflate(mActivity, R.layout.base_page, null);
        flContent = (FrameLayout) mRootView.findViewById(R.id.fl_content);
    }

    /**
     * 初始化数据
     */
    public void initData() {

    }
}