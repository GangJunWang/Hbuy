package au.com.hbuy.aotong.utils.persistentcookiejar;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.BuyPageDetailsActivity;
import au.com.hbuy.aotong.PayOrderBuyDetailsActivity;
import au.com.hbuy.aotong.PayOrderDetailsActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.domain.BuyWaitPayBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.NoDoubleClickListener;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyAddMsgCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import au.com.hbuy.aotong.view.NestFullListView;
import au.com.hbuy.aotong.view.NestFullListViewAdapter;
import au.com.hbuy.aotong.view.NestFullViewHolder;

public class PkgOrderBuyViewHolder extends BaseViewHolder<BuyWaitPayBean> {
    TextView tvNo, tvStatus, tvStatus2, tvNum, btOk, tvTime;
    private Activity mActivity;
    private NestFullListView tvContent;
    List<BuyWaitPayBean.BuyWait> lists = null;
    private CustomProgressDialog progressDialog;
    private String type;

    public PkgOrderBuyViewHolder(ViewGroup parent, Activity activity) {
        super(parent, R.layout.pkg_orderbuy_list_item);
        tvContent = $(R.id.lv_content);
        tvStatus = $(R.id.tv_status);
        /*tvStatus2 = $(R.id.tv_status2);*/
        btOk = $(R.id.bt_commit);
        tvNum = $(R.id.tv_num);
        tvNo = $(R.id.tv_no);
        tvTime = $(R.id.tv_time);
        this.mActivity = activity;
    }

    @Override
    public void setData(final BuyWaitPayBean o) {
        this.tvTime.setText(o.getTime());
        type = o.getType();
        if (type.equals("1")) {
            this.tvNo.setText("代购订单: " + o.getNo());
        } else {
            this.tvNo.setText("代付订单: " + o.getNo());
        }

        lists = o.getGoods();
        if (null != lists) {
            tvContent.setAdapter(new NestFullListViewAdapter<BuyWaitPayBean.BuyWait>(R.layout.pkg_orderbuy_data_list_item, lists) {
                @Override
                public void onBind(int pos, final BuyWaitPayBean.BuyWait testBean, NestFullViewHolder holder) {
                    String title = testBean.getTitle();
                    if ("".equals(title)) {
                        holder.setText(R.id.tv_title, testBean.getLink());
                    } else {
                        holder.setText(R.id.tv_title, title);
                    }
                    String tmp1 = "单价: ¥" + StringUtils.keepDouble(testBean.getMoney());
                    if (!"".equals(testBean.getProp())) {
                        tmp1 += " , 规格:" + testBean.getProp();
                    }
                    holder.setText(R.id.tv_all, tmp1);
                    holder.setOnClickListener(R.id.layout, new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View view) {
                            Intent in = new Intent(mActivity, BuyPageDetailsActivity.class);
                            in.putExtra("no", o.getNo());
                            mActivity.startActivityForResult(in, 2);
                        }
                    });
                    if (!"".equals(testBean.getImg())) {
                        Picasso.with(mActivity)
                                .load(testBean.getImg())
                                .placeholder(R.drawable.buy_empty)
                                .error(R.drawable.buy_default_hint)
                                .fit()
                                .tag(this)
                                .into((ImageView) holder.getView(R.id.iv_img));
                    } else {
                        KLog.d(o.getType());
                        if ("1".equals(o.getType())) {
                            //代购
                            holder.setImageResource(R.id.iv_img, R.drawable.buy_default_hint);
                        } else {
                            //代付
                            holder.setImageResource(R.id.iv_img, R.drawable.waitpay_default);
                        }
                    }

                    String status = testBean.getStatus();
                    String tmp = "";
                    if (type.equals("1")) {
                        //代购
                        if ("1".equals(status)) {
                            tmp = "待算价";
                        } else if ("2".equals(status)) {
                            tmp = "待支付";
                        } else if ("3".equals(status)) {
                            tmp = "已支付";
                        } else if ("4".equals(status)) {
                            tmp = "购买成功";
                        } else if ("5".equals(status)) {
                            tmp = "异常";
                        } else if ("6".equals(status)) {
                            tmp = "退货";
                        }
                    } else {
                        //代付
                        if ("2".equals(status)) {
                            tmp = "待支付";
                        } else if ("3".equals(status)) {
                            tmp = "已支付";
                        } else if ("4".equals(status)) {
                            tmp = "购买成功";
                        } else if ("5".equals(status)) {
                            tmp = "异常";
                        }
                    }
                    holder.setText(R.id.tv_status, tmp);
                }
            });
        }

        this.tvNum.setText("共" + o.getGoods().size() + "件");
        String status = o.getStatus();
        if ("1".equals(status)) {
            this.tvStatus.setText("待算价");
            btOk.setEnabled(false);
        } else if ("2".equals(status)) {
            this.tvStatus.setText("待提交");
        } else if ("3".equals(status)) {
            this.tvStatus.setText("已提交");
            btOk.setEnabled(false);
        } else if ("4".equals(status)) {
            this.tvStatus.setText("等待购买");
            btOk.setEnabled(false);
        } else if ("5".equals(status)) {
            this.tvStatus.setText("购买成功");
            btOk.setEnabled(false);
        } else if ("6".equals(status)) {
            this.tvStatus.setText("已取消");
            btOk.setEnabled(false);
        }

        btOk.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                KLog.d(o.getNo());
             /*   Intent i = new Intent(mActivity, PayOrderBuyDetailsActivity.class);
                i.putExtra("no", o.getNo());
                mActivity.startActivity(i);*/

                //提交订单
                if (NetUtils.hasAvailableNet(mActivity)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                    HashMap parars = new HashMap();
                    parars.put("value", o.getNo());
                    parars.put("type", "2");
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.createOrder, parars, new RespModifyAddMsgCallback(mActivity, progressDialog) {
                        @Override
                        public void onSuccess(String msg) {
                            AppUtils.stopProgressDialog(progressDialog);
                            KLog.d(msg);
                            Intent i = new Intent(mActivity, PayOrderBuyDetailsActivity.class);
                            i.putExtra("no", msg);
                            mActivity.startActivity(i);
                          /*  ShowToastUtils.toast(mActivity, "提交成功");
                            btOk.setVisibility(View.GONE);*/
                        }

                        @Override
                        public void onFail(String str) {
                            if ("-1".equals(str)) {
                                ShowToastUtils.toast(mActivity, "包裹数量不匹配");
                            } else if ("-2".equals(str)) {
                                ShowToastUtils.toast(mActivity, "包裹还没算钱");
                            } else if ("-3".equals(str)) {
                                ShowToastUtils.toast(mActivity, "包裹状态异常");
                            }
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
                }
            /*    Intent i = new Intent(mActivity, PaymentActivity.class);
                i.putExtra("no", o.getNo());
                double allMoney = 0.00;
                DecimalFormat df  = new DecimalFormat("######0.00");
                for (BuyWaitPayBean.BuyWait pkgBean : o.getGoods()) {
                    KLog.d(pkgBean.getMoney());
                    allMoney += StringUtils.toDouble(pkgBean.getMoney());
                }
                i.putExtra("money", df.format(allMoney));
                mActivity.startActivity(i);
                mActivity.finish();*/
            }
        });
    }
}
