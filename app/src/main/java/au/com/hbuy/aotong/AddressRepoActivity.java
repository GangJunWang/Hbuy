package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;

import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespRepoAddressCallback;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class AddressRepoActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_name)
    TextView mName;
    @Bind(R.id.tv_name_copy)
    TextView mNameCopy;
    @Bind(R.id.tv_phone)
    TextView mPhone;
    @Bind(R.id.tv_phone_copy)
    TextView mPhoneCopy;
    @Bind(R.id.tv_address_copy)
    TextView mAddressCopy;
    @Bind(R.id.tv_address)
    TextView mAddress;
    @Bind(R.id.tv_zip)
    TextView mZip;
    @Bind(R.id.tv_zip_copy)
    TextView mZipCopy;
    @Bind(R.id.bt_copy)
    Button mCopy;
    @Bind(R.id.layout_title)
    RelativeLayout layoutTitle;
    @Bind(R.id.tv_gochat)
    TextView tvGochat;
    @Bind(R.id.tv_issue)
    TextView tvIssue;
    private Address bean;
    private FragmentActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_repo);
        mContext = AddressRepoActivity.this;
        ButterKnife.bind(this);
       /* AddressSaveBean bean = AppUtils.getSaveBeanClass("repoAddress", AddressSaveBean.class);
        if (null != bean) {
            Address address = bean.getAddress();
            mName.setText(address.getName());
            mAddress.setText(address.getAddress());
            mZip.setText(address.getZip());
            mPhone.setText(address.getPhone());
        }*/
        mName.setText(SharedUtils.getString(this, "repo_name", "服务器异常"));
        mAddress.setText(SharedUtils.getString(this, "repo_address", "服务器异常"));
        mZip.setText(SharedUtils.getString(this, "repo_zip", "服务器异常"));
        mPhone.setText(SharedUtils.getString(this, "repo_phone", "服务器异常"));
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvGochat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.goChat(mContext);
            }
        });

        tvIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, FaqActivity.class);
                i.putExtra("url", ConfigConstants.faq_url);
                mContext.startActivity(i);
            }
        });

        if (mName.getText().toString().trim().equals("服务器异常")) {
            getData();
        }
    }

    private void getData() {
        if (NetUtils.hasAvailableNet(this)) {
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getRepoAddress, new RespRepoAddressCallback<Address>(this) {
                @Override
                public void onSuccess(Address address) {
                    if (null != address) {
                        bean = address;
                        mName.setText(address.getName());
                        mAddress.setText(address.getAddress());
                        mZip.setText(address.getZip());
                        mPhone.setText(address.getPhone());
                    }
                }

                @Override
                public void onFail(String str) {
                    ShowToastUtils.toast(AddressRepoActivity.this, "获取地址失败", 2);
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint), 3);
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_name_copy, R.id.tv_phone_copy, R.id.tv_address_copy, R.id.tv_zip_copy, R.id.bt_copy})
    public void onClick(View view) {
        String name = "", phone = "", address = "", zip = "";
        name = mName.getText().toString().trim();
        phone = mPhone.getText().toString().trim();
        address = mAddress.getText().toString().trim();
        zip = mZip.getText().toString().trim();
        String tmp = "";
        switch (view.getId()) {
            case R.id.tv_name_copy:
                tmp = "收件人: " + name;
                StringUtils.copyText(tmp, this, "复制成功");
                break;
            case R.id.tv_phone_copy:
                tmp = "收件电话: " + phone;
                StringUtils.copyText(tmp, this, "复制成功");
                break;
            case R.id.tv_address_copy:
                tmp = "仓库地址: " + address;
                StringUtils.copyText(tmp, this, "复制成功");
                break;
            case R.id.tv_zip_copy:
                tmp = "邮编: " + zip;
                StringUtils.copyText(tmp, this, "复制成功");
                break;
            case R.id.bt_copy:
                tmp = "收件人: " + name + "\n收件电话: " + phone + "\n仓库地址: " + address + "\n邮编: " + zip;
                ClipboardManager myClipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("text", tmp);
                myClipboard.setPrimaryClip(myClip);
                new SuperDialog.Builder(mContext).setRadius(10)
                        .setAlpha(1f)
                        .setTitle("温馨提示").setMessage(" 亲~已为您复制好Hbuy中转地址信息哟,快将您购买的物品寄到Hbuy吧!在首页“添加包裹”能及时更新物流哟！")
                        .setPositiveButton("好的", new SuperDialog.OnClickPositiveListener() {
                            @Override
                            public void onClick(View v) {
                                mContext.finish();
                            }
                        }).build();
                break;
        }
    }
}
