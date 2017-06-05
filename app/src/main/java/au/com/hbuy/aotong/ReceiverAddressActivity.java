package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.socks.library.KLog;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.listener.OnItemClickListener;
import au.com.hbuy.aotong.utils.AddressViewHolder;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.WaitOrderViewHolder;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAddTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespPkgListCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class ReceiverAddressActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AddressViewHolder.OnItemSubClicked {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    /* @Bind(R.id.recycler_view)
     SwipeMenuRecyclerView mSwipeMenuRecyclerView;
     @Bind(R.id.swipe_layout)
     SwipeRefreshLayout mSwipeRefreshLayout;*/
    @Bind(R.id.bt_add_address)
    Button btAddAddress;
    @Bind(R.id.lv_orders)
    EasyRecyclerView mListView;
    /* @Bind(R.id.iv_hint_img)
     ImageView ivHintImg;*/
    private CustomProgressDialog mDialog;
    //    private MenuAddressAdapter mAdapter;
    private List<Address> mList = null;
    private AddressAdapter adapter;
    private Handler handler;
    public final static int EDITADDRESS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_address);
        ButterKnife.bind(this);
        handler = new Handler();
        //     mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setEmptyView(R.layout.waitpay_view_empty);
        mListView.setRefreshListener(this);
        mListView.setAdapterWithProgress(adapter = new AddressAdapter(this));
        mListView.setEmptyView(R.layout.receiver_address_view_empty);

        adapter.setError(R.layout.view_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.resumeMore();
            }
        });

        initData();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetUtils.hasAvailableNet(mContext)) {
                    mDialog = AppUtils.startProgressDialog(mContext, "正在加载", mDialog);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getAddresses, new RespAddTCallback<List<Address>>(mContext, mDialog) {
                        @Override
                        public void onSuccess(List<Address> listAdd) {
                            AppUtils.stopProgressDialog(mDialog);
                            if (null != listAdd) {
                                mList = listAdd;
                                adapter.clear();
                                adapter.addAll(listAdd);
                                /*mAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        KLog.d();
                                        Address o = mList.get(position);
                                        String type = getIntent().getStringExtra("type");
                                        if (null != type) {
                                            Intent intent = new Intent();
                                            intent.putExtra("address", o);
                                            ReceiverAddressActivity.this.setResult(RESULT_OK, intent);
                                            ReceiverAddressActivity.this.finish();
                                            return;
                                        }
                                        Activity a = ReceiverAddressActivity.this;
                                        Intent in = new Intent(a, AddressDetailsActivity.class);
                                        in.putExtra("id", o.getId());
                                        if (null != mList && mList.size() == 1) {
                                            in.putExtra("num", false);
                                        } else {
                                            in.putExtra("num", true);
                                        }
                                        a.startActivity(in);
                                    }
                                });*/
                            }
                        }

                        @Override
                        public void onFail(List<Address> Addresss) {
                            AppUtils.stopProgressDialog(mDialog);
                            mList = new ArrayList<Address>();
                            adapter.clear();
                            adapter.addAll(mList);
                            ShowToastUtils.toast(ReceiverAddressActivity.this, "没有地址请添加地址");
                        }
                    });
                } else {
                    ShowToastUtils.toast(mContext, getString(R.string.no_net_hint));
                }
            }
        }, 2000);
    }

    /**
     * 刷新监听。
     *//*
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            KLog.d();
            mSwipeMenuRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    KLog.d();
                    getData();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 2000);
        }
    };*/
    private void initData() {
      /*  mSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        mSwipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mSwipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mSwipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(mDialog);
    }

    @Override
    public void onItemSubClicked(String id) {
        KLog.d(id);
        for (Address a : mList) {
            a.setIs_default("0");
            if (id.equals(a.getId())) {
                a.setIs_default("1");
            }
        }
     //   mList.get(position).setIs_default("1");
        adapter.clear();
        adapter.addAll(mList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteSub(String id) {
        for (int i = 0; i < mList.size(); i++) {
            if (id.equals(mList.get(i).getId())) {
                mList.remove(i);
            }
        }
        adapter.clear();
        adapter.addAll(mList);
        adapter.notifyDataSetChanged();
    }

    public class AddressAdapter extends RecyclerArrayAdapter<Address> {
        private AddressViewHolder addHolder;

        public AddressAdapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            addHolder = new AddressViewHolder(parent, mContext, ReceiverAddressActivity.this);
            KLog.d(addHolder);
            return addHolder;
        }

      /*  @Override
        public void onBindViewHolder(final BaseViewHolder holder, final int position, List<Object> payloads) {
            if (null != addHolder && position < mList.size()) {
                KLog.d(mList);
                KLog.d("position=" + position);
                addHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetUtils.hasAvailableNet(mContext)) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("id", mList.get(position).getId());
                            ApiClient.getInstance(mContext.getApplicationContext()).postForm(ConfigConstants.deleteAddress, params, new RespModifyMsgCallback(mContext) {
                                @Override
                                public void onSuccess() {
                                    mList.remove(position);
                                    KLog.d(mList.size());
                                    adapter.clear();
                                    adapter.addAll(mList);
                                    adapter.notifyDataSetChanged();
                                    ShowToastUtils.toast(mContext, "删除地址成功", 1);
                                }

                                @Override
                                public void onFail(String status) {
                                    ShowToastUtils.toast(mContext, "删除地址失败", 2);
                                }
                            });
                        } else {
                            ShowToastUtils.toast(mContext, mContext.getString(R.string.no_net_hint));
                        }
                    }
                });

                addHolder.tv_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, AddressEditActivity.class);
                        i.putExtra("address", mList.get(position));
                        mContext.startActivityForResult(i, ReceiverAddressActivity.EDITADDRESS);
                    }
                });

                addHolder.cb_default.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = addHolder.cb_default.isChecked();
                        KLog.d(position + "isChecked=" + checked);
                        if (NetUtils.hasAvailableNet(mContext)) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("id", mList.get(position).getId());
                            ApiClient.getInstance(mContext.getApplicationContext()).postForm(ConfigConstants.setDefaultAddress, params, new RespModifyMsgCallback(mContext) {
                                @Override
                                public void onSuccess() {
                                    for (Address a : mList) {
                                        a.setIs_default("0");
                                    }
                                    mList.get(position).setIs_default("1");
                                    adapter.clear();
                                    adapter.addAll(mList);
                                    adapter.notifyDataSetChanged();
                                    ShowToastUtils.toast(mContext, "设置默认地址成功", 1);
                                }

                                @Override
                                public void onFail(String status) {
                                    ShowToastUtils.toast(mContext, "设置默认地址失败", 2);
                                }
                            });
                        } else {
                            ShowToastUtils.toast(mContext, mContext.getString(R.string.no_net_hint));
                        }
                    }
                });
            }
            KLog.d(addHolder);
            KLog.d(holder);
            //  KLog.d(addHolder.tv_delete);
            super.onBindViewHolder(holder, position, payloads);
        }*/
    }

   /* *//**
     * 菜单点击监听。
     *//*
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        *//**
     * Item的菜单被点击的时候调用。
     *
     * @param closeable       closeable. 用来关闭菜单。
     * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
     * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
     * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
     *//*
        @Override
        public void onItemClick(Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。
            // TODO 推荐调用Adapter.notifyItemRemoved(position)，也可以Adapter.notifyDataSetChanged();
            final String id = mList.get(adapterPosition).getId();
            if (menuPosition == 1) {
                if (NetUtils.hasAvailableNet(ReceiverAddressActivity.this)) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", id);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.deleteAddress, params, new RespModifyMsgCallback(ReceiverAddressActivity.this) {
                        @Override
                        public void onSuccess() {
                            mList.remove(adapterPosition);
                            mAdapter.notifyItemRemoved(adapterPosition);
                            mAdapter.notifyDataSetChanged();
                            ShowToastUtils.toast(ReceiverAddressActivity.this, "删除地址成功", 1);
                        }

                        @Override
                        public void onFail(String status) {
                            ShowToastUtils.toast(ReceiverAddressActivity.this, "删除地址失败", 2);
                        }
                    });
                } else {
                    ShowToastUtils.toast(ReceiverAddressActivity.this, getString(R.string.no_net_hint));
                }
            } else if (menuPosition == 0) {
                if (NetUtils.hasAvailableNet(ReceiverAddressActivity.this)) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", id);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.setDefaultAddress, params, new RespModifyMsgCallback(ReceiverAddressActivity.this) {
                        @Override
                        public void onSuccess() {
                            for (Address a : mList) {
                                a.setIs_default("0");
                            }
                            mList.get(adapterPosition).setIs_default("1");
                            mAdapter.notifyDataSetChanged();
                            ShowToastUtils.toast(ReceiverAddressActivity.this, "设置默认地址成功", 1);
                        }

                        @Override
                        public void onFail(String status) {
                            ShowToastUtils.toast(ReceiverAddressActivity.this, "设置默认地址失败", 2);
                        }
                    });
                } else {
                    ShowToastUtils.toast(ReceiverAddressActivity.this, getString(R.string.no_net_hint));
                }
            }
        }
    };
    */

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     *//*
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.x70);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            {
                SwipeMenuItem modifyItem = new SwipeMenuItem(ReceiverAddressActivity.this)
                        .setBackgroundDrawable(R.color.button_color)
                        .setText("设为默认")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(modifyItem); // 添加一个按钮到右侧菜单。
                SwipeMenuItem deleteItem = new SwipeMenuItem(ReceiverAddressActivity.this)
                        .setBackgroundDrawable(R.color.red_color)
                        .setText("删除")
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem); // 添加一个按钮到右侧菜单。
            }
        }
    };
*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                setResult(1);
                finish();
                break;
        }
        return false;
    }

    @OnClick({R.id.iv_back, R.id.bt_add_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //back
                setResult(1);
                finish();
                break;
            case R.id.bt_add_address:
                //add address
                if (null != mList && mList.size() > 20) {
                    ShowToastUtils.toast(ReceiverAddressActivity.this, "地址个数不能超过20");
                    return;
                }
                Intent intent = new Intent(ReceiverAddressActivity.this, AddressEditActivity.class);
                startActivityForResult(intent, EDITADDRESS);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
           if (requestCode == EDITADDRESS) {
                onRefresh();
            }
        }
    }
}
