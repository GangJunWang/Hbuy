package au.com.hbuy.aotong;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/8/2--11:25.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class ConversationActivity extends FragmentActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_touch_us)
    TextView tvTouchUs;
    private ImageView mBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        ButterKnife.bind(this);

        tvName.setText(SharedUtils.getString(this, "kfname", "Hbuy客服001(美美)"));
        boolean isVisible = SharedUtils.getBoolean(this, "isVisible", false);
        if (isVisible) {
            //显示去下单
            SharedUtils.putBoolean(this, "isVisible", false);
            tvTouchUs.setVisibility(View.VISIBLE);
        } else {
            //不显示
            tvTouchUs.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_touch_us})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_touch_us:
                AppUtils.showActivity(this, WaitPlaceOrderActivity.class);
                finish();
                break;
        }
    }
}
