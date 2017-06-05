package au.com.hbuy.aotong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
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
import au.com.hbuy.aotong.view.CustomProgressDialog;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneRegisterActivity extends BaseActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.code_edit)
    TextView codeEdit;
    @Bind(R.id.code_text)
    TextView codeText;
    @Bind(R.id.phone_edit)
    ClearEditText phoneEdit;
    @Bind(R.id.register_agree_button)
    TextView registerAgreeButton;
    @Bind(R.id.tv_email_register)
    TextView tvEmailRegister;
    @Bind(R.id.tv_feedback)
    TextView tvFeedback;
    private Activity mActivity = PhoneRegisterActivity.this;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        ButterKnife.bind(this);

        setTitle("手机号注册");
        findViewById(R.id.register_agree_button).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                final String numStr = phoneEdit.getText().toString().trim();
                if (!StringUtils.isNumber(numStr)) {
                    ShowToastUtils.toast(mActivity, "请输入正确的手机号码");
                    return;
                }
                final String code = codeText.getText().toString().trim();
                if (null == code || StringUtils.isEmpty(code)) {
                    ShowToastUtils.toast(mActivity, "国家码不能为空");
                    return;
                }

                if (NetUtils.isNetworkAvailable(mActivity)) {
                    progressDialog = AppUtils.startProgressDialog(mActivity, "", progressDialog);

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("phonecode", code.substring(1, code.length()));
                    params.put("phone", numStr);
                    ApiClient.getInstance(getApplicationContext()).postForm(ConfigConstants.send_phone_code_url, params, new RespSendCodeCallback<User>(mActivity) {
                        @Override
                        public void onSuccess(String str, String msg) {
                            AppUtils.stopProgressDialog(progressDialog);
                            KLog.d(str + "---msg = " + msg);
                            if (null != str && str.equals("1")) {
                                if (msg.equals("1")) {
                                    //发送成功
                                    Intent intent = new Intent(mActivity, PhoneRegisterNextActivity.class);
                                    intent.putExtra("phone", code + " " + numStr);
                                    intent.putExtra("msg", str);
                                    mActivity.startActivityForResult(intent, 2);
                                } else {
                                    ShowToastUtils.toast(mActivity, "验证码发送失败", 2);
                                    return;
                                }
                            } else if (null != str && str.equals("0")) {
                                ShowToastUtils.toast(mActivity, "该手机号码已注册,请更换手机号码");
                                return;
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

        Spannable span2 = new SpannableString(tvEmailRegister.getText());
        span2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.phone_register_hint)), 6, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvEmailRegister.setText(span2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.stopProgressDialog(progressDialog);
    }

    @OnClick({R.id.code_edit, R.id.register_agree_button, R.id.tv_email_register, R.id.iv_back, R.id.tv_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_feedback:
                AppUtils.showActivity(this, FeekBackActivity.class);
                break;
            case R.id.code_edit:
                //   StringUtils.selectCode(0, codeEdit, codeText, this);
                Intent i = new Intent();
                i.setClass(this, SelectNationAndCodeActivity.class);
                i.putExtra("type", "3");
                startActivityForResult(i, 1);
                break;
            case R.id.tv_email_register:
                mActivity.startActivityForResult(new Intent(this, EmailRegisterActivity.class), 2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.d("request =" + requestCode + "result=" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    //邮箱注册 手机注册
                    setResult(RESULT_OK);
                    finish();
                    break;
                case 1:
                    if (null != data) {
                        Bundle bunde = data.getExtras();
                        String code = bunde.getString("code");
                        String nation = bunde.getString("nation");
                        codeEdit.setText(nation);
                        codeText.setText("+" + code);
                    }
                    break;
            }
        }
    }
}
