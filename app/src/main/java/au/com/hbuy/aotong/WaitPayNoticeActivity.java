package au.com.hbuy.aotong;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import au.com.hbuy.aotong.domain.BuyCommit;
import au.com.hbuy.aotong.domain.WaitPayCommit;
import au.com.hbuy.aotong.domain.WaitPayGroup;
import au.com.hbuy.aotong.greenDao.BuyDbBean;
import au.com.hbuy.aotong.greenDao.GreenDaoManager;
import au.com.hbuy.aotong.greenDao.WaitPayBean;
import au.com.hbuy.aotong.greenDao.gen.WaitPayBeanDao;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class WaitPayNoticeActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_cart)
    ImageView ivCart;
    @Bind(R.id.tv_hint)
    TextView tvHint;
    @Bind(R.id.tv_copy)
    TextView tvCopy;
    @Bind(R.id.et_mk)
    ClearEditText etMk;
    @Bind(R.id.bt_add_pkg)
    Button btAddPkg;
    private FragmentActivity mActivity = WaitPayNoticeActivity.this;
    private CustomProgressDialog progressDialog;
    private List<WaitPayBean> mLists = new ArrayList<>();
    private WaitPayBeanDao mMessageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitpay_notice);
        ButterKnife.bind(this);
        btAddPkg.setText("确认无误, 提交代付");
        mMessageDao = GreenDaoManager.getInstance().getSession().getWaitPayBeanDao();
        mLists = mMessageDao.queryBuilder().build().list();
    }

    @OnClick({R.id.iv_back, R.id.iv_cart, R.id.tv_hint, R.id.tv_copy, R.id.bt_add_pkg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.iv_cart:
                AppUtils.goChatByBuy(mActivity);
                break;
            case R.id.tv_hint:
                ShowToastUtils.toast(mActivity, "按照顺序优先代付有效订单");
                break;
            case R.id.tv_copy:
                StringUtils.copyText(mActivity.getString(R.string.zfb), this, "复制成功");
                break;
            case R.id.bt_add_pkg:
                if (NetUtils.hasAvailableNet(this)) {
                    if (mLists.size() == 0) {
                        ShowToastUtils.toast(this, "当前代付订单提交");
                        return;
                    }
                    progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
                    HashMap<String, HashMap> goods = new HashMap<String, HashMap>();
                    HashMap params = new HashMap();
                    KLog.d(params.toString());
                    List tmp = new ArrayList();
                    for (WaitPayBean bean : mLists) {
                        WaitPayCommit w = new WaitPayCommit(bean.getName(), bean.getNum());
                        tmp.add(w);
                    }
                    WaitPayGroup group = new WaitPayGroup();
                    group.setS(tmp);
                    String jsonString = JSON.toJSONString(group);

                    System.out.println("jsonString:" + jsonString.subSequence(5, jsonString.length() - 1));
                    params.put("type", "2");
                    params.put("goods", jsonString.subSequence(5, jsonString.length() - 1));
                    String mk = etMk.getText().toString().trim();
                    if ("".equals(mk)) {
                        params.put("note", "");
                    } else {
                        params.put("note", mk);
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
                    ShowToastUtils.toast(this, getString(R.string.no_net_hint));
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
