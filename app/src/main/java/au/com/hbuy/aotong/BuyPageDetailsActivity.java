package au.com.hbuy.aotong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.domain.BuyDetailsBean;
import au.com.hbuy.aotong.domain.Good;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAddTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyAddMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespRepoAddressCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import au.com.hbuy.aotong.view.NestFullListView;
import au.com.hbuy.aotong.view.NestFullListViewAdapter;
import au.com.hbuy.aotong.view.NestFullViewHolder;
import au.com.hbuy.aotong.view.OverScrollView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BuyPageDetailsActivity extends BaseDetailsActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_cart)
    ImageView ivCart;
    @Bind(R.id.iv_menu)
    ImageView ivMenu;
    @Bind(R.id.tv_no)
    TextView tvNo;
    @Bind(R.id.tv_status)
    TextView tvStatus;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.tv_details)
    TextView tvDetails;
    @Bind(R.id.lv_transter_list)
    NestFullListView lvTransterList;
    @Bind(R.id.tv_note)
    TextView tvNote;
    @Bind(R.id.bt_ok)
    Button btOk;
    @Bind(R.id.tv_all_money)
    TextView tvAllMoney;
    @Bind(R.id.layout_address)
    LinearLayout layoutAddress;
    private FragmentActivity mActivity = BuyPageDetailsActivity.this;
    private double allMoney = 0.00;
    private String mNo = null, mType;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buypage_details);
        ButterKnife.bind(this);
        layout = (OverScrollView) findViewById(R.id.layout);

        Intent intent = getIntent();
        mNo = intent.getStringExtra("no");
       /* mType = intent.getStringExtra("type");
        if (null != mType"1".equals(mType)) {
            tvTitle.setText("代购详情");
            tvNo.setText("代购单号: " + mNo);
            if (NetUtils.hasAvailableNet(this)) {
                ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getRepoAddress, new RespRepoAddressCallback<Address>(this) {
                    @Override
                    public void onSuccess(Address address) {
                        if (null != address) {
                            tvName.setText(address.getName());
                            tvPhone.setText(address.getPhone());
                            tvDetails.setText(address.getAddress());
                        }
                    }

                    @Override
                    public void onFail(String str) {
                        ShowToastUtils.toast(mActivity, "获取地址失败");
                    }

                });
            } else {
                ShowToastUtils.toast(this, getString(R.string.no_net_hint));
            }
        } else {
            layoutAddress.setVisibility(View.GONE);
            tvTitle.setText("代付详情");
            tvNo.setText("代付单号: " + mNo);
        }*/

        initData();
        setListener();
    }

    @Override
    public void initData() {
        if (null == mNo) {
            ShowToastUtils.toast(this, "获取详情失败", 2);
            return;
        }
        if (NetUtils.hasAvailableNet(this)) {
            progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
            HashMap params = new HashMap();
            params.put("no", mNo);
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getItemBUY, params, new RespAddTCallback<BuyDetailsBean>(this, progressDialog) {
                @Override
                public void onSuccess(final BuyDetailsBean bean) {
                    AppUtils.stopProgressDialog(progressDialog);
                    mType = bean.getType();
                    if ("1".equals(mType)) {
                        //代购
                        tvTitle.setText("代购详情");
                        tvNo.setText("代购单号: " + mNo);
                        if (NetUtils.hasAvailableNet(mActivity)) {
                            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getRepoAddress, new RespRepoAddressCallback<Address>(mActivity) {
                                @Override
                                public void onSuccess(Address address) {
                                    if (null != address) {
                                        tvName.setText(address.getName());
                                        tvPhone.setText(address.getPhone());
                                        tvDetails.setText(address.getAddress());
                                    }
                                }

                                @Override
                                public void onFail(String str) {
                                    ShowToastUtils.toast(mActivity, "获取地址失败", 2);
                                }

                            });
                        } else {
                            ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint), 3);
                        }
                    } else {
                        //代付
                        layoutAddress.setVisibility(View.GONE);
                        tvTitle.setText("代付详情");
                        tvNo.setText("代付单号: " + mNo);
                    }

                    tvTime.setText(bean.getTime());
                    String status = bean.getStatus();
                    KLog.d(bean.getTime() + bean + "--" + bean.getStatus());
                    String tmp = "";
                    if ("1".equals(status)) {
                        tmp = "待算价";
                        ivMenu.setVisibility(View.VISIBLE);
                    } else if ("2".equals(status)) {
                        tmp = "待提交";
                        btOk.setVisibility(View.VISIBLE);
                        ivMenu.setVisibility(View.VISIBLE);
                    } else if ("3".equals(status)) {
                        //  btOk.setVisibility(View.VISIBLE);
                        tmp = "已提交";
                    } else if ("4".equals(status)) {
                        tmp = "等待购买";
                    } else if ("5".equals(status)) {
                        tmp = "购买成功";
                    } else if ("6".equals(status)) {
                        tmp = "取消";
                    }
                    tvStatus.setText(tmp);
                    if (!"".equals(bean.getNote())) {
                        tvNote.setText(bean.getNote());
                    } else {
                        tvNote.setText("没有备注");
                    }
                    lvTransterList.setAdapter(new NestFullListViewAdapter<Good>(R.layout.pkg_orderbuy_data_list_item, bean.getGoods()) {
                        @Override
                        public void onBind(int pos, final Good testBean, NestFullViewHolder holder) {
                            String title = testBean.getTitle();
                            if ("".equals(title)) {
                                holder.setText(R.id.tv_title, testBean.getLink());
                            } else {
                                holder.setText(R.id.tv_title, title);
                            }
                            String tmp = "单价: ¥" + StringUtils.keepDouble(testBean.getMoney());
                            if (!"".equals(testBean.getProp())) {
                                tmp += ", 规格:" + testBean.getProp();
                            }
                            holder.setText(R.id.tv_all, tmp);

                            String status = testBean.getStatus();
                            String statusStr = "";
                            if ("1".equals(status)) {
                                statusStr = "待算价";
                            } else if ("2".equals(status)) {
                                statusStr = "待支付";
                            } else if ("3".equals(status)) {
                                statusStr = "已支付";
                            } else if ("4".equals(status)) {
                                statusStr = "购买成功";
                            } else if ("5".equals(status)) {
                                statusStr = "异常";
                            } else if ("6".equals(status)) {
                                statusStr = "退货";
                            } else if ("7".equals(status)) {
                                statusStr = "取消";
                            }
                            holder.setText(R.id.tv_status, statusStr);

                            if (!"".equals(testBean.getImg())) {
                                Picasso.with(mActivity)
                                        .load(testBean.getImg())
                                        .placeholder(R.drawable.buy_empty)
                                        .error(R.drawable.buy_default_hint)
                                        .fit()
                                        .tag(this)
                                        .into((ImageView) holder.getView(R.id.iv_img));
                            } else {
                                KLog.d(testBean.getType());
                                if ("1".equals(testBean.getType())) {
                                    //代购
                                    holder.setImageResource(R.id.iv_img, R.drawable.buy_default_hint);
                                } else {
                                    //代付
                                    holder.setImageResource(R.id.iv_img, R.drawable.waitpay_default);
                                }
                            }
                        }
                    });

                    DecimalFormat df = new DecimalFormat("######0.00");
                    KLog.d(bean.getGoods());
                    for (Good pkgBean : bean.getGoods()) {
                        KLog.d(pkgBean.getMoney());
                        allMoney += StringUtils.toDouble(pkgBean.getMoney()) + StringUtils.toDouble(pkgBean.getFreight());
                    }
                    tvAllMoney.setText("￥ " + df.format(allMoney));
                    KLog.d(allMoney);
                }

                @Override
                public void onFail(BuyDetailsBean orderDetails) {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(mActivity, "获取详情失败", 2);
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint), 3);
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_cart, R.id.iv_menu, R.id.bt_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_cart:
                AppUtils.goChatByBuy(this);
                break;
            case R.id.iv_menu:
                List<String> list = new ArrayList<>();
                if ("1".equals(mType)) {
                    list.add("取消代购");
                } else {
                    list.add("取消代付");
                }
                new SuperDialog.Builder(mActivity)
                        .setAlpha(1f)
                        .setCanceledOnTouchOutside(false)
                        .setItems(list, new SuperDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                switch (position) {
                                    case 0:
                                       /* if (StringUtils.toInt(mStatuus) > 2) {
                                            ShowToastUtils.toast(mActivity, "该状态不能取消代购, 请联系客服");
                                            return;
                                        }*/
                                        if (NetUtils.hasAvailableNet(mActivity)) {
                                            progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                                            HashMap params = new HashMap();
                                            params.put("no", mNo);
                                            ApiClient.postForm(ConfigConstants.CANCELBUYORDER, params, new RespModifyMsgCallback(mActivity, progressDialog) {
                                                @Override
                                                public void onSuccess() {
                                                    AppUtils.stopProgressDialog(progressDialog);
                                                    ShowToastUtils.toast(mActivity, "取消成功", 1);
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }

                                                @Override
                                                public void onFail(String str) {
                                                    //0失败-1错误(状态不对)-2未找到该代购单
                                                    if ("0".equals(str)) {
                                                        ShowToastUtils.toast(mActivity, "失败", 2);
                                                    } else if ("-1".equals(str)) {
                                                        ShowToastUtils.toast(mActivity, "错误", 3);
                                                    } else if ("-2".equals(str)) {
                                                        ShowToastUtils.toast(mActivity, "未找到该代购单", 3);
                                                    }
                                                    AppUtils.stopProgressDialog(progressDialog);

                                                }
                                            });
                                        } else {
                                            ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint), 3);
                                        }
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .build();
                break;
            case R.id.bt_ok:
                //提交订单
                if (NetUtils.hasAvailableNet(this)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                    HashMap parars = new HashMap();
                    parars.put("value", mNo);
                    parars.put("type", "2");
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.createOrder, parars, new RespModifyAddMsgCallback(mActivity, progressDialog) {
                        @Override
                        public void onSuccess(String msg) {
                            AppUtils.stopProgressDialog(progressDialog);
                            KLog.d(msg);
                            Intent i = new Intent(mActivity, PayOrderBuyDetailsActivity.class);
                            i.putExtra("no", msg);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onFail(String str) {
                            if ("-1".equals(str)) {
                                ShowToastUtils.toast(mActivity, "包裹数量不匹配", 3);
                            } else if ("-2".equals(str)) {
                                ShowToastUtils.toast(mActivity, "包裹还没算钱", 3);
                            } else if ("-3".equals(str)) {
                                ShowToastUtils.toast(mActivity, "包裹状态异常", 3);
                            }
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint), 3);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
