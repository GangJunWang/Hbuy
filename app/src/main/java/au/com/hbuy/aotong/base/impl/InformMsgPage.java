package au.com.hbuy.aotong.base.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.BuyPageDetailsActivity;
import au.com.hbuy.aotong.PayOrderBuyDetailsActivity;
import au.com.hbuy.aotong.PayOrderDetailsActivity;
import au.com.hbuy.aotong.PkgDetailsActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.WorkOrderDetailsActivity;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.domain.InformMesBean;
import au.com.hbuy.aotong.domain.InformMesBeans;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.InformMsgViewHolder;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespGetTypeback;
import au.com.hbuy.aotong.utils.okhttp.RespTakeMessageCallback;
/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class InformMsgPage extends BasePage implements SwipeRefreshLayout.OnRefreshListener{
    EasyRecyclerView lvTakePage;
    private Handler handler = new Handler();
    private List<InformMesBean> mTakeMsgs;
    private InformMsgAdapter adapter;
    private  final static String DataHint = "informmsgpage2";

    public InformMsgPage(FragmentActivity activity) {
        super(activity);
    }
    @Override
    public void initViews() {
        super.initViews();
        View mView = View.inflate(mActivity, R.layout.informmgs_page, null);
        flContent.addView(mView);
        lvTakePage = (EasyRecyclerView) mView.findViewById(R.id.lv_orders);
    }

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (NetUtils.hasAvailableNet(mActivity)) {
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GETMSGLIST + "/1", new RespTakeMessageCallback<List<InformMesBean>>(mActivity, DataHint) {
                        @Override
                        public void onFail(List<InformMesBean> takeMsgBeans) {

                        }
                        @Override
                        public void onSuccess(List<InformMesBean> t) {
                            if (null != t) {
                                KLog.d(t.size());
                                mTakeMsgs = t;
                                adapter.clear();
                                adapter.addAll(mTakeMsgs);
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
    public void initData() {
        lvTakePage.setLayoutManager(new LinearLayoutManager(mActivity));
        lvTakePage.setAdapterWithProgress(adapter = new InformMsgAdapter(mActivity));
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String[] tepArray = mTakeMsgs.get(position).getValue().split(":");
                Intent in = null;
                String name = tepArray[0];
                String value = tepArray[1];
                if (tepArray.length > 1) {

                    if ("package".equals(name)) {
                        //包裹详情  "value": "package://mailno/425001693883",
                        String[] pkgids = value.split("/");
                        if (pkgids.length > 3) {
                            String type = pkgids[2];
                            in = new Intent(mActivity, PkgDetailsActivity.class);
                            if ("id".equals(type)) {
                                //id 查询物流
                                in.putExtra("type", "1");

                            } else {
                                //单号查询
                                in.putExtra("type", "2");
                            }
                            in.putExtra("id", pkgids[3]);
                        }
                    } else if ("ticket".equals(name)) {
                        //工单详情"value": "ticket://1011000000000100",
                        in = new Intent(mActivity, WorkOrderDetailsActivity.class);
                        in.putExtra("no", value.substring(2, value.length()));
                    } else if ("order".equals(name)) {
                        //订单详情
                        final String tmp = value.substring(2, value.length());
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("no", tmp);
                        if (NetUtils.hasAvailableNet(mActivity)) {
                            ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.getItemOrder, params, new RespGetTypeback(mActivity) {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFail(String str) {

                                }

                                @Override
                                public void onBusiness(String str) {
                                    Intent i = null;
                                    if ("1".equals(str)) {
                                        //转运
                                        i = new Intent(mActivity, PayOrderDetailsActivity.class);
                                    } else {
                                        //代购
                                        i = new Intent(mActivity, PayOrderBuyDetailsActivity.class);
                                    }
                                    i.putExtra("no", tmp);
                                    mActivity.startActivity(i);
                                    return;
                                }
                            });
                        }
                    } else if ("helpbuy".equals(name)) {
                        //代购订单详情
                        in = new Intent(mActivity, BuyPageDetailsActivity.class);
                        in.putExtra("no", value.substring(2, value.length()));
                    }
                    if (null != in)
                    mActivity.startActivity(in);
                }
            }
        });

        lvTakePage.setRefreshListener(this);
        InformMesBeans bean = AppUtils.getSaveBeanClass(DataHint, InformMesBeans.class);
        if (null != bean) {
            List<InformMesBean> lists = bean.getData();
            adapter.clear();
            mTakeMsgs = lists;
            adapter.addAll(lists);
        }
            onRefresh();
    }

    public class InformMsgAdapter extends RecyclerArrayAdapter<InformMesBean> {
        private Context activity;
        public InformMsgAdapter(Context context) {
            super(context);
            this.activity = context;
        }

        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new InformMsgViewHolder(parent, activity);
        }
    }
}
