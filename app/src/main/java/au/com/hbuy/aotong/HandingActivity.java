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
import com.socks.library.KLog;

import java.util.List;

import au.com.hbuy.aotong.domain.OtherBean;
import au.com.hbuy.aotong.domain.WorkOrderBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.NoDoubleRecycItemClickListener;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.WorkOrderViewHolder;
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
public class HandingActivity extends BaseActivity implements View.OnClickListener, RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.lv_orders)
    EasyRecyclerView mListView;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    private WorkOrderAdapter adapter;
    private int mPage = 1, mAllPage = 1;
    private static String DataHint = "handingactivity";
    private Handler handler = new Handler();
    private Activity activity = HandingActivity.this;
    private String mType, tmpId;
    private List<WorkOrderBean> mOrders;
    private final static int GETTRANSFERNEXT = 1;
    private final static int GETDETAILS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handing);
        ButterKnife.bind(this);
        tmpId = getIntent().getStringExtra("id");
        if ("1".equals(tmpId)) {
            mType = "8";
            DataHint = "handingactivity";
            mListView.setEmptyView(R.layout.workorder_view_empty);
        } else if ("2".equals(tmpId)) {
            DataHint = "history";
            mType = "7";
            tvTitle.setText("打包记录");
            mListView.setEmptyView(R.layout.historyworkorder_view_empty);
       //     mListView.setEmptyView(imageView);
        }

        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapterWithProgress(adapter = new WorkOrderAdapter(this));
        mListView.setRefreshListener(this);
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setOnItemClickListener(new NoDoubleRecycItemClickListener() {
            @Override
            public void onNoDoubleClickItem(int position) {
                WorkOrderBean o = mOrders.get(position);
                String status = o.getStatus();
                Intent in = null;
                KLog.d(tmpId);
              /*  if ("1".equals(tmpId)) {
                    in = new Intent(activity, TransferNextActivity.class);
                    in.putExtra("no", o.getNo());
                    in.putExtra("status", status);
                    activity.startActivityForResult(in, GETTRANSFERNEXT);
                } else if ("2".equals(tmpId)) {*/
                in = new Intent(activity, WorkOrderDetailsActivity.class);
                in.putExtra("status", status);
                in.putExtra("no", o.getNo());
                activity.startActivityForResult(in, GETDETAILS);
                //       }
            }
        });
        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });

        OtherBean bean = AppUtils.getSaveBeanClass(DataHint, OtherBean.class);
        if (null != bean) {
            List<WorkOrderBean> lists = bean.getData();
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
        KLog.d();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (mAllPage == mPage - 1) {
                    adapter.stopMore();
                    return;
                }
                KLog.d();
                if (NetUtils.hasAvailableNet(activity)) {
                    ApiClient.getInstance(activity.getApplicationContext()).postForm(ConfigConstants.getAllworkList +
                            "/" + mType + "?p=" + mPage, new RespPkgListCallback<List<WorkOrderBean>>(activity, DataHint) {

                        @Override
                        public void onFail(List<WorkOrderBean> lists) {
                        }

                        @Override
                        public void onSuccess(List<WorkOrderBean> lists, String str, String s, String num) {
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
                    ShowToastUtils.toast(HandingActivity.this, activity.getString(R.string.no_net_hint));
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
                if (NetUtils.hasAvailableNet(HandingActivity.this)) {
                    ApiClient.getInstance(HandingActivity.this.getApplicationContext()).postForm(ConfigConstants.getAllworkList + "/" + mType + "?p=1"
                            , new RespPkgListCallback<List<WorkOrderBean>>(HandingActivity.this, DataHint) {
                                @Override
                                public void onFail(List<WorkOrderBean> lists) {
                                }

                                @Override
                                public void onSuccess(List<WorkOrderBean> lists, String str, String s, String num) {
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
                    ShowToastUtils.toast(HandingActivity.this, HandingActivity.this.getString(R.string.no_net_hint));
                }
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GETTRANSFERNEXT:
                    onRefresh();
                    break;
            }
        }
    }

    public class WorkOrderAdapter extends RecyclerArrayAdapter<WorkOrderBean> {
        public WorkOrderAdapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new WorkOrderViewHolder(parent, HandingActivity.this);
        }
    }
}
