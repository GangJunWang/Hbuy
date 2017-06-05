package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;

import java.util.HashMap;

import au.com.hbuy.aotong.domain.User;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.NoDoubleClickListener;
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

public class AuthUserActivity extends BaseFragmentActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_phone_hint)
    TextView tvPhoneHint;
    @Bind(R.id.auto_edit)
    ClearEditText autoEdit;
    @Bind(R.id.code_text)
    TextView codeText;
    @Bind(R.id.register_agree_button)
    Button registerAgreeButton;
    private CountTimer mTimeCount;
    private String mPhone, mPhoneCode, mEmail, mHint;
    private Activity mActivity = AuthUserActivity.this;
    private CustomProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_user);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mHint = intent.getStringExtra("hint");
        String msg = intent.getStringExtra("msg");
        mTimeCount = new CountTimer(codeText, 0xfff30008, 0xff969696);//传入了文字颜色值
        if ("1".equals(mHint)) {
            //phone
            mPhoneCode = intent.getStringExtra("phoneCode");
            mPhone = intent.getStringExtra("phone");
            if ("1".equals(msg)) {
                tvPhoneHint.setText("已向" + mPhoneCode + " " + mPhone + "发送验证码,请注意查收");
                mTimeCount.start();
            } else {
                tvPhoneHint.setText("发送验证码失败,请重新发送");
            }
        } else if ("2".equals(mHint)) {
            //email
            mEmail = intent.getStringExtra("email");
            KLog.d(msg + mHint);
            if ("1".equals(msg)) {
                tvPhoneHint.setText("已向" + mEmail + "发送验证码,请注意查收");
                mTimeCount.start();
            } else {
                tvPhoneHint.setText("发送验证码失败,请重新发送");
            }
        }

        registerAgreeButton.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if (NetUtils.isNetworkAvailable(mActivity)) {
                    String code = AppUtils.getTextStr(autoEdit);

                    if (StringUtils.isEmpty(code)) {
                        ShowToastUtils.toast(mActivity, "验证码不能为空", 3);
                        return;
                    }
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                    HashMap param = new HashMap();

                    param.put("code", code);
                    if ("1".equals(mHint)) {
                        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SEND_CHECK_PHONE_CODE_AUTH_SECURITY_URL, param, new RespSendCodeCallback<User>(mActivity) {
                            @Override
                            public void onSuccess(String str, String msg) {
                                AppUtils.stopProgressDialog(progressDialog);

                                if (null != str) {
                                    if (str.equals("1")) {
                                        Intent intent = new Intent(mActivity, PhoneBoundActivity.class);
                                        intent.putExtra("hint", "2");
                                        startActivity(intent);
                                        finish();
                                        return;
                                    } else if (str.equals("0")) {
                                        ShowToastUtils.toast(mActivity, "失败,请稍后再试", 2);
                                        return;
                                    } else if (str.equals("-1")) {
                                        ShowToastUtils.toast(mActivity, "短信验证码过期", 3);
                                        return;
                                    } else if (str.equals("-2")) {
                                        ShowToastUtils.toast(mActivity, "验证码错误", 3);
                                        return;
                                    } else if (str.equals("-3")) {
                                        ShowToastUtils.toast(mActivity, "短信验证码错误", 3);
                                        return;
                                    }
                                }
                            }
                            @Override
                            public void onFail(User o) {
                                AppUtils.stopProgressDialog(progressDialog);
                            }
                        });

                    } else if ("2".equals(mHint)) {
                        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SEND_CHECK_EMAIL_CODE_AUTH_SECURITY_URL, param, new RespSendCodeCallback<User>(mActivity, progressDialog) {
                            @Override
                            public void onSuccess(String str, String msg) {
                                AppUtils.stopProgressDialog(progressDialog);

                                if (null != str) {
                                    if (str.equals("1")) {
                                        Intent intent = new Intent(mActivity, EmailBoundActivity.class);
                                        intent.putExtra("hint", "2");
                                        startActivity(intent);
                                        finish();
                                        return;
                                    } else if (str.equals("0")) {
                                        ShowToastUtils.toast(mActivity, "失败,请稍后再试", 2);
                                        return;
                                    } else if (str.equals("-1")) {
                                        ShowToastUtils.toast(mActivity, "短信验证码过期", 3);
                                        return;
                                    }
                                }
                            }

                            @Override
                            public void onFail(User o) {
                                AppUtils.stopProgressDialog(progressDialog);

                            }
                        });
                    }
                } else {
                    AppUtils.stopProgressDialog(progressDialog);
                    ShowToastUtils.toast(mActivity, getString(R.string.net_hint), 3);
                }
            }
        });

        codeText.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if (NetUtils.isNetworkAvailable(mActivity)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);

                    if ("1".equals(mHint)) {
                        HashMap param = new HashMap();
                        param.put("phone", mPhone);
                        param.put("phonecode", mPhoneCode);
                        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SEND_PHONE_CODE_SECURITY_URL, param, new RespSendCodeCallback<User>(mActivity, progressDialog) {
                            @Override
                            public void onSuccess(String str, String msg) {
                                AppUtils.stopProgressDialog(progressDialog);

                                if (null != str) {
                                    if ("1".equals(str)) {
                                        tvPhoneHint.setText("已向+" + mPhoneCode + " " + mPhone + "发送验证码,请注意查收");
                                        ShowToastUtils.toast(mActivity, "发送成功", 1);
                                        mTimeCount.start();
                                    } else if ("0".equals(str)) {
                                        tvPhoneHint.setText("发送验证码失败,请重新发送");
                                        ShowToastUtils.toast(mActivity, "发送失败", 2);
                                    }
                                }
                            }

                            @Override
                            public void onFail(User o) {
                                AppUtils.stopProgressDialog(progressDialog);

                            }
                        });

                    } else if ("2".equals(mHint)) {
                        HashMap param = new HashMap();
                        param.put("email", mEmail);
                        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SEND_EMAIL_CODE_SECURITY_URL, param, new RespSendCodeCallback<User>(mActivity) {
                            @Override
                            public void onSuccess(String str, String msg) {
                                AppUtils.stopProgressDialog(progressDialog);

                                if (null != str) {
                                    if ("1".equals(str)) {
                                        mTimeCount.start();
                                        tvPhoneHint.setText("已向+" + mEmail + "发送验证码,请注意查收");
                                        ShowToastUtils.toast(mActivity, "发送成功", 1);
                                    } else if ("0".equals(str)) {
                                        tvPhoneHint.setText("发送验证码失败,请重新发送");
                                        ShowToastUtils.toast(mActivity, "发送失败", 2);
                                    }
                                }
                            }

                            @Override
                            public void onFail(User o) {
                                AppUtils.stopProgressDialog(progressDialog);
                            }
                        });
                    }
                } else {
                    ShowToastUtils.toast(mActivity, getString(R.string.net_hint), 3);
                    AppUtils.stopProgressDialog(progressDialog);
                }
            }
        });

    }

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
