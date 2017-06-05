package au.com.hbuy.aotong;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by yangwei on 2017/1/10--17:35.
 * <p>
 * E-Mail:yangwei199402@gmail.com
 */

public class BaseActivity extends Activity {
    public Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
