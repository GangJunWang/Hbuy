package au.com.hbuy.aotong;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.HashMap;
import java.util.List;

import au.com.hbuy.aotong.domain.WuliuBean;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.NoDoubleClickListener;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespAddTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespClassTCallback;
import au.com.hbuy.aotong.utils.okhttp.RespModifyMsgCallback;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class AddPkgActivity extends BaseFragmentActivity implements View.OnClickListener {
   /* @Bind(R.id.tv_gochat)
    TextView tvGochat;
    @Bind(R.id.tv_issue)
    TextView tvIssue;*/
    private ImageView mBack, mScan;
    private EditText mNumber;
    private TextView mName, mTitle;
    private EditText mContent;
    private Button mAddPkg, mNext;
    private Intent mIntent;
    private String mUrl = ConfigConstants.addpackage_url;
    private CustomProgressDialog progressDialog;
    public final static String MSCANRESULT = "result";
    public final static int TOSCAN = 1;
    private Activity mActivity;
    private List<WuliuBean> mWuliuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpkg);
        ButterKnife.bind(this);
        mActivity = AddPkgActivity.this;
        initView();
        mIntent = getIntent();
        if (null != mIntent) {
            initData();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initView() {
        mBack = (ImageView) findViewById(R.id.iv_back);
        mNext = (Button) findViewById(R.id.bt_next);
        mScan = (ImageView) findViewById(R.id.iv_scan);

        mNumber = (EditText) findViewById(R.id.et_number);
        mName = (TextView) findViewById(R.id.tv_name);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mContent = (EditText) findViewById(R.id.et_content);
        mAddPkg = (Button) findViewById(R.id.bt_add_pkg);
        mAddPkg.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                addPkg();
            }
        });

        mNext.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                AppUtils.showActivity(AddPkgActivity.this, TransferActivity.class);
            }
        });
        mName.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mScan.setOnClickListener(this);
      /*  tvGochat.setOnClickListener(this);
        tvIssue.setOnClickListener(this);*/

        //  mName.addTextChangedListener(new ITextWatcher(new View[]{mNumber, mContent}, mAddPkg, this));
        //  mContent.addTextChangedListener(new ITextWatcher(new View[]{mName, mNumber}, mAddPkg, this));
        //   mNumber.addTextChangedListener(new ITextWatcher(new View[]{mName, mContent}, mAddPkg, this));

      /*  ShadowViewHelper.bindShadowHelper(
                new ShadowProperty()
                        .setShadowColor(AddPkgActivity.this.getColor(R.color.red_color))
                        .setShadowRadius(ScreenUtils.dip2px(1, this))
                , findViewById(R.id.layout_01));

        ShadowViewHelper.bindShadowHelper(
                new ShadowProperty()
                        .setShadowColor(AddPkgActivity.this.getColor(R.color.red_color))
                        .setShadowRadius(ScreenUtils.dip2px(1, this))
                , findViewById(R.id.layout_02));*/

    }

    private void initData() {
        String code = mIntent.getStringExtra("code");

        String number = mIntent.getStringExtra("number");
        String nameId = mIntent.getStringExtra("nameId");
        String context = mIntent.getStringExtra("context");
        if (null != number) {
            mNumber.setText(number.toString().trim());
            mTitle.setText("修改包裹");
            mAddPkg.setText("修改信息");
            mUrl = ConfigConstants.updatePkg;
        }
        if (null != nameId) {
            StringUtils.setTextAndImg(nameId, mName, null);
            if (null != context) {
                mContent.setText(context);
            }
        }

        if (null != code) {
            mNumber.setText(code);
        }
    }

    private void addPkg() {
        String number = getTextStr(mNumber);
        String name = getTextStr(mName);
        String content = getTextStr(mContent);
        if (name.equals("") || "".equals(number) || "".equals(content)) {
            ShowToastUtils.toast(mActivity, "请补全信息后再提交哦", 3);
            return;
        }

        String id = null;
        for (WuliuBean bean : mWuliuList) {
            if (bean.getName().equals(name)) {
                id = bean.getId();
            }
        }
        if (null == id) {
            ShowToastUtils.toast(mActivity, "获取物流商失败", 3);
            return;
        }

        if (NetUtils.hasAvailableNet(this)) {
            progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
            HashMap<String, String> params = new HashMap<String, String>();
            String pkgId = mIntent.getStringExtra("id");
            KLog.d(pkgId);
            if (null != pkgId) {
                params.put("id", pkgId);
            }
            params.put("mail_no", number);
            params.put("carrier", id);
            params.put("content", content);
            ApiClient.getInstance(this).postForm(mUrl, params, new RespModifyMsgCallback(this) {
                @Override
                public void onSuccess() {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(AddPkgActivity.this, "添加包裹成功", 1);
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFail(String status) {
                    if ("-2".equals(status)) {
                        ShowToastUtils.toast(AddPkgActivity.this, "该单号已添加", 3);
                    } else {
                        ShowToastUtils.toast(AddPkgActivity.this, "失败", 2);
                    }
                    AppUtils.stopProgressDialog(progressDialog);
                }
            });
        } else {
            ShowToastUtils.toast(this, getString(R.string.no_net_hint), 3);
        }
    }

    private String getTextStr(View view) {
        if (view instanceof TextView) {
            return ((TextView) view).getText().toString().trim();
        }
        return "";
    }

    private void selectSex(int item, List<WuliuBean> list) {
        String[] tmpItems = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            tmpItems[i] = list.get(i).getName();
        }
        final String items[] = tmpItems;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("请选择快递:"); //设置标题
        builder.setIcon(R.drawable.express_hint);//设置图标，图片id即可
        builder.setSingleChoiceItems(items, item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mName.setText(items[which]);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        KLog.d();
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //back
                finish();
                break;
            case R.id.iv_scan:
                Intent intent = new Intent(this, ScanActivity.class);
                startActivityForResult(intent, TOSCAN);
                break;
          /*  case R.id.tv_gochat:
                AppUtils.goChat(this);
                break;
            case R.id.tv_issue:
                Intent i = new Intent(this, FaqActivity.class);
                i.putExtra("url", ConfigConstants.faq_url);
                this.startActivity(i);
                break;*/
            case R.id.tv_name:
                //select name
                if (null != mWuliuList) {
                    String name = mName.getText().toString();
                    int id = StringUtils.getExpressId(name);
                    if (id == 0) {
                        selectSex(0, mWuliuList);
                    } else {
                        selectSex(id - 361, mWuliuList);
                    }
                    return;
                }
                progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
                if (NetUtils.hasAvailableNet(this)) {
                    ApiClient.getInstance(this).postForm(ConfigConstants.GETLISTWULIU, new RespAddTCallback<List<WuliuBean>>(this, progressDialog) {
                        @Override
                        public void onSuccess(List<WuliuBean> wuliuBeen) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != wuliuBeen && wuliuBeen.size() > 0) {
                                mWuliuList = wuliuBeen;
                                String name = mName.getText().toString();
                                int id = StringUtils.getExpressId(name);
                                if (id == 0) {
                                    selectSex(0, wuliuBeen);
                                } else {
                                    selectSex(id - 361, wuliuBeen);
                                }
                            } else {
                                ShowToastUtils.toast(mActivity, "获取物流公司失败", 3);
                            }
                        }

                        @Override
                        public void onFail(List<WuliuBean> wuliuBeen) {
                            ShowToastUtils.toast(mActivity, "获取物流公司失败", 3);
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                } else {
                    ShowToastUtils.toast(this, getString(R.string.no_net_hint), 3);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TOSCAN:
                    KLog.d(data);
                    if (null != data) {
                        String result = data.getStringExtra(MSCANRESULT);
                        KLog.d(result);
                        mNumber.setText(result);
                    }
                    break;
            }
        }
    }
}
