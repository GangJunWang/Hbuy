package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import au.com.hbuy.aotong.domain.User;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.SharedUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespSendCodeCallback;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CountTimer;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmailBoundActivity extends BaseFragmentActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.email_edit)
    ClearEditText emailEdit;
    @Bind(R.id.pwd_edit)
    ClearEditText pwdEdit;
    @Bind(R.id.tv_getcode)
    TextView tvGetcode;
    @Bind(R.id.bt_next)
    Button btNext;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    private CountTimer mTimeCount;
    private Activity mActivity = EmailBoundActivity.this;
    private String mEmail, mHint;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_bound);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mHint = intent.getStringExtra("hint");
        if ("1".equals(mHint)) {
            //绑定
            tvTitle.setText("绑定邮箱");
        } else {
            tvTitle.setText("换绑邮箱");
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_getcode, R.id.bt_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_getcode:
                mEmail = AppUtils.getTextStr(emailEdit);
                if (StringUtils.isEmpty(mEmail)) {
                    ShowToastUtils.toast(EmailBoundActivity.this, "邮箱不能为空");
                    return;
                }
                if (!StringUtils.isEmail(mEmail)) {
                    ShowToastUtils.toast(this, "请输入正确的邮箱格式");
                    return;
                }
                if (NetUtils.isNetworkAvailable(this)) {
                    progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);

                    HashMap params = new HashMap();
                    params.put("email", mEmail);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.GETEMAILCODE_SECURITY, params, new RespSendCodeCallback<User>(this, progressDialog) {
                        @Override
                        public void onSuccess(String str, String msg) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != str) {
                                if (str.equals("0")) {
                                    ShowToastUtils.toast(EmailBoundActivity.this, "发送失败,请稍后再试", 2);
                                    return;
                                } else if (str.equals("1")) {
                                    ShowToastUtils.toast(EmailBoundActivity.this, "发送成功", 1);
                                    mTimeCount = new CountTimer(tvGetcode, 0xfff30008, 0xff969696);//传入了文字颜色值
                                    mTimeCount.start();
                                    return;
                                } else if (str.equals("-1")) {
                                    ShowToastUtils.toast(EmailBoundActivity.this, "邮箱已经存在");
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onFail(User o) {
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                } else {
                    ShowToastUtils.toast(this, getString(R.string.net_hint));
                }
                break;
            case R.id.bt_next:
                String code = AppUtils.getTextStr(pwdEdit);
                if (StringUtils.isEmpty(code)) {
                    ShowToastUtils.toast(EmailBoundActivity.this, "验证码不能为空");
                    return;
                }
                if (NetUtils.isNetworkAvailable(this)) {
                    progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);

                    HashMap params = new HashMap();
                    params.put("code", code);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SEND_EMAIL_CODE_AUTH_SECURITY_URL, params, new RespSendCodeCallback<User>(mActivity, progressDialog) {
                        @Override
                        public void onSuccess(String str, String msg) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != str) {
                                if (str.equals("1")) {
                                    SharedUtils.putString(mActivity, "email", mEmail);
                                    //发送成功
                                    if ("1".equals(mHint)) {
                                        ShowToastUtils.toast(mActivity, "绑定邮箱成功", 1);
                                        setResult(RESULT_OK);
                                    } else if ("2".equals(mHint)) {
                                        ShowToastUtils.toast(mActivity, "换绑邮箱成功", 1);

                                        Intent intent = new Intent(mActivity, SecurityCenterActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                } else if (str.equals("0")) {
                                    ShowToastUtils.toast(mActivity, "失败,请稍后再试", 2);
                                    return;
                                } else if (str.equals("-1")) {
                                    ShowToastUtils.toast(mActivity, "验证码过期");
                                    return;
                                } else if (str.equals("-2")) {
                                    ShowToastUtils.toast(mActivity, "验证码错误");
                                    return;
                                } else if (str.equals("-3")) {
                                    ShowToastUtils.toast(mActivity, "邮箱已存在");
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onFail(User o) {
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                } else {
                    ShowToastUtils.toast(this, getString(R.string.net_hint));
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
