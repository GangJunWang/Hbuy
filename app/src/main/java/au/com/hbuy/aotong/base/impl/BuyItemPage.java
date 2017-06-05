package au.com.hbuy.aotong.base.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import au.com.hbuy.aotong.AddBuyActivity;
import au.com.hbuy.aotong.BuyListActivity;
import au.com.hbuy.aotong.BuyPageDetailsActivity;
import au.com.hbuy.aotong.GuideActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.WaitPayListActivity;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.domain.BuySaveBean;
import au.com.hbuy.aotong.domain.BuyWaitPayBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.NoDoubleRecycItemClickListener;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespPkgListCallback;
import au.com.hbuy.aotong.utils.persistentcookiejar.PkgOrderBuyViewHolder;
import au.com.hbuy.aotong.view.NoScrollViewPager;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class BuyItemPage extends BasePage implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private TextView mHint;
    public final static int FROM_DEL = 2;
    private View mView;
    private List<BuyWaitPayBean> mOrders;
    private EasyRecyclerView mListView;
    public String DataHint = "buypage";
    private PkgOrderBuyAdapter adapter;
    private Handler handler;
    private int mPage = 1, mAllPage = 1;
    public BuyItemPage(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.buy_item_page, null);
        mListView = (EasyRecyclerView) mView.findViewById(R.id.lv_orders);

        flContent.addView(mView);
       /* mView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> list = new ArrayList<>();
                list.add("代购商品");
                list.add("我要代付");
                new SuperDialog.Builder(mActivity)
                        .setAlpha(1f)
                        .setCanceledOnTouchOutside(false)
                        .setItems(list, new SuperDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                switch (position) {
                                    case 0:
                                        mActivity.startActivityForResult(new Intent(mActivity, BuyListActivity.class), FROM_DEL);
                                        break;
                                    case 1:
                                        mActivity.startActivityForResult(new Intent(mActivity, WaitPayListActivity.class), FROM_DEL);
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .build();
            }
        });*/

        mListView.setLayoutManager(new LinearLayoutManager(mActivity));
        mListView.setAdapterWithProgress(adapter = new PkgOrderBuyAdapter(mActivity));
        mListView.setRefreshListener(this);
        adapter.setNoMore(R.layout.view_nomore);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.buypage_view_empty, null);
        TextView hint01 = (TextView) view.findViewById(R.id.tv_hint);
        TextView issue = (TextView) view.findViewById(R.id.tv_issue);
        view.findViewById(R.id.tv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, AddBuyActivity.class));
            }
        });
        issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, GuideActivity.class);
                i.putExtra("type", "2");
                mActivity.startActivity(i);
            }
        });
            hint01.setText("暂无代购单需要处理, ");
            issue.setText("代购须知?");

        mListView.setEmptyView(view);

        adapter.setOnItemClickListener(new NoDoubleRecycItemClickListener() {
            @Override
            public void onNoDoubleClickItem(int position) {
                Intent in = new Intent(mActivity, BuyPageDetailsActivity.class);
                BuyWaitPayBean bean = mOrders.get(position);
                in.putExtra("no", bean.getNo());
                mActivity.startActivityForResult(in, FROM_DEL);
            }
        });
        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });
    }

    @Override
    public void initData() {
        handler = new Handler();
        BuySaveBean bean = AppUtils.getSaveBeanClass(DataHint, BuySaveBean.class);
        KLog.d(DataHint);
        if (null != bean) {
            KLog.d(bean);
            List<BuyWaitPayBean> lists = bean.getData();
            mPage = StringUtils.toInt(bean.getPage()) + 1;
            adapter.clear();
            mOrders = lists;
            adapter.addAll(lists);
            adapter.notifyDataSetChanged();
        }
        onRefresh();
    }

    @Override
    public void onRefresh() {
       // adapter.setMore(R.layout.view_more, this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (NetUtils.hasAvailableNet(mActivity)) {
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GETALLBUYLIST + "?type=1&p=1"
                            , new RespPkgListCallback<List<BuyWaitPayBean>>(mActivity, DataHint) {
                                @Override
                                public void onFail(List<BuyWaitPayBean> lists) {
                                }

                                @Override
                                public void onSuccess(List<BuyWaitPayBean> lists, String str, String s, String num) {
                                    if (null != lists) {
                                        mOrders = lists;
                                        adapter.clear();
                                        adapter.addAll(lists);
                                        adapter.notifyDataSetChanged();
                                        mPage = 2;
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
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GETALLBUYLIST +
                            "?type=1&p=" + mPage, new RespPkgListCallback<List<BuyWaitPayBean>>(mActivity, DataHint) {
                        @Override
                        public void onFail(List<BuyWaitPayBean> lists) {
                        }

                        @Override
                        public void onSuccess(List<BuyWaitPayBean> lists, String str, String s, String num) {
                            if (null != lists) {
                                mOrders.addAll(lists);
                                adapter.addAll(lists);
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

    public class PkgOrderBuyAdapter extends RecyclerArrayAdapter<BuyWaitPayBean> {
        public PkgOrderBuyAdapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new PkgOrderBuyViewHolder(parent, mActivity);
        }
    }
}
