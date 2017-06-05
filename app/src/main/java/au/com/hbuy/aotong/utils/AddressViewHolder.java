package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.AddressEditActivity;
import au.com.hbuy.aotong.PaymentActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.ReceiverAddressActivity;
import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.domain.OrderDetailsBean;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespByPayDetailsCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;

public class AddressViewHolder extends BaseViewHolder<Address>/* implements View.OnClickListener*/ {
    public TextView tvName, tv_Phone, tv_details, tv_edit, tv_delete;
    public ImageView iv_default;
    private Activity mActivity;
    public CheckBox cb_default;
    private OnItemSubClicked onItemSubClicked;

    public AddressViewHolder(ViewGroup parent, Activity activity, OnItemSubClicked onItemSubClicked) {
        super(parent, R.layout.address_list_item);
        tvName = $(R.id.tv_name);
        tv_Phone = $(R.id.tv_number);
        tv_details = $(R.id.tv_details);
        tv_edit = $(R.id.tv_edit);
        tv_delete = $(R.id.tv_delete);
        iv_default = $(R.id.iv_default);
        cb_default = $(R.id.cb_default);
        this.mActivity = activity;
        this.onItemSubClicked = onItemSubClicked;
    }

    @Override
    public void setData(final Address o) {
        this.tvName.setText("收件人: " + o.getReceiver());
        this.tv_Phone.setText("+" + o.getPhonecode() + " " + o.getPhone());
        this.tv_details.setText(o.getCountry() + " " + o.getCity() + " " + o.getAddress());
        KLog.d("default=" + o.getIs_default() + " 邮编：" + o.getZip());
        if ("1".equals(o.getIs_default())) {
            this.iv_default.setVisibility(View.VISIBLE);
            this.cb_default.setChecked(true);
            this.cb_default.setClickable(false);
        } else {
            this.iv_default.setVisibility(View.GONE);
            this.cb_default.setChecked(false);
            this.cb_default.setClickable(true);
        }
        this.cb_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtils.hasAvailableNet(mActivity)) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", o.getId());
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.setDefaultAddress, params, new RespModifyMsgCallback(mActivity) {
                        @Override
                        public void onSuccess() {
                           /* for (Address a : mList) {
                                a.setIs_default("0");
                            }
                            mList.get(StringUtils.toInt(mAdd.getId())).setIs_default("1");*/
                            KLog.d();
                            //       this.notifyDataSetChanged();
                            onItemSubClicked.onItemSubClicked(o.getId());
                            ShowToastUtils.toast(mActivity, "设置默认地址成功", 1);
                        }

                        @Override
                        public void onFail(String status) {
                            ShowToastUtils.toast(mActivity, "设置默认地址失败", 2);
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
                }
            }
        });

        this.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtils.hasAvailableNet(mActivity)) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", o.getId());
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.deleteAddress, params, new RespModifyMsgCallback(mActivity) {
                        @Override
                        public void onSuccess() {
                            onItemSubClicked.onDeleteSub(o.getId());
                            ShowToastUtils.toast(mActivity, "删除地址成功", 1);
                        }

                        @Override
                        public void onFail(String status) {
                            ShowToastUtils.toast(mActivity, "删除地址失败", 2);
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
                }
            }
        });

        this.tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, AddressEditActivity.class);
                i.putExtra("address", o);
                mActivity.startActivityForResult(i, ReceiverAddressActivity.EDITADDRESS);
            }
        });
    }

    public interface OnItemSubClicked {
        void onItemSubClicked(String id);
        void onDeleteSub(String id);
    }

   /* @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_default:
                if (NetUtils.hasAvailableNet(mActivity)) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", mAdd.getId());
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.setDefaultAddress, params, new RespModifyMsgCallback(mActivity) {
                        @Override
                        public void onSuccess() {
                            for (Address a : mList) {
                                a.setIs_default("0");
                            }
                            mList.get(StringUtils.toInt(mAdd.getId())).setIs_default("1");
                     //       this.notifyDataSetChanged();
                            ShowToastUtils.toast(mActivity, "设置默认地址成功", 1);
                        }

                        @Override
                        public void onFail(String status) {
                            ShowToastUtils.toast(mActivity, "设置默认地址失败", 2);
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
                }
                break;
            case R.id.tv_edit:
                Intent i = new Intent(mActivity, AddressEditActivity.class);
                i.putExtra("address", mAdd);
                mActivity.startActivityForResult(i, ReceiverAddressActivity.EDITADDRESS);
                break;
            case R.id.tv_delete:
                if (NetUtils.hasAvailableNet(mActivity)) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", mAdd.getId());
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.deleteAddress, params, new RespModifyMsgCallback(mActivity) {
                        @Override
                        public void onSuccess() {
//                            mList.remove(adapterPosition);
//                            mAdapter.notifyItemRemoved(adapterPosition);
//                            mAdapter.notifyDataSetChanged();
                            ShowToastUtils.toast(mActivity, "删除地址成功", 1);
                        }

                        @Override
                        public void onFail(String status) {
                            ShowToastUtils.toast(mActivity, "删除地址失败", 2);
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
                }
                break;
        }
    }*/

}
