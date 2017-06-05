package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.CouponBean;
import au.com.hbuy.aotong.domain.CouponBeans;
import au.com.hbuy.aotong.domain.UpdateOrderBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.CouponViewHolder;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ScreenUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespOrderUpdateCallback;
import au.com.hbuy.aotong.utils.okhttp.RespPkgListCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyCouponActivity extends BaseActivity implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private final static String DataHint = "mycouponactivity";
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.lv_orders)
    EasyRecyclerView lvOrders;
    @Bind(R.id.bt_cancel)
    Button btCancel;
    @Bind(R.id.layout_di)
    LinearLayout layoutDi;
    private Handler handler = new Handler();

    private WorkOrderAdapter adapter;
    private int mPage = 1, mAllPage = 1;
    private Activity mActivity = MyCouponActivity.this;
    private List<CouponBean> mCoupons;
    private String mType = "5";
    private Intent intent;
    private CustomProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupon);
        ButterKnife.bind(this);
        intent = getIntent();
        KLog.d(getIntent().getStringExtra("type"));
        lvOrders.setLayoutManager(new LinearLayoutManager(this));
        lvOrders.setAdapterWithProgress(adapter = new WorkOrderAdapter(this));
        adapter.setNoMore(R.layout.view_nomore);

        lvOrders.setEmptyView(R.layout.mycoupon_view_empty);
        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });
        if (null != getIntent().getStringExtra("type")) {
            KLog.d();
            mType = "4";
            layoutDi.setVisibility(View.VISIBLE);
            adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    CouponBean o = mCoupons.get(position);
                    if (NetUtils.hasAvailableNet(mActivity)) {
                        progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                        final String id = o.getId();
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("use_balance", intent.getStringExtra("use_balance"));
                        params.put("no", intent.getStringExtra("no"));
                        params.put("coupon_id", id);
                        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.UPDATEORDER, params, new RespOrderUpdateCallback<UpdateOrderBean>(mActivity) {
                            @Override
                            public void onSuccess(UpdateOrderBean updateOrderBean) {
                                AppUtils.stopProgressDialog(progressDialog);
                                Intent in = new Intent(mActivity, PaymentActivity.class);
                                in.putExtra("money", updateOrderBean.getReal_money());
                                in.putExtra("id", id);
                                setResult(RESULT_OK, in);
                                finish();
                            }
                            @Override
                            public void onFail(String status) {
                                AppUtils.stopProgressDialog(progressDialog);
                                if ("-1".equals(status)) {
                                    ShowToastUtils.toast(mActivity, "订单过期");
                                } else if ("-2".equals(status)) {
                                    ShowToastUtils.toast(mActivity, "订单状态不对");
                                }
                            }

                            @Override
                            public void onBusiness() {
                                AppUtils.stopProgressDialog(progressDialog);
                                ShowToastUtils.toast(mActivity, "失败", 2);
                            }
                        });
                    } else {
                        ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint));
                    }
                }
            });
        }
        lvOrders.setRefreshListener(this);
        CouponBeans bean = AppUtils.getSaveBeanClass(DataHint, CouponBeans.class);
        if (null != bean) {
            List<CouponBean> lists = bean.getData();
            mPage = StringUtils.toInt(bean.getPage()) + 1;
            adapter.clear();
            mCoupons = lists;
            adapter.addAll(lists);
        }
        onRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }

    @OnClick({R.id.iv_back, R.id.bt_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_cancel:
                if (NetUtils.hasAvailableNet(mActivity)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("use_balance", intent.getStringExtra("use_balance"));
                    params.put("no", intent.getStringExtra("no"));
                    params.put("coupon_id", "0");
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.UPDATEORDER, params, new RespOrderUpdateCallback<UpdateOrderBean>(mActivity) {
                        @Override
                        public void onSuccess(UpdateOrderBean updateOrderBean) {
                            AppUtils.stopProgressDialog(progressDialog);
                            Intent in = new Intent(mActivity, PaymentActivity.class);
                            in.putExtra("money", updateOrderBean.getReal_money());
                            in.putExtra("id", "0");
                            setResult(RESULT_OK, in);
                            finish();
                        }
                        @Override
                        public void onFail(String status) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if ("-1".equals(status)) {
                                ShowToastUtils.toast(mActivity, "订单过期");
                            } else if ("-2".equals(status)) {
                                ShowToastUtils.toast(mActivity, "订单状态不对");
                            }
                        }

                        @Override
                        public void onBusiness() {
                            AppUtils.stopProgressDialog(progressDialog);
                    //        ShowToastUtils.toast(mActivity, "失败");
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint));
                }
                break;
        }
    }

    @Override
    public void onLoadMore() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (mAllPage == mPage - 1) {
                    adapter.stopMore();
                    return;
                }
                if (NetUtils.hasAvailableNet(mActivity)) {
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GETCOUPONLIST +
                            "/" + mType + "?p=" + mPage, new RespPkgListCallback<List<CouponBean>>(mActivity, DataHint) {
                        @Override
                        public void onFail(List<CouponBean> lists) {
                        }

                        @Override
                        public void onSuccess(List<CouponBean> lists, String str, String s, String num) {
                            if (null != lists) {
                                mCoupons.addAll(lists);
                                adapter.addAll(lists);
                                mPage++;
                                mAllPage = StringUtils.toInt(str);
                            }
                        }
                    });
                } else {
                    adapter.pauseMore();
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
                }
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        adapter.setMore(R.layout.view_more, this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (NetUtils.hasAvailableNet(mActivity)) {
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GETCOUPONLIST + "/" + mType + "?p=1"
                            , new RespPkgListCallback<List<CouponBean>>(mActivity, DataHint) {
                                @Override
                                public void onFail(List<CouponBean> lists) {
                                }

                                @Override
                                public void onSuccess(List<CouponBean> lists, String str, String s, String num) {
                                    if (null != lists) {
                                        mCoupons = lists;
                                        adapter.clear();
                                        adapter.addAll(lists);
                                        mPage = 2;
                                        mAllPage = StringUtils.toInt(str);
                                    }
                                }
                            });
                } else {
                    adapter.pauseMore();
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
                }
            }
        }, 2000);
    }

    public class WorkOrderAdapter extends RecyclerArrayAdapter<CouponBean> {
        private Context mContext;
        public WorkOrderAdapter(Context context) {
            super(context);
            this.mContext = context;
        }
        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new CouponViewHolder(parent, mContext);
        }
    }
}
