package au.com.hbuy.aotong;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socks.library.KLog;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.ExtraBean;
import au.com.hbuy.aotong.domain.OrderDetails;
import au.com.hbuy.aotong.domain.OrderDetailsBean;
import au.com.hbuy.aotong.domain.PkgBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.NoDoubleClickListener;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespByPayDetailsCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import au.com.hbuy.aotong.view.NestFullListView;
import au.com.hbuy.aotong.view.NestFullListViewAdapter;
import au.com.hbuy.aotong.view.NestFullViewHolder;
import au.com.hbuy.aotong.view.OverScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* 订单详情页面
*    -----------转运订单
* 1.创建订单
* 2.代付款列表
* 3.历史订单列表
 */
public class PayOrderDetailsActivity extends BaseDetailsActivity implements OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_cart)
    ImageView ivCart;
    /*  @Bind(R.id.iv_menu)
      ImageView ivMenu;*/
    @Bind(R.id.tv_status)
    TextView tvStatus;
    @Bind(R.id.tv_no)
    TextView tvNo;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.cstFullShowListView)
    NestFullListView lvList;
    @Bind(R.id.tv_daofu)
    TextView tvDaofu;
    @Bind(R.id.tv_yunfei)
    TextView tvYunfei;
    @Bind(R.id.tv_allprice)
    TextView tvAllprice;
    @Bind(R.id.bt_ok)
    Button btOk;
    @Bind(R.id.layout01)
    LinearLayout layout01;
    @Bind(R.id.layout_02)
    RelativeLayout layout02;
    @Bind(R.id.tv_chaibao)
    TextView tvChaibao;
    @Bind(R.id.layout_chaibao)
    RelativeLayout layoutChaibao;
    @Bind(R.id.layout_03)
    RelativeLayout layout03;
    @Bind(R.id.tv_xiaoci)
    TextView tvXiaoci;
    @Bind(R.id.layout_xiaoci)
    RelativeLayout layoutXiaoci;
    @Bind(R.id.tv_dabao)
    TextView tvDabao;
    @Bind(R.id.layout_dabao)
    RelativeLayout layoutDabao;
    @Bind(R.id.tv_other)
    TextView tvOther;
    @Bind(R.id.layout_other)
    RelativeLayout layoutOther;
    @Bind(R.id.bt_cancel)
    Button btCancel;
    private CustomProgressDialog progressDialog;
    private String mNOId = "";
    private FragmentActivity mActivity = PayOrderDetailsActivity.this;
    private OrderDetailsBean mBean = null;
    double allMoney = 0.00, daofu = 0.00, chaibao = 0.00, yunfei = 0.00, other = 0.00, xiaoci = 0.00, dabao = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order_details);
        ButterKnife.bind(this);
        layout = (OverScrollView) findViewById(R.id.layout);

        mNOId = getIntent().getStringExtra("no");
        KLog.d(mNOId);
        if (null != mNOId)
            initData();
        setListener();
    }

    public void initData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("no", mNOId);
        if (NetUtils.hasAvailableNet(this)) {
            progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getItemOrder, params, new RespByPayDetailsCallback<OrderDetailsBean>(this, progressDialog) {
                @Override
                public void onSuccess(OrderDetailsBean t) {
                    allMoney = 0.00;
                    daofu = 0.00;
                    chaibao = 0.00;
                    yunfei = 0.00;
                    other = 0.00;
                    xiaoci = 0.00;
                    dabao = 0.00;
                    AppUtils.stopProgressDialog(progressDialog);
                    DecimalFormat df = new DecimalFormat("######0.00");
                    List<OrderDetails> tmpList = t.getOrderList();
                    if (null != tmpList) {
                        for (int i = 0; i < tmpList.size(); i++) {
                            OrderDetails details = tmpList.get(i);
                            ExtraBean bean = details.getExtraBean();
                            if (null != bean) {
                                daofu += StringUtils.toDouble(bean.getArrive_pay());
                                chaibao += StringUtils.toDouble(bean.getOpen_pay());
                            }
                           /* List<PkgBean> pkglist = details.getPkgBeanList();
                            if (null != pkglist) {
                                for (int j = 0; j < pkglist.size(); i++) {
                                }
                            }*/
                        }
                        if (daofu > 0) {
                            tvDaofu.setText("¥" + df.format(daofu));
                        } else {
                            layout02.setVisibility(View.GONE);
                        }
                        if (chaibao > 0) {
                            tvChaibao.setText("¥" + df.format(chaibao));
                        } else {
                            layoutChaibao.setVisibility(View.GONE);
                        }
                    }
                    mBean = t;
//                    KLog.d(t + "---" + t.getTime() + "---" + t.getOrderList().get(1).getId() + "---" + t.getOrderList().get(0).getPkgBeanList().get(0).getCarrier_name() + "---" + t.getOrderList().size());
                    tvTime.setText(t.getTime());
                    tvNo.setText("NO: " + t.getNo() + " | 复制订单号 |");
                    String status = t.getStatus();
                    if ("1".equals(status)) {
                        //ivMenu.setVisibility(View.VISIBLE);
                        btCancel.setVisibility(View.VISIBLE);
                        tvStatus.setText("待付款");
                    } else if ("2".equals(status)) {
                        //    ivMenu.setVisibility(View.VISIBLE);
                        btCancel.setVisibility(View.VISIBLE);
                        tvStatus.setText("付款失败");
                    } else if ("3".equals(status)) {
                        tvStatus.setText("处理中");
                        btOk.setVisibility(View.GONE);
                    } else if ("4".equals(status)) {
                        tvStatus.setText("已付款");
                        btOk.setVisibility(View.GONE);
                    } else if ("5".equals(status)) {
                        layout01.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.wuliu_default));
                        tvStatus.setText("已取消");
                        btOk.setVisibility(View.GONE);
                    } else if ("6".equals(status)) {
                        layout01.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.wuliu_default));
                        tvStatus.setText("超时关闭");
                        btOk.setVisibility(View.GONE);
                    }

                    lvList.setAdapter(new NestFullListViewAdapter<OrderDetails>(R.layout.list_item_details_bywait_pay, t.getOrderList()) {
                        @Override
                        public void onBind(int pos, final OrderDetails testBean, NestFullViewHolder holder) {
                            holder.setText(R.id.tv_name, "打包号:" + testBean.getId());
                            ExtraBean bean = testBean.getExtraBean();
                            if (null != bean) {
                                holder.setText(R.id.tv_shaibao, "拆包费: ¥" + StringUtils.keepDouble(bean.getOpen_pay()));
                                holder.setText(R.id.tv_daofu, "到付费: ¥" + StringUtils.keepDouble(bean.getArrive_pay()));
                            }
                            double allMoney = 0.00;
                            DecimalFormat df = new DecimalFormat("######0.00");
                            for (PkgBean pkgBean : testBean.getPkgBeanList()) {
                                KLog.d(pkgBean.getMoney());
                                allMoney += StringUtils.toDouble(pkgBean.getFreight_pay());
                            }
                            holder.setText(R.id.tv_yunfei, "运费: ¥" + df.format(allMoney));

                            holder.setOnClickListener(R.id.tv_details, new NoDoubleClickListener() {
                                @Override
                                public void onNoDoubleClick(View view) {
                                    Intent i = new Intent(mActivity, WorkOrderDetailsActivity.class);
                                    i.putExtra("no", testBean.getId());
                                    startActivity(i);
                                }
                            });

                            ((NestFullListView) holder.getView(R.id.cstFullShowListView)).setAdapter(new NestFullListViewAdapter<PkgBean>(R.layout.orderdetails_item_list_item, testBean.getPkgBeanList()) {
                                @Override
                                public void onBind(int pos, PkgBean nestBean, NestFullViewHolder holder) {
                                    yunfei += StringUtils.toDouble(nestBean.getFreight_pay());
                                    xiaoci += StringUtils.toDouble(nestBean.getDegauss_pay());
                                    dabao += StringUtils.toDouble(nestBean.getPackage_pay());
                                    other += StringUtils.toDouble(nestBean.getExtra_pay());
                                    KLog.d();
                                    holder.setText(R.id.tv_name, nestBean.getCarrier_name());
                                    holder.setText(R.id.tv_tiji, nestBean.getVolume());
                                    holder.setText(R.id.tv_zhongliang, nestBean.getWeight());
                                    holder.setText(R.id.tv_yufei, StringUtils.keepDouble(nestBean.getFreight_pay()));
                                }
                            });
                        }
                    });
                    if (yunfei > 0) {
                        tvYunfei.setText("¥" + df.format(yunfei));
                    } else {
                        layout03.setVisibility(View.GONE);
                    }
                    if (xiaoci > 0) {
                        tvXiaoci.setText("¥" + df.format(xiaoci));
                    } else {
                        layoutXiaoci.setVisibility(View.GONE);
                    }
                    if (dabao > 0) {
                        tvDabao.setText("¥" + df.format(dabao));
                    } else {
                        layoutDabao.setVisibility(View.GONE);
                    }
                    if (other > 0) {
                        tvOther.setText("¥" + df.format(other));
                    } else {
                        layoutOther.setVisibility(View.GONE);
                    }
                  /*  allMoney = daofu + chaibao + yunfei;
                    KLog.d(daofu + "---" + chaibao + "-----" + yunfei);*/
                    tvAllprice.setText("¥" + StringUtils.keepDouble(t.getMoney()));
                }

                @Override
                public void onFail() {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(PayOrderDetailsActivity.this, "获取订单信息失败", 2);
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                back();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void back() {
        if (null != getIntent().getStringExtra("waitplace")) {
            Intent waitList = new Intent(mActivity, WaitPaymentActivity.class);
            waitList.putExtra("style", "7");
            mActivity.startActivity(waitList);
        }
        finish();
    }

    @OnClick({R.id.iv_back, R.id.iv_cart/*, R.id.iv_menu*/, R.id.tv_no, R.id.bt_ok, R.id.bt_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                back();
                break;
            case R.id.bt_cancel:
                if (NetUtils.hasAvailableNet(mActivity)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                    HashMap params = new HashMap();
                    params.put("no", mNOId);
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.CANCELORDER, params, new RespModifyMsgCallback(mActivity, progressDialog) {
                        @Override
                        public void onSuccess() {
                            ShowToastUtils.toast(mActivity, "订单取消成功", 1);
                            AppUtils.stopProgressDialog(progressDialog);
                            setResult(RESULT_OK);
                            finish();
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
            case R.id.iv_cart:
                AppUtils.goChat(this);
                break;
          /*  case R.id.iv_menu:
                List<String> list = new ArrayList<>();
                list.add("取消订单");
                new SuperDialog.Builder(this)
                        .setAlpha(1f)
                        .setCanceledOnTouchOutside(false)
                        .setItems(list, new SuperDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                switch (position) {
                                    case 0:

                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .build();
                break;*/
            case R.id.tv_no:
                ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("text", mNOId);
                myClipboard.setPrimaryClip(myClip);
                ShowToastUtils.toast(this, "订单号复制成功", 1);
                break;
            case R.id.bt_ok:
                //提交订单
                if (null == mBean) {
                    ShowToastUtils.toast(this, "亲, 稍后再试");
                    return;
                }
                Intent i = new Intent(this, PaymentActivity.class);
                i.putExtra("no", mBean.getNo());
                i.putExtra("coupon", mBean.getCoupon());
                i.putExtra("money", mBean.getUser_balance());
                i.putExtra("Use_balance", mBean.getUse_balance());
                i.putExtra("price", mBean.getReal_money());
                //  i.putExtra("content", mBean.get());
                i.putExtra("num", mBean.getAvailable_coupon());
                i.putExtra("type", mBean.getType());
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
