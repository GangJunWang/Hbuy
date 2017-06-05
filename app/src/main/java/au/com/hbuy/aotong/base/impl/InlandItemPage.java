package au.com.hbuy.aotong.base.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import au.com.hbuy.aotong.AddBuyActivity;
import au.com.hbuy.aotong.AddPkgActivity;
import au.com.hbuy.aotong.BuyPageDetailsActivity;
import au.com.hbuy.aotong.GuideActivity;
import au.com.hbuy.aotong.PkgDetailsActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.domain.BuySaveBean;
import au.com.hbuy.aotong.domain.BuyWaitPayBean;
import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.domain.OrderBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.NoDoubleRecycItemClickListener;
import au.com.hbuy.aotong.utils.PkgOrderViewHolderBy;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespPkgListCallback;
import au.com.hbuy.aotong.utils.persistentcookiejar.PkgOrderBuyViewHolder;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class InlandItemPage extends BasePage implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private View mView;
    private List<Order> mOrders;
    private EasyRecyclerView mListView;
    public String DataHint = "inlanditempage";
    private PkgOrderAdapter adapter;
    private Handler handler;
    private int mPage = 1, mAllPage = 1;
    private boolean mIsland;

    public InlandItemPage(FragmentActivity activity, boolean mIsland) {
        super(activity);
        this.mIsland = mIsland;
        KLog.d(mIsland);
    }

    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.buy_item_page, null);
        mListView = (EasyRecyclerView) mView.findViewById(R.id.lv_orders);

        mListView.setLayoutManager(new LinearLayoutManager(mActivity));
        mListView.setAdapterWithProgress(adapter = new PkgOrderAdapter(mActivity));
        mListView.setRefreshListener(this);
        adapter.setNoMore(R.layout.view_nomore);

        mListView.setEmptyView(R.layout.inland_pkg_view_empty);
        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });
        flContent.addView(mView);
    }

    @Override
    public void initData() {
        handler = new Handler();
        OrderBean bean = AppUtils.getSaveBeanClass(DataHint, OrderBean.class);
        if (null != bean) {
            List<Order> lists = bean.getData();
            mPage = StringUtils.toInt(bean.getPage()) + 1;
            adapter.clear();
            mOrders = AppUtils.getClassifys(lists, mIsland);
            adapter.addAll(mOrders);
            adapter.notifyDataSetChanged();
        }
        onRefresh();

        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Order o = mOrders.get(position);
                int status = StringUtils.toInt(o.getStatus());
               /* if (status < 3) {
                    //编辑
                    Intent intent = new Intent(mActivity, AddPkgActivity.class);
                    intent.putExtra("number", o.getMail_no());
                    intent.putExtra("id", o.getId());
                    intent.putExtra("nameId", o.getCarrier_id());
                    intent.putExtra("context", o.getContent());
                    mActivity.startActivityForResult(intent, FROM_ADD_PKG);
                } else {*/
                    Intent in = new Intent(mActivity, PkgDetailsActivity.class);
                    in.putExtra("id", o.getId());
                    in.putExtra("status", o.getStatus());
                    in.putExtra("openPay", o.getOpen_pay());
                    mActivity.startActivity(in);
              //  }
            }
        });
    }

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (NetUtils.hasAvailableNet(mActivity)) {
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.getbatchpackage_url + "/14?p=1", new
                            RespPkgListCallback<List<Order>>(mActivity, DataHint) {
                                @Override
                                public void onFail(List<Order> historyOrder) {
                                }

                                @Override
                                public void onSuccess(List<Order> lists, String str, String number, String num) {
                                    KLog.d(lists.size());
                                    int size = lists.size();
                                    if (null != lists && size > 0) {
                                        mOrders = AppUtils.getClassifys(lists, mIsland);
                                        KLog.d(mOrders.size());
                                        KLog.d("adapter=" + adapter);
                                        adapter.clear();
                                        adapter.addAll(mOrders);
                                        adapter.notifyDataSetChanged();
                                        mPage = 2;
                                        mAllPage = StringUtils.toInt(str);
                                    } else {
                                        adapter.clear();
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
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.getbatchpackage_url + "/14?p=1", new
                            RespPkgListCallback<List<Order>>(mActivity, DataHint) {
                                @Override
                                public void onFail(List<Order> lists) {
                                }

                                @Override
                                public void onSuccess(List<Order> lists, String str, String s, String num) {
                                    if (null != lists) {
                                        List tmp = AppUtils.getClassifys(lists, mIsland);
                                        mOrders.addAll(tmp);
                                        adapter.addAll(mOrders);
                                        adapter.notifyDataSetChanged();
                                        mPage++;
                                        mAllPage = StringUtils.toInt(str);
                                    }
                                }
                            });
                } else {
                    adapter.pauseMore();
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint), 3);
                }
            }
        }, 2000);
    }

    public class PkgOrderAdapter extends RecyclerArrayAdapter<Order> {
        public PkgOrderAdapter(Context context) {
            super(context);
            KLog.d();
        }

        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new PkgOrderViewHolderBy(parent, mActivity);
        }
    }
}
