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

import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.Good;
import au.com.hbuy.aotong.domain.OrderDetails;
import au.com.hbuy.aotong.domain.OrderDetailsBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespByPayBuyDetailsCallback;
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
public class PayOrderBuyDetailsActivity extends BaseDetailsActivity implements OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_cart)
    ImageView ivCart;
    @Bind(R.id.iv_menu)
    ImageView ivMenu;
    @Bind(R.id.tv_status)
    TextView tvStatus;
    @Bind(R.id.tv_status_hint)
    TextView tvStatusHint;
    @Bind(R.id.tv_no)
    TextView tvNo;
    @Bind(R.id.iv_hint)
    ImageView ivHint;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.cstFullShowListView)
    NestFullListView lvList;
    @Bind(R.id.tv_allprice)
    TextView tvAllprice;
    @Bind(R.id.bt_ok)
    Button btOk;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.bt_ok_layout)
    LinearLayout btOkLayout;
    @Bind(R.id.tv_details)
    TextView tvDetails;
    @Bind(R.id.layout01)
    RelativeLayout layout01;
    private CustomProgressDialog progressDialog;
    private String mNOId = "", mId = null, mType = null;
    private FragmentActivity mActivity = PayOrderBuyDetailsActivity.this;
    private OrderDetailsBean mBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order_buy_details);
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
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getItemOrder, params, new RespByPayBuyDetailsCallback<OrderDetailsBean>(this, progressDialog) {
                @Override
                public void onSuccess(OrderDetailsBean t) {
                    AppUtils.stopProgressDialog(progressDialog);
                    mBean = t;
//                    KLog.d(t + "---" + t.getTime() + "---" + t.getOrderList().get(1).getId() + "---" + t.getOrderList().get(0).getPkgBeanList().get(0).getCarrier_name() + "---" + t.getOrderList().size());
                    tvTime.setText(t.getTime());
                    tvNo.setText("NO: " + t.getNo() + " | 复制订单号 |");
                    String status = t.getStatus();
                    if ("1".equals(status)) {
                        ivMenu.setVisibility(View.VISIBLE);
                        tvStatus.setText("待付款");
                        btOkLayout.setVisibility(View.VISIBLE);
                    } else if ("2".equals(status)) {
                        ivMenu.setVisibility(View.VISIBLE);
                        tvStatus.setText("付款失败");
                        btOkLayout.setVisibility(View.VISIBLE);
                    } else if ("3".equals(status)) {
                        tvStatus.setText("处理中");
                        btOkLayout.setVisibility(View.GONE);
                    } else if ("4".equals(status)) {
                        tvStatus.setText("已付款");
                        btOkLayout.setVisibility(View.GONE);
                    } else if ("5".equals(status)) {
                        tvStatus.setText("已取消");
                        layout01.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.wuliu_default));
                        btOkLayout.setVisibility(View.GONE);
                    } else if ("6".equals(status)) {
                        tvStatus.setText("超时关闭");
                        layout01.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.wuliu_default));
                        btOkLayout.setVisibility(View.GONE);
                    }
                    tvAllprice.setText("¥" + StringUtils.keepDouble(t.getMoney()));
                    OrderDetails details = t.getOrderList().get(0);
                    List<Good> list = details.getBuyList();
                    if (null != list) {
                        mId = details.getId();
                        tvName.setText(mId);
                        lvList.setAdapter(new NestFullListViewAdapter<Good>(R.layout.list_item_details_buy_pay, list) {
                            @Override
                            public void onBind(int pos, final Good testBean, NestFullViewHolder holder) {

                                holder.setText(R.id.tv_name, testBean.getTitle());
                                holder.setText(R.id.tv_price, "¥" + StringUtils.keepDouble(testBean.getMoney()));
                                holder.setText(R.id.tv_yunfei, "运费: ¥" + StringUtils.keepDouble(testBean.getFreight()));
                                holder.setText(R.id.tv_num, "数量: " + testBean.getNum());
                                //   holder.setText(R.id.tv_mk, testBean.getId());
                              /*  double allMoney = 0.00;
                                DecimalFormat df = new DecimalFormat("######0.00");
                                for (PkgBean pkgBean : testBean.getPkgBeanList()) {
                                    KLog.d(pkgBean.getMoney());
                                    allMoney += StringUtils.toDouble(pkgBean.getMoney());
                                }*/
                                //     holder.setText(R.id.tv_yunfei, "运费: ¥" + df.format(allMoney));
                                mType = testBean.getHelpbuy_type();
                                if (!"".equals(testBean.getImg())) {
                                    Picasso.with(mActivity)
                                            .load(testBean.getImg())
                                            .placeholder(R.drawable.buy_empty)
                                            .error(R.drawable.buy_default_hint)
                                            .fit()
                                            .tag(this)
                                            .into((ImageView) holder.getView(R.id.iv_img));
                                } else {
                                    if ("1".equals(testBean.getType())) {
                                        //代购
                                        holder.setImageResource(R.id.iv_name, R.drawable.buy_default_hint);
                                    } else {
                                        //代付
                                        holder.setImageResource(R.id.iv_name, R.drawable.waitpay_default);
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFail() {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(PayOrderBuyDetailsActivity.this, "获取订单信息失败", 2);
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
                KLog.d();
                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.iv_back, R.id.iv_cart, R.id.iv_menu, R.id.tv_no, R.id.bt_ok, R.id.tv_details})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_cart:
                AppUtils.goChat(this);
                break;
            case R.id.iv_menu:
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
                                        if (NetUtils.hasAvailableNet(mActivity)) {
                                            progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                                            HashMap params = new HashMap();
                                            params.put("no", mNOId);
                                            ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.CANCELORDER, params, new RespModifyMsgCallback(mActivity) {
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
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .build();
                break;
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
            case R.id.tv_details:
                if (null == mId || null == mType) {
                    ShowToastUtils.toast(this, "亲, 刷新后再试");
                    return;
                }
                Intent in = new Intent(mActivity, BuyPageDetailsActivity.class);
                in.putExtra("no", mId);
                startActivity(in);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
