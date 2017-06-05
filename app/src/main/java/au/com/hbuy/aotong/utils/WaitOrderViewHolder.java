package au.com.hbuy.aotong.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.socks.library.KLog;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.PayOrderDetailsActivity;
import au.com.hbuy.aotong.PaymentActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.ExtraBean;
import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.domain.OrderDetails;
import au.com.hbuy.aotong.domain.OrderDetailsBean;
import au.com.hbuy.aotong.domain.PkgBean;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespByPayDetailsCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import au.com.hbuy.aotong.view.NestFullListView;
import au.com.hbuy.aotong.view.NestFullListViewAdapter;
import au.com.hbuy.aotong.view.NestFullViewHolder;

public class WaitOrderViewHolder extends BaseViewHolder<Order> implements View.OnClickListener{
    private TextView mContent, mTvNumber, mTime, mStatus, mStyle, mAllPrice;
    private TextView /*mCancel, */mPay;
    private Activity mActivity;
    private String mNo = null, mMoney = null;
    private CustomProgressDialog progressDialog;

    public WaitOrderViewHolder(ViewGroup parent, Activity activity) {
        super(parent, R.layout.waitpayment_order_list_item);
        mTvNumber = $(R.id.tv_no);
        mStyle = $(R.id.tv_style);
        mTime = $(R.id.tv_time);
        mStatus = $(R.id.tv_status);
        mContent = $(R.id.tv_content);
        mAllPrice = $(R.id.tv_money);
    //    mCancel = $(R.id.bt_cancel);
        mPay = $(R.id.bt_pay);
        this.mActivity = activity;
    }
    @Override
    public void setData(final Order o){
        KLog.d(o + o.getMoney());
        mNo = o.getNo();
        mTvNumber.setText("订单号: " + mNo);
        if ("1".equals(o.getType())) {
            mStyle.setText("转运订单");
            mStyle.setBackgroundResource(R.drawable.order_type_transfer);
        } else {
            mStyle.setText("代购订单");
            mStyle.setBackgroundResource(R.drawable.order_type_daigou);
        }
        mTime.setText("创建时间: " + o.getTime());
        String status = o.getStatus();
        if ("1".equals(status)) {
            mStatus.setText("等待支付");
            mPay.setVisibility(View.VISIBLE);
     //       mCancel.setVisibility(View.VISIBLE);
        } else if ("2".equals(status)) {
            mPay.setVisibility(View.VISIBLE);
       //     mCancel.setVisibility(View.VISIBLE);
            mStatus.setText("付款失败");
        } else if ("3".equals(status)) {
            mPay.setVisibility(View.GONE);
         //   mCancel.setVisibility(View.GONE);
            mStatus.setText("处理中");
        } else if ("4".equals(status)) {
            mPay.setVisibility(View.GONE);
        //    mCancel.setVisibility(View.GONE);
            mStatus.setText("已付款");
        } else if ("5".equals(status)) {
            mPay.setVisibility(View.GONE);
        //    mCancel.setVisibility(View.GONE);
            mStatus.setText("已取消");
        } else if ("6".equals(status)) {
            mPay.setVisibility(View.GONE);
        //    mCancel.setVisibility(View.GONE);
            mStatus.setText("超时关闭");
        }

        mContent.setText(o.getContent());
        mMoney = o.getMoney();
        mAllPrice.setText("实付金额: ¥" + StringUtils.keepDouble(mMoney));
    //    mCancel.setOnClickListener(this);
        mPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_cancel:
                //cancel order
                if (NetUtils.hasAvailableNet(mActivity)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                    HashMap params = new HashMap();
                    params.put("no", mNo);
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.CANCELORDER, params, new RespModifyMsgCallback(mActivity, progressDialog) {
                        @Override
                        public void onSuccess() {
                            ShowToastUtils.toast(mActivity, "订单取消成功", 1);
                            mStatus.setText("已取消");
                            AppUtils.stopProgressDialog(progressDialog);
                            mPay.setVisibility(View.GONE);
                   //         mCancel.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFail(String str) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if ("0".equals(str)) {
                                ShowToastUtils.toast(mActivity, "订单取消失败", 2);
                            } else if ("-1".equals(str)) {
                                ShowToastUtils.toast(mActivity, "错误(订单状态不许取消)");
                            } else if ("-2".equals(str)) {
                                ShowToastUtils.toast(mActivity, "订单不存在");
                            }
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
                }
                break;
            case R.id.bt_pay:
                //pay order
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("no", mNo);
                if (NetUtils.hasAvailableNet(mActivity)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.getItemOrder, params, new RespByPayDetailsCallback<OrderDetailsBean>(mActivity, progressDialog) {
                        @Override
                        public void onSuccess(OrderDetailsBean t) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != t) {
                                if (StringUtils.toInt(t.getStatus()) < 3) {
                                    Intent i = new Intent(mActivity, PaymentActivity.class);
                                    i.putExtra("no", t.getNo());
                                    i.putExtra("coupon", t.getCoupon());
                                    i.putExtra("money", t.getUser_balance());
                                    i.putExtra("Use_balance", t.getUse_balance());
                                    i.putExtra("price", t.getReal_money());
                                    //  i.putExtra("content", mBean.get());
                                    i.putExtra("num", t.getAvailable_coupon());
                                    i.putExtra("type", t.getType());
                                    mActivity.startActivity(i);
                                } else {
                                    ShowToastUtils.toast(mActivity, "该订单不存在");
                                }
                            }
                        }
                        @Override
                        public void onFail() {
                            AppUtils.stopProgressDialog(progressDialog);
                            ShowToastUtils.toast(mActivity, "获取订单信息失败", 2);
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
                }

                break;
        }
    }
}
