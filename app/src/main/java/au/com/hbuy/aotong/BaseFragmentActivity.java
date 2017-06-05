package au.com.hbuy.aotong;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import au.com.hbuy.aotong.greenDao.BuyDbBean;
import au.com.hbuy.aotong.greenDao.GreenDaoManager;
import au.com.hbuy.aotong.greenDao.gen.BuyDbBeanDao;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.view.ClearEditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class BaseFragmentActivity extends FragmentActivity {
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
