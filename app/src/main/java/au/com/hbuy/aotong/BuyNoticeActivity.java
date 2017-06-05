package au.com.hbuy.aotong;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.mylhyl.superdialog.SuperDialog;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.Address;
import au.com.hbuy.aotong.domain.BuyCommit;
import au.com.hbuy.aotong.domain.WaitPayGroup;
import au.com.hbuy.aotong.greenDao.BuyDbBean;
import au.com.hbuy.aotong.greenDao.GreenDaoManager;
import au.com.hbuy.aotong.greenDao.gen.BuyDbBeanDao;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.utils.okhttp.RespRepoAddressCallback;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CustomDialog;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class BuyNoticeActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_cart)
    ImageView ivCart;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_select_type)
    TextView tvSelectType;
    @Bind(R.id.et_mk)
    ClearEditText etMk;
    @Bind(R.id.bt_add_pkg)
    Button btAddPkg;
    @Bind(R.id.iv_hint)
    ImageView ivHint;
    private List<BuyDbBean> mLists = new ArrayList<>();
    private BuyDbBeanDao mMessageDao;
    private FragmentActivity mActivity;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_notice);
        ButterKnife.bind(this);
        mActivity = BuyNoticeActivity.this;
        mMessageDao = GreenDaoManager.getInstance().getSession().getBuyDbBeanDao();
        mLists = mMessageDao.queryBuilder().build().list();
        if (NetUtils.hasAvailableNet(this)) {
            ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.getRepoAddress, new RespRepoAddressCallback<Address>(this) {
                @Override
                public void onSuccess(Address address) {
                    if (null != address) {
                        tvName.setText(address.getName());
                        tvAddress.setText(address.getAddress());
                        tvPhone.setText(address.getPhone());
                    }
                }

                @Override
                public void onFail(String str) {
                    ShowToastUtils.toast(mActivity, "获取地址失败", 2);
                }

            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint), 3);
        }
        btAddPkg.setText("信息无误,提交代购");
    }

    @OnClick({R.id.iv_back, R.id.iv_cart, R.id.tv_select_type, R.id.bt_add_pkg, R.id.iv_hint})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.iv_cart:
                AppUtils.goChatByBuy(mActivity);
                break;
            case R.id.iv_hint:
                final Dialog dialog = CustomDialog.create(mActivity, R.layout.buy_notice_dialog);
                dialog.show();
                break;
            case R.id.tv_select_type:
                List<String> list = new ArrayList<>();
                list.add("这件商品暂不买,其余正常购买");
                list.add("降低商品规格,购买低配版");
                list.add("所有商品都不买");
                new SuperDialog.Builder(mActivity)
                        .setAlpha(1f)
                        .setCanceledOnTouchOutside(false)
                        .setItems(list, new SuperDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                switch (position) {
                                    case 0:
                                        tvSelectType.setText("这件商品暂不买,其余正常购买");
                                        break;
                                    case 1:
                                        tvSelectType.setText("降低商品规格,购买低配版");
                                        break;
                                    case 2:
                                        tvSelectType.setText("所有商品都不买");
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .build();
                break;
            case R.id.bt_add_pkg:
                if (NetUtils.hasAvailableNet(this)) {
                    if (mLists.size() == 0) {
                        ShowToastUtils.toast(this, "当前没有订单提交", 3);
                        return;
                    }
                    progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
                    HashMap params = new HashMap();
                    List tmp = new ArrayList();
                    for (BuyDbBean bean : mLists) {
                        KLog.d(bean.getImg() + bean.getLink() + bean.getMk());
                        BuyCommit w = new BuyCommit(bean.getImg(), bean.getLink(), bean.getMk(), bean.getNum(), bean.getSize(), bean.getTitle());
                        tmp.add(w);
                    }
                    WaitPayGroup group = new WaitPayGroup();
                    group.setS(tmp);
                    String jsonString = JSON.toJSONString(group);
                    System.out.println("jsonString:" + jsonString.subSequence(5, jsonString.length() - 1));
                    params.put("type", "1");
                    params.put("goods", jsonString.subSequence(5, jsonString.length() - 1));
                    String note = etMk.getText().toString().trim();
                    if (!"".equals(note))
                        params.put("note", note);
                    String type = tvSelectType.getText().toString().trim();
                    if ("这件商品暂不买,其余正常购买".equals(type)) {
                        params.put("extra", "1");
                    } else if ("降低商品规格,购买低配版".equals(type)) {
                        params.put("extra", "2");
                    } else if ("所有商品都不买".equals(type)) {
                        params.put("extra", "3");
                    }
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.ADDBUYORDER, params, new RespModifyMsgCallback(this, progressDialog) {
                        @Override
                        public void onSuccess() {
                            AppUtils.stopProgressDialog(progressDialog);
                            ShowToastUtils.toast(mActivity, "提交成功", 1);
                            //清除数据库数据
                            mMessageDao.deleteAll();
                            mLists.clear();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onFail(String str) {
                            AppUtils.stopProgressDialog(progressDialog);
                            ShowToastUtils.toast(mActivity, "提交失败", 2);
                        }
                    });
                } else {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(this, getString(R.string.no_net_hint), 3);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
