package au.com.hbuy.aotong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import au.com.hbuy.aotong.domain.ContentData;
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

public class PhoneBoundActivity extends BaseFragmentActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.code_edit)
    TextView codeEdit;
    @Bind(R.id.code_text)
    TextView codeText;
    @Bind(R.id.phone_edit)
    ClearEditText phoneEdit;
    @Bind(R.id.code_edit_input)
    ClearEditText codeEditInput;
    @Bind(R.id.tv_getcode)
    TextView tvGetcode;
    @Bind(R.id.register_agree_button)
    Button registerAgreeButton;
    private String mHint, mPhone, mPhoneCode;
    private CountTimer mTimeCount;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_phone);
        ButterKnife.bind(this);
        mTimeCount = new CountTimer(tvGetcode, 0xfff30008, 0xff969696);//传入了文字颜色值

        mHint = getIntent().getStringExtra("hint");
        if ("1".equals(mHint)) {
            tvTitle.setText("绑定手机");
        } else if ("2".equals(mHint)) {
            tvTitle.setText("换绑手机");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }

    @OnClick({R.id.iv_back, R.id.code_edit, R.id.tv_getcode, R.id.register_agree_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.code_edit:
                // StringUtils.selectCode(0, codeEdit, codeText, this);
                Intent i = new Intent();
                i.setClass(this, SelectNationAndCodeActivity.class);
                i.putExtra("type", "3");
                startActivityForResult(i, 1);
                break;
            case R.id.tv_getcode:
                mPhone = AppUtils.getTextStr(phoneEdit);
                if (StringUtils.isNumber(mPhone)) {
                    if (NetUtils.isNetworkAvailable(this)) {
                        progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);
                        HashMap<String, String> params = new HashMap<String, String>();
                        mPhoneCode = AppUtils.getTextStr(codeText);
                        params.put("phonecode", mPhoneCode.substring(1, mPhoneCode.length()));
                        params.put("phone", mPhone);
                        ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SEND_PHONE_CODE_SECURITY_URL, params, new RespSendCodeCallback<User>(this, progressDialog) {
                            @Override
                            public void onSuccess(String str, String msg) {
                                AppUtils.stopProgressDialog(progressDialog);
                                if (null != str) {
                                    if (str.equals("1")) {
                                        mTimeCount.start();
                                    } else if (str.equals("0")) {
                                        ShowToastUtils.toast(PhoneBoundActivity.this, "该手机号码已注册,请更换手机号码");
                                        return;
                                    } else if ("-1".equals(str)) {
                                        ShowToastUtils.toast(PhoneBoundActivity.this, "发送时间小于60s");
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
                } else {
                    ShowToastUtils.toast(this, "请输入正确的手机号码");
                    registerAgreeButton.setEnabled(true);
                    return;
                }
                break;
            case R.id.register_agree_button:
                String code = codeEditInput.getText().toString().trim();
                if (code.equals("")) {
                    ShowToastUtils.toast(this, "验证码不能为空");
                    return;
                }
                if (NetUtils.isNetworkAvailable(this)) {
                    progressDialog = AppUtils.startProgressDialog(this, "", progressDialog);

                    HashMap<String, String> params = new HashMap<String, String>();
                    mPhoneCode = AppUtils.getTextStr(codeText);
                    params.put("code", code);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.SEND_PHONE_CODE_AUTH_SECURITY_URL, params, new RespSendCodeCallback<User>(this, progressDialog) {
                        @Override
                        public void onSuccess(String str, String msg) {
                            AppUtils.stopProgressDialog(progressDialog);
                            if (null != str) {
                                if (str.equals("1")) {
                                    SharedUtils.putString(PhoneBoundActivity.this, "phone", mPhone);
                                    SharedUtils.putString(PhoneBoundActivity.this, "phonecode", mPhoneCode);
                                    //成功  1成功  0失败-1验证码过期-2验证码错误-3手机号已存在
                                    if ("1".equals(mHint)) {
                                        setResult(RESULT_OK);
                                        ShowToastUtils.toast(PhoneBoundActivity.this, "绑定手机成功", 1);
                                    } else if ("2".equals(mHint)) {
                                        ShowToastUtils.toast(PhoneBoundActivity.this, "换绑手机成功", 1);
                                        Intent intent = new Intent(PhoneBoundActivity.this, SecurityCenterActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                } else if (str.equals("0")) {
                                    ShowToastUtils.toast(PhoneBoundActivity.this, "失败", 2);
                                    mTimeCount.onFinish();
                                    return;
                                } else if ("-1".equals(str)) {
                                    mTimeCount.onFinish();
                                    ShowToastUtils.toast(PhoneBoundActivity.this, "验证码过期");
                                    return;
                                } else if ("-2".equals(str)) {
                                    mTimeCount.onFinish();
                                    ShowToastUtils.toast(PhoneBoundActivity.this, "验证码错误");
                                    return;
                                } else if ("-3".equals(str)) {
                                    mTimeCount.onFinish();
                                    ShowToastUtils.toast(PhoneBoundActivity.this, "手机号已存在");
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onFail(User o) {
                            AppUtils.stopProgressDialog(progressDialog);
                        }
                    });
                    break;
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            switch (requestCode) {
                case 1:
                    Bundle bunde = data.getExtras();
                    String nation = bunde.getString("nation");
                    String code = bunde.getString("code");
                    codeEdit.setText(nation);
                    codeText.setText("+" + code);
                    break;
            }
        }
    }
}
