package au.com.hbuy.aotong;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import au.com.hbuy.aotong.domain.User;
import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.utils.ConfigConstants;
import au.com.hbuy.aotong.utils.NetUtils;
import au.com.hbuy.aotong.utils.ShowToastUtils;
import au.com.hbuy.aotong.utils.StringUtils;
import au.com.hbuy.aotong.utils.okhttp.ApiClient;
import au.com.hbuy.aotong.utils.okhttp.RespSendCodeCallback;
import au.com.hbuy.aotong.view.AutoFillEmailEditText;
import au.com.hbuy.aotong.view.ClearEditText;
import au.com.hbuy.aotong.view.CountTimer;
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmailRegisterActivity extends BaseFragmentActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.email_edit)
    AutoFillEmailEditText emailEdit;
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
    private CountTimer mTimeCount;
    private CustomProgressDialog progressDialog;
    private Activity mActivity = EmailRegisterActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);
        ButterKnife.bind(this);
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
                String email = emailEdit.getText().toString().trim();
                if (!StringUtils.isEmail(email)) {
                    ShowToastUtils.toast(this, "请输入正确的邮箱格式");
                    return;
                } else {
                    if (NetUtils.isNetworkAvailable(this)) {
                        progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);
                        HashMap params = new HashMap();
                        params.put("email", email);
                        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.GETEMAILCODE, params, new RespSendCodeCallback<User>(this) {
                            @Override
                            public void onSuccess(String str, String msg) {
                                if (null != str) {
                                    if (str.equals("0")) {
                                        ShowToastUtils.toast(EmailRegisterActivity.this, "发送失败,请稍后再试", 2);
                                    } else if (str.equals("1")) {
                                        ShowToastUtils.toast(EmailRegisterActivity.this, "发送成功", 1);
                                        mTimeCount = new CountTimer(codeText, 0xfff30008, 0xff969696);//传入了文字颜色值
                                        mTimeCount.start();
                                    } else if (str.equals("-1")) {
                                        ShowToastUtils.toast(EmailRegisterActivity.this, "邮箱已经存在");
                                    }
                                }
                                AppUtils.stopProgressDialog(progressDialog);
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
                }
                break;
            case R.id.register_agree_button:
                if (NetUtils.isNetworkAvailable(this)) {
                    String pwd = pwdEdit.getText().toString().trim();
                    String pwd2 = pwdEditVerify.getText().toString().trim();
                    String email2 = emailEdit.getText().toString().trim();
                    String code = autoEdit.getText().toString().trim();
                    int pwdLength = StringUtils.getStringLength(pwd);
                    if (!StringUtils.isEmail(email2)) {
                        ShowToastUtils.toast(this, "请输入正确的邮箱格式");
                        return;
                    }
                    if (code.isEmpty() || email2.isEmpty()) {
                        ShowToastUtils.toast(this, "请补全信息");
                        return;
                    } else if (pwdLength < 6 || pwdLength > 20) {
                        ShowToastUtils.toast(this, "密码的长度为6-20");
                        return;
                    }
                    if (pwd.isEmpty() || pwd2.isEmpty()) {
                        ShowToastUtils.toast(this, "请输入密码");
                        return;
                    }
                    if (!pwd.equals(pwd2)) {
                        ShowToastUtils.toast(this, "二次输入的密码不同,请重新输入");
                        return;
                    }
                    HashMap params = new HashMap();
                    params.put("code", code);
                    params.put("passwd", pwd);
                    params.put("repasswd", pwd2);
                    params.put("email", email2);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.EMAILSIGNURL, params, new RespSendCodeCallback<User>(this) {
                        @Override
                        public void onSuccess(String str, String msg) {
                            if (null != str) {
                                if (StringUtils.toInt(str) > 0) {
                                    ShowToastUtils.toast(EmailRegisterActivity.this, "注册成功", 1);
                                    setResult(RESULT_OK);
                                    finish();
                                    return;
                                } else if (str.equals("0")) {
                                    ShowToastUtils.toast(EmailRegisterActivity.this, "注册失败,请稍后再试", 2);
                                    return;
                                } else if (str.equals("-1")) {
                                    ShowToastUtils.toast(EmailRegisterActivity.this, "短信验证码过期");
                                    return;
                                } else if (str.equals("-2")) {
                                    ShowToastUtils.toast(EmailRegisterActivity.this, "验证码错误");
                                    return;
                                } else if (str.equals("-3")) {
                                    ShowToastUtils.toast(EmailRegisterActivity.this, "两次密码不一致");
                                    return;
                                } else if (str.equals("-4")) {
                                    ShowToastUtils.toast(EmailRegisterActivity.this, "该邮箱已被注册");
                                    return;
                                } else if (str.equals("-5")) {
                                    ShowToastUtils.toast(EmailRegisterActivity.this, "邮箱格式错误");
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onFail(User o) {
                        }
                    });
                } else {
                    ShowToastUtils.toast(this, getString(R.string.net_hint));
                }
                break;
            case R.id.tv_user_deal:
                //用户协议
                AppUtils.showActivity(mActivity, UserdealActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }
}
