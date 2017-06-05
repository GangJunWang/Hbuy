package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class PhoneRegisterNextActivity extends BaseFragmentActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_phone_hint)
    TextView tvPhoneHint;
    @Bind(R.id.auto_edit)
    ClearEditText autoEdit;
    @Bind(R.id.code_text)
    TextView codeText;
    @Bind(R.id.pwd_edit)
    ClearEditText pwdEdit;
    @Bind(R.id.pwd_edit_verify)
    ClearEditText pwdEditVerify;
    @Bind(R.id.register_agree_button)
    Button registerAgreeButton;
    @Bind(R.id.tv_user_deal)
    TextView tvUserDeal;
    @Bind(R.id.tv_feedback)
    TextView tvFeedback;
    private String mPhone;

    private CountTimer mTimeCount;
    private CustomProgressDialog progressDialog;
    private Activity mActivity = PhoneRegisterNextActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone_next);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mPhone = intent.getStringExtra("phone");
        tvPhoneHint.setText("已向" + mPhone + "发送验证码,请注意查收");
        mTimeCount = new CountTimer(codeText, 0xfff30008, 0xff969696);//传入了文字颜色值
        KLog.d(intent.getStringExtra("phone") + "---" + intent.getStringExtra("msg"));
        String msg = intent.getStringExtra("msg");
        if (null != msg && msg.equals("1")) {
            mTimeCount.start();
        }

        registerAgreeButton.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if (NetUtils.isNetworkAvailable(mActivity)) {
                    String pwd = pwdEdit.getText().toString().trim();
                    String pwd2 = pwdEditVerify.getText().toString().trim();
                    String code = autoEdit.getText().toString().trim();

                    if (pwd.isEmpty() || pwd2.isEmpty() || code.isEmpty()) {
                        ShowToastUtils.toast(mActivity, "请完善信息");
                        return;
                    }
                    if (!pwd.equals(pwd2)) {
                        ShowToastUtils.toast(mActivity, "二次输入的密码不同,请重新输入");
                        return;
                    }
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);

                    HashMap params = new HashMap();
                    params.put("code", autoEdit.getText().toString().trim());
                    params.put("passwd", pwd);
                    params.put("repasswd", pwd2);

                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.PHONESIGNURL, params, new RespSendCodeCallback<User>(mActivity, progressDialog) {
                        @Override
                        public void onSuccess(String str, String msg) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != str) {
                                if (StringUtils.toInt(str) > 0) {
                                    ShowToastUtils.toast(PhoneRegisterNextActivity.this, "注册成功", 1);
                                    setResult(RESULT_OK);
                                    finish();
                                    return;
                                } else if (str.equals("0")) {
                                    ShowToastUtils.toast(PhoneRegisterNextActivity.this, "注册失败,请稍后再试", 2);
                                    return;
                                } else if (str.equals("-1")) {
                                    ShowToastUtils.toast(PhoneRegisterNextActivity.this, "短信验证码过期");
                                    return;
                                } else if (str.equals("-2")) {
                                    ShowToastUtils.toast(PhoneRegisterNextActivity.this, "两次密码不一致");
                                    return;
                                } else if (str.equals("-3")) {
                                    ShowToastUtils.toast(PhoneRegisterNextActivity.this, "短信验证码错误");
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
                    ShowToastUtils.toast(mActivity, getString(R.string.net_hint));
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.code_text, R.id.register_agree_button, R.id.tv_user_deal, R.id.tv_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_feedback:
                AppUtils.showActivity(this, FeekBackActivity.class);
                break;
            case R.id.code_text:
                if (NetUtils.isNetworkAvailable(this)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.resend_phone_code_url, new RespSendCodeCallback<User>(this, progressDialog) {
                        @Override
                        public void onSuccess(String str, String msg) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != str) {
                                if (str == "1") {
                                    mTimeCount.start();
                                    ShowToastUtils.toast(PhoneRegisterNextActivity.this, "发送成功", 1);
                                    return;
                                } else if (str == "0") {
                                    ShowToastUtils.toast(PhoneRegisterNextActivity.this, "发送失败,请稍后再试", 2);
                                    return;
                                } else if (str == "-2") {
                                    ShowToastUtils.toast(PhoneRegisterNextActivity.this, "60秒后再试");
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
                    AppUtils.stopProgressDialog(progressDialog);
                }
                break;
            case R.id.tv_user_deal:
                AppUtils.showActivity(mActivity, UserdealActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        AppUtils.stopProgressDialog(progressDialog);
    }

    @OnClick(R.id.tv_feedback)
    public void onClick() {
    }
}
