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

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import java.util.List;

import au.com.hbuy.aotong.domain.HistoryOrder;
import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.domain.OrderBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.NoDoubleRecycItemClickListener;
import au.com.hbuy.aotong.utils.PkgOrderViewHolder;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespPkgListCallback;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class HistoryPkgActivity extends BaseActivity implements View.OnClickListener, RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    private final static String DataHint = "historypkgactivity";
    @Bind(R.id.lv_orders)
    EasyRecyclerView mListView;
    private int mPage = 1, mAllPage = 1;
    private Handler handler = new Handler();
    private Activity activity = HistoryPkgActivity.this;
    private List<Order> mOrders;
    private PkgOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_pkg);
        ButterKnife.bind(this);

        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapterWithProgress(adapter = new PkgOrderAdapter(this));
        mListView.setRefreshListener(this);
        mListView.setEmptyView(R.layout.historypkg_view_empty);
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setOnItemClickListener(new NoDoubleRecycItemClickListener() {
            @Override
            public void onNoDoubleClickItem(int position) {
                Order o = mOrders.get(position);
                Intent in = new Intent(activity, PkgDetailsActivity.class);
                in.putExtra("id", o.getId());
                in.putExtra("Open_pay", o.getOpen_pay());
                in.putExtra("status", o.getStatus());
                activity.startActivity(in);
            }
        });
        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });
        OrderBean bean = AppUtils.getSaveBeanClass(DataHint, OrderBean.class);
        if (null != bean) {
            List<Order> lists = bean.getData();
            mPage = StringUtils.toInt(bean.getPage()) + 1;
            adapter.clear();
            mOrders = lists;
            adapter.addAll(lists);
        }
        onRefresh();
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //back
                finish();
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
                if (NetUtils.hasAvailableNet(activity)) {
                    ApiClient.getInstance(activity.getApplicationContext()).postForm(ConfigConstants.getbatchpackage_url +
                            "/15?p=" + mPage, new RespPkgListCallback<List<Order>>(activity, DataHint) {
                        @Override
                        public void onFail(List<Order> lists) {

                        }
                        @Override
                        public void onSuccess(List<Order> lists, String str, String s, String num) {
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
                    ShowToastUtils.toast(activity, activity.getString(R.string.no_net_hint));
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
                if (NetUtils.hasAvailableNet(activity)) {
                    ApiClient.getInstance(activity.getApplicationContext()).postForm(ConfigConstants.getbatchpackage_url + "/15?p=1"
                            , new RespPkgListCallback<List<Order>>(activity, DataHint) {
                        @Override
                        public void onFail(List<Order> lists) {
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
                    ShowToastUtils.toast(activity, activity.getString(R.string.no_net_hint));
                }
            }
        }, 2000);
    }

    public class PkgOrderAdapter extends RecyclerArrayAdapter<Order> {
        public PkgOrderAdapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new PkgOrderViewHolder(parent, activity);
        }
    }
}
