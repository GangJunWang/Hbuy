package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.adapter.TransferAdapter;
import au.com.hbuy.aotong.domain.Order;
import au.com.hbuy.aotong.domain.WorkOrderBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAddTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespPkgListCallback;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class TransferActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_edit)
    TextView tvEdit;
    @Bind(R.id.rv_transfer)
    ListView mRecyclerView;
    @Bind(R.id.tv_pkg_hint)
    TextView tvPkgHint;
    @Bind(R.id.bt_next)
    Button btNext;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.iv_hint_img)
    ImageView ivHintImg;
    private TransferAdapter mSlideAdapter;
    private List<Order> mOrders;
    private Activity mActivity = TransferActivity.this;
    private String ids = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        ButterKnife.bind(this);
        getOrder();
    }

    private void getOrder() {
        if (NetUtils.hasAvailableNet(this)) {
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getbatchpackage_url + "/7", new RespPkgListCallback<List<Order>>(this, null) {
                @Override
                public void onFail(List<Order> lists) {

                }

                @Override
                public void onSuccess(List<Order> lists, String str, String s, String num) {
                    if (null == lists || lists.size() <= 0) {
                        tvPkgHint.setText("您当前没有包裹可转运");
                        btNext.setText("我要寄包裹");
                        tvEdit.setVisibility(View.GONE);
                        ivHintImg.setVisibility(View.VISIBLE);
                        return;
                    }
                    if (null != lists && lists.size() > 0) {
                        ivHintImg.setVisibility(View.GONE);
                        tvPkgHint.setText("您共有" + lists.size() + "个包裹要转运");
                        btNext.setText("开始打包");
                        mOrders = lists;
                        mSlideAdapter = new TransferAdapter(TransferActivity.this, lists);
                        mRecyclerView.setAdapter(mSlideAdapter);
                    }
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint));
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_edit, R.id.bt_next, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_next:
                String hint = btNext.getText().toString().trim();
                if ("我要寄包裹".equals(hint)) {
                    AppUtils.showActivity(this, AddressRepoActivity.class);
                    return;
                }
                if (null == mSlideAdapter) {
                    ShowToastUtils.toast(this, "你当前没有转运包裹");
                    return;
                }
                ids = "";
                List<Order> lists = mSlideAdapter.getmDatas();
                    for (Order o : lists) {
                        if (o.isChecked()) {
                            ids += o.getId() + ",";
                        }
                    }
                    if (StringUtils.isEmpty(ids)) {
                        for (Order o : lists) {
                            ids += o.getId() + ",";
                        }
                    }
                    new SuperDialog.Builder(TransferActivity.this).setRadius(20)
                            .setAlpha(1f)
                            .setWidth(0.8f)
                            .setTitle("提示").setMessage("请及时配合校验工作，超时未回复视为放弃本次转运；转运内容不得涉及毒品、枪支等禁运物品")
                            .setPositiveButton("确定", new SuperDialog.OnClickPositiveListener() {
                                @Override
                                public void onClick(View v) {
                                    toSave(ids);
                                }
                            })
                            .setNegativeButton("取消", new SuperDialog.OnClickNegativeListener() {
                                @Override
                                public void onClick(View v) {
                                    return;
                                }
                            }).build();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {

        }
    }

    private void toSave(String ids) {
        KLog.d("ids =" + ids);
        if (NetUtils.hasAvailableNet(this)) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("ids", ids.substring(0, ids.length() - 1));
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.submitWorkOrder, params, new RespAddTCallback<WorkOrderBean>(this) {
                @Override
                public void onSuccess(WorkOrderBean bean) {
                    Intent i = new Intent(mActivity, WorkOrderDetailsActivity.class);
                    //   i.putExtra("time", bean.getTime());
                    //   i.putExtra("list", bean.getList());
                    i.putExtra("no", bean.getNo());
                    //    i.putExtra("waiting", bean.getWaiting());
                    startActivity(i);
                    finish();
                }

                @Override
                public void onFail(WorkOrderBean workOrderBean) {
                    ShowToastUtils.toast(mActivity, "提交失败", 2);
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint));
        }
    }
}
