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
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.robin.lazy.cache.CacheLoaderManager;
import com.socks.library.KLog;

import java.util.List;

import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.domain.OrderBean;
import au.com.hbuy.aotong.domain.OtherBean;
import au.com.hbuy.aotong.domain.WorkOrderBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.WaitOrderViewHolder;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespPkgListCallback;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class WaitPaymentActivity extends BaseActivity implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private EasyRecyclerView mListView;
    private final Activity mActivity = this;
    private int mPage = 1, mAllPage = 1;
    private Handler handler = new Handler();
    private List<Order> mOrders;
    private OrderAdapter adapter;
    private static String DataHint = "waitpayData";
    private final static int CANCELORDER = 1;
    private String mStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitpayment);
        mStyle = getIntent().getStringExtra("style");
        TextView title = (TextView) findViewById(R.id.tv_title);

        mListView = (EasyRecyclerView) findViewById(R.id.lv_orders);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setEmptyView(R.layout.waitpay_view_empty);
        mListView.setRefreshListener(this);
        mListView.setAdapterWithProgress(adapter = new OrderAdapter(this));
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (AppUtils.isNotFastClick()) {
                    Order o = mOrders.get(position);
                    String type = o.getType();
                    Intent i = null;
                    if ("1".equals(type)) {
                        //转运订单详情
                        i = new Intent(mActivity, PayOrderDetailsActivity.class);
                    } else {
                        i = new Intent(mActivity, PayOrderBuyDetailsActivity.class);
                    }
                    i.putExtra("no", o.getNo());
                    mActivity.startActivityForResult(i, CANCELORDER);
                }
            }
        });
        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if ("7".equals(mStyle)) {
            DataHint = "waitpayData";
            title.setText("待付款");
            mListView.setEmptyView(R.layout.waitpay_view_empty);
        } else if ("8".equals(mStyle)) {
            findViewById(R.id.iv_cart).setVisibility(View.VISIBLE);
            DataHint = "historyOrderData";
            title.setText("历史订单");
            mListView.setEmptyView(R.layout.historyorder_view_empty);
        }

        OrderBean bean = AppUtils.getSaveBeanClass(DataHint, OrderBean.class);
        if (null != bean) {
            List<Order> lists = bean.getData();
            mPage = StringUtils.toInt(bean.getPage()) + 1;
            mOrders = lists;
            adapter.addAll(mOrders);
        }
        onRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.d(requestCode + "resultCode = " + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CANCELORDER:
                    onRefresh();
                    break;
            }
        }
    }

    @Override
    public void onLoadMore() {
        KLog.d();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (mAllPage == mPage - 1) {
                    adapter.stopMore();
                    return;
                }
                if (NetUtils.hasAvailableNet(mActivity)) {
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.waitingPay
                            + "/" + mStyle + "?=p" + mPage, new RespPkgListCallback<List<Order>>(mActivity, DataHint) {
                        @Override
                        public void onFail(List<Order> orders) {

                        }

                        @Override
                        public void onSuccess(List<Order> lists, String str, String number, String num) {
                            if (null != lists) {
                                mOrders.addAll(lists);
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
        KLog.d();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (NetUtils.hasAvailableNet(mActivity)) {
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.waitingPay
                            + "/" + mStyle + "?p=1", new RespPkgListCallback<List<Order>>(mActivity, DataHint) {
                        @Override
                        public void onFail(List<Order> workOrderBeans) {
                        }

                        @Override
                        public void onSuccess(List<Order> lists, String str, String s, String num) {
                            if (null != lists) {
                                mOrders = lists;
                                adapter.clear();
                                adapter.addAll(lists);
                                mPage = 2;
                                mAllPage = StringUtils.toInt(str);
                            }
                        }
                    });
                } else {
                    adapter.pauseMore();
                    ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint));
                }
            }
        }, 2000);
    }

    public class OrderAdapter extends RecyclerArrayAdapter<Order> {
        public OrderAdapter(Context context) {
            super(context);
        }
        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new WaitOrderViewHolder(parent, mActivity);
        }
    }
}
