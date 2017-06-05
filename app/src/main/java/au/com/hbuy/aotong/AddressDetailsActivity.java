package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.HashMap;
import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAddTCallback;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class AddressDetailsActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_edit)
    TextView tvEdit;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.tv_nation)
    TextView tvNation;
    @Bind(R.id.tv_zip)
    TextView tvZip;
    @Bind(R.id.tv_details)
    TextView tvDetails;
    private Address a;
    private final static int MODIFYADDRESS = 1;
    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_details);
        ButterKnife.bind(this);
        mIntent = getIntent();
        String id = mIntent.getStringExtra("id");
        if (null != id) {
            if (NetUtils.hasAvailableNet(this)) {
                HashMap params = new HashMap();
                params.put("id", id);
                ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getAddressDetails, params, new RespAddTCallback<Address>(this) {
                    @Override
                    public void onSuccess(Address o) {
                        if (null != o) {
                            tvName.setText("收件人: " + o.getReceiver());
                            tvPhone.setText("电话: " + o.getPhonecode() + " " + o.getPhone());
                            tvNation.setText("国家: " + o.getCountry());
                            tvZip.setText("邮编: " + o.getZip());
                            tvDetails.setText(o.getCountry() + o.getCity() + o.getAddress());
                            a = o;
                        }
                    }

                    @Override
                    public void onFail(Address o) {

                    }
                });
            } else {
                ShowToastUtils.toast(this, getString(R.string.no_net_hint), 3);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        KLog.d("code = " + resultCode + "---" + RESULT_OK + "---" + data + "-" + requestCode);
        switch (resultCode) {
            case RESULT_OK:
                if (null != data) {
                    a = (Address) data.getSerializableExtra("address");
                    tvName.setText("收件人: " + a.getReceiver());
                    tvPhone.setText("电话: " + a.getPhone());
                    tvNation.setText("国家: " + a.getCountry());
                    tvZip.setText("邮编: " + a.getZip());
                    tvDetails.setText(a.getAddress());
                }
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_edit:
                if (null != a) {
                    Intent i = new Intent(this, AddressEditActivity.class);
                    boolean booleanNum = mIntent.getBooleanExtra("num", false);
                    i.putExtra("address", a);
                    i.putExtra("num", booleanNum);
                    startActivityForResult(i, MODIFYADDRESS);
                }
                break;
        }
    }
}
