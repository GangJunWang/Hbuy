package au.com.hbuy.aotong.base.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.robin.lazy.cache.CacheLoaderManager;
import com.socks.library.KLog;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.FaqActivity;
import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.base.BasePage;
import au.com.hbuy.aotong.domain.ContentData;
import au.com.hbuy.aotong.domain.TakeMsgBean;
import au.com.hbuy.aotong.domain.TakeMsgBeans;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.NoDoubleRecycItemClickListener;
import au.com.hbuy.aotong.utils.NoViewHolder;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.TakeMsgViewHolder;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespTakeMsgCallback;

/**
 * Created by yangwei on 2016/7/2215:50.
 * E-Mail:yangwei199402@gmail.com
 */
public class TakeMsgPage extends BasePage implements SwipeRefreshLayout.OnRefreshListener {
    EasyRecyclerView lvTakePage;
    EasyRecyclerView mListView;
    private Handler handler = new Handler();
    private List<TakeMsgBean> mTakeMsgs;
    private TakeMsgAdapter adapter;
    private  final static String DataHint = "takemsgpage";
    public TakeMsgPage(FragmentActivity activity) {
        super(activity);
    }
    private View mView;
    @Override
    public void initViews() {
        super.initViews();
        mView = View.inflate(mActivity, R.layout.takemsg_page, null);
        flContent.addView(mView);
    }

    @Override
    public void initData() {
        lvTakePage = (EasyRecyclerView) mView.findViewById(R.id.lv_orders);
        lvTakePage.setLayoutManager(new LinearLayoutManager(mActivity));
        lvTakePage.setAdapterWithProgress(adapter = new TakeMsgAdapter(mActivity));
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });

       /* adapter.setOnItemClickListener(new NoDoubleRecycItemClickListener() {
            @Override
            public void onNoDoubleClickItem(int position) {
                KLog.d(position);

            }
        });*/
        lvTakePage.setRefreshListener(this);
        String string = CacheLoaderManager.getInstance().loadString(DataHint);
        KLog.d(string);
        List<TakeMsgBean> respByOrder = null;
        if (null != string) {
            try {
                respByOrder = JSON.parseArray(string, TakeMsgBean.class);
                if (null != respByOrder) {
                    for (TakeMsgBean bean1 : respByOrder) {
                        KLog.d(bean1.getContent().getType() + bean1.getContent().getData());
                        try {
                            if ("0".equals(bean1.getContent().getType())) {
                                List<ContentData> dataList = JSON.parseArray(bean1.getContent().getData(), ContentData.class);
                                bean1.getContent().setListdata(dataList);
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (null != respByOrder) {
            adapter.clear();
            mTakeMsgs = respByOrder;
            adapter.addAll(respByOrder);
        } else {
            onRefresh();
       }
    }

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (NetUtils.hasAvailableNet(mActivity)) {
                    ApiClient.getInstance(mActivity.getApplicationContext()).postForm(ConfigConstants.GETMSGLIST, new RespTakeMsgCallback<List<TakeMsgBean>>(mActivity, DataHint) {
                        @Override
                        public void onFail(List<TakeMsgBean> takeMsgBeans) {

                        }
                        @Override
                        public void onSuccess(List<TakeMsgBean> t) {
                            KLog.d(t.size());
                            if (t.size() == 0) {
                                adapter.pauseMore();
                                adapter.clear();
                                return;
                            }
                            if (null != t) {
                                KLog.d(t.get(0).getContent().getData());
                                mTakeMsgs = t;
                                KLog.d(mTakeMsgs.size());
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

    public class TakeMsgAdapter extends RecyclerArrayAdapter<TakeMsgBean> {
        public static final int TYPE_NO = 1;
        public static final int TYPE_ALL = 2;
        private Context activity;
        public TakeMsgAdapter(Context context) {
            super(context);
            this.activity = context;
        }

        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            KLog.d(viewType);
            switch (viewType){
                case TYPE_NO:
                    return new NoViewHolder(parent, activity);
                case TYPE_ALL:
                    return new TakeMsgViewHolder(parent, activity);
                default:
                    throw new InvalidParameterException();
            }
        }

        @Override
        public int getViewType(int position) {
            TakeMsgBean bean = getItem(position);
            String type = bean.getContent().getType();
            KLog.d(type + "---" + "0".equals(type));
            if("0".equals(type)){
                return TYPE_ALL;
            }else {
                return TYPE_NO;
            }
        }

        @Override
        public TakeMsgBean getItem(int position) {
            return super.getItem(position);
        }
    }
}
