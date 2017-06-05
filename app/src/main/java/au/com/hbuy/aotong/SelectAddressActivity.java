package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAddTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyAddMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class SelectAddressActivity extends BaseActivity implements View.OnClickListener {
    private CustomProgressDialog mDialog;
    private ImageView mBack;
    private ListView mAddressListView;
    private BaseAdapter mAdapter;
    private TextView mManage;
    private Button mNext;
    private List<Address> mList = new ArrayList<>();
    private String mAddressId, pkgids, mId;
    private Activity mActivity = SelectAddressActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);
        Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        pkgids = intent.getStringExtra("pkgids");
        mNext = (Button) findViewById(R.id.bt_next);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mAddressListView = (ListView) findViewById(R.id.lv_address);
        mManage = (TextView) findViewById(R.id.tv_manage);

        mManage.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mNext.setOnClickListener(this);
        initData();
    }

    private void initData() {
        if (NetUtils.hasAvailableNet(this)) {
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getAddresses + "/" + mId, new RespAddTCallback<List<Address>>(this) {
                @Override
                public void onSuccess(List<Address> Addresss) {
                    mList = Addresss;
                    mAdapter = new AddressAdapter(Addresss);
                    mAddressListView.setAdapter(mAdapter);
                }

                @Override
                public void onFail(List<Address> Addresss) {
                    ShowToastUtils.toast(SelectAddressActivity.this, "没有地址,请添加地址");
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint));
        }

        mAddressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAddressId = mList.get(i).getId();
                if (NetUtils.hasAvailableNet(mActivity)) {
                    mDialog = AppUtils.startProgressDialog(mActivity, "", mDialog);
                    HashMap params = new HashMap();
                    params.put("address_id", mAddressId);
                    params.put("pkgs", pkgids);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SETPKGADDRESS, params, new RespModifyMsgCallback(mActivity, mDialog) {
                        @Override
                        public void onSuccess() {
                            AppUtils.stopProgressDialog(mDialog);
                            mAdapter.notifyDataSetChanged();
                            ShowToastUtils.toast(mActivity, "设置地址成功", 1);
                        }

                        @Override
                        public void onFail(String status) {
                            mAddressId = null;
                            AppUtils.stopProgressDialog(mDialog);
                            if ("-1".equals(status)) {
                                ShowToastUtils.toast(mActivity, "提交了不同国家的包裹");
                            } else if ("-2".equals(status)) {
                                ShowToastUtils.toast(mActivity, "地址没找到");
                            } else if ("-3".equals(status)) {
                                ShowToastUtils.toast(mActivity, "找到的包裹和提交的包裹数量不一致");
                            } else if ("-4".equals(status)) {
                                ShowToastUtils.toast(mActivity, "提交包裹参数为空");
                            } else {
                                ShowToastUtils.toast(mActivity, "失败", 2);
                            }
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_manage:
                if (AppUtils.isNotFastClick()) {
                    Intent intent = new Intent(this, ReceiverAddressActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.bt_next:
                if (null == mAddressId) {
                    ShowToastUtils.toast(mActivity, "请选择地址");
                    return;
                }
                if (NetUtils.hasAvailableNet(this)) {
                    mDialog = AppUtils.startProgressDialog(mActivity, "", mDialog);
                    HashMap parars = new HashMap();
                    parars.put("value", pkgids);
                    //      parars.put("address_id", mDefaultAddressId);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.createOrder, parars, new RespModifyAddMsgCallback(mActivity, mDialog) {
                        @Override
                        public void onSuccess(String msg) {
                            AppUtils.stopProgressDialog(mDialog);
                            KLog.d(msg);
                            Intent intent = new Intent(mActivity, WaitPlaceOrderActivity.class);
                            intent.putExtra("no", msg);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onFail(String str) {
                            if ("-1".equals(str)) {
                                ShowToastUtils.toast(mActivity, "包裹数量不匹配");
                            } else if ("-2".equals(str)) {
                                ShowToastUtils.toast(mActivity, "运费异常");
                            } else if ("-3".equals(str)) {
                                ShowToastUtils.toast(mActivity, "包裹状态异常");
                            } else if ("0".equals(str)) {
                                ShowToastUtils.toast(mActivity, "失败", 2);
                            }
                            AppUtils.stopProgressDialog(mDialog);
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(mDialog);
    }

    class AddressAdapter extends BaseAdapter {
        private List<Address> list;

        public AddressAdapter(List<Address> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int j, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(SelectAddressActivity.this, R.layout.select_address_list_item, null);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.address = (TextView) convertView.findViewById(R.id.tv_address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Address a = list.get(j);
            final String id = a.getId();
            final String name = a.getReceiver();
            final String phone = a.getPhone();
            final String address = a.getAddress();
            final String city = a.getCity();
            final String nation = a.getCountry();
            KLog.d(mAddressId + "---" + id);
            if (id.equals(mAddressId)) {
                holder.address.setText("【已选地址】收货地址:" + nation + city + address);
            } else {
                holder.address.setText("收货地址:" + nation + city + address);
            }
            holder.name.setText("收件人: " + name);
            holder.phone.setText(phone);

            return convertView;
        }
    }

    private class ViewHolder {
        private TextView name;
        private TextView phone;
        private TextView address;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            if (requestCode == 1) {
                initData();
            }
        }
    }
}
