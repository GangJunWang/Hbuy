package au.com.hbuy.aotong;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.socks.library.KLog;

import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import au.com.hbuy.aotong.view.OverScrollView;


public class BaseDetailsActivity extends BaseFragmentActivity implements OverScrollView.OverScrollListener, OverScrollView.OverScrollTinyListener {
    public OverScrollView layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder_details);

    }

    public void setListener() {
        layout.setOverScrollListener(this);
        layout.setOverScrollTinyListener(this);
        //   scrollLoosen();
        layout.setOverScrollTrigger(60);
    }
    public void initData() {
       //刷新数据
    }

    @Override
    public void headerScroll() {
        initData();
    }

    @Override
    public void footerScroll() {

    }

    @Override
    public void scrollDistance(int tinyDistance, int totalDistance) {

    }

    @Override
    public void scrollLoosen() {

    }
}
