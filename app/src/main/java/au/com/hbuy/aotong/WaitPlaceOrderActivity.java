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

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.socks.library.KLog;

import java.util.List;

import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.domain.OrderBean;
import au.com.hbuy.aotong.domain.WaitPlaceBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.WaitPlaceOrderViewHolder;
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
public class WaitPlaceOrderActivity extends BaseActivity implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.lv_orders)
    EasyRecyclerView mListView;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.bt_next)
    Button btNext;
    private final static String DataHint = "WaitPlaceOrderActivity";

    private WaitPlaceOrderAdapter adapter;
    private Handler handler = new Handler();
    private List<WaitPlaceBean> mOrders;
    private int mPage = 1, mAllPage = 1;
    private Activity mActivity = WaitPlaceOrderActivity.this;
    private final static int SUCCEEDPLACEORDER = 1;    //成功下单
    private boolean mIs = false;  //判断是否是最新数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_placeorder);
        ButterKnife.bind(this);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapterWithProgress(adapter = new WaitPlaceOrderAdapter(this));
        mListView.setRefreshListener(this);
        adapter.setNoMore(R.layout.view_nomore);
        mListView.setEmptyView(R.layout.waitplaceorder_view_empty);
        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });

        OrderBean bean = AppUtils.getSaveBeanClass(DataHint, OrderBean.class);
        if (null != bean) {
            KLog.d(bean.getData().size());
            List<Order> lists = bean.getData();
            mPage = StringUtils.toInt(bean.getPage()) + 1;
            mOrders = AppUtils.WaitPlaceOrderSort(lists);
            adapter.addAll(mOrders);
        }

       /* btNext.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if (null != mSlideAdapter) {
                    List<Order> lists = mSlideAdapter.getmDatas();
                    String ids = "";
                    for (Order o : lists) {
                        if (o.isChecked()) {
                            ids += o.getId() + ",";
                        }
                    }
                    if (!"".equals(ids)) {
                        ids = ids.substring(0, ids.length() - 1);
                    }
                    if (StringUtils.isEmpty(ids)) {
                        ShowToastUtils.toast(mActivity, "请选择你要下单的包裹");
                        return;
                    }
                    Intent intent = new Intent(mActivity, TransferAddressActivity.class);
                    intent.putExtra("ids", ids);
                    startActivity(intent);
                }
            }
        });*/
        onRefresh();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case SUCCEEDPLACEORDER:  //成功下单
                    Intent i = new Intent(mActivity, PayOrderDetailsActivity.class);
                    i.putExtra("no", data.getStringExtra("no"));
                    i.putExtra("waitplace", "sb");
                    startActivity(i);
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        adapter.setMore(R.layout.view_more, this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (NetUtils.hasAvailableNet(mActivity)) {

                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getbatchpackage_url + "/10?p=1"
                            , new RespPkgListCallback<List<Order>>(mActivity, DataHint) {
                                @Override
                                public void onFail(List<Order> lists) {
                                }

                                @Override
                                public void onSuccess(List<Order> lists, String str, String s, String num) {
                                    if (null != lists) {
                                        mIs = true;
                                        List temp = AppUtils.WaitPlaceOrderSort(lists);
                                        mOrders = temp;
                                        adapter.clear();
                                        adapter.addAll(temp);
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
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.getbatchpackage_url +
                            "/10?p=" + mPage, new RespPkgListCallback<List<Order>>(mActivity, DataHint) {
                        @Override
                        public void onFail(List<Order> lists) {
                        }

                        @Override
                        public void onSuccess(List<Order> lists, String str, String s, String num) {
                            if (null != lists) {
                                List temp = AppUtils.WaitPlaceOrderSort(lists);
                                mOrders.addAll(temp);
                                adapter.addAll(temp);
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

    @OnClick({R.id.iv_back, R.id.bt_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_next:
                if (!mIs) {
                    ShowToastUtils.toast(this, "请稍后再操作");
                    return;
                }
                if (null == mOrders) {
                    ShowToastUtils.toast(this, "当前没有包裹可下单");
                    return;
                }
                String id = "";
                String pkgids = "";
                String ids = "";
                for (WaitPlaceBean bean : mOrders) {
                    for (Order o : bean.getList()) {
                        if (o.isChecked()) {
                            id = bean.getDestination_country_id();
                            pkgids += o.getId() + ",";
                            ids += id + ",";
                        }
                    }
                }
                String[] tempArr = ids.split(",");
                if (StringUtils.isEmpty(id) && StringUtils.isEmpty(pkgids)) {
                    ShowToastUtils.toast(mActivity, "请选择你要下单的包裹");
                    return;
                }
                for (String s : tempArr) {
                    if (!s.equals(id)) {
                        ShowToastUtils.toast(mActivity, "亲,不能同时选择多个国家哦");
                        return;
                    }
                }
                KLog.d(pkgids);
                Intent intent = new Intent(mActivity, SelectAddressActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("pkgids", pkgids.substring(0, pkgids.length() - 1));
                startActivityForResult(intent, SUCCEEDPLACEORDER);
                break;
        }
    }
/*
    private void onLoad() {
        mRecyclerView.stopRefresh();
        mRecyclerView.stopLoadMore();
        mRecyclerView.setRefreshTime("刚刚");
    }

    @OnClick({R.id.iv_back, R.id.tv_edit, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_edit:
                ivBack.setVisibility(View.GONE);
                tvCancel.setVisibility(View.VISIBLE);
                if ("编辑".equals(tvEdit.getText().toString())) {
                    tvEdit.setText("全选");
                    mSlideAdapter.flage = !mSlideAdapter.flage;
                    mSlideAdapter.notifyDataSetChanged();

                } else if ("全选".equals(tvEdit.getText().toString())) {
                    for (int i = 0; i < mOrders.size(); i++) {
                        mOrders.get(i).setIsChecked(true);
                    }
                    mSlideAdapter.setmDatas(mOrders);
                }
                break;
            case R.id.tv_cancel:
                tvEdit.setText("编辑");
                tvCancel.setVisibility(View.INVISIBLE);
                ivBack.setVisibility(View.VISIBLE);
                mSlideAdapter.flage = !mSlideAdapter.flage;
                for (int i = 0; i < mOrders.size(); i++) {
                    mOrders.get(i).setIsChecked(false);
                }
                mSlideAdapter.setmDatas(mOrders);
                break;
        }
    }
    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetUtils.hasAvailableNet(mActivity)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);

                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getbatchpackage_url + "/10?p=1", new RespPkgListCallback<List<Order>>(mActivity, DataHint) {

                        @Override
                        public void onFail(List<Order> lists) {
                            AppUtils.stopProgressDialog(progressDialog);

                        }

                        @Override
                        public void onSuccess(List<Order> lists, String str, String s, String num) {
                            AppUtils.stopProgressDialog(progressDialog);

                            if (null == lists || lists.size() <= 0) {
                                tvEdit.setVisibility(View.GONE);
                                return;
                            }
                            if (null != lists) {
                                mAllPage = StringUtils.toInt(str);
                                mPage = 2;
                                mOrders = lists;
                                mSlideAdapter = new WaitPlaceOrderAdapter(WaitPlaceOrderActivity.this, mOrders);
                                mRecyclerView.setAdapter(mSlideAdapter);
                                defalutStatus();
                            }
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, getString(R.string.no_net_hint));
                }
                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (mAllPage == mPage - 1) {
                    ShowToastUtils.toast(mActivity, "没有数据啦.....");
                    onLoad();
                    return;
                }
                if (NetUtils.hasAvailableNet(mActivity)) {
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.getbatchpackage_url + "/10?p=" + mPage, new RespPkgListCallback<List<Order>>(mActivity, DataHint) {
                        @Override
                        public void onFail(List<Order> lists) {

                        }

                        @Override
                        public void onSuccess(List<Order> lists, String str, String s, String num) {
                            if (null != lists) {
                                mOrders.addAll(lists);
                                mSlideAdapter.setmDatas(mOrders);
                                mPage++;
                                mAllPage = StringUtils.toInt(str);
                                defalutStatus();
                            }
                        }
                    });
                } else {
                    ShowToastUtils.toast(mActivity, mActivity.getString(R.string.no_net_hint));
                }
                onLoad();
            }
        }, 2000);
    }
    private void defalutStatus() {
        tvCancel.setVisibility(View.GONE);
        tvEdit.setText("编辑");
        mSlideAdapter.flage = false;
        for (int i = 0; i < mOrders.size(); i++) {
            mOrders.get(i).setIsChecked(false);
        }
        mSlideAdapter.setmDatas(mOrders);
    }*/

    public class WaitPlaceOrderAdapter extends RecyclerArrayAdapter<WaitPlaceBean> {
        public WaitPlaceOrderAdapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new WaitPlaceOrderViewHolder(parent, mActivity);
        }
    }
}
